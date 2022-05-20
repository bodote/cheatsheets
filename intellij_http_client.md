# Intellij Http-Client
## View "Endpoints" 
right tab "HTTP Client" -> "open in Editor"

## Examples for getting Keycloak tokens:
```
### Authorization by token, part 1. Retrieve and save token.
POST http://localhost:8180/auth/realms/bodo/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&client_id=login-app&username=user1&password=geheim

> {%
     client.test("Response content-type is json", function() {
        var type = response.contentType.mimeType;
        client.assert(type === "application/json", "Expected 'application/json' but received '" + type + "'");
        client.global.set("refresh_token", response.body['refresh_token']);
        client.log("refresh_token set")
        client.log(client.global.get("refresh_token"))
     });
  %}

### Authorization by token, part 2. Use refresh_token to get new access_token.
POST http://localhost:8180/auth/realms/bodo/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=refresh_token&client_id=login-app&username=user1&refresh_token={{refresh_token}}

> {%
   client.test("Response content-type is json", function() {
      client.log("refresh_token: got it!")
   });
 %}


```