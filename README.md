## SETUP
1. Import initial database structure and data from `microdb.sql`

2. Update database credentials at `config-server/src/main/resources/common-config/application.properties`
```properties
spring.datasource.url=jdbc:mysql://<ip>:<port>/<schema>?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=round&useSSL=true
spring.datasource.username=<username>
spring.datasource.password=<password>
```
3. Run the services in this order. Recommended to use compound to run these services together

    <ins>Essentials</ins>
   - config-server
   - service-registry
   - springboot-admin
   
    <ins>Main services</ins>
   - system-service
   - `<add your services here>`
   
    <ins>Frontend</ins>
   - auth-service
   - edge-service
   - user-frontend
   

   For development, run frontend through `npm start` in `FRONTEND/sakai-react` instead of the service.
   
   

## ACCESS
Default admin account
> Username: admin
> 
> Password: 000000"# UniversalStore" 
