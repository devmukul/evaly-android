package bd.com.evaly.evalyshop.models.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;

public class Cart implements Serializable {

    @SerializedName("items")
    private List<CartEntity> items;

    public List<CartEntity> getItems() {
        return items;
    }

    public void setItems(List<CartEntity> items) {
        this.items = items;
    }
}
