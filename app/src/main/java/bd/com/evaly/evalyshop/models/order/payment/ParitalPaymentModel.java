package bd.com.evaly.evalyshop.models.order.payment;

import com.google.gson.annotations.SerializedName;

public class ParitalPaymentModel {

    @SerializedName("invoice_no")
    private String invoice_no;

    @SerializedName("amount")
    private double amount;

    public ParitalPaymentModel(){

    }

    public ParitalPaymentModel(String invoice_no, int amount) {
        this.invoice_no = invoice_no;
        this.amount = amount;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
