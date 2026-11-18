import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper

import internal.GlobalVariable as GlobalVariable
CreateResponse = WS.sendRequest(findTestObject('reqres/user_create'))

WS.verifyResponseStatusCode(CreateResponse, 201)

WS.verifyElementPropertyValue(CreateResponse, 'id', '4')

CreatedBody = new JsonSlurper().parseText(CreateResponse.getResponseText())

if (CreatedBody.createdAt == null || CreatedBody.createdAt.toString().isEmpty()) {
	KeywordUtil.markFailedAndStop("createdAt is missing")
}

UpdateResponse = WS.sendRequest(findTestObject('reqres/user_update'))

WS.verifyResponseStatusCode(UpdateResponse, 200)

WS.verifyElementPropertyValue(UpdateResponse, 'job', 'senior tester')

UpdatedBody = new JsonSlurper().parseText(UpdateResponse.getResponseText())

if (UpdatedBody.updatedAt == null || UpdatedBody.updatedAt.toString().isEmpty()) {
	KeywordUtil.markFailedAndStop("updatedAt is missing")
}

DeleteResponse = WS.sendRequest(findTestObject('reqres/user_delete'))

WS.verifyResponseStatusCode(DeleteResponse, 204)
