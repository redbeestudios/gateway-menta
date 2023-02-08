
# bff-devices-login

[![Quality Gate Status](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=alert_status&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Reliability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=reliability_rating&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Security Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=security_rating&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Maintainability Rating](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=sqale_rating&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Coverage](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=coverage&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Duplicated Lines (%)](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=duplicated_lines_density&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Bugs](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=bugs&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Code Smells](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=code_smells&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Technical Debt](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=sqale_index&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Vulnerabilities](https://sonar.tools.menta.global/api/project_badges/measure?project=com.menta%3Abff-devices-login&metric=vulnerabilities&token=74788f80637d1005d16b6e98eed6c11ad1db91b7)](https://sonar.tools.menta.global/dashboard?id=com.menta%3Abff-devices-login)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white)](https://api.dev.apps.menta.global/bff-devices-login/swagger-ui/index.html)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](postman)

## Description: 
bff-devices-login provee tokens para la autenticación y autorización de usuarios, además de información adicional como
terminals, customers y merchants según corresponda.

## Index:
- [Stack](#stack)
- [Dependencies](#dependencies)
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

## Dependencies
- [api-login](https://git.menta.global/backend/auth/api-login)
- [api-users](https://git.menta.global/backend/entities/api-users)
- [api-customers](https://git.menta.global/backend/entities/api-customers)
- [api-merchants](https://git.menta.global/backend/entities/api-merchants)
- [api-terminals](https://git.menta.global/backend/entities/api-terminals)

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

En _Run/Debug Configurations_ -> _Build and run_ -> _VM Options_ agregar `-DAPP_PATH=/bff-devices-login`

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
El comportamiento se mantiene tanto para __login__ como para __refresh__ y __challenge__

```mermaid 
sequenceDiagram
    participant device
    participant bffDevicesLogin
    participant apiLogin
    participant apiCustomers
    participant apiMerchants
    participant apiTerminals

    link bffDevicesLogin: Repo @ https://git.menta.global/backend/devices-bffs/bff-devices-login
    link apiLogin: Repo @ https://git.menta.global/backend/auth/api-login
    link apiCustomers: Repo @ https://git.menta.global/backend/entities/api-customers
    link apiMerchants: Repo @ https://git.menta.global/backend/entities/api-merchants
    link apiTerminals: Repo @ https://git.menta.global/backend/entities/api-terminals

    autonumber
        
        device->>+bffDevicesLogin: POST: /login
        bffDevicesLogin->>+apiLogin: POST: /login
        apiLogin-->>-bffDevicesLogin: auth
        
        par Get Customer
        Note over bffDevicesLogin,apiCustomers: if user is customer | non required
            bffDevicesLogin->>+apiCustomers: GET /customers/{id}
            apiCustomers-->>-bffDevicesLogin: customer?

        and Get Merchant
        Note over bffDevicesLogin,apiMerchants: if user is merchant | non required
            bffDevicesLogin->>+apiMerchants: GET /merchants/{id}
            apiMerchants-->>-bffDevicesLogin: merchant?

        and Get Terminal
        Note over bffDevicesLogin,apiTerminals: if serialCode is informed | non required
            bffDevicesLogin->>+apiTerminals: GET /terminals?serialCode={serialCode}
            apiTerminals-->>-bffDevicesLogin: terminal?

        end

        bffDevicesLogin-->>-device: auth + customer? + merchant? + terminal?
```
