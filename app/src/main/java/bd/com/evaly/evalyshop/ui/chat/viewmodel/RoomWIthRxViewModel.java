package bd.com.evaly.evalyshop.ui.chat.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
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
                                    if (chatItem.getSender().contains(CredentialManager.getUserName())) {
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
                                                if (chatItem.getReceiver_name().replace(" ", "").isEmpty()) {
                                                    table.name = name;
                                                    table.nick_name = object.getString("NICKNAME");
                                                } else {
                                                    table.name = chatItem.getReceiver_name();
                                                    table.nick_name = chatItem.getReceiver_name();
                                                }

                                                if (chatItem.getReceiver_image() != null && !chatItem.getReceiver_image().isEmpty()) {
                                                    table.imageUrl = chatItem.getReceiver_image();
                                                } else {
                                                    table.imageUrl = object.get("URL").toString();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            table.id = model.getJid();
                                            table.lastMessage = model.getLast_message();
                                            table.unreadCount = model.getUnseen_messages();
                                            table.messageId = model.getLast_unread_message_id();
                                            tableList.add(table);
                                        } else {
                                            table.id = model.getJid();
                                            table.lastMessage = model.getLast_message();
                                            table.unreadCount = model.getUnseen_messages();
                                            table.messageId = model.getLast_unread_message_id();

                                            if (chatItem.getReceiver_name() == null || chatItem.getReceiver_name().isEmpty()){
                                                table.name = "Evaly User";
                                            } else {
                                                table.name = chatItem.getReceiver_name();
                                                table.nick_name = chatItem.getReceiver_name();
                                            }

                                            table.nick_name = chatItem.getReceiver_name();


                                            if (chatItem.getReceiver_image() != null && !chatItem.getReceiver_image().isEmpty()) {
                                                table.imageUrl = chatItem.getReceiver_image();
                                            }
                                            tableList.add(table);
                                        }
                                    } else {
                                        RosterTable table = new RosterTable();
                                        if (model.getVcard() != null) {
                                            XmlToJson xmlToJson = new XmlToJson.Builder(model.getVcard()).build();
                                            try {
                                                JSONObject object = xmlToJson.toJson().getJSONObject("vCard");
                                                String name = object.getString("FN");
                                                if (name == null) {
                                                    name = "";
                                                }
                                                if (chatItem.getName().replace(" ", "").isEmpty()) {
                                                    table.name = name;
                                                    table.nick_name = object.getString("NICKNAME");
                                                } else {
                                                    table.name = chatItem.getName();
                                                    table.nick_name = chatItem.getName();
                                                }

                                                if (chatItem.getReceiver_image() != null && !chatItem.getReceiver_image().isEmpty()) {
                                                    table.imageUrl = chatItem.getImage();
                                                } else {
                                                    table.imageUrl = object.get("URL").toString();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            table.id = model.getJid();
                                            table.lastMessage = model.getLast_message();
                                            table.unreadCount = model.getUnseen_messages();
                                            table.messageId = model.getLast_unread_message_id();
                                            tableList.add(table);
                                        } else {
                                            table.id = model.getJid();
                                            table.lastMessage = model.getLast_message();
                                            table.unreadCount = model.getUnseen_messages();
                                            table.messageId = model.getLast_unread_message_id();

                                            if (chatItem.getName() == null || chatItem.getName().isEmpty()){
                                                table.name = "Evaly User";
                                            } else {
                                                table.name = chatItem.getName();
                                                table.nick_name = chatItem.getName();
                                            }


                                            if (chatItem.getReceiver_image() != null && !chatItem.getReceiver_image().isEmpty()) {
                                                table.imageUrl = chatItem.getImage();
                                            }
                                            tableList.add(table);
                                        }
                                    }
                                } else {

                                }
                            } catch (Exception e) {
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
