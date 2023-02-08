# api-customers

[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)](https://api-internal.dev.apps.menta.global/customers/swagger-ui/index.html)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](postman)

**Description**: api que brinda información general de los `Customers`


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
- [mongodb](https://mongodb.com/)
- [apache-kafka](https://kafka.apache.org/)

### How to Run

#### Prerequisites

[_docker-local-environment_](https://git.menta.global/backend/utils/docker-local-environment)
#### Config

- ##### springdoc

  Esto se usa para definir el path en el que se expondrá el swagger del servicio

  ```yaml
  springdoc:
    api-docs:
      path: ${APP_PATH}/swagger-ui/api-docs
    swagger-ui:
      path: ${APP_PATH}/index.html
  ```

  En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `-DAPP_PATH=/customers`

- ##### datasource
  Esto se usa para definir la conexión a la base de datos
  ```yaml
  mongodb:
      uri: ${MONGO_URL}
  ```

  En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar

  `-DMONGO_URL=mongodb://customers:customers@localhost:27017/db-customers`

- ##### elastic apm
  Esto se usa para métricas
  ```yaml
  elastic:
    apm:
      enabled: true
      server-url: ${ELASTIC_CLOUD_URL}
      service-name: ${ELASTIC_CLOUD_TAG_APP}
      secret-token: ${ELASTIC_CLOUD_TOKEN}
      environment: ${ELASTIC_CLOUD_TAG_ENV}
      application-packages: com.menta.api.customers
      log-level: ERROR
  ```

  En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar

#### Build

Ejecutar este comando para instalar las dependencias y _buildear_ el proyecto:

```bash
$ ./gradlew build
```

#### Run

Para correr el proyecto:

```bash
$ ./gradlew bootRun
```

El entorno de desarrollo corre sobre <http://localhost:8080>. Ejecutar una llamada GET de prueba en <http://localhost:8080/actuator>

#### Unit Tests

Para correr los tests unitarios
```bash
$ ./gradlew test
```

#### Linter
- ##### Check
  Para comprobar
  ```bash
  $ ./gradlew ktlintCheck
  ```
- ##### Format
  Para formatear el código
  ```bash
  $ ./gradlew ktlintFormat
  ```

#### Sonarqube

Para correr sonarqube localmente. Para más información acerca de la configuración dirigirse al siguiente [link](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/16777217/Sonarqube+local)
```bash
$ ./gradlew sonarqube \
-Dsonar.projectKey=<PROJECT-KEY> \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=<TOKEN-SONAR-PROJECT>
```

### Contributing
Leer [Material Técnico](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/3965199/Material+T+cnico), sobre todo la seccion sobre [Arquitectura Hexagonal](https://mentaglobal.atlassian.net/wiki/spaces/PROD/pages/4423684/Arquitectura+Hexagonal)
