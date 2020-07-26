package bd.com.evaly.evalyshop.rest.apiHelper.token;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.BaseApiHelper;

public class ChatApiHelper extends BaseApiHelper {
    public static void getMessageCount(ResponseListenerAuth<CommonDataResponse<String>, String> listener) {
        getiApiClient().getUnreadedMessageCount(CredentialManager.getToken(), CredentialManager.getUserName()).enqueue(getResponseCallBackDefault(listener));
    }
}
