
# api-users

[![Quality Gate Status](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=alert_status&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Reliability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=reliability_rating&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Security Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=security_rating&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Maintainability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=sqale_rating&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Duplicated Lines (%)](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=duplicated_lines_density&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Coverage](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=coverage&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Bugs](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=bugs&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Code Smells](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=code_smells&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Technical Debt](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=sqale_index&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Vulnerabilities](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-users&metric=vulnerabilities&token=2188feea25c5438e900b8459f6b4846a6cd4832f)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-users)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)](https://api-internal.dev.apps.menta.global/users/swagger-ui/index.html)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](postman)

**Description**: api-users es un servicio para la manipulacion de usuarios


## Index:
- [Stack](#stack)
- [How to Run](#how-to-run)
    - [Config](#config)
    - [Build](#build)
    - [Run](#run)
    - [Unit Tests](#unit-tests)
- [Contributing](#contributing)
- [Usage](#usage)

### Stack 🛠️
- java 11
- kotlin 1.6
- spring-boot 2.6
- [arrow-kt](https://arrow-kt.io/)
- [kotest](https://kotest.io/)
- [cognito](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-cognito.html)

### How to Run

#### Config

##### springdoc

Esto se usa para definir el path en el que se expondra el swagger del servicio

```yaml
springdoc:
  api-docs:
    path: ${APP_PATH}/swagger-ui/api-docs
  swagger-ui:
    path: ${APP_PATH}/index.html
```

En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `-DAPP_PATH=/api-users`

##### cognito

Para utilizar cognito se requiere:
- **credenciales**: secret y accessKey
- **user-pool** de cada tipo de usuario

En el caso de levantar cognito en un ambiente, no hacen falta las credenciales.

```yaml
cognito:
  provider:
    credentials:
      secret: "${COGNITO_AWS_SECRET}"
      accessKey: "${COGNITO_AWS_ACCESS_KEY}"
    region: "${AWS_REGION}"
    user-pools:
      MERCHANT:
        code: "${COGNITO_AWS_MERCHANT_POOL}"
        client-id: "${COGNITO_AWS_MERCHANT_CLIENT_ID}"
      CUSTOMER:
        code: "${COGNITO_AWS_CUSTOMER_POOL}"
        client-id: "${COGNITO_AWS_CUSTOMER_CLIENT_ID}"
      SUPPORT:
        code: "${COGNITO_AWS_SUPPORT_POOL}"
        client-id: "${COGNITO_AWS_CUSTOMER_CLIENT_ID}"
````
En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `...`

##### autenticación
Para utilizar cognito se requiere:
- **cognito-aws-zone** de cada tipo de usuario
- **user-pool** de cada tipo de usuario
- **dependencia** de autenticación de Menta en el build.gradle.kts
```bash
implementation("com.menta:libs-security:0.0.2-SNAPSHOT")
```

```yaml
libs:
  security:
    resource-server:
      issuers:
        - name: "merchant"
          uri: "https://cognito-idp.${AWS_REGION}.amazonaws.com/${COGNITO_AWS_MERCHANT_POOL}"
          authorities-claim-key: "cognito:groups"
        - name: "customers"
          uri: "https://cognito-idp.${AWS_REGION}.amazonaws.com/${COGNITO_AWS_CUSTOMER_POOL}"
          authorities-claim-key: "cognito:groups"
````
En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `...`


#### Build

Ejecuta éste comando para instalar las dependencias y buildear el proyecto:

```bash
$ ./gradlew build
```

#### Run

Para correr el proyecto:

```bash
$ ./gradlew bootRun
```

El entorno de desarrollo corre sobre <http://localhost:8080>. Ejecuta una llamada GET de prueba en <http://localhost:8080/actuator>

#### Unit Tests

Para correr los tests unitarios
```bash
$ ./gradlew test
```

### Contributing
Leer [Material Técnico](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/3965199/Material+T+cnico), sobre todo la seccion sobre [Arquitectura Hexagonal](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/4423684/Arquitectura+Hexagonal)


### Usage
![](readmeResources/sequence-diagram.png)
