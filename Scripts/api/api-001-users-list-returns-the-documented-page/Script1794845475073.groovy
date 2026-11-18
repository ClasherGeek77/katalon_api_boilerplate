import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import internal.GlobalVariable as GlobalVariable
Response = WS.sendRequest(findTestObject('reqres/users_list'))

WS.verifyResponseStatusCode(Response, 200)

WS.verifyElementPropertyValue(Response, 'page', 1)

WS.verifyElementPropertyValue(Response, 'per_page', 6)

WS.verifyElementPropertyValue(Response, 'total', 12)
