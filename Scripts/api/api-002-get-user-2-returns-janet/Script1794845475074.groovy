import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import internal.GlobalVariable as GlobalVariable
Response = WS.sendRequest(findTestObject('reqres/user_get'))

WS.verifyResponseStatusCode(Response, 200)

WS.verifyElementPropertyValue(Response, 'data.email', 'janet.weaver@reqres.in')

WS.verifyElementPropertyValue(Response, 'data.first_name', 'Janet')
