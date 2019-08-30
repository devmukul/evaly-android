package bd.com.evaly.evalyshop.models;

public class WishList {

    String id,productSlug,name,image,price;
    long time;

    public WishList(){}

    public WishList(String id, String productSlug, String name, String image, String price, long time) {
        this.id = id;
        this.productSlug = productSlug;
        this.name = name;
        this.image = image;
        this.price = price;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
