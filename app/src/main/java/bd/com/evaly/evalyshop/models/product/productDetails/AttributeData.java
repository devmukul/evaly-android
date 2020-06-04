package bd.com.evaly.evalyshop.models.product.productDetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttributeData {

    @SerializedName("type")
    private String type;

    @SerializedName("values")
    private List<AttributeValuesItem> values;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttributeValuesItem> getValues() {
        return values;
    }

    public void setValues(List<AttributeValuesItem> values) {
        this.values = values;
    }
}
