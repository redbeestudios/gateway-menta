{
	"capture": {
		"card": {
			"holder": {
				"name": "Alguien",
                "identification": {
                	"number": "40719073",
                    "type": "DNI"
                }
			},
            "pan": "1234567890123456",
            "expiration_date": "0324",
            "cvv": "123",
            "track1": "123456789098765432",
            "track2": "653217890865234545",
            "card_sequence_number": "00",
			"bank":"SANTANDER", 
			"type":"DEBIT", 
			"brand":"VISA"
		}, 
		"input_mode":"CONTACTLESS"
	}, 
	amount:{
		"total":"100", 
		"currency":"ARS", 
		"breakdown":[
			{
				"description":"OPERATION", 
				"amount":"100"
			}
		]
	}, 
	"installments":"01", 
	"trace":"4", 
	"ticket":"4", 
	"batch":"1", 
	"terminal":{
		"id":"1239603a-5144-4132-bdda-ad1146c31eac", 
		"merchant_id":"67f4ce8e-11e8-4493-9909-8cb8ed56d278", 
		"customer_id":"8e0f09ea-2969-42f3-b992-3cb901a43c10", 
		"serial_code":"15005927", 
		"hardware_version":"10", 
		"software_version":"1", 
		"trade_mark":"UROVO", 
		"model":"9100", 
		"status":"ACTIVE", 
		"features":[
			"CONTACTLESS"
		]
	}, 
	"merchant":{
		"id":"67f4ce8e-11e8-4493-9909-8cb8ed56d278", 
		"customer_id":"2d789db3-e113-497e-84fe-85555af5f219", 
		"country":"ARG", 
		"legal_type":"NATURAL_PERSON", 
		"business_name":"Kiw3", 
		"fantasy_name":"Ment3", 
		"business_owner":{
			"name":"Menta", 
			"surname":"Kiwi", 
			"birth_date":"2000-12-01T10:15:30Z", 
			"id":{
				"type":"DNI", 
				"number":"99999999"
			}
		}, 
		"merchant_code":"abc1234", 
		"address":{
			"state":"XXX", 
			"city":"CORDOBA CAPIT", 
			"zip":"5000", 
			"street":"Miguel Calixto D", 
			"number":"312", 
		}, 
		"email":"hola@hola", 
		"phone":"12349876", 
		"activity":"Activity", 
		"category":"7372", 
		"tax":{
			"id":"taxId", 
			"type":"type"
		}, 
		"settlement_condition":{
			"transaction_fee":"transactionFee", 
			"settlement":"settlement", 
			"cbu_or_cvu":"1234567890"
		}
	}, 
	"datetime":"2022-08-03T17:26:40Z", 
	"customer":{
		"id":"8e0f09ea-2969-42f3-b992-3cb901a43c10", 
		"country":"ARG", 
		"legal_type":"LEGAL_ENTITY", 
		"business_name":"KIWI", 
		"fantasy_name":"MENTA", 
		"tax":{
			"type":"1", 
			"id":"2"
		}, 
		"activity":"Activity", 
		"email":"prueba@menta.com", 
		"phone":"1245632748", 
		"address":{
			"state":"4", 
			"city":"CORDOBA CAPITAL", 
			"zip":"X5019EAB", 
			"street":"Miguel Calixto D", 
			"number":"312", 
		}, 
		"representative":{
			"representative_id":{
				"type":"DNI", 
				"number":"32456789"
			}, 
			"birth_date":"2022-03-31T15:01:01.999Z", 
			"name":"Soy un nombre de represent", 
			"surname":"soy un apellido de represen"
		}, 
		"status":"ACTIVE"
	}
}
