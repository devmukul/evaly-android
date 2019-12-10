package bd.com.evaly.evalyshop.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.RosterItemModel;
import bd.com.evaly.evalyshop.util.Constants;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import retrofit2.Response;

public class RoomWIthRxViewModel extends ViewModel {

    public MutableLiveData<List<RosterTable>> rosterList = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasNext = new MutableLiveData<>();


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
//                            Logger.e(model.getLast_message());
                            try {
                                ChatItem chatItem = new Gson().fromJson(model.getLast_message(), ChatItem.class);
                                if (!chatItem.isInvitation()) {
                                    RosterTable table = new RosterTable();
                                    if (model.getVcard() != null) {
                                        XmlToJson xmlToJson = new XmlToJson.Builder(model.getVcard()).build();
//                                Logger.json(xmlToJson.toJson().toString());
                                        try {
                                            JSONObject object = xmlToJson.toJson().getJSONObject("vCard");
                                            String name = object.getString("FN");
                                            if (name == null) {
                                                name = "";
                                            }

                                            table.name = name;
                                            table.nick_name = object.getString("NICKNAME");
                                            table.imageUrl = object.get("URL").toString();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        table.id = model.getJid();
                                        table.lastMessage = model.getLast_message();
                                        table.unreadCount = model.getUnseen_messages();
                                        table.messageId = model.getLast_unread_message_id();
//                                Logger.json(new Gson().toJson(table));
                                        tableList.add(table);
                                    } else {
                                        table.id = model.getJid();
//                                table.name = "Evaly User";
                                        table.lastMessage = model.getLast_message();
                                        table.unreadCount = model.getUnseen_messages();
                                        table.messageId = model.getLast_unread_message_id();
                                        tableList.add(table);
                                    }
                                }else {
                                    if (chatItem.getSender().contains(CredentialManager.getUserName())){
                                        RosterTable table = new RosterTable();
                                        if (model.getVcard() != null) {
                                            XmlToJson xmlToJson = new XmlToJson.Builder(model.getVcard()).build();
//                                Logger.json(xmlToJson.toJson().toString());
                                            try {
                                                JSONObject object = xmlToJson.toJson().getJSONObject("vCard");
                                                String name = object.getString("FN");
                                                if (name == null) {
                                                    name = "";
                                                }

                                                table.name = name;
                                                table.nick_name = object.getString("NICKNAME");
                                                table.imageUrl = object.get("URL").toString();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            table.id = model.getJid();
                                            table.lastMessage = model.getLast_message();
                                            table.unreadCount = model.getUnseen_messages();
                                            table.messageId = model.getLast_unread_message_id();
//                                Logger.json(new Gson().toJson(table));
                                            tableList.add(table);
                                        } else {
                                            table.id = model.getJid();
//                                table.name = "Evaly User";
                                            table.lastMessage = model.getLast_message();
                                            table.unreadCount = model.getUnseen_messages();
                                            table.messageId = model.getLast_unread_message_id();
                                            tableList.add(table);
                                        }
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
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
