# Hateoas-demo
Hateoas demo with Spring Boot. Also contains integration test practice with and without mocking services and repositories

Launch application with
```
$ mvnw clean spring-boot:run
```

then we can view all employees with
```
$ curl -v localhost:8080/employees | json_pp
```

and the response is
```
*   Trying 127.0.0.1:8080...
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /employees HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.80.0
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Type: application/prs.hal-forms+json
< Transfer-Encoding: chunked
< Date: Fri, 22 Sep 2023 11:35:23 GMT
<
{ [804 bytes data]
100   797    0   797    0     0   115k      0 --:--:-- --:--:-- --:--:--  129k
* Connection #0 to host localhost left intact
{
   "_embedded" : {
      "employeeList" : [
         {
            "_links" : {
               "employees" : {
                  "href" : "http://localhost:8080/employees"
               },
               "self" : {
                  "href" : "http://localhost:8080/employees/1"
               }
            },
            "firstName" : "Sam",
            "id" : 1,
            "lastName" : "Smith",
            "role" : "dev",
            "salary" : 3000
         },
         {
            "_links" : {
               "employees" : {
                  "href" : "http://localhost:8080/employees"
               },
               "self" : {
                  "href" : "http://localhost:8080/employees/2"
               }
            },
            "firstName" : "John",
            "id" : 2,
            "lastName" : "Smith",
            "role" : "dev",
            "salary" : 3500
         }
      ]
   },
   "_links" : {
      "self" : {
         "href" : "http://localhost:8080/employees"
      }
   },
   "_templates" : {
      "default" : {
         "method" : "POST",
         "properties" : [
            {
               "name" : "firstName",
               "regex" : "^(?=\\s*\\S).*$",
               "required" : true,
               "type" : "text"
            },
            {
               "name" : "lastName",
               "regex" : "^(?=\\s*\\S).*$",
               "required" : true,
               "type" : "text"
            },
            {
               "name" : "role",
               "regex" : "^(?=\\s*\\S).*$",
               "required" : true,
               "type" : "text"
            },
            {
               "name" : "salary",
               "type" : "number"
            }
         ]
      }
   }
}
```

We can follow the links to view employees
```
$ curl -v localhost:8080/employees/1 | json_pp
```
and the respond is
```
$ curl -v localhost:8080/employees/1 | json_pp
*   Trying 127.0.0.1:8080...
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /employees/1 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.80.0
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Type: application/prs.hal-forms+json
< Transfer-Encoding: chunked
< Date: Fri, 22 Sep 2023 11:38:56 GMT
<
{ [198 bytes data]
100   192    0   192    0     0  36697      0 --:--:-- --:--:-- --:--:-- 48000
* Connection #0 to host localhost left intact
{
   "_links" : {
      "employees" : {
         "href" : "http://localhost:8080/employees"
      },
      "self" : {
         "href" : "http://localhost:8080/employees/1"
      }
   },
   "firstName" : "Sam",
   "id" : 1,
   "lastName" : "Smith",
   "role" : "dev",
   "salary" : 3000
}

```

