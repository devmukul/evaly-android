package bd.com.evaly.evalyshop.util;

public class Brands {

    String productID,name,slug,thumbnailImage[];
    int productCount;

    public Brands(String productID, String name, String slug, String thumbnailImage[], int productCount) {
        this.productID = productID;
        this.name = name;
        this.slug = slug;
        this.thumbnailImage = thumbnailImage;
        this.productCount = productCount;
    }

    public Brands() {
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String[] getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String[] thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
