# Spring and Akka's hybrid microservices in real-world applications

## Run & Test

### Starting data services with the Docker

```
cd dockers
docker-compose up -d yj-consul-server yj-consul-agent yj-postgres
```

### Run the Samples

- Run OAuth Authorization Server
    - `cd scala-projects/`
    - Run: -> `sbt "project auth-server-app" run`
- Run User Server
    - `cd java-projects/`
    - Run: -> `mvn -pl user-app spring-boot:run`
        - *Perform spring-boot:run before must be perform `mvn -DskipTests compile`*
- Rune Resource Server
    - `cd java-projects/`
    - Run: -> `mvn -pl portal-app spring-boot:run`
        - *Perform spring-boot:run before must be perform `mvn -DskipTests compile`*

### Beginning test

#### Get `access_token`

```
curl -i -XPOST 'http://localhost:9000/oauth2/token' \
  -u 'ec-client:secret' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials&scope=message.read%20api.read'
```

Example of response:

```
HTTP/1.1 200 OK
Server: akka-http/10.2.0
Date: Mon, 21 Sep 2020 02:03:54 GMT
Content-Type: application/json
Content-Length: 667

{
  "access_token":"eyJraWQiOiJlYy1rZXkiLCJhbGciOiJFUzI1NiJ9.eyJzdWIiOiJlYy1jbGllbnQiLCJzY29wZSI6Im1lc3NhZ2UucmVhZCIsImlzcyI6Imh0dHBzOlwvXC9ha2thLXNlY3VyaXR5LmhlbGxvc2NhbGEuY29tIiwiZXhwIjoxNjA1Nzk5ODgxLCJpYXQiOjE2MDUxOTQ3ODEsImp0aSI6ImVjLWtleSJ9.ebtN29ey5lkp2wtH9NeABqpcDswLZHBWgVhof2qMvD-QgKeu5rJHArxnnh_4hRDlUCH9bo6H_UKu5BYtq-E-Kw",
  "scope":"message.read",
  "token_type":"Bearer",
  "expires_in":"605100"
}
```

> JWS uses the `ES256` algorithm by default, and you can change it by using parameter such as `algorithm=RS256`. 
> ```
> curl -i -XPOST 'http://localhost:9000/oauth2/token' \
>   -u 'messaging-client:secret' \
>   -H 'Content-Type: application/x-www-form-urlencoded' \
>   -d 'grant_type=client_credentials&scope=message.read%20api.read&algorithm=RS256'
> ```

#### Access to `/api/user/get/{id}`

```
curl -i 'http://localhost:8090/api/user/get/1' \
  -H 'Authorization: Bearer <access_token>'
```

*Please replace the `<access_token>` punctuation character with the correct `access_token`, which can be found in the previous example.* Example of response:

```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 37
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1 ; mode=block
Referrer-Policy: no-referrer

{
  "id": "1",
  "username": "Hello ==> ",
  "phone": "13845678901",
  "email": "",
  "status": 0
}
```

#### Access to `messages`

```
curl -i 'http://localhost:8090/messages' \
  -H 'Authorization: Bearer <access_token>'
```

*Please replace the `<access_token>` punctuation character with the correct `access_token`, which can be found in the previous example.* Example of response:

```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 37
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1 ; mode=block
Referrer-Policy: no-referrer

["Message 1","Message 2","Message 3"]
```

