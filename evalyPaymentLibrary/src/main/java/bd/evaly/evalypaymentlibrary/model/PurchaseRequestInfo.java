package bd.evaly.evalypaymentlibrary.model;

public final class PurchaseRequestInfo {
    private String authToken;
    private String amount;
    private String invoiceNo;

    public PurchaseRequestInfo(){

    }

    public PurchaseRequestInfo(String authToken, String amount, String invoiceNo) {
        this.authToken = authToken;
        this.amount = amount;
        this.invoiceNo = invoiceNo;
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
