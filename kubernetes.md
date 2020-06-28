[Back to Table of Contents](README.md)
# wichtige minikube commandos
`minikube start`

`minikube dashboard &`: startet im background das dashboard-server und öffnet zugehörige URL im Browser

`minikube service hello-minikube --url`: get URL of an already deployed and exposed service

`minikube stop`

`minikube delete` : delete the cluster

# wichtige kubectl commandos

`kubectl create deployment hello-minikube --image=k8s.gcr.io/echoserver:1.10`: create a deplyment

`kubectl proxy` : erzeugt eine RestEndpoint der als http proxy funktioniert, um ports innerhalb eines Pods abzufragen, der noch nicht nach außen veröffentlich ist.  Mit z.B.: `curl http://127.0.0.1:8001/version` sieht man ob der Proxy läuft und mit `http://localhost:8001/api/v1/namespaces/default/pods/kubernetes-bootcamp-fcc5bfb48-9vjdf/proxy/` kann man in einer 2. shell den im `deployment/kubernetes-bootcamp`  definierten port abragen, auch wenn der noch nicht vonaußen zugänglich ist. The proxy enables direct access to the API from these terminals. Hilft, wenn ein deployment noch keine service definiert hat, so kann man trotzdem mit der API/http-server  sprechen.

`kubectl expose deployment hello-minikube --type=NodePort --port=8080`: expose the deployment as a "service"
oder

`kubectl expose deployment hello-minikube --type=LoadBalancer --port=8080`: expose outside of cluster as a "service"

`kubectl get services`: get the services

`export NODE_PORT=$(kubectl get services/kubernetes-bootcamp -o go-template='{{(index .spec.ports 0).nodePort}}')` : get public visible Portnumber
`curl $(minikube ip):$NODE_PORT` : call the service 

`kubectl get pod`: show status

`kubectl get pod,svc -n kube-system`: get pods and services in the "kube-system" namespace

`kubectl get pod,svc -n default`: get pods and services in the "default" namespace

`kubectl get pods -o wide` : Zustand der einzelnen Pods , ausführliche Darstellung

`kubectl scale deployments/kubernetes-bootcamp --replicas=4` : anzahl der replicas erhöhen auf 4 

`kubectl get rs` : get ReplicaSet : vergleich Soll/Ist Zustand der Scalierung

`kubectl get deployments` : Ist-Zustand des Deployments (scalierung, updates , ready )

`kubectl set image deployments/kubernetes-bootcamp kubernetes-bootcamp=jocatalin/kubernetes-bootcamp:v2`: update deployment auf neue Version "v2" imagename "jocatalin/kubernetes-bootcamp:v2"

`kubectl describe rs `: details , wie z.B. image name für jedes ReplicationSet

`kubectl exec -ti $POD_NAME bash` : analog zu docker exec -it <dockerid> bash 

`kubectl exec -ti $POD_NAME curl localhost:8080` run a curl inside the pod to see if the application is still up on port 8080

`kubectl delete services hello-minikube`: delete the serice but not the deployment 

`kubectl delete deployment hello-minikube` delete deployment

## config maps
`kubectl create configmap <map-name> <data-source>`  : erstellt eine config map aus einem directory (alle files darin werden verwendet) mit <data-source> = `--from-file=configure-pod-container/configmap/` oder einzelnen Dateien mit `--from-file=configure-pod-container/configmap/game.properties`

# About container runtime
* docker (uses internally containerd)
* containerd without docker
* kata containers (VMs on OpenStack) using Dockerfiles (hypervisor: QEMU), better isolation for containers
* firecracker (Amazon) also better isolation, uses KVM (Linux Kernel-based Virtual Machine)Firecracker is a Virtual Machine Manager like QEMU.
* gVisor from Google with "Sentry" as a user-space OS Kernel , also Docker-compatible
* CRI-O: lightweight container runtime for Kubernetes Fascade for any OCI runtime compliant software 
* Trend: eliminate Docker from Kubernetes installations, but still using Dockerfile and docker compatible images
* Conclusion: **kata** might be more secure

# add-ons
`minikube addons list`: dashboard, storage-provisioner etc.

# Anmerkungen zum Tutorial
https://kubernetes.io/docs/tutorials/kubernetes-basics/explore/explore-interactive/
## Module 3: 
siehe https://github.com/kubernetes/website/issues/18079

Wenn das Kommando 
`curl http://localhost:8001/api/v1/namespaces/default/pods/$POD_NAME/proxy/`
local ausgeführt wird kommt die Fehlermeldung `Error trying to reach service: 'dial tcp 172.17.0.4:80: connect: connection refused`

daher statt dessen verwenden: 
`curl http://localhost:8001/api/v1/namespaces/default/pods/$POD_NAME:8080/proxy/`

anscheinend macht  `kubectl proxy` ein falsches default Portvorwarding

alternative Lösung 1 : `kubectl run kubernetes-bootcamp --image=gcr.io/google-samples/kubernetes-bootcamp:v1 --port 8080`
wobei `kubectl run` wohl mehr oder weniger synonym mit `kubectl create deployment`ist

alternative Lösung 2 : `kubectl edit deployment/kubernetes-bootcamp` after running `kubectl create deployment` and add this to the containers spec: 
```json
spec:
  containers:
    ports:
    - containerPort: 8080
      protocol: TCP
``` 
