package bd.com.evaly.evalyshop.models.refundSettlement;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RefundSettlementResponse implements Serializable {

    @SerializedName(value = "bank", alternate = "bank_settlement_account")
    private Bank bank;

    @SerializedName(value = "bkash", alternate = "bkash_settlement_account")
    private String bkash;

    @SerializedName(value = "nagad", alternate = "nagad_settlement_account")
    private String nagad;

    @SerializedName("username")
    private String username;

    public Bank getBank() {
        return bank;
    }

    public String getBkash() {
        return bkash;
    }

    public String getNagad() {
        return nagad;
    }

    public String getUsername() {
        return username;
    }

}