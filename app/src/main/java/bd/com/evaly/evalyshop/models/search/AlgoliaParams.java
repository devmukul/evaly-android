package bd.com.evaly.evalyshop.models.search;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.util.Utils;

public class AlgoliaParams implements Serializable {
    private Gson gson;
    @NonNull
    private String query;
    private int page;
    private String maxValuesPerFacet;
    @NonNull
    private String highlightPreTag;
    @NonNull
    private String highlightPostTag;
    @NonNull
    private List<String> facets = new ArrayList<>();
    @NonNull
    private String tagFilters;
    @NonNull
    private List<List<String>> facetFilters = new ArrayList<>();
    @NonNull
    private List<String> numericFilters = new ArrayList<>();


    public AlgoliaParams() {
        if (gson == null)
            gson = new Gson();

        query = "";
        maxValuesPerFacet = "20";
        page = 1;
        highlightPreTag = "<ais-highlight-0000000000>";
        highlightPostTag = "</ais-highlight-0000000000>";
        tagFilters = "";

        facets.add("price");
        facets.add("category_name");
        facets.add("brand_name");
        facets.add("color");
        facets.add("shop_name");
    }

    public Map<String, String> getMap() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("query", query);
        paramsMap.put("maxValuesPerFacet", String.valueOf(maxValuesPerFacet));
        paramsMap.put("page", String.valueOf(page));
        paramsMap.put("highlightPreTag", highlightPreTag);
        paramsMap.put("highlightPostTag", highlightPostTag);
        paramsMap.put("facets", gson.toJson(facets));
        paramsMap.put("tagFilters", tagFilters);
        return paramsMap;
    }

    public String getParams() {
        return Utils.urlEncodeUTF8(getMap());
    }


    public void setQuery(@NonNull String query) {
        this.query = query;
    }

    public void setFacetFilters(@NonNull List<List<String>> facetFilters) {
        this.facetFilters = facetFilters;
    }

    public void setNumericFilters(@NonNull List<String> numericFilters) {
        this.numericFilters = numericFilters;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    @NonNull
    public String getQuery() {
        return query;
    }
}
