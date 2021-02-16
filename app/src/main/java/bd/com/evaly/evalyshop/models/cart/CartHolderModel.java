package bd.com.evaly.evalyshop.models.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartHolderModel implements Serializable {

    @SerializedName("context")
    private String context;

    @SerializedName("cart")
    private Cart cart;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
