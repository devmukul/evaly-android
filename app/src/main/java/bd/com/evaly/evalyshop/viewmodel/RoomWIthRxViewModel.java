package bd.com.evaly.evalyshop.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.AppDatabase;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.RosterItemModel;
import bd.com.evaly.evalyshop.util.Constants;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import io.reactivex.Flowable;
import retrofit2.Response;

public class RoomWIthRxViewModel extends ViewModel {

    public MutableLiveData<List<RosterTable>> rosterList = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasNext = new MutableLiveData<>();


//    public Flowable<List<RosterTable>> getList() {
//        Logger.d("UPDATE");
//        return appDatabase.taskDao().getAllRoster();
//    }

    public void loadRosterList(String userName, int page, int limit) {
        AuthApiHelper.getRosterList(userName, page, limit, new DataFetchingListener<Response<List<RosterItemModel>>>() {
            @Override
            public void onDataFetched(Response<List<RosterItemModel>> response) {
                if (response.code() == 200) {
                    if (response.body().size() == limit) {
                        hasNext.setValue(true);
                    } else {
                        hasNext.setValue(false);
                    }

                    List<RosterTable> tableList = new ArrayList<>();
                    for (RosterItemModel model : response.body()) {
                        if (!model.getJid().contains(Constants.EVALY_NUMBER)) {
                            RosterTable table = new RosterTable();
                            if (model.getVcard() != null) {
                                XmlToJson xmlToJson = new XmlToJson.Builder(model.getVcard()).build();

                                table.id = model.getJid();
                                try {
//                                Logger.json(xmlToJson.toJson().toString());
                                    String name = xmlToJson.toJson().getJSONObject("vCard").getString("FN");
                                    if (name == null) {
                                        name = "";
                                    }

                                    table.name = name;
                                    table.nick_name = xmlToJson.toJson().getJSONObject("vCard").getString("NICKNAME");
                                    table.imageUrl = xmlToJson.toJson().getJSONObject("VCard").getString("URL");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                table.lastMessage = model.getLast_message();
                                table.unreadCount = model.getUnseen_messages();
                                table.messageId = model.getLast_unread_message_id();
                                tableList.add(table);
                            } else {
                                table.id = model.getJid();
                                table.lastMessage = model.getLast_message();
                                table.unreadCount = model.getUnseen_messages();
                                table.messageId = model.getLast_unread_message_id();
                                tableList.add(table);
                            }
                        }
                    }
                    rosterList.setValue(tableList);
                } else {
                    isSuccess.setValue(false);
                }
            }

            @Override
            public void onFailed(int status) {
                isSuccess.setValue(false);
            }
        });
    }

}
