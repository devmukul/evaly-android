package bd.com.evaly.evalyshop.ui.brand.adapter;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.product.productList.ProductGrid;
import bd.com.evaly.evalyshop.ui.shop.adapter.ShopCategoryAdapter;

public class BrandProductHeader extends RecyclerView.ViewHolder {

    View view;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;
    private NavController navController;
    private Context context;
    private String slug = "", title = "", categoryString = "", imgUrl = "", categorySlug = "";
    private ImageView logo;
    private TextView name, categoryName, address, number;
    private NestedScrollView nestedSV;
    private ShopCategoryAdapter adapter;
    private MainActivity mainActivity;
    private ProductGrid productGrid;
    private ImageView placeHolder;
    private ProgressBar progressBar;
    private View dummyView;
    private HashMap<String, String> data;

    public BrandProductHeader(View itemView, Context context, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController, HashMap<String, String> data) {
        super(itemView);
        this.context = context;
        this.fragmentInstance = fragmentInstance;
        this.activityInstance = activityInstance;
        this.navController = navController;
        this.data = data;

        this.slug = data.get("slug");
        this.title = data.get("title");
        this.categoryString = data.get("categoryString");
        this.categorySlug = data.get("categorySlug");

        view = itemView;
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        initHeader(view);
    }

    private void initHeader(View view) {

        name = view.findViewById(R.id.name);
        categoryName = view.findViewById(R.id.categoryName);
        address = view.findViewById(R.id.address);
        number = view.findViewById(R.id.number);
        logo = view.findViewById(R.id.logo);
        placeHolder = view.findViewById(R.id.placeholder_image);

        name.setText(title);
        categoryName.setText(categoryString);

        if (context != null)
            Glide.with(context)
                    .load(imgUrl)
                    .into(logo);



    }



}