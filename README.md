# mybatis-test

Spring Boot 3 + MyBatis Spring Boot Starter 3.x + Gradle sample project.

## Run

```bash
./gradlew bootRun
```

Swagger UI:

- http://localhost:8080/swagger-ui.html

Sample API:

- `GET /api/mybatis/dynamic-select?active=true&includeScore=true`

## What this sample shows

`DynamicQueryService` creates test query data as `List<Map<String, Object>>`, stores it in `DynamicQueryRequest`, and passes the DTO to MyBatis.

`src/main/resources/mappers/DynamicQueryMapper.xml` has a base `SELECT` that uses `<include>` to include a `<sql>` fragment. The fragment uses `<foreach>` to create variable select columns.
