package bd.com.evaly.evalyshop.ui.efood.home.controller;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.CarouselModel_;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.ui.efood.home.model.RestaurantHorizontalModel_;
import bd.com.evaly.evalyshop.ui.efood.home.model.RestaurantVerticalModel_;
import bd.com.evaly.evalyshop.ui.efood.home.model.SectionTitleModel_;

public class eFoodHomeController extends EpoxyController {

    @AutoModel
    CarouselModel_ popularRestaurantCarousel;

    @AutoModel
    SectionTitleModel_ popularTitle;

    @AutoModel
    SectionTitleModel_ allTitle;

    @Override
    protected void buildModels() {

        popularTitle.title("Popular Restaurants").addTo(this);

        List<RestaurantHorizontalModel_> popularRestaurantList = new ArrayList<>();

        popularRestaurantList.add(new RestaurantHorizontalModel_()
                .id(1)
                .title("Pizzahut Bashundhara")
                .slug("pizza")
                .coverUrl("https://evaly.com.bd/static/images/express/express_fresh.png")
                .rating(4.3));

        popularRestaurantList.add(new RestaurantHorizontalModel_()
                .id(2)
                .title("Tasty Bite Bashundhara")
                .slug("Bite")
                .coverUrl("https://evaly.com.bd/static/images/express/express_food.png")
                .rating(4.4));

        popularRestaurantCarousel.models(popularRestaurantList).addTo(this);


        allTitle.title("All Restaurants").addTo(this);

        new RestaurantVerticalModel_()
                .id(3)
                .title("Dominos Bashundhara")
                .slug("pizzaz")
                .coverUrl("https://evaly.com.bd/static/images/express/express_fresh.png")
                .rating(4.5)
                .addTo(this);

        new RestaurantVerticalModel_()
                .id(4)
                .title("Meal Deal")
                .slug("pizzazz")
                .coverUrl("https://evaly.com.bd/static/images/express/express_fish_meat.png")
                .rating(4.5)
                .addTo(this);

        new RestaurantVerticalModel_()
                .id(5)
                .title("Pizzaroma")
                .slug("pizzaazz")
                .coverUrl("https://evaly.com.bd/static/images/express/express_fish_meat.png")
                .rating(4.5)
                .addTo(this);

    }


}
