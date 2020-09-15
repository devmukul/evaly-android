package bd.com.evaly.evalyshop.ui.image.controller;

import com.airbnb.epoxy.TypedEpoxyController;

import java.util.List;

import bd.com.evaly.evalyshop.ui.image.model.ImageSliderModel_;

public class ImageSliderController extends TypedEpoxyController<List<String>> {

    public ImageSliderController() {
        setFilterDuplicates(true);
    }

    @Override
    protected void buildModels(List<String> data) {
        for (String image : data)
            new ImageSliderModel_()
                    .id(image)
                    .image(image)
                    .addTo(this);
    }
}
