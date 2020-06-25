package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.issueNew.category.IssueCategoryModel;
import bd.com.evaly.evalyshop.models.issueNew.comment.IssueCommentBody;
import bd.com.evaly.evalyshop.models.issueNew.comment.IssueTicketCommentModel;
import bd.com.evaly.evalyshop.models.issueNew.create.IssueCreateBody;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;

public class IssueApiHelper extends BaseApiHelper {


    public static void getCategories(ResponseListenerAuth<CommonDataResponse<List<IssueCategoryModel>>, String> listener) {

        getiApiClient().getIssueTicketCategory(CredentialManager.getToken(), 50).enqueue(getResponseCallBackDefault(listener));

    }

    public static void getIssueList(String invoice, ResponseListenerAuth<CommonDataResponse<List<IssueListModel>>, String> listener) {

        getiApiClient().getIssueTicketList(CredentialManager.getToken(), invoice).enqueue(getResponseCallBackDefault(listener));

    }

    public static void createIssue(IssueCreateBody body, ResponseListenerAuth<CommonDataResponse<IssueListModel>, String> listener) {

        getiApiClient().createIssueTicket(CredentialManager.getToken(), body).enqueue(getResponseCallBackDefault(listener));

    }

    public static void getIssueCommentList(int ticketId, ResponseListenerAuth<CommonDataResponse<List<IssueTicketCommentModel>>, String> listener) {

        getiApiClient().getIssueTicketComment(CredentialManager.getToken(), ticketId).enqueue(getResponseCallBackDefault(listener));

    }

    public static void createIssueComment(int ticketId, String comment, ResponseListenerAuth<CommonDataResponse<IssueTicketCommentModel>, String> listener) {

        IssueCommentBody body = new IssueCommentBody();
        body.setComment(comment);
        body.setTicket(ticketId);

        getiApiClient().createIssueTicketComment(CredentialManager.getToken(), body, "undefined").enqueue(getResponseCallBackDefault(listener));

    }


}
