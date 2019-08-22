package bd.com.evaly.evalyshop.util;

import java.util.ArrayList;

public class Filter {

    String header;
    ArrayList<String> values,slugs;

    public Filter(String header, ArrayList<String> values, ArrayList<String> slugs) {
        this.header = header;
        this.values = values;
        this.slugs = slugs;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public ArrayList<String> getSlugs() {
        return slugs;
    }

    public void setSlugs(ArrayList<String> slugs) {
        this.slugs = slugs;
    }
}
