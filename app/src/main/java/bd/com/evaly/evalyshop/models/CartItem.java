package bd.com.evaly.evalyshop.models;

public class CartItem {

    private String id,slug,name,image,sellerJson,shopSlug;
    private int price = 0, quantity = 1;
    private boolean selected = true;
    private long time = 0;


    public CartItem(String id, String productSlug, String name, String image, int price, long time, String sellerJson, int quantity, boolean selected, String shopSlug) {
        this.id = id;
        this.slug = productSlug;
        this.name = name;
        this.image = image;
        this.price = price;
        this.time = time;
        this.sellerJson = sellerJson;
        this.quantity = quantity;
        this.selected = selected;
        this.shopSlug = shopSlug;
    }



    public CartItem(){

    }


    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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


    public String getSellerJson() {
        return sellerJson;
    }

    public void setSellerJson(String sellerJson) {
        this.sellerJson = sellerJson;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
