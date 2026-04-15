it's a microservice backend practice project has following features:
1. provide external api (potentially, assume client will integrate in their products)
   - can be call be post api/v1/admin/with-api-key
   - autheticate valid api key with client id
     - generate api key for specific client id
     - generate JWT token
2. receive data from client via api call and send to kafka
   - with kafka produce and consume
   - store data in db by microservice call (could add SAGA transaction for todo, but not yet)
3. provice client dashboard for data query
   - can generate AI report by specific data query design and prompt
   - have role-based access control and JWT and Spring Security design for authetication and authorization
