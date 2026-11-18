import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper

import internal.GlobalVariable as GlobalVariable
Response = WS.sendRequest(findTestObject('reqres/login_ok'))

WS.verifyResponseStatusCode(Response, 200)

Body = new JsonSlurper().parseText(Response.getResponseText())

if (Body.token == null || Body.token.toString().isEmpty()) {
	KeywordUtil.markFailedAndStop("login token is missing")
}
