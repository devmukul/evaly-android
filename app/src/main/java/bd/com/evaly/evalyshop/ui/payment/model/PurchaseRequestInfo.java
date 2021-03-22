package bd.com.evaly.evalyshop.ui.payment.model;

public final class PurchaseRequestInfo {
    private String authToken;
    private String amount;
    private String invoiceNo;
    private String gateway;

    public PurchaseRequestInfo(){

    }

    public PurchaseRequestInfo(String authToken, String amount, String invoiceNo, String gateway) {
        this.authToken = authToken;
        this.amount = amount;
        this.invoiceNo = invoiceNo;
        this.gateway = gateway;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


}
