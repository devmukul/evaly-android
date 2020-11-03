package bd.com.evaly.evalyshop.models.search;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlgoliaRequest {

    @SerializedName("requests")
    private List<RequestsItem> requests;

    public void addRequest(RequestsItem requestsItem) {
        requests = new ArrayList<>();
        requests.add(requestsItem);
    }

    public List<RequestsItem> getRequests() {
        return requests;
    }

    public void setRequests(List<RequestsItem> requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        return
                "AlgoliaRequest{" +
                        "requests = '" + requests + '\'' +
                        "}";
    }
}