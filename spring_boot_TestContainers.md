# README TestContainers Infrastructure

This document explains in detail the test infrastructure provided by the class `TestContainersBase`.
It covers purpose, structure, and usage of the components: Kafka (via Toxiproxy), MongoDB (via a custom TCP proxy), Redis, and the Spring test configuration.

## Goal
The class offers a reusable, performant, and flexibly manipulable container-based test environment to:
- Enable parallel tests with stable, reproducible environments
- Simulate network failures (chaos testing) – Kafka via Toxiproxy, MongoDB via TCP proxy
- Provide topics dynamically while avoiding conflicts
- Start containers only once (lazy singleton pattern)

## Usage in a Test
Register the dynamic properties in your test class:
```java
@DynamicPropertySource
static void register(DynamicPropertyRegistry registry) {
    TestContainersBase.configureDynamicPropertySource(registry, false); // or true for unique topics
}
```
`forceUniqueTopicNames` ensures unique topic names per test run:
```java
TestContainersBase.configureDynamicPropertySource(registry, true); // avoids conflicts in parallel runs
```

## Key Static Fields Overview
```java
public static final int KAFKA_DEFAULT_PORT = 9092;
public static final int KAFKA_PROXY_PORT = 9099;
public static final int TOXY_PROXY_CONTROL_PORT = 8474;
public static final List<String> TOPICS = List.of(
    "topic1", "topic2", "topic3", "topic4", "topic5", "topic6", "topic7", "topic8");
private static Proxy kafkaProxy; // Toxiproxy proxy instance
private static volatile boolean containersHaveStarted = false; // singleton start guard
```
These variables manage ports, the topic list, and container lifecycle.

## Network & Toxiproxy Initialization
The static block creates the network, Toxiproxy, and Kafka container such that **no direct port** of the Kafka container is published externally. All traffic flows through Toxiproxy and can be interrupted for failure simulation:
```java
SHARED_NETWORK = Network.newNetwork();
toxiproxy = new ToxiproxyContainer(DockerImageName.parse("ghcr.io/shopify/toxiproxy:2.5.0"))
    .withNetwork(SHARED_NETWORK)
    .withExposedPorts(KAFKA_PROXY_PORT, TOXY_PROXY_CONTROL_PORT);
toxiproxy.start();
var advertisedListener = "PLAINTEXT://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(KAFKA_PROXY_PORT);   
confluentKafkaContainer = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
                .withNetwork(SHARED_NETWORK)
                .withNetworkAliases("kafka-broker")
                .withEnv("KAFKA_BROKER_ID", "1")
                .withEnv("KAFKA_NODE_ID", "1")
                .withEnv("KAFKA_PROCESS_ROLES", "broker,controller")
                .withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093")
                .withEnv("KAFKA_ADVERTISED_LISTENERS", advertisedListener)
                .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT")
                .withEnv("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
                .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
                .withEnv("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@kafka-broker:9093")
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
                .withEnv("KAFKA_LOG_DIRS", "/tmp/kraft-combined-logs")
                .withEnv("CLUSTER_ID", "MkU3OEVBNTcwNTJENDM2Qk")
                .withCreateContainerCmdModifier(cmd -> {
                    // ✅ Keine Ports nach außen, damit wir sicher sind, dass der Traffic nur
                    // über Toxiproxy läuft
                    cmd.withExposedPorts();
                    Objects.requireNonNull(cmd.getHostConfig()).withPublishAllPorts(false);
                })
                .waitingFor(Wait.forLogMessage(".*KafkaRaftServer nodeId=1.*started.*", 1));
```
`advertisedListener` points directly to the proxy host + port:
```java
var advertisedListener = "PLAINTEXT://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(KAFKA_PROXY_PORT);
```
This is critical because Kafka clients cache the advertised listeners from the **initial metadata response** and reconnect later using those. By advertising the proxy address, all future reconnect attempts remain controllable.

## Creating the Kafka Proxy
The proxy is created on first startup:
```java
ToxiproxyClient toxiproxyClient = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
kafkaProxy = toxiproxyClient.createProxy(
    "kafka_proxy", "0.0.0.0:" + KAFKA_PROXY_PORT, "kafka-broker:" + KAFKA_DEFAULT_PORT);
```
- Upstream: local bind `0.0.0.0:9099`
- Downstream: actual broker in internal network `kafka-broker:9092`

## Fast Disable / Restore of Kafka Connectivity
Kafka communication can be cut using a toxic named `network-cut`:
```java
public static void disableKafkaConnection() throws IOException {
    if (kafkaProxy != null) {
        kafkaProxy.toxics().resetPeer("network-cut", ToxicDirection.DOWNSTREAM, 0);
        log.info("Kafka connection disabled via Toxiproxy");
    }
}
```
Restore connectivity by removing the toxic:
```java
public static void enableKafkaConnection() throws IOException {
    if (kafkaProxy != null) {
        kafkaProxy.toxics().getAll().stream()
            .filter(t -> t.getName().equals("network-cut"))
            .forEach(toxic -> toxic.remove());
        log.info("Kafka connection Toxiproxy, all toxics removed");
    }
}
```
Advantages:
- Works even after clients cached cluster metadata
- No Kafka container restart required
- Enables robust failure scenario tests (retry/backoff/error handling)

### Typical Test Use Case
```java
@Test
void shouldHandleKafkaOutageGracefully() throws IOException {
    // Arrange
    disableKafkaConnection();
    // Act -> send message / trigger consumer logic
    // Assert -> verify failure / retry handling
    enableKafkaConnection();
}
```

## Kafka Properties and Topic Configuration
```java
registry.add("spring.kafka.bootstrap-servers", kafkaBootstrapServer);
...
if (forceUniqueTopicNames) {
    var finalTopic = topic + "-" + topicInstanceCounter; // unique topic
    registry.add("spring.kafka.%s.topic".formatted(topic), () -> finalTopic);
}
```
Ensuring topics exist (with retry):
```java
try (var admin = AdminClient.create(props)) {
    Set<String> existing = admin.listTopics().names().get(...);
    var toCreate = topicNames.stream().filter(t -> !existing.contains(t))
        .map(t -> new NewTopic(t, 1, (short)1)).toList();
    admin.createTopics(toCreate).all().get(...);
}
```
### Why `ensureKafkaTopicsExist(...)` is useful
The method `ensureKafkaTopicsExist(List<String> topicNames, String bootstrapServers)` proactively hardens tests and reduces flakiness.

Key benefits:
- Avoids race conditions: Producers/consumers may start before auto-creation finishes; explicit creation removes that timing gap.
- Eliminates reliance on broker defaults: Auto-created topics might have unexpected partition or replication settings (in tests we enforce 1/1 deterministically).
- Idempotent safety: Existing topics are detected and skipped – parallel or repeated test runs remain stable.
- Reduces `UnknownTopicOrPartitionException`: Ensures the first send / subscribe succeeds without transient errors.
- Controlled timeouts & retries: Up to several attempts with short backoff cover early broker initialization phases.
- Fast failure feedback: If topic creation keeps failing you get a clear, early exception instead of hanging clients.
- Supports unique topic naming: Directly validates that dynamically generated (forceUniqueTopicNames) topics are present before use.

Excerpt of retry loop (simplified):
```java
int attempts = 0;
while (attempts++ < 5) {
    try {
        admin.createTopics(toCreate).all().get(...);
        return; // success
    } catch (Exception e) {
        attempts++;
        if (attempts >= 5) {
            throw new RuntimeException("Topic creation failed", e);
        }
        Thread.sleep(1000); // backoff
    }
}
```
Best practices:
- Call immediately after registering dynamic properties, before any producer/consumer beans are exercised.
- If extending: parameterize partitions/replication via test configuration for performance scenario variations.

## MongoDB with Replica Set + TCP Proxy
Replica set startup:
```java
mongoDbContainer = new MongoDBContainer(DockerImageName.parse("mongo:7"))
    .withCommand("--replSet", "rs0");
...
mongoDbContainer.start();
mongoDbContainer.execInContainer("mongosh", "--eval", "rs.initiate({...})");
mongoDbContainer.execInContainer("mongosh", "--eval", "while (!rs.isMaster().ismaster) { sleep(100); }");
```
### Why is the replica set required?
MongoDB Change Streams are only available on replica sets or sharded clusters. A standalone server cannot provide them. Therefore `mongoStartWithReplicaSet()` initializes the replica set and waits until `ismaster` becomes true. Only then can tests relying on change streams execute reliably.

Simplified sequence:
```java
mongoDbContainer.start();
mongoDbContainer.execInContainer("mongosh", "--eval", "rs.initiate({...})");
mongoDbContainer.execInContainer("mongosh", "--eval", "while (!rs.isMaster().ismaster) { sleep(100); }");
```
If omitted, operations expecting a `MongoChangeStreamCursor` fail.

Custom TCP proxy configuration (used especially to test the deployed application, not only local integration):
```java
proxyURI = mongoTCPProxyParameters.reserveServerSocketsAndCreateNewTargetUri(
    mongoDbContainer.getConnectionString() + "/" + mongoDbTest, 0);
registry.add("spring.data.mongodb.uri", () -> proxyURI);
```
Modifying `MongoProperties` and starting the proxy:
```java
MongoProperties modifiedMongoProperties(MongoProperties mongoProperties, TCPProxy tcpProxy) {
    tcpProxy.setBindAddress(mongoTCPProxyParameters.getBindAddress());
    tcpProxy.setServerSockets(mongoTCPProxyParameters.getProxyServerSockets());
    tcpProxy.setTargetHosts(mongoTCPProxyParameters.getTargetHosts());
    tcpProxy.setTargetPorts(mongoTCPProxyParameters.getTargetPorts());
    tcpProxy.start();
}
```
Order matters – call `configureDynamicPropertySource` before bean creation to ensure new ports are reserved.

### End-to-End Failure Simulation (Deployment) with `TCPProxy` + `MongoTCPProxyParameters`
Beyond Testcontainers, the same mechanism can be leveraged in a deployed environment to simulate real end-to-end outages against the database without stopping the application process. Goal: validate retry strategies, circuit breaker behavior, error messaging, and observability (logs/metrics).

#### Flow / Components
1. `MongoTCPProxyParameters` reserves local server ports and rewrites the Mongo URI to point to them.
2. The modified URI forces the `MongoClient` to connect through the `TCPProxy` instead of directly.
3. `TCPProxy` transparently forwards traffic from the reserved local ports to the real Mongo host/port.
4. Toggling `forwardingEnabled` immediately cuts or restores traffic.

#### Important URI Modifications
From `MongoTCPProxyParameters`:
```java
newUri.append("&directConnection=true&tlsInsecure=true");
if (ssl) {
    newUri.append("&ssl=true");
}
```
- `directConnection=true`: prevents the driver from performing additional server discovery that could bypass the proxy.
- `tlsInsecure=true`: required since the proxy cannot present a valid TLS certificate (use only if acceptable under your security policy).

Port reservation and target registration:
```java
ServerSocket serverSocket = createNewServerSocketRandomPort();
proxyServerSockets.add(serverSocket);
targetHosts.add(originalHost);
targetPorts.add(originalPort);
```

#### Proxy Startup in a Spring Configuration (Deployment Variant)
Example bean setup outside the test environment:
```java
@Configuration
public class MongoProxyConfig {

    @Bean
    public MongoTCPProxyParameters mongoTCPProxyParameters(
            @Value("${app.mongo.uri}") String originalUri) throws IOException {
        var params = new MongoTCPProxyParameters();
        params.reserveServerSocketsAndCreateNewTargetUri(originalUri, 0); // first host
        return params;
    }

    @Bean
    public TCPProxy mongoTcpProxy(MongoTCPProxyParameters params) throws IOException {
        TCPProxy proxy = new TCPProxy("mongoTcpProxy");
        proxy.setBindAddress(params.getBindAddress());
        proxy.setServerSockets(params.getProxyServerSockets());
        proxy.setTargetHosts(params.getTargetHosts());
        proxy.setTargetPorts(params.getTargetPorts());
        proxy.start();
        return proxy;
    }

    @Bean
    @Primary
    public MongoProperties mongoProperties(MongoTCPProxyParameters params) {
        MongoProperties props = new MongoProperties();
        // Use the modified URI from params (store it via additional bean or configuration)
        return props;
    }
}
```
The application then uses the modified `MongoProperties` with the proxy port.

#### Switching Network State (Failure Injection)
From `TCPProxy#setForwardingEnabled`:
```java
public void setForwardingEnabled(boolean enabled) {
    forwardingEnabled.set(enabled);
    if (!enabled) {
        closeSockets(activeClientSockets);
        closeSockets(activeTargetSockets);
        activeClientSockets.clear();
        activeTargetSockets.clear();
    }
    log.atInfo().addKeyValue("enabled", forwardingEnabled.get()).log("TCP-Proxy forwarding changed");
}
```
- `enabled = false`: Immediately drops all active connections (simulates network cut / timeout / connection refused depending on client behavior)
- `enabled = true`: New connections will again be forwarded normally

#### Example: Manual Outage Simulation in a Running System
```java
@RestController
@RequestMapping("/ops/mongo")
class MongoOpsController {
    private final TCPProxy mongoTcpProxy;
    MongoOpsController(@Qualifier("mongoTcpProxy") TCPProxy mongoTcpProxy) {
        this.mongoTcpProxy = mongoTcpProxy;
    }

    @PostMapping("/disconnect")
    public ResponseEntity<String> disconnect() {
        mongoTcpProxy.setForwardingEnabled(false);
        return ResponseEntity.ok("Mongo connection disconnected");
    }

    @PostMapping("/reconnect")
    public ResponseEntity<String> reconnect() {
        mongoTcpProxy.setForwardingEnabled(true);
        return ResponseEntity.ok("Mongo connection restored");
    }
}
```
These endpoints allow external scripts (or a chaos controller) to cut and restore the database connection mid-flow.

#### Deployment Test Ideas
- Write operation during disconnect: expect retry / failure event / dead letter handling
- Change Stream listener resilience: verify logging and recovery after reconnect
- Metrics & alerts: ensure health/liveness probes or observability alerts trigger

#### Benefits
- No application restart required
- Realistic simulation of network outages
- Reproducible failure modes for QA / chaos engineering
- Fine-grained control (timing & duration)

#### Important Notes
- Use of `tlsInsecure=true` must be vetted for security implications (internal testing only)
- Ensure resource cleanup: invoke `stop()` at shutdown (Spring captures `ContextClosedEvent` already)
- Multiple hosts in a Mongo URI (replica/sharded) may require extending parameter logic (currently single selected index `serverIdx`)

## Redis Configuration
Simple container with port mapping:
```java
redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
    .withExposedPorts(6379);
registry.add("spring.data.redis.host", redisContainer::getHost);
registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
```

## Singleton Container Startup
```java
synchronized (LOCK) {
    if (!containersHaveStarted) {
        confluentKafkaContainer.start();
        mongoStartWithReplicaSet();
        redisContainer.start();
        containersHaveStarted = true;
    }
}
```
Advantages:
- Prevents multiple container startups across many test classes
- Improves overall test execution time

## Spring TestConfiguration (inner class)
The `@TestConfiguration` supplies uniform beans for all tests:
1. Repository spies (simplify verification):
```java
@Bean
public static BeanPostProcessor mockRepoBeans() {
    return new BeanPostProcessor() {
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof Repository) return Mockito.spy(bean);
            return bean;
        }
    };
}
```
2. Kafka producer/consumer factories:
```java
@Bean @Primary
ProducerFactory<String, String> producerFactory4String() {
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer.get());
    return new DefaultKafkaProducerFactory<>(props);
}
@Bean @Primary
ConsumerFactory<String, String> consumerFactory() { ... }
```
3. MongoDB TCP proxy activation:
```java
@Bean @Primary
MongoProperties modifiedMongoProperties(...) { tcpProxy.start(); }
```

## Chaos & Resilience Test Ideas
- Kafka disconnect during active consumer processing
- MongoDB proxy stop -> reconnect & error propagation validation
- Combined outages to test circuit breaker / retry logic

## Best Practices / Notes
- Use `forceUniqueTopicNames = true` for parallel test execution
- Avoid adding `@SpringBootTest` (performance); prefer targeted slices (`@WebMvcTest`, repository tests, etc.)
- Never simulate outages by stopping the Kafka container – always use Toxiproxy (faster, no re-initialization)

## Example Kafka Network Outage Test
```java
@Test
void shouldRecoverAfterKafkaOutage() throws Exception {
    kafkaTemplate.send("topic3", "init");
    TestContainersBase.disableKafkaConnection();
    kafkaTemplate.send("topic3", "will-fail");
    TestContainersBase.enableKafkaConnection();
    kafkaTemplate.send("topic3", "after-recovery");
}
```

## Test Execution (Examples)
PowerShell commands (working directory = project root):
```powershell
mvn test "-Dtest=KafkaConsumerServiceJgivenIT#shouldRecoverAfterKafkaOutage"
mvn test "-Dtest=SomeControllerWebMvcTest#shouldReturn200"
```

## Troubleshooting
- "Kafka proxy not yet initialized": Did you call `configureDynamicPropertySource`?
- Mongo TCP proxy errors: Was dynamic property registration done before bean creation?
- Missing topics: Check logs of `ensureKafkaTopicsExist`

## Extension Ideas
- Additional toxics: latency (`latency`), packet loss (`limit_data`), bandwidth limiting (`bandwidth`)
- Automated chaos sequences for resilience validation
- Collect metrics (retry counters, latencies) via Micrometer

## Summary
`TestContainersBase` provides a lightweight, controllable, highly flexible test environment for distributed components. Using proxy-based approaches for Kafka and MongoDB enables realistic failure scenarios without restarting containers or increasing test time.

---
Status: automatically generated translation – keep this README in sync with future changes to `TestContainersBase`.
