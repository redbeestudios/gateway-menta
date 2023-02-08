
# api-seeds

[![Quality Gate Status](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=alert_status&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Reliability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=reliability_rating&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Security Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=security_rating&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Maintainability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=sqale_rating&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Coverage](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=coverage&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Duplicated Lines (%)](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=duplicated_lines_density&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Bugs](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=bugs&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Code Smells](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=code_smells&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Technical Debt](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=sqale_index&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Vulnerabilities](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Aapi-seeds&metric=vulnerabilities&token=fe4414cc6541f50c3ddef42bb26dd4b7bb7bc7ef)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Aapi-seeds)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)](https://api.dev.apps.menta.global/api-seeds/swagger-ui/index.html)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](postman)

**Description**: Semilla con la configuración básica de los microservicios en Kotlin


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

En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `-DAPP_PATH=/api-seeds`

##### autenticación
En caso de requerir el uso de autenticación se deberá agregar como variable de entorno las siguientes variables

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
