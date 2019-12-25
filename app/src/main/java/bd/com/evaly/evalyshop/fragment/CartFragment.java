package bd.com.evaly.evalyshop.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.ui.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.adapter.CartAdapter;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CartItem;
import bd.com.evaly.evalyshop.models.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {


    private DbHelperCart db;
    private ArrayList<CartItem> itemList;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private LinearLayoutManager manager;
    private Context context;
    private CheckBox selectAll;
    private Button checkout, btnBottomSheet;
    private EditText customAddress, contact_number;
    private Switch addressSwitch;
    private Spinner addressSpinner;
    private ArrayList<String> spinnerArray;
    private ArrayList<Integer> spinnerArrayID;
    private UserDetails userDetails;
    private ViewDialog dialog;
    private boolean cartItem=false;
    private ViewDialog alert;
    private CompoundButton.OnCheckedChangeListener selectAllListener;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private int spinnerPosition, paymentMethod = 2;
    private double totalPriceDouble = 0;
    private View view;

    private Toolbar mToolbar;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }


    

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        context = getContext();
        dialog = new ViewDialog(getActivity());
        db = new DbHelperCart(context);
        userDetails = new UserDetails(context);

        itemList = new ArrayList<>();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;


        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        mToolbar.inflateMenu(R.menu.delete_btn);
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case android.R.id.home:
                    return true;
                case R.id.action_delete:
                    if(itemList.size() == 0){
                        Toast.makeText(context, "No item is available in cart to delete", Toast.LENGTH_SHORT).show();
                    }else{
                        new AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to delete the selected products from the cart?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                    ArrayList<CartItem> listAdapter = adapter.getItemList();
                                    for (int i = 0; i < listAdapter.size(); i++){
                                        if(listAdapter.get(i).isSelected()){
                                            db.deleteData(listAdapter.get(i).getId());
                                        }
                                    }
                                    getCartList();
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                    return true;
            }

            return false;
        });



        recyclerView = view.findViewById(R.id.recycle);

        checkout = view.findViewById(R.id.button);
        selectAll = view.findViewById(R.id.checkBox);
        alert = new ViewDialog(getActivity());

        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new CartAdapter(itemList,context, db);

        adapter.setListener(new CartAdapter.CartListener() {
            @Override
            public void updateCartFromRecycler() {
                CartFragment.this.updateCartFromRecycler();
            }

            @Override
            public void uncheckSelectAllBtn(boolean isChecked) {
                CartFragment.this.uncheckSelectAllBtn(isChecked);
            }
        });

        recyclerView.setAdapter(adapter);

        // bottom sheet

        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_checkout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        btnBottomSheet = bottomSheetView.findViewById(R.id.bs_button);

        checkout.setOnClickListener(v -> {
            if(userDetails.getToken().equals("")) {
                Toast.makeText(context, "You need to login first.", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean selected=false;
            for (int i = 0; i < itemList.size(); i++){
                if(itemList.get(i).isSelected()){
                    bottomSheetDialog.show();
                    selected=true;
                    break;
                }
            }
            if(!selected){
                Toast.makeText(context, "Please select item from cart", Toast.LENGTH_SHORT).show();
            }
        });

        selectAllListener = (buttonView, isChecked) -> {
            for (int i = 0; i < itemList.size(); i++)
                itemList.get(i).setSelected(isChecked);

            adapter.notifyDataSetChanged();
        };


        TextView privacyText = bottomSheetView.findViewById(R.id.privacyText);
        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());
        CheckBox checkBox = bottomSheetView.findViewById(R.id.checkBox);

        selectAll.setOnCheckedChangeListener(selectAllListener);
        btnBottomSheet.setOnClickListener(v -> {

            if(userDetails.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
                return;
            }

            if (!checkBox.isChecked()){
                Toast.makeText(getContext(), "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                return;
            }

            if (addressSwitch.isChecked() && customAddress.getText().toString().equals("")){
                Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Utils.isValidNumber(contact_number.getText().toString())){
                Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            placeOrder(generateOrderJson(), dialog);

        });

        // bottom sheet

        contact_number = bottomSheetView.findViewById(R.id.contact_number);
        addressSwitch = bottomSheetView.findViewById(R.id.addressSwitch);
        customAddress = bottomSheetView.findViewById(R.id.customAddress);
        addressSpinner = bottomSheetView.findViewById(R.id.spinner);

        contact_number.setText(userDetails.getPhone());
        spinnerArray = new ArrayList<String>();
        spinnerArrayID = new ArrayList<Integer>();

        customAddress.setText(userDetails.getJsonAddress());

        addressSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                customAddress.setVisibility(View.VISIBLE);
                addressSpinner.setVisibility(View.GONE);

            } else{
                customAddress.setVisibility(View.GONE);
                addressSpinner.setVisibility(View.VISIBLE);

            }

        });

        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spinnerPosition = arg2; //Here is your selected position
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                spinnerPosition = 0;
            }

        });


        if(spinnerArray.size() < 1){
            addressSwitch.setChecked(true);
        }

        // updateAddress();
        ImageView cod = bottomSheetView.findViewById(R.id.cod);
        ImageView evalyPay = bottomSheetView.findViewById(R.id.evaly_pay);

        // select payment method
        cod.setOnClickListener(v -> paymentMethod = 1);

        cod.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.performClick();
            }
        });

        evalyPay.setOnClickListener(v -> paymentMethod = 2);

        evalyPay.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.performClick();
            }
        });

        getCartList();
        
        
    }

    public void uncheckSelectAllBtn(boolean isChecked){

        if(!isChecked) {
            selectAll.setOnCheckedChangeListener(null);
            selectAll.setChecked(false);
            selectAll.setOnCheckedChangeListener(selectAllListener);
        }
        else {

            boolean isAllSelected = true;

            for (int i = 0; i < adapter.getItemList().size(); i++){
                if(!adapter.getItemList().get(i).isSelected()){
                    isAllSelected = false;
                    break;
                }
            }
            selectAll.setOnCheckedChangeListener(null);
            selectAll.setChecked(isAllSelected);
            selectAll.setOnCheckedChangeListener(selectAllListener);
        }
    }




    public void getCartList(){
        adapter.notifyItemRangeRemoved(0, itemList.size());
        itemList.clear();
        Cursor res=db.getData();
        if(res.getCount()==0){

            LinearLayout empty = view.findViewById(R.id.empty);
            LinearLayout cal = view.findViewById(R.id.cal);
            recyclerView.setVisibility(View.GONE);
            cal.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            Button button = view.findViewById(R.id.button);
            button.setVisibility(View.GONE);
            NestedScrollView scrollView = view.findViewById(R.id.scroller);
            scrollView.setBackgroundColor(Color.WHITE);
        }else{

            totalPriceDouble = 0;
            cartItem=true;
            while(res.moveToNext()){
                itemList.add(new CartItem(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getInt(4),res.getLong(5),res.getString(6),res.getInt(7),true, res.getString(8), res.getString(9)));
                totalPriceDouble +=  res.getInt(4);
                adapter.notifyItemInserted(itemList.size());
                updateCartFromRecycler();
            }
            adapter.notifyDataSetChanged();
        }
    }


    public void updateCartFromRecycler(){

        ArrayList<CartItem> listAdapter = adapter.getItemList();
        int totalPrice = 0;
        totalPriceDouble = 0;

        for (int i = 0; i < listAdapter.size(); i++){
            if(listAdapter.get(i).isSelected()){
                totalPrice += listAdapter.get(i).getPrice() * listAdapter.get(i).getQuantity();
                totalPriceDouble += listAdapter.get(i).getPrice() * listAdapter.get(i).getQuantity();
            }
        }

        TextView priceView = view.findViewById(R.id.totalPrice);
        priceView.setText("৳ "+totalPrice);

        TextView priceView2 = bottomSheetView.findViewById(R.id.bs_price);
        priceView2.setText("৳ "+totalPrice);

    }


    public void placeOrder(JsonObject payload, ViewDialog dialog){

        dialog.showDialog();

        OrderApiHelper.placeOrder(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                bottomSheetDialog.hide();
                dialog.hideDialog();

                if (response != null) {

                    String errorMsg = response.get("message").getAsString();

                    if (response.has("data")) {
                        if (response.getAsJsonArray("data").size() < 1) {
                            Toast.makeText(context, "Order couldn't be placed", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                            orderPlaced();
                        }

                        JsonArray data = response.getAsJsonArray("data");
                        for (int i = 0; i < data.size(); i++) {
                            JsonObject item = data.get(i).getAsJsonObject();
                            String invoice = item.get("invoice_no").getAsString();
                            Intent intent = new Intent(context, OrderDetailsActivity.class);
                            intent.putExtra("orderID", invoice);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(context, "Couldn't place order.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                dialog.hideDialog();
                Toast.makeText(context, "Couldn't place order, might be a server error.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    placeOrder(payload, dialog);
                else
                if (getContext() != null)
                    AppController.logout(getActivity());
            }
        });

    }




    private JsonObject generateOrderJson(){

        PlaceOrderItem orderObejct = new PlaceOrderItem();

        orderObejct.setContactNumber(contact_number.getText().toString());
        orderObejct.setCustomerAddress(customAddress.getText().toString());
        orderObejct.setOrderOrigin("app");


        orderObejct.setPaymentMethod("evaly_pay");

        List<OrderItemsItem> productList = new ArrayList<>();

        ArrayList<CartItem> adapterItems = adapter.getItemList();

        for(int i=0; i < adapterItems.size(); i++) {

            if(adapterItems.get(i).isSelected()) {

                String fromShopJson = adapterItems.get(i).getSellerJson();
                OrderItemsItem item = new OrderItemsItem();
                item.setQuantity(adapterItems.get(i).getQuantity());

                try{
                    JSONObject sellerJson = new JSONObject(fromShopJson);
                    String item_id = sellerJson.getString("shop_item_id");
                    item.setShopItemId(Integer.parseInt(item_id));
                    productList.add(item);

                } catch (Exception e){ }

            }
        }

        orderObejct.setOrderItems(productList);


        return new Gson().toJsonTree(orderObejct).getAsJsonObject();
    }

    public void orderPlaced(){

        dialog.hideDialog();
        ArrayList<CartItem> listAdapter = adapter.getItemList();

        for (int i = 0; i < listAdapter.size(); i++){
            if(listAdapter.get(i).isSelected()){
                db.deleteData(listAdapter.get(i).getId());
            }
        }

        bottomSheetDialog.hide();
        getCartList();
        Toast.makeText(context, "Your order has been placed!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        if(db!=null){
            db.close();
        }
        super.onDestroy();
    }



}
