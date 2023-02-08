# api-feenicia-adapter

## Description

Microservice for the connection with the acquirer Feenicia.

## Tech Stack

- Kotlin
- JDK 11
- Gradle
- Spring Boot


## Build

```
 ./gradlew build
```

## Run

```
 ./gradlew bootRun
```

## Endpoints

### Payment EMV Request

```
curl --request POST \
  --url https://api-internal.dev.apps.menta.global/feenicia/payments \
  --header 'Content-Type: application/json' \
  --data '{
    "capture": {
        "card": {
            "holder": {
                "name": "TEST EMV",
                "identification": {
                    "number": "34415200",
                    "type": "DNI"
                }
            },
            "pan": "4123220000000222",
            "expiration_date": "2212",
            "cvv": "396",
            "bank": "SANTANDER",
            "type": "CREDIT",
            "brand": "VISA",
            "track2": "5439240350653004=25022011185370500000",
            "emv": {
                "icc_data": "9F26084681D8BB64AEC1049F2701809F10120310A74005020000000000000000000000FF9F3704CF09B5589F360200A4950500000080009A032109089B02E8009C01009F02060000000000015F2A020484820239009F1A0204849F03060000000000009F3303E0F8C89F34034403029F3501229F1E0834343535303834378407A0000000041010981477335E2F91A291EF0F7994A40FD5CDEE376F5A2E4F07A000000004101050104465626974204D6173746572636172645A0852567860972511979F090200029F4104000000945F3401015F201A42454345525241204D4952414E44412F4A4F454C2041202020209F0607A00000000410109F0702FFC05F2403250831"
            }
        },
        "input_mode": "CHIP"
    },
    "amount": {
        "total": "400",
        "currency": "ARS",
        "breakdown": [
            {
                "description": "OPERATION",
                "amount": "400"
            }
        ]
    },
    "datetime": "2022-01-02T17:14:08.774Z",
    "trace": "1",
    "ticket": "1",
    "terminal": {
        "id": "1",
        "serial_code": "FGT345",
        "software_version": "10",
        "features": [
            "CONTACTLESS"
        ]
    },
    "installments": null,
    "merchant": {
        "id": "0000000000021077"
    },
    "batch": "1",
    "host_message": ""
}'
```


### Reimbursement Request
```
curl --request POST \
  --url https://api-internal.dev.apps.menta.global/feenicia/refunds \
  --header 'Content-Type: application/json' \
  --data '{
    "retrieval_reference_number": "8767742",
    "capture": {
        "card": {
            "holder": {
                "name": "TEST EMV",
                "identification": {
                    "number": "34415200",
                    "type": "DNI"
                }
            },
            "pan": "5450290010006048",
            "expiration_date": "2512",
            "cvv": "104",
            "bank": "SANTANDER",
            "type": "CREDIT",
            "brand": "MASTERCARD"
        },
        "input_mode": "MANUAL"
    },
    "amount": {
        "total": "25400",
        "currency": "ARS",
        "breakdown": [
            {
                "description": "OPERATION",
                "amount": "25400"
            }
        ]
    },
    "datetime": "2022-01-02T16:59:09Z",
    "trace": "1",
    "ticket": "2",
    "terminal": {
        "id": "1",
        "serial_code": "FGT345",
        "software_version": "10",
        "features": [
            "CONTACTLESS"
        ]
    },
    "installments": "03",
    "merchant": {
        "id": "0000000000021077"
    },
    "batch": "3",
    "host_message": ""
}'
```
### Reversal Request
```
curl --location --request POST 'localhost:8080/private/reversals' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data-raw '
{
    "capture": {
        "card": {
            "holder": {
                "name": "Yanina Torres",
                "identification": {
                    "number": "34415200",
                    "type": "DNI"
                }
            },
            "pan": "4123220000000222",
            "expiration_date": "2212",
            "cvv": "396",
            "bank": "SANTANDER",
            "type": "CREDIT",
            "brand": "VISA",
            "track2": "5439240350653004=25022011185370500000",
            "emv": {
                "icc_data": "9F26084681D8BB64AEC1049F2701809F10120310A74005020000000000000000000000FF9F3704CF09B5589F360200A4950500000080009A032109089B02E8009C01009F02060000000000015F2A020484820239009F1A0204849F03060000000000009F3303E0F8C89F34034403029F3501229F1E0834343535303834378407A0000000041010981477335E2F91A291EF0F7994A40FD5CDEE376F5A2E4F07A000000004101050104465626974204D6173746572636172645A0852567860972511979F090200029F4104000000945F3401015F201A42454345525241204D4952414E44412F4A4F454C2041202020209F0607A00000000410109F0702FFC05F2403250831"
            }
        },
        "input_mode": "CHIP"
    },
    "amount": {
        "total": "400",
        "currency": "ARS",
        "breakdown": [
            {
                "description": "OPERATION",
                "amount": "400"
            }
        ]
    },
    "datetime": "2022-01-02T17:14:08.774Z",
    "trace": "1",
    "ticket": "1",
    "terminal": {
        "id": "1",
        "serial_code": "FGT345",
        "software_version": "10",
        "features": [
            "CONTACTLESS"
        ]
    },
    "installments": "03",
    "merchant": {
        "id": "0000000000021077"
    },
    "batch": "1",
    "host_message": "",
    "order_id": "7872934"
}'
```