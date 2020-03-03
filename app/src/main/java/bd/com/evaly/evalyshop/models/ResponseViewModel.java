package bd.com.evaly.evalyshop.models;

public class ResponseViewModel {

    private ResponseViewModel model;
    private String onSuccess;
    private String onFailed;
    private String onAuthError;


    public ResponseViewModel() {
    }

    public ResponseViewModel build() {
        if (model == null)
            this.model = new ResponseViewModel();
        return model;
    }

    public String getOnSuccess() {
        return onSuccess;
    }

    public ResponseViewModel setOnSuccess(String onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public String getOnFailed() {
        return onFailed;
    }

    public ResponseViewModel setOnFailed(String onFailed) {
        this.onFailed = onFailed;
        return this;
    }

    public String getOnAuthError() {
        return onAuthError;
    }

    public ResponseViewModel setOnAuthError(String onAuthError) {
        this.onAuthError = onAuthError;
        return this;
    }
}
