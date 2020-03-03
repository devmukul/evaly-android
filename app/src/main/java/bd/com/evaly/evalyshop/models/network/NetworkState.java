package bd.com.evaly.evalyshop.models.network;

public class NetworkState {

    public static final NetworkState LOADING_FIRST;

    private final Status status;
    private final String msg;

    public static final NetworkState LOADED;
    public static final NetworkState LOADING;

    static {
        LOADED=new NetworkState(Status.SUCCESS,"Success");
        LOADING=new NetworkState(Status.RUNNING,"Running");
        LOADING_FIRST = new NetworkState(Status.LOADING_FIRST, "Loading First");
    }


    public NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public enum Status{
        RUNNING,
        SUCCESS,
        LOADING_FIRST,
        FAILED,
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}