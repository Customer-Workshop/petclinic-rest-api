# Upgrade Notes: Java 8 / Spring Boot 2.6.2 -> Java 21 / Spring Boot 3.5

## Final versions

| Component            | Before                          | After                                   |
|----------------------|---------------------------------|-----------------------------------------|
| Java (bytecode/JDK)  | 1.8                             | 21 (built with Temurin 21.0.7)          |
| Spring Boot          | 2.6.2                           | 3.5.16                                  |
| Spring Framework     | 5.3.x                           | 6.2.x (Boot-managed)                    |
| Spring Security      | 5.6.x                           | 6.5.11 (Boot-managed)                   |
| Hibernate ORM        | 5.6.x                           | 6.6.53.Final (Boot-managed)             |
| springdoc-openapi    | 1.6.11 (`springdoc-openapi-ui`) | 2.8.17 (`springdoc-openapi-starter-webmvc-ui`) |
| Maven (wrapper)      | 3.6.3                           | 3.9.9                                   |

## Dependency / build decisions (`pom.xml`)

| Change | Why |
|--------|-----|
| Parent `spring-boot-starter-parent` 2.6.2 -> 3.5.16 | Target release line. |
| Added `<java.version>21</java.version>`; removed explicit `maven-compiler-plugin.version` (3.8.1) | Boot 3 parent drives source/target from `java.version` and manages a compiler plugin that understands release 21. |
| `spring-boot-maven-plugin` build-info: `${maven.compiler.source/target}` -> `${java.version}` | Old properties are no longer defined by the parent; build-info failed with "Additional property 'java.source' is illegal as its value is null". |
| `mysql:mysql-connector-java` -> `com.mysql:mysql-connector-j` (Boot-managed 9.7.0) | Old coordinates are unmaintained and not managed by Boot 3. `application-mysql.properties` driver class changed to `com.mysql.cj.jdbc.Driver`. |
| `org.springdoc:springdoc-openapi-ui` 1.6.11 -> `springdoc-openapi-starter-webmvc-ui` 2.8.17 | springdoc 1.x is Boot 2 only; 2.x is required for Spring 6 / Jakarta. |
| `javax.xml.bind:jaxb-api` 2.3.0 -> `jakarta.xml.bind:jakarta.xml.bind-api` (Boot-managed) | Jakarta namespace; generated OpenAPI model code imports `jakarta.xml.bind`. |
| `org.openapitools:jackson-databind-nullable` 0.2.1 -> 0.2.11 | Jackson 2.19 / Jakarta compatibility. |
| `mapstruct` 1.4.1.Final -> 1.6.3 | Java 21 annotation-processor support. |
| `openapi-generator-maven-plugin` 6.0.1 -> 7.24.0 with `<useSpringBoot3>true</useSpringBoot3>` | Generator 6.x emits `javax.*` imports and Spring 5 APIs; `useSpringBoot3` makes generated interfaces use `jakarta.*`. |
| `jacoco-maven-plugin` 0.8.7 -> 0.8.13 | 0.8.7 cannot instrument Java 21 class files. |
| `build-helper-maven-plugin` 3.2.0 -> 3.6.1, `jib-maven-plugin` 1.3.0 -> 3.4.6 | Maven 3.9 / JDK 21 compatibility. |
| `.mvn/wrapper/maven-wrapper.properties`: Maven 3.6.3 -> 3.9.9, URLs switched to `https://maven-central.storage-download.googleapis.com/maven2/` | Maven Central returned HTTP 429 for the wrapper download. `~/.m2/settings.xml` (outside the repo) mirrors `central` to the same GCS URL. |

Nothing was removed outright; every dependency was either kept or replaced by its Jakarta/Boot 3 equivalent listed above.

## Breaking code / framework rewrites

| File | Change |
|------|--------|
| `src/main/java/.../model/*.java` (BaseEntity, NamedEntity, Person, Owner, Pet, PetType, Role, Specialty, User, Vet, Visit) | `javax.persistence` / `javax.validation` -> `jakarta.*`. |
| `src/main/java/.../repository/jpa/*.java`, `repository/springdatajpa/*.java`, `repository/jdbc/JdbcOwnerRepositoryImpl.java` | `javax.persistence`, `javax.transaction` -> `jakarta.*`. |
| `src/main/java/.../rest/controller/*RestController.java` | `javax.validation`, `javax.servlet`, `javax.transaction` -> `jakarta.*`. |
| `security/BasicAuthenticationConfig.java` | Replaced `WebSecurityConfigurerAdapter` (+`AuthenticationManagerBuilder` field injection) with a `SecurityFilterChain` bean using the lambda DSL (`authorizeHttpRequests`, `httpBasic(Customizer.withDefaults())`, `csrf(AbstractHttpConfigurer::disable)`) and a `JdbcUserDetailsManager` `UserDetailsService` bean taking `DataSource` as a method parameter; `@EnableGlobalMethodSecurity` -> `@EnableMethodSecurity`. |
| `security/DisableSecurityConfig.java` | Replaced `WebSecurityConfigurerAdapter` with a permit-all `SecurityFilterChain` bean (lambda DSL). |
| `repository/jpa/JpaPetRepositoryImpl.java`, `repository/springdatajpa/SpringDataPetRepositoryImpl.java` | Hibernate 6 rejects column names in HQL (`WHERE pet_id=...`). Rewrote to entity paths with bind parameters: `visit.pet.id = :petId`, `pet.id = :petId`. |
| `repository/jpa/JpaPetTypeRepositoryImpl.java`, `repository/springdatajpa/SpringDataPetTypeRepositoryImpl.java` | Same HQL fix (`pet.type.id = :petTypeId`). Also reordered `delete(PetType)`: dependent visits/pets are bulk-deleted first and the `PetType` is removed last via `em.remove`. Hibernate 6 auto-flushed the early `em.remove(petType)` before the pet query and raised `TransientObjectException` (pets still referenced the removed type). |
| `src/main/resources/openapi.yml` | `ValidationMessage.message.example` used escaped `\"string\"`; OpenAPI Generator 7.x copied it into a Java annotation as-is, producing uncompilable code (`illegal character: '\'`). Changed the example to `['string']`. |
| `src/main/resources/application-mysql.properties` | `com.mysql.jdbc.Driver` -> `com.mysql.cj.jdbc.Driver`. |

## Test changes (strictly required by the migration)

No test was removed, disabled, or had its assertions changed. Two kinds of edits only:

1. `src/test/java/.../model/ValidatorTests.java`: `javax.validation` imports -> `jakarta.validation`.
2. Spring Framework 6 no longer matches a trailing slash to a mapping without one (`TrailingSlashMatch` default removed); requests such as `GET /api/owners/` now return 400/404 instead of hitting the handler. Request URLs in the following MockMvc tests were changed from `/api/xxx/` to `/api/xxx` (30 lines, URL strings only, no other edits):
   `OwnerRestControllerTests`, `PetRestControllerTests`, `PetTypeRestControllerTests`, `SpecialtyRestControllerTests`, `UserRestControllerTests`, `VetRestControllerTests`, `VisitRestControllerTests`.

## Test results

Before (branch `eval/boot2-java8-baseline`, `./mvnw -q clean verify`, run on JDK 17 because no JDK 8 was installed on the box):

```
Tests run: 172, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

After (branch `eval/devin-java21-upgrade`, `JAVA_HOME=<JDK 21> ./mvnw -B -q clean verify`, exit code 0):

```
Tests run: 172, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Things that could not be done as specified

- `/usr/lib/jvm/java-21-openjdk-amd64` does not exist on the build machine; the gate was run with Temurin 21.0.7 from SDKMAN (`$HOME/.sdkman/candidates/java/21.0.7-tem`) as `JAVA_HOME`. No code depends on the JDK path.
- The baseline "before" run used JDK 17 (no JDK 8 available); results were identical to the documented baseline (172 green).
- Maven Central rate-limited (HTTP 429) the wrapper download; the GCS mirror was used for both the wrapper distribution and artifact resolution.
