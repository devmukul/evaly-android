package bd.com.evaly.evalyshop.ui.campaign.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSliderCarouselModelModel_;

public class CampaignController extends EpoxyController {

    private List<BannerItem> bannerList = new ArrayList<>();
    @AutoModel
    CampaignSliderCarouselModelModel_ sliderCarousel;

    @Override
    protected void buildModels() {

//        List<CampaignSliderModel_> bannerModels = new ArrayList<>();
//        for (BannerItem item : bannerList) {
//            bannerModels.add(new CampaignSliderModel_()
//                    .id(item.slug)
//                    .model(item));
//        }
//        sliderCarousel.models(bannerModels).addTo(this);
    }

    public void setBannerList(List<BannerItem> bannerList) {
        this.bannerList = bannerList;
    }
}
