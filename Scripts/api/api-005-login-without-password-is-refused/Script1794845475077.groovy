import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import internal.GlobalVariable as GlobalVariable
Response = WS.sendRequest(findTestObject('reqres/login_refused'))

WS.verifyResponseStatusCode(Response, 400)

WS.verifyElementPropertyValue(Response, 'error', 'Missing password')
