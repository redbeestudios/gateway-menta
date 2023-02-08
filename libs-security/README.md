# libs-security

[![Quality Gate Status](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=alert_status&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Reliability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=reliability_rating&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Security Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=security_rating&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Maintainability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=sqale_rating&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Duplicated Lines (%)](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=duplicated_lines_density&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Coverage](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=coverage&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Bugs](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=bugs&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Code Smells](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=code_smells&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Technical Debt](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=sqale_index&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)
[![Vulnerabilities](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Alibs-security&metric=vulnerabilities&token=cbc0de6128ae2715daf22788e6bde1fc43531979)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Alibs-security)

**Description**: libs-security facilita el manejo de seguridad para nuestras apis


## Index:
- [Stack](#stack)
- [How to Implement](#how-to-implement)
  - [0. Elegir Target](#0-elegir-target)
  - [1. Agregar repositorio en build.gradle.kts](#1-agregar-repositorio-en-buildgradlekts)
  - [2. Agregar dependencia en build.gradle.kts](#2-agregar-dependencia-en-buildgradlekts)
  - [3. Configurar libs-security](#3-configurar-libs-security)
  - [3. Error handling](#4-error-handling)
- [Behavior](#behavior)
  - [Authentication](#authentication)
  - [Authorization](#authorization)
  - [Requester User](#requester-user)
  - [Ownership](#ownership)
- [How to Run](#how-to-run)
    - [Build](#build)
    - [Publish](#publish)
    - [Unit Tests](#unit-tests)
    - [Linter](#linter)
    - [Sonarqube](#sonarqube)
- [Contributing](#contributing)

### Stack 🛠️
- java 11
- kotlin 1.6
- spring-boot 2.6
- [kotest](https://kotest.io/)

### How to Implement

#### 0. Elegir target
Todo componente que implemente libs-security deberá utilizar la version __RELEASE__ de la misma. En el caso de quere probar un feature en desarrollo, se podrá utilizar __SNAPSHOT__.
Tomar en cuenta que __SNAPSHOT__ no es __inmutable__, por lo que podrá cambiar entre un deploy a otro, por esto mismo no se recomienda que quede en el __main__ de ningún proyecto. 



#### 1. Agregar repositorio en build.gradle.kts

```kotlin
repositories {
    mavenCentral()

    maven {
        url = uri("https://nexus.tools.menta.global/repository/maven-releases/")
        credentials {
            username = "{user}"
            password = "{password}"
        }
    }
}
```

#### 2. Agregar dependencia en build.gradle.kts

```kotlin
dependencies {
  // Menta Security
  implementation("com.menta:libs-security:x.x.x-RELEASE")
}
```

#### 3. Configurar libs-security

```yaml
libs:
  security:
    resource-server:
      issuers:
        - name: "merchant"
          uri: "https://cognito-idp.${AWS_REGION}.amazonaws.com/${COGNITO_AWS_MERCHANT_POOL}"
          authorities-claim-key: "cognito:groups"
        - name: "customer"
          uri: "https://cognito-idp.${AWS_REGION}.amazonaws.com/${COGNITO_AWS_CUSTOMER_POOL}"
          authorities-claim-key: "cognito:groups"
        - name: "support"
          uri: "https://cognito-idp.${AWS_REGION}.amazonaws.com/${COGNITO_AWS_SUPPORT_POOL}"
          authorities-claim-key: "cognito:groups"
    requester-user:
      provider:
        enabled: true
    ownership:
      enabled: true
```

#### 4. Error handling

Agregar la siguiente configuración 
```kotlin
    @Configuration
class SecurityConfiguration {
  @Bean
  fun securityErrorHandler(handler: ErrorHandler): SecurityErrorHandler<ApiErrorResponse> =
    SecurityErrorHandler { handler.handleSecurityException(it) }
}
```

Agregar al handler de errores:
```kotlin
   fun handleSecurityException(ex: Throwable): ResponseEntity<ApiErrorResponse> =
    doHandle { unauthorizedError(throwable = ex) }
```
Agregar manejo para _403: Access FORBIDDEN_
```kotlin
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: Throwable): ResponseEntity<ApiErrorResponse> {}
```

#### 5. (Opcional) en caso de usar validaciones de ownership

Agregar una implementación de EntityOwnershipValidationAnnotationProvider
```kotlin
    @Component
class EntityOwnershipValidationAnnotationProvider(
  private val requestMappingHandlerMapping: RequestMappingHandlerMapping
) : EntityOwnershipValidationAnnotationProvider {

  override fun provideFrom(request: HttpServletRequest): List<EntityOwnershipValidation> =
    getHandlerMethod(request)?.let {
      it.getMethodAnnotation(EntityOwnershipValidations::class.java)
        ?.validations
        ?.toList()
        ?: it.getMethodAnnotation(EntityOwnershipValidation::class.java)
          ?.let { listOf(it) }
    } ?: emptyList()

  private fun getHandlerMethod(request: HttpServletRequest): HandlerMethod? {
    if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
      ServletRequestPathUtils.parseAndCache(request)
    }
    return requestMappingHandlerMapping
      .getHandler(request)
      ?.handler
      ?.let { it as HandlerMethod }
  }
}
```


### Behavior

#### Authentication
Todo endpoint público requiere autenticación. Cualquier problema con la autenticación, será informado por __spring_security__ con un error http: _401: Unauthorized_
```bash
curl --location --request GET 'localhost:8080/foo/{id}' \
--header 'Authorization: Bearer eyJraWQiOiJHVThWK2pib2xIUlRkaWhLMHVaVVVZZVVVUm9VbExOdmo2U2swVDVyU0k4PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIxNTViYmM5Ny1iZDMyLTQxYjUtYTkyYi03ZGUwMTA5ZGEzYmMiLCJjb2duaXRvOmdyb3VwcyI6WyJBbm51bG1lbnQ6OlJlYWQiLCJSZWZ1bmQ6OlJlYWQiLCJBbm51bG1lbnQ6OlVwZGF0ZSIsIlJlZnVuZFJldmVyc2FsOjpEZWxldGUiLCJQYXltZW50OjpVcGRhdGUiLCJQYXltZW50OjpDcmVhdGUiLCJSZWZ1bmQ6OlVwZGF0ZSIsIlBheW1lbnRSZXZlcnNhbDo6RGVsZXRlIiwiUmVmdW5kOjpDcmVhdGUiLCJBbm51bG1lbnRSZXZlcnNhbDo6VXBkYXRlIiwiUmVmdW5kUmV2ZXJzYWw6OkNyZWF0ZSIsIkFubnVsbWVudDo6Q3JlYXRlIiwiUmVmdW5kOjpEZWxldGUiLCJSZWZ1bmRSZXZlcnNhbDo6UmVhZCIsIkFubnVsbWVudDo6RGVsZXRlIiwiQW5udWxtZW50UmV2ZXJzYWw6OkNyZWF0ZSIsIlBheW1lbnRSZXZlcnNhbDo6VXBkYXRlIiwiQW5udWxtZW50UmV2ZXJzYWw6OlJlYWQiLCJQYXltZW50UmV2ZXJzYWw6OlJlYWQiLCJSZWZ1bmRSZXZlcnNhbDo6VXBkYXRlIiwiUGF5bWVudFJldmVyc2FsOjpDcmVhdGUiLCJQYXltZW50OjpSZWFkIiwiQW5udWxtZW50UmV2ZXJzYWw6OkRlbGV0ZSIsIlBheW1lbnQ6OkRlbGV0ZSJdLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9UbGxGQzFiYWciLCJjb2duaXRvOnVzZXJuYW1lIjoiMTU1YmJjOTctYmQzMi00MWI1LWE5MmItN2RlMDEwOWRhM2JjIiwibWVyY2hhbnRfaWQiOiJlOWQ1MTQxYi02Y2YzLTRjMTMtODIyNi1iZTVlMTQ0NzRhZTYiLCJvcmlnaW5fanRpIjoiYzNkOWE4NDAtOWJjMS00YWVkLWI1ZWYtOGJiY2RiMThlYzM4IiwiYXVkIjoiNzc0MzYycm5iZWFhdXR1amRqcG5qcDV2YzgiLCJldmVudF9pZCI6ImNmZDI1ZWVhLTIzOWYtNGZhNi04NGQ2LTM4MjkyMjY3MTUwOCIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNjU4MjUzNjk2LCJjdXN0b21lcl9pZCI6ImM0NGViM2MyLTEwMWEtNGI0Yy04MzUwLWIxYjdmZGU5NDllNSIsImV4cCI6MTY1ODM0MDA5NiwiaWF0IjoxNjU4MjUzNjk2LCJqdGkiOiIzMDRjNzA5Ni1jYTdkLTQ1OWYtYjhkNC02MDk4NzUyYmMyYzQiLCJlbWFpbCI6InNlYmFzdGlhbi5tYXJ0aW5lekByZWRiLmVlIn0.x7sEMsa73zGn_Fbg70vhYUJUkmqtnUtomg00naNl8c6_tEBFMJEu_HOdOiEBlmMPo7xJZArVTaVUkLN7Hlxc_W9EcrNR2qWYDVEzovRlc49J1F3cr6rBnCZ1T4RX3AbrRKWteLeDhPh35ZBwYcONN2DgGoSaD6TVZRIdabX_WBUNnh56SOTgdBwuuNkrPnI6CmalLLUAvgDGFTkJsSQaRV1IzHbXqkR7N_FcFikzUef6ene-XCURy97yekVwUctb8rzv6nOdY19GRwMzc66TLozFoArkT2mXcVVRJ5FOgI0GqZdrOvMkTBWdSWAD6ybfdd1Ur3b75vmGo1oER09EoQ'
```

#### Authorization
Utilizando la anotación [PreAuthorize](https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html) de  spring-security, se puede exigir una autoridad para ejecutar una accion sobre un recurso. Si el usuario no cuenta con las autoridades necesarias, se informara con un _403 Forbidden_.

```kotlin
@PreAuthorize("hasAuthority('Foo::Read')")
@GetMapping("/foo/{id}")
fun get(@PathVariable id: UUID): Foo {}
```
Para más información sobre autoridades ver [api-users](https://git.menta.global/backend/entities/api-users)


#### Requester User
El requester-user es el usuario autenticado y autorizado para llevar a cabo el request.

##### Provider
En caso de que el [feature-flag](#3-configurar-libs-security) este activado, se registrará un [RequesterUserProvider](src/main/kotlin/com/menta/libs/security/requesterUser/provider/RequesterUserProvider.kt) el cual proveera el requester-user actual.
Por default, se registrará un [FromJwtRequesterUserProvider](src/main/kotlin/com/menta/libs/security/requesterUser/provider/FromJwtRequesterUserProvider.kt) el cual obtiene el usuario del [SecuirtyContext](https://docs.spring.io/spring-security/site/docs/3.1.x/apidocs/org/springframework/security/core/context/SecurityContext.html).

#### Ownership

En el caso de que el [feature-flag](#3-configurar-libs-security) para ownership y [requester user provider](#requester-user), se registraran los componentes necesarios para validar ownership de las identidades.

La validación se aplicará unicamente sobre los endpdoints anotados con [EntityOwnershipValidation](src/main/kotlin/com/menta/libs/security/ownership/annotation/EntityOwnershipValidation.kt) o, en el caso de definir mas de un owner, [EntityOwnershipValidations](src/main/kotlin/com/menta/libs/security/ownership/annotation/EntityOwnershipValidation.kt).

En un primer paso se validará que el tipo del usuario corresponda con alguno de los owners definidos. De cumplirse esto se aplicaran estrategias de acuerdo al tipo
- Merchant: se validará que el merchantId del user corresponda con el merchantId extraído del token de autenticación del request
- Merchant: se validará que el customerId del user corresponda con el customerId extraído del token de autenticación del request
- Support: no se realizaran validaciones extras

Los argumentos del request se extraeran de acuerdo al parametro __argumentName__ y __argumentSource__ definidos en la anotación
### How to Run

#### Build

```bash
$ ./gradlew build
```

#### Publish
El __CI__ realiza un __publish__ a __RELEASE__ al comitear en __main__ y a __SNAPSHOT__ sobre cualquier otro branch  
```bash
./gradlew publish -DPUBLISH_REPOSITORY=https://nexus.tools.menta.global/repository/maven-snapshots -DPUBLISH_USERNAME={user} -DPUBLISH_PASSWORD={password} -DPUBLISH_TARGET=SNAPSHOT
```
#### Unit Tests

```bash
$ ./gradlew test
```

#### Linter
- ##### Check
  ```bash
  $ ./gradlew ktlintCheck
  ```
- ##### Format
  ```bash
  $ ./gradlew ktlintFormat
  ```

#### Sonarqube

Para correr sonarqube localmente. [[mas info]](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/16777217/Sonarqube+local)
```bash
$ ./gradlew sonarqube \
-Dsonar.projectKey=<PROJECT-KEY> \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=<TOKEN-SONAR-PROJECT>
```

### Contributing
Leer [Material Técnico](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/3965199/Material+T+cnico), sobre todo la sección sobre [Arquitectura Hexagonal](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/4423684/Arquitectura+Hexagonal)
