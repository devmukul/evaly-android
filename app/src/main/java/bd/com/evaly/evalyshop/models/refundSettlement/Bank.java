package bd.com.evaly.evalyshop.models.refundSettlement;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bank implements Serializable {

    @SerializedName("account_number")
    private String accountNumber;

    @SerializedName("branch_name")
    private String branchName;

    @SerializedName("account_name")
    private String accountName;

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("routing_number")
    private String routingNumber;

    public String getAccountNumber() {
        return nonNull(accountNumber);
    }

    public String getBranchName() {
        return nonNull(branchName);
    }

    public String getAccountName() {
        return nonNull(accountName);
    }

    public String getBankName() {
        return nonNull(bankName);
    }

    public String getRoutingNumber() {
        return nonNull(routingNumber);
    }

    public String nonNull(String value) {
        if (value == null)
            return "";
        else
            return value;
    }
}