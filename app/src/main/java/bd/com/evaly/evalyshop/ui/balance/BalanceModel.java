package bd.com.evaly.evalyshop.ui.balance;

public class BalanceModel {

    private double balance, holding_balance, gift_card_balance, cashback_balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getHolding_balance() {
        return holding_balance;
    }

    public void setHolding_balance(double holding_balance) {
        this.holding_balance = holding_balance;
    }

    public double getGift_card_balance() {
        return gift_card_balance;
    }

    public void setGift_card_balance(double gift_card_balance) {
        this.gift_card_balance = gift_card_balance;
    }

    public double getCashback_balance() {
        return cashback_balance;
    }

    public void setCashback_balance(double cashback_balance) {
        this.cashback_balance = cashback_balance;
    }
}
