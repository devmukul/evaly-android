package bd.com.evaly.evalyshop.views;

public class AvailableShop {

    String name,logo,price,slug,phone,address,shopJson,maximumPrice;
    boolean stock;


    public AvailableShop(){

    }

    public AvailableShop(String name, String logo, String price, String slug, String phone, String address, String shopJson,boolean stock,String maximumPrice) {
        this.name = name;
        this.logo = logo;
        this.price = price;
        this.slug = slug;
        this.phone = phone;
        this.address = address;
        this.shopJson = shopJson;
        this.stock=stock;
        this.maximumPrice=maximumPrice;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShopJson() {
        return shopJson;
    }

    public void setShopJson(String shopJson) {
        this.shopJson = shopJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean getStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

    public String getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(String maximumPrice) {
        this.maximumPrice = maximumPrice;
    }
}
