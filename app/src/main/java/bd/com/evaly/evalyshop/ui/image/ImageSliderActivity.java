package bd.com.evaly.evalyshop.ui.image;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import bd.com.evaly.evalyshop.databinding.ActivityImageSliderBinding;
import bd.com.evaly.evalyshop.ui.image.controller.ImageSliderController;

public class ImageSliderActivity extends AppCompatActivity {

    private ActivityImageSliderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityImageSliderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        List<String> imageList = intent.getStringArrayListExtra("image_list");

        ImageSliderController controller = new ImageSliderController();
        binding.viewPager.setAdapter(controller.getAdapter());
        new TabLayoutMediator(binding.sliderIndicator, binding.viewPager,
                (tab, position) -> tab.setText("")
        ).attach();
        controller.setData(imageList);
    }
}