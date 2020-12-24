package bd.com.evaly.evalyshop.ui.cart.model;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.views.smoothCheckBox.SmoothCheckBox;

public interface CheckedListener {
    void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked, CartEntity entity);
}
