package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

public class ProductSpecificationsItem {

    @SerializedName("specification_name")
    private String specificationName;

    @SerializedName("specification_value")
    private String specificationValue;

    @SerializedName("id")
    private int id;

    public ProductSpecificationsItem(String specificationName, String specificationValue, int id) {
        this.specificationName = specificationName;
        this.specificationValue = specificationValue;
        this.id = id;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(String specificationName) {
        this.specificationName = specificationName;
    }

    public String getSpecificationValue() {
        return specificationValue;
    }

    public void setSpecificationValue(String specificationValue) {
        this.specificationValue = specificationValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return
                "ProductSpecificationsItem{" +
                        "specification_name = '" + specificationName + '\'' +
                        ",specification_value = '" + specificationValue + '\'' +
                        ",id = '" + id + '\'' +
                        "}";
    }
}