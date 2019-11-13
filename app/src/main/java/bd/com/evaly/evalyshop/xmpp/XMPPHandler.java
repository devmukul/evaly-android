package bd.com.evaly.evalyshop.xmpp;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.provider.ChatStateExtensionProvider;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageHeader;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.jid.util.JidUtil;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.ChatStateModel;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
import bd.com.evaly.evalyshop.models.xmpp.SignupModel;
import bd.com.evaly.evalyshop.util.Constants;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class XMPPHandler {

    private final String TAG = getClass().getSimpleName();
    public static boolean connected = false;
    public boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = false; //Show toast for events? set false to just print via Log
    private HashMap<String, Boolean> chat_created_for = new HashMap<>(); //for single chat env
    public static AbstractXMPPConnection connection;
    public String userId = CredentialManager.getUserName();
    public String userPassword = CredentialManager.getPassword();
    public boolean autoLogin;
    public VCard mVcard;
    public List<RoasterModel> roasterList;
    Roster roster;
    MamManager mamManager;
    MamManager.MamQueryArgs mamQueryArgs;
    MamManager.MamQuery mamQueryResult;
    static ReconnectionManager reconnectionManager;

    Gson gson;
    public XMPPService service;
    public static XMPPHandler instance = null;
    public static boolean instanceCreated = false;
    boolean isFirstTime;

    private final boolean debug = Constants.XMPP_DEBUG;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    //XMPP Stuffs
    public Chat mChat;
    private XMPPConnectionListener mConnectionListener = new XMPPConnectionListener();
    private MyIncomingChatManagerListener mIncomingChatManagerListener;
    private MyOutGoingChatManagerListener mOutGoingChatManagerListener;
    private MyChatStateManagerListener mChatStateManagerListener;
    private MyMessageListener mMessageListener;
    private MyStanzaListener mStanzaListener;
    private MyRosterListener mRoasterListener;


    /*
     * A default constructor which only service instance
     * This allows to connect, without needing to be loggedin
     * This way, we will get instance to XMPPHandler, and can login whenever we want,
     * using .login() method
     */
    public XMPPHandler(XMPPService service) {
        this.service = service;
        this.autoLogin = true;

        if (instance == null) {
            instance = this;
            instanceCreated = true;
        }

        //Prepare the connections and listeners
        init();
    }

    // Get XMPPHandler instance
    public static XMPPHandler getInstance() {
        return instance;
    }

    public void init() {

        if (debug) Log.e(TAG, "starting XMPPHandler");

        gson = new Gson(); //We need GSON to parse chat messages
        mMessageListener = new MyMessageListener(); //Message event Listener
        mOutGoingChatManagerListener = new MyOutGoingChatManagerListener(); //Chat Manager
        mIncomingChatManagerListener = new MyIncomingChatManagerListener(); //Chat Manager
        mChatStateManagerListener = new MyChatStateManagerListener(); //Chat Manager
        mStanzaListener = new MyStanzaListener(); // Listen for incoming stanzas (packets)
        mRoasterListener = new MyRosterListener();
        roasterList = new ArrayList<>();

        // Ok, now that events have been attached, we can prepare connection
        // (we will initialize connection by calling ".connect()" method later on.
        initialiseConnection();
    }

    //Pass server address, port to initialize connection
    private void initialiseConnection() {


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    XMPPTCPConnectionConfiguration config = null;

                    InetAddress addr = InetAddress.getByName(Constants.XMPP_HOST);
                    config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(userId, userPassword)
                            .setXmppDomain(Constants.XMPP_DOMAIN)
                            .setHostAddress(addr)
                            .setHost(Constants.XMPP_HOST)
                            .setPort(Constants.XMPP_PORT)
                            .build();

                    connection = new XMPPTCPConnection(config);
                    connection.addConnectionListener(mConnectionListener);
                    connection.addAsyncStanzaListener(mStanzaListener, new StanzaFilter() {
                        @Override
                        public boolean accept(Stanza stanza) {
                            //You can also return only presence packets, since we are only filtering presences
                            return true;
                        }
                    });


                    reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                    reconnectionManager.enableAutomaticReconnection();
                    reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
                    reconnectionManager.setFixedDelay(5);
                    reconnectionManager.addReconnectionListener(new ReconnectionListener() {
                        @Override
                        public void reconnectingIn(int seconds) {
                            service.onReConnection();
                            Logger.e("Reconnection    " + seconds);
                            loggedin = false;
                        }

                        @Override
                        public void reconnectionFailed(Exception e) {
                            service.onReConnectionError();

                            if (debug) Log.d(TAG, "ReconnectionFailed!");

                            //Reset the variables
                            connected = false;
                            chatInstanceIterator(chat_created_for);
                            loggedin = false;
                        }
                    });

                    roster = Roster.getInstanceFor(connection);
                    roster.addRosterListener(mRoasterListener);
                    roster.setSubscriptionMode(Roster.SubscriptionMode.manual);


                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });


//        ProviderManager pm = new ProviderManager();
//        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
//        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
//        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
//        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());
//        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtensionProvider());

    }

    // Set username and password explicitly for login
    public void setUserPassword(String mUsername, String mPassword) {
        this.userId = mUsername;
        this.userPassword = mPassword;
    }

    //This method sets every chat instances to false (in situations where connection closes, or error happens)
    public static void chatInstanceIterator(Map<String, Boolean> mp) {

        for (Map.Entry<String, Boolean> entry : mp.entrySet()) {
            entry.setValue(false);
        }
    }

    //check if a connection is already established
    public boolean isConnected() {
        return connected;
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    //Explicitly start a connection
    public void connect() {

        initialiseConnection();

        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                //There is no point in reconnecting an already established connection. So abort, if we do
                if (connection != null && connection.isConnected())
                    return false;

                //We are currently in "connection" phase, so no requests should be made while we are connecting.
                isconnecting = true;

                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(service, "connecting....", Toast.LENGTH_LONG).show();
                        }
                    });

                if (debug) Log.d(TAG, "connecting....");

                try {
                    if (connection != null) {
                        connection.connect();

                        /**
                         * Set delivery receipt for every Message, so that we can confirm if message
                         * has been received on other end.
                         *
                         * @NOTE: This feature is not yet implemented in this example. Maybe, I'll add it later on.
                         * Feel free to pull request to add one.
                         *
                         * Read more about this: http://xmpp.org/extensions/xep-0184.html
                         **/


                        DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(connection);
                        dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
                        dm.addReceiptReceivedListener(new ReceiptReceivedListener() {
                            @Override
                            public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
                                Logger.d(fromJid.asUnescapedString() + "  ---->  " + toJid.asUnescapedString() + "     " + receiptId);
                            }

                        });

                        connected = true;
                    }

                } catch (IOException e) {
                    service.onConnectionClosed();
                    if (isToasted)
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(service, "IOException: ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    if (debug) Log.e(TAG, "IOException: " + e.getMessage());
                } catch (SmackException e) {
                    service.onConnectionClosed();
                    if (isToasted)
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(service, "SMACKException: ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    if (debug) Log.e(TAG, "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    service.onConnectionClosed();
                    if (isToasted)
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(service, "XMPPException: ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    if (debug) Log.e(TAG, "XMPPException: " + e.getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Our "connection" phase is now complete. We can tell others to make requests from now on.
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    //Explicitly Disconnect a connection
    public static void disconnect() {

        try {
            reconnectionManager.disableAutomaticReconnection();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public VCard getCurrentUserDetails() {
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        VCard mCard = null;
        try {
            mCard = vCardManager.loadVCard();
            return mCard;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Logger.d(new Gson().toJson(mCard));
        return mCard;
    }

    //Dummy method. Checks if roster belongs to one of our user
    public Boolean checkSender(Jid roasterModel, BareJid user) {
        Presence presence = roster.getPresence(user);
        return (presence.getFrom().toString().contains(roasterModel));
    }

    public boolean isAlreadyInvited(String id){
        Collection<RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries) {
//            confirmSubscription(entry.getJid(), true);
            try {
                Presence presence = roster.getPresence(entry.getJid());
//                Logger.e(entry.getJid().asUnescapedString()+"    "+ entry.getType());
                if (entry.getJid().asUnescapedString().contains(id)){
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    //Sends a subscription request to particular user (JID)
    public boolean sendRequestTo(String id, String name) {

        //Making the Full JID
        if (!id.contains("@")) {
            id = id + "@" + Constants.XMPP_DOMAIN;
        }

        BareJid jid = null;
        try {
            jid = JidCreate.bareFrom(id);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

//
//        IQ iq = new IQ("query") {
//            @Override
//            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
//               xml.append("<presence id="+ CredentialManager.getUserName()+System.currentTimeMillis()+"type='subscribe'"+">");
//                return xml;
//            }
//        };
//
//
//        iq.setTo(jid);
//        Logger.d(iq);
//        connection.sendIqRequestAsync(iq);


        //get Entry
        RosterEntry userEntry = roster.getEntry(jid);

//        String nickname = XmppStringUtils.parseLocalpart(String.valueOf(jid));

        boolean isSubscribed = true;
        if (userEntry != null) {
            isSubscribed = userEntry.getGroups().size() == 0;
        }

        if (isSubscribed) {
            try {
                roster.createEntry(jid, name, null);

            } catch (XMPPException | SmackException e) {
                if (debug) Log.e(TAG, "Unable to add new entry " + jid, e);
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            roster.getEntry(jid);
            return true;
        }else {
            return false;
        }
    }

    public void createGroupChat(String mucJid, String name) {
        String DomainName = "conference." + Constants.XMPP_HOST;
        // Create a MultiUserChat using a Connection for a room
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(name + "@"
                    + DomainName);

// Create a MultiUserChat using an XMPPConnection for a room
            MultiUserChat muc = manager.getMultiUserChat(jid);

// Prepare a list of owners of the new room
            Set<Jid> owners = JidUtil.jidSetFrom(new String[]{});

//            Logger.d(owners);
// Create the room

            Resourcepart nickname = Resourcepart.from(name);

            muc.createOrJoin(nickname);
            roster.createEntry(jid.asBareJid(), nickname.toString(), null);

            Logger.d(muc.getNickname());


//            muc.get
//            muc.create(nickname);
//            Form form = muc.getConfigurationForm().createAnswerForm();
//
//            form.setAnswer("muc#roomconfig_roomowners", mucJid + "@" + DomainName);
//            muc.sendConfigurationForm(form);
//            muc.join(nickname);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.MucAlreadyJoinedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        }

    }

    //Get subscription requests which came "from" other users
    public ArrayList<Jid> getPendingRequests() {
        Collection<RosterEntry> entries = roster.getEntries();

        ArrayList<Jid> pendingRequestList = new ArrayList<>();
        for (RosterEntry entry : entries) {
//            confirmSubscription(entry.getJid(), true);
            try {
                Presence presence = roster.getPresence(entry.getJid());
                Logger.e(entry.getJid().asUnescapedString()+"    "+ entry.getType());
                if (entry.getType() == RosterPacket.ItemType.from) {
                    pendingRequestList.add(presence.getFrom());
                    Logger.e(presence.getFrom().asUnescapedString()+"  ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return pendingRequestList;
    }

    public ArrayList<RoasterModel> getAllRoaster() {
        Collection<RosterEntry> entries = roster.getEntries();

        ArrayList<RoasterModel> roasterModelArrayList = new ArrayList<>();
        for (RosterEntry entry : entries) {
//            Logger.d(new Gson().toJson(entry.getGroups()));
            try {
                if (entry != null) {
                    Presence presence = roster.getPresence(entry.getJid());

                    Presence.Mode mode = presence.getMode();

                    int status = retreiveState(mode, presence.isAvailable());
                    roasterModelArrayList.add(
                            new RoasterModel(entry.getJid(), presence.getFrom(), presence.getStatus(), mode, status, entry.getName()));

                    if (debug) {
                        Logger.e( entry.getJid().asUnescapedString() + "   " + entry.getType());

                        String isSubscribePending = (entry.getType() == RosterPacket.ItemType.both) ? "Yes" : "No";
                        //                Log.e(TAG, "sub: " + isSubscribePending);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return roasterModelArrayList;
    }

    public ChatItem getLastMessage(Jid jid) {
        ChatItem chatItem = null;
        try {
            MamManager mamManager = MamManager.getInstanceFor(connection);
            MamManager.MamQueryArgs mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .setResultPageSize(1)
                    .limitResultsToJid(jid)
                    .queryLastPage().build();
            MamManager.MamQuery mamQueryResult = mamManager.queryArchive(mamQueryArgs);
            List<Message> forwardedMessages = mamQueryResult.getMessages();
            Iterator<Message> forwardedIterator = forwardedMessages.iterator();
//            Logger.d(forwardedMessages.size());
            while (forwardedIterator.hasNext()) {
                Message message = forwardedIterator.next();
//                Logger.d(new Gson().toJson(message));
//                Logger.d(message.getBody());

                try {
                    chatItem = new Gson().fromJson(message.getBody(), ChatItem.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = new JSONObject(new Gson().toJson(message));

                JSONObject ob1 = jsonObject.getJSONObject("packetExtensions").getJSONObject("map");
                String json = ob1.toString();
//                Logger.d(json);
                String json1 = "{\"" + json.substring(json.indexOf("urn:xmpp:sid:0"));
                JSONObject object = new JSONObject(json1);
//                Logger.d(json1);
                JSONArray jsonArray = object.getJSONArray("urn:xmpp:sid:0");
                String id = jsonArray.getJSONObject(0).getString("id");
//                Logger.d(id+" ==========");
                chatItem.setMessageId(id);
            }
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chatItem;
    }

    public VCard getUserDetails(EntityBareJid jid) {
//        Logger.d(jid);
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        VCard vCard = null;
//        Logger.d(connection.isConnected());
//        Logger.d(connection.getUser().asEntityBareJid());
        try {
            vCard = vCardManager.loadVCard(jid);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return vCard;
    }

    public Stanza getUnreadMessage(Jid jid, Jid mJid) {
        IQ iq = new IQ("query", "urn:xmpp:mark_as_read") {
            @Override
            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                xml.attribute("jid", mJid);
                xml.rightAngleBracket();
                return xml;
            }
        };
        iq.setType(IQ.Type.get);
        iq.setFrom(mJid);
        iq.setTo(jid);
        iq.setStanzaId();

//        Logger.d(iq.getChildElementXML());

        Stanza stanza = null;

//        iq.createResultIQ(iq);
//        Logger.d(iq.createResultIQ(iq));
//
//        connection.sendIqRequestAsync(iq);
        try {
            stanza = connection.sendIqRequestAndWaitForResponse(iq);
//            Logger.d(stanza.toXML("unseen_messages"));
            String line = stanza.toString();
//            while ((line)!=null) {
//                int ind = line.indexOf("amount=");
//                if (ind >= 0) {
//                    String yourValue = line.substring(ind+"amount=".length(), line.length()-1).trim(); // -1 to remove de ";"
//                    Logger.d(yourValue);
//                }
//            }
//            Logger.d(new Gson().toJson(stanza));

//            connection.sendIqWithResponseCallback(iq, new StanzaListener() {
//                @Override
//                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
//                    Logger.d(packet.toXML("iq"));
//                    Logger.d(new Gson());
//
//                }
//            });
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }

        return stanza;
    }

    //Get only online users, i.e. users having subscription mode "BOTH" with you
    public Stanza getOnlineUsers() {


//        Collection<RosterEntry> entries = roster.getEntries();
//
//        ArrayList<RoasterModel> roasterModelArrayList = new ArrayList<>();
//        for (RosterEntry entry : entries) {
//            Presence presence = roster.getPresence(entry.getJid());
//
//            if (presence != null && entry.getType() == RosterPacket.ItemType.both) {
//                Presence.Mode mode = presence.getMode();
//
//                int status = retreiveState(mode, presence.isAvailable());
//                roasterModelArrayList.add(
//                        new RoasterModel(entry.getJid(), presence.getFrom(), presence.getStatus(), mode, status));
//            }
//
//            if (debug) {
//                Logger.e(entry.getUser());
//                Logger.e(entry.getName());
//                Log.e(TAG, "" + presence.getType().name());
//                Log.e(TAG, "" + presence.getStatus());
//                Log.e(TAG, "" + presence.getMode());
//                Log.e(TAG, "" + entry.getType());
//
//                String isSubscribePending = (entry.getType() == RosterPacket.ItemType.both) ? "Yes" : "No";
//                Log.e(TAG, "sub: " + isSubscribePending);
//            }
//        }
        return null;
    }


    public void markAsRead(String id, Jid jid, Jid mJid) {
        if (id != null) {
            IQ iq = new IQ("ack", "urn:xmpp:mark_as_read") {
                @Override
                protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                    xml.attribute("id", id);
                    xml.rightAngleBracket();
                    return xml;
                }
            };
            iq.setType(IQ.Type.set);
            iq.setFrom(mJid);
            iq.setTo(jid);
            iq.setStanzaId();
            Logger.d(iq.getChildElementXML());
            try {
                connection.sendIqWithResponseCallback(iq, new StanzaListener() {
                    @Override
                    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                        Logger.d(packet.toXML("iq"));
                    }
                });
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Getting presence mode, to check user status
    private int retreiveState(Presence.Mode usermode, boolean isOnline) {

        int userState = Constants.PRESENCE_MODE_OFFLINE_INT;

        if (usermode == Presence.Mode.dnd) {
            userState = Constants.PRESENCE_MODE_DND_INT;
        } else if (usermode == Presence.Mode.away || usermode == Presence.Mode.xa) {
            userState = Constants.PRESENCE_MODE_AWAY_INT;
        } else if (isOnline) {
            userState = Constants.PRESENCE_MODE_AVAILABLE_INT;
        }

        return userState;

    }

    public void updateUserInfo(UserModel userModel) {
        StanzaError.Condition condition = null;
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        VCard vCard = new VCard();
        Logger.d(connection.isConnected());
        Logger.d(new Gson().toJson(userModel));
//        Logger.d(connection.getUser().asEntityBareJid());
        try {
            vCard = vCardManager.loadVCard(JidCreate.entityBareFrom(userModel.getUsername() + "@" + Constants.XMPP_HOST));
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        vCard.setNickName(userModel.getFirst_name());
        vCard.setEmailHome(userModel.getEmail());
        vCard.setFirstName(userModel.getFirst_name());
        vCard.setLastName(userModel.getLast_name());
        vCard.setPhoneHome("mobile", userModel.getContacts());
        vCard.setAddressFieldHome("REGION", userModel.getAddresses());
        vCard.setField("URL", userModel.getImage_sm());

        try {
            vCardManager.saveVCard(vCard);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            condition = ((XMPPException.XMPPErrorException) e).getXMPPError().getCondition();

            if (condition == null) {
                condition = StanzaError.Condition.internal_server_error;
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        if (condition == null) {
            Logger.d("NO ERROR");
            service.onUpdateUserSuccess();
        } else {
            Logger.d("ERROR");
            service.onUpdateUserFailed(condition.toString());
        }

//        Logger.d(vCard.getAddressFieldHome("REGION"));
//        Logger.d(vCard.getField("URL"));
//        Logger.d(vCard.getPhoneHome("mobile"));

    }

    public void changePassword(String password) {
        AccountManager accountManager = AccountManager.getInstance(connection);
        try {
            accountManager.sensitiveOperationOverInsecureConnection(true);
            Logger.d(connection.getUser());
            Logger.d(userId + "   " + userPassword);
            accountManager.changePassword(password);
            service.onPasswordChanged();
        } catch (SmackException.NoResponseException e) {
            service.onPasswordChangeFailed(e.getMessage());
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            Logger.d(e.getMessage());
            service.onPasswordChangeFailed(e.getMessage());
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            Logger.d(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Signup to server
    public void Signup(SignupModel signupModel, String name) {
        StanzaError.Condition condition = null;
        boolean errors = false;
        String errorMessage = "";

        String mUsername = signupModel.getUsername();
        String mPassword = signupModel.getPassword();

        boolean isPasswordValid = signupModel.checkPassword();
        boolean areFieldsValid = signupModel.validateFields();


        if (!isPasswordValid) {
            errors = true;
            errorMessage = Constants.SIGNUP_ERR_INVALIDPASS;
        }

        if (!areFieldsValid) {
            errors = true;
            errorMessage = Constants.SIGNUP_ERR_FIELDERR;
        }

        if (errors) {
            service.onSignupFailed(errorMessage);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!connected && !isconnecting) connect();
            }
        }).start();

        try {
            final AccountManager accountManager = AccountManager.getInstance(connection);
            Logger.d(accountManager.supportsAccountCreation());
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(Localpart.from(mUsername), mPassword);

        } catch (XMPPException | SmackException e) {

            e.printStackTrace();
            Logger.d(e.getMessage());
            if (debug) Log.e(TAG, "Username: " + mUsername + ",Password: " + mPassword);

            if (e instanceof XMPPException.XMPPErrorException) {
                condition = ((XMPPException.XMPPErrorException) e).getXMPPError().getCondition();
            }

            if (condition == null) {
                condition = StanzaError.Condition.internal_server_error;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        if (condition == null) {

            Logger.d(connection.isConnected() + "   [[[[[[[");
            service.onSignupSuccess();
//            Logger.d("%%%%%%%%%%%%%%%%%%%%%%%%%%");
//            HashMap<String, String> data = new HashMap<>();
//            data.put("localuser", CredentialManager.getUserName());
//            data.put("localserver", Constants.XMPP_HOST);
//            data.put("user", Constants.EVALY_NUMBER);
//            data.put("server", Constants.XMPP_HOST);
//            data.put("nick", "Evaly");
//            data.put("subs", "both");
//            data.put("group", "evaly");
//
//            AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
//                @Override
//                public void onDataFetched(Response<JsonPrimitive> response) {
//                    if (response.code() == 200 || response.code() == 201) {
//                        addRosterByOther(name);
//                    } else {
//                    }
//                }
//
//                @Override
//                public void onFailed(int status) {
//                }
//            });


        } else {
            switch (condition) {
                case conflict:
                    errorMessage = Constants.SIGNUP_ERR_CONFLICT;
                    break;
                case internal_server_error:
                    errorMessage = Constants.SIGNUP_ERR_SERVER_ERR;
                    break;
                default:
                    errorMessage = condition.toString();
                    break;

            }

            service.onSignupFailed(errorMessage);
        }
    }

    private void addRosterByOther(String name) {
        HashMap<String, String> data = new HashMap<>();
        data.put("localuser", Constants.EVALY_NUMBER);
        data.put("localserver", Constants.XMPP_HOST);
        data.put("user", CredentialManager.getUserName());
        data.put("server", Constants.XMPP_HOST);
        data.put("nick", name);
        data.put("subs", "both");
        data.put("group", "evaly");
        AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
            @Override
            public void onDataFetched(Response<JsonPrimitive> response) {
                service.onSignupSuccess();
            }

            @Override
            public void onFailed(int status) {

            }
        });
    }

    //Login to server
    public void login() {
        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!connected && !isconnecting) connect();
                }
            }).start();

            Log.e(TAG, "User " + userId + userPassword);

            if (userPassword == null || userPassword.equals("")) {
                service.onLoginFailed("password empty");
                return;
            } else if (userId == null || userId.equals("")) {
                service.onLoginFailed("username empty");
                return;
            }
            connection.login(userId, userPassword);

            Log.e(TAG, "Yey! We're logged in to the Xmpp server!");

            service.onLoggedIn();
        } catch (XMPPException | SmackException | IOException e) {
            Logger.d(e.getMessage());
            service.onLoginFailed(e.getMessage());
            if (debug) e.printStackTrace();
        } catch (InterruptedException e) {
            Logger.d(e.getMessage());
            e.printStackTrace();
        }
    }

    //Update which chat instance is running currently. Set it to true.
    public void updateChatEntryMap(String key) {
        for (Map.Entry<String, Boolean> entry : chat_created_for.entrySet()) {
            entry.setValue(entry.getKey().equals(key));
        }
    }

    // Send Message (you can call this method from other activity)
    // This method will try to create connection (if not established already),
    // will open up a TCP connection to another user (usually a roaster in jabber language),
    // will throw exception if there is an error
    public void sendMessage(ChatItem chatMessage) throws SmackException {
        String body = gson.toJson(chatMessage);

//        Stanza stanza = new Stanza() {
//            @Override
//            public String toString() {
//                return null;
//            }
//
//            @Override
//            public CharSequence toXML(String enclosingNamespace) {
//                return "<message" +
//                        "    to='"+chatMessage.getReceiver()+"'" +
//                        "    from='"+chatMessage.getSender()+"'" +
//                        "    type='chat'" +
//                        "    xml:lang='en'>" +
//                        "  <body>"+body+"</body>" +
//                        "</message>";
//            }
//        };
//        try {
//            connection.sendStanza(stanza);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
//            connection.sendStanzaWithResponseCallback(stanza, new IQReplyFilter(), new StanzaListener() {
//                @Override
//                public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
//                    Logger.d(packet.toString());
//                    Logger.d(packet.toXML("message"));
//                }
//            });
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        if (chat_created_for.get(chatMessage.getReceiver()) == null)
            chat_created_for.put(chatMessage.getReceiver().toString(), false);

        if (!chat_created_for.get(chatMessage.getReceiver())) {
            Log.e(TAG, "jusabtsend:" + chatMessage.getReceiver());

//            mChat = ChatManager.getInstanceFor(connection).createChat(chatMessage.getReceiver(), mMessageListener);
            try {
                mChat = ChatManager.getInstanceFor(connection).chatWith(JidCreate.entityBareFrom(chatMessage.getReceiver()));

            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }

            updateChatEntryMap(chatMessage.getReceiver().toString());
            chat_created_for.put(chatMessage.getReceiver().toString(), true);
        }

        final Message message = new Message();
        message.setBody(body);
        message.setSubject(System.currentTimeMillis() + "");
        message.setType(Message.Type.chat);

        Logger.d(new Gson().toJson(message));
        Logger.e(connection.isConnected() + "     " + connection.isAuthenticated());
        try {
            mChat.send(message);
        } catch (SmackException.NotConnectedException e) {
            if (debug) Log.e(TAG, "msg Not sent!-Not Connected!");
            throw new SmackException(e);

        } catch (Exception e) {
            e.printStackTrace();
            if (debug) Log.e(TAG, "msg Not sent!" + e.getMessage());
        }

    }

    public void sendGroupMessage(ChatItem chatMessage, String groupName) throws SmackException {
        String body = gson.toJson(chatMessage);

        if (chat_created_for.get(chatMessage.getReceiver()) == null)
            chat_created_for.put(chatMessage.getReceiver().toString(), false);

        if (!chat_created_for.get(chatMessage.getReceiver())) {
            Log.e(TAG, "jusabtsend:" + chatMessage.getReceiver());

//            mChat = ChatManager.getInstanceFor(connection).createChat(chatMessage.getReceiver(), mMessageListener);
            try {
                mChat = ChatManager.getInstanceFor(connection).chatWith(JidCreate.entityBareFrom(chatMessage.getReceiver()));

            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }

            updateChatEntryMap(chatMessage.getReceiver().toString());
            chat_created_for.put(chatMessage.getReceiver().toString(), true);
        }

        final Message message = new Message();
        message.setBody(body);
        message.setStanzaId(CredentialManager.getUserName() + System.currentTimeMillis());
        message.setType(Message.Type.groupchat);

        String DomainName = "conference." + Constants.XMPP_HOST;
        // Create a MultiUserChat using a Connection for a room
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(groupName + "@"
                    + DomainName);

// Create a MultiUserChat using an XMPPConnection for a room
            MultiUserChat muc = manager.getMultiUserChat(jid);
            muc.addMessageListener(mMessageListener);

// Prepare a list of owners of the new room
            Set<Jid> owners = JidUtil.jidSetFrom(new String[]{});

//            Logger.d(owners);
// Create the room

            Resourcepart nickname = Resourcepart.from(groupName);

            muc.createOrJoin(nickname);
            roster.createEntry(jid.asBareJid(), nickname.toString(), null);


            Logger.d(muc.getNickname());
            if (connection.isAuthenticated()) {
                muc.sendMessage(message);
            }
        } catch (SmackException.NotConnectedException e) {
            if (debug) Log.e(TAG, "msg Not sent!-Not Connected!");
            throw new SmackException(e);

        } catch (Exception e) {
            e.printStackTrace();
            if (debug) Log.e(TAG, "msg Not sent!" + e.getMessage());
        }

    }


    // Our own connection Listener
    // Here, you can handle several connection events in our own way
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(XMPPConnection connection) {
            Logger.d("CONNECT");
            Logger.d(userId + "    " + userPassword);
            Log.e(TAG, "Connected!");
            Log.e(TAG, autoLogin + "");
            service.onConnected();
            connected = true;

            if (userPassword != null && !userPassword.trim().equals("")) {
                if (!connection.isAuthenticated() && autoLogin) {
                    login();
                }
            }
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
//            Logger.d("AUTHENTICATED");
//            Logger.d(userId + "    " + userPassword + "        " + connection.getUser().asEntityBareJidString() + "    ");
            chatInstanceIterator(chat_created_for);
            loggedin = true;


            ChatManager.getInstanceFor(connection).addIncomingListener(mIncomingChatManagerListener);
            ChatManager.getInstanceFor(connection).addOutgoingListener(mOutGoingChatManagerListener);
            ChatStateManager.getInstance(connection).addChatStateListener(mChatStateManagerListener);

            mVcard = getCurrentUserDetails();


            if (!isFirstTime) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        roasterList = getAllRoaster();
                        //                      Logger.d(roasterList.size() + "    ()()()()()()(()");
                        List<RosterTable> list = new ArrayList<>();
                        for (RoasterModel model : roasterList) {
                            RosterTable table = new RosterTable();
                            table.id = model.getRoasterEntryUser().asUnescapedString();
                            table.rosterName = model.getName();
                            table.status = model.getStatus();
                            list.add(table);
                        }

                        updateRoster(list);

//                        AsyncTask.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                AppController.database.taskDao().addAllRoster(list);
//                            }
//                        });
                    }
                }).start();
                isFirstTime = true;
            }
//


            //Wait for 500ms before showing we are authenticated
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            if (debug) Log.d(TAG, "Yay!! We are now authenticated!!");

            service.onAuthenticated();

            Roster roster = Roster.getInstanceFor(connection);


            Logger.d(roster.isRosterLoadedAtLogin());
            if (!roster.isLoaded())
                try {
                    roster.reloadAndWait();
                } catch (SmackException.NotLoggedInException e) {
                    Logger.d("NotLoggedInException");
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    Logger.d("NotConnectedException");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


//            Collection<RosterEntry> entries = roster.getEntries();
//            Logger.d(entries.size() + "      =====");
//            for (RosterEntry entry : entries) {
//                Logger.d(entry);
//            }
        }

        @Override
        public void connectionClosed() {
            Logger.d("CONNECTION_CLOSED");
            service.onConnectionClosed();

            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(service, "ConnectionCLosed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });

            if (debug) Log.d(TAG, "ConnectionCLosed!");

            connected = false;
            //    chat_created = false;
            chatInstanceIterator(chat_created_for);
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Logger.d("CONNECTION_ERROR   " + e.getMessage());
            service.onConnectionClosed();

            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(service, "ConnectionClosedOn Error!!",
                                Toast.LENGTH_SHORT).show();

                    }
                });

            if (debug) Log.d(TAG, "ConnectionClosedOn Error!");

            connected = false;
            chatInstanceIterator(chat_created_for);
            loggedin = false;
        }

//        //Our connection has closed, due to error. Still, it is same thing as above. Reset everything
//        @Override
//        public void connectionClosedOnError(Exception arg0) {
//
//            service.onConnectionClosed();
//
//            if (isToasted)
//
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(service, "ConnectionClosedOn Error!!",
//                                Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//            if( debug ) Log.d(TAG, "ConnectionClosedOn Error!");
//
//            connected = false;
//            chatInstanceIterator(chat_created_for);
//            loggedin = false;
//        }
//
//        @Override
//        public void reconnectingIn(int arg0) {
//
//            service.onReConnection();
//            if( debug ) Log.d(TAG, "Reconnectingin " + arg0);
//            loggedin = false;
//        }
//
//        // Our reconnection attemp failed. Reset everything. Basically, we reset whenever our connection failed,
//        // no matter whatever the cause is
//        @Override
//        public void reconnectionFailed(Exception arg0) {
//
//            service.onReConnectionError();
//
//            if( debug ) Log.d(TAG, "ReconnectionFailed!");
//
//            //Reset the variables
//            connected = false;
//            chatInstanceIterator(chat_created_for);
//            loggedin = false;
//        }
//
//        //Below two methods are quite useful. These handles a successfull connection attempt.
//        @Override
//        public void reconnectionSuccessful() {
//
//            service.onReConnected();
//
//            if( debug ) Log.d(TAG, "ReconnectionSuccessful");
//
//            //We are only connected, not authenticated yet. See the next method
//            connected = true;
//            chatInstanceIterator(chat_created_for);
//            loggedin = false;
//        }
//
//        //This is main method, we authentication stuff happens
//        @Override
//        public void authenticated(XMPPConnection connectionNew, boolean resumed) {
//
//            chatInstanceIterator(chat_created_for);
//            loggedin = true;
//
//            ChatManager.getInstanceFor(connection).addChatListener(mChatManagerListener);
//
//            //Wait for 500ms before showing we are authenticated
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
//
//            if( debug ) Log.d(TAG,"Yay!! We are now authenticated!!");
//
//            service.onAuthenticated();
//        }
    }

    public void changePresence() {
        Presence presence = new Presence(Presence.Type.unavailable);
        try {
            connection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateRoster(List<RosterTable> roasterList) {
        List<RosterTable> list = new ArrayList<>();
        for (int i = 0; i < roasterList.size(); i++) {
            VCard vCard = null;
            RosterTable table = roasterList.get(i);
            try {
                vCard = getUserDetails(JidCreate.bareFrom(table.id).asEntityBareJidIfPossible());
                if (vCard != null) {
                    table.name = vCard.getFirstName() + " " + vCard.getLastName();
                    table.nick_name = vCard.getNickName();
                    table.imageUrl = vCard.getField("URL");
                    list.add(table);
                    Logger.d(list.size() + "    " + roasterList.size());
                }

                if (list.size() == roasterList.size()) {
                    updateMessage(list);
                }

            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
//                updateMessage(list);

        }


    }


    private void updateMessage(List<RosterTable> roasterList) {
        List<RosterTable> list = roasterList;
        int count = 0;

//        Logger.d(new Gson().toJson(list));
        for (int i = 0; i < list.size(); i++) {
            try {
                ChatItem chatItem = getLastMessage(JidCreate.bareFrom(list.get(i).id));

                if (chatItem != null) {
                    list.get(i).lastMessage = new Gson().toJson(chatItem);
                    list.get(i).time = chatItem.getLognTime();
                }
                try {
                    Stanza stanza = getUnreadMessage(JidCreate.bareFrom(list.get(i).id), mVcard.getFrom());
//                       Logger.d(new Gson().toJson(stanza));
                    XmlToJson xmlToJson = new XmlToJson.Builder(stanza.toXML("unseen-messages").toString())
                            .forceIntegerForPath("iq/unseen-messages/amount")
                            .build();
//                       Logger.d(xmlToJson);
                    JSONObject jsonObject = new JSONObject(xmlToJson.toString());
                    String value = jsonObject.getString("content").replace("<unseen-messages xmlns='urn:xmpp:mark_as_read' ", "").replaceAll("\\D", "");
//                       Logger.d(value);
                    list.get(i).unreadCount = Integer.valueOf(value);

//                       if (Integer.parseInt(value)>0){
//                           holder.tvUnreadCount.setText(value);
//                           holder.tvUnreadCount.setVisibility(View.VISIBLE);
//                       }else {
//                           holder.tvUnreadCount.setVisibility(View.GONE);
//                       }
//                       unreadCount.add(Integer.parseInt(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count = count + 1;
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        }

        if (list.size() == 0) {
            AppController.allDataLoaded = true;
        }
        if (count == list.size()) {
            Logger.d(count + "    ==========");

            for (RosterTable table : list) {
                // Logger.d(Constants.EVALY_NUMBER+"      "+table.id);
                if (!table.id.contains(Constants.EVALY_NUMBER)) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppController.database.taskDao().addRoster(table);
                        }
                    });
                }
            }
            AppController.allDataLoaded = true;
        }
    }

    private int getListPosition(ChatItem chatItem) {
        int pos = -1;
        for (int i = 0; i < roasterList.size(); i++) {
//            Logger.d(chatItem.getSender() + "      " + roasterList.get(i).getRoasterEntryUser());
            if (roasterList.get(i).getRoasterEntryUser().asUnescapedString().equalsIgnoreCase(chatItem.getSender())) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    //Your own Chat Manager. We attach the message events here
    private class MyChatStateManagerListener implements ChatStateListener {

        @Override
        public void stateChanged(Chat chat, ChatState state, Message message) {
            Logger.d(new Gson().toJson(message));
            Logger.d(chat.getXmppAddressOfChatPartner());
            if (debug)
                Log.e(TAG, "Chat State local: " + chat.getXmppAddressOfChatPartner() + ": " + state.name());

            if (state.toString().equals(ChatState.composing.toString())) {
                if (debug) Logger.e(TAG, "User is typing");
            } else if (state.toString().equals(ChatState.paused.toString())) {
                if (debug) Logger.e(TAG, "User is paused");
            } else if (state.toString().equals(ChatState.active.toString())) {
                if (debug) Logger.e(TAG, "User is active");
            } else if (state.toString().equals(ChatState.gone.toString())) {
                if (debug) Logger.e(TAG, "User is away");
            } else if (state.toString().equals(ChatState.inactive.toString())) {
                if (debug) Logger.e(TAG, "User is inactive");
            } else {
                if (debug) Logger.e(TAG, "User is nothing");
            }

            service.onChatStateChange(new ChatStateModel(chat.getXmppAddressOfChatPartner().asUnescapedString(), state));
        }
    }

    private class MyIncomingChatManagerListener implements IncomingChatMessageListener, ChatStateListener {

        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
//            chat.addMessageListener((ChatMessageListener) mMessageListener);
            Logger.d(message.getBody());
            Logger.d(connection.isConnected());
            ChatItem chatItem = new Gson().fromJson(message.getBody(), ChatItem.class);
            Logger.d(new Gson().toJson(chatItem));

            if (!from.asUnescapedString().contains(CredentialManager.getUserName())) {
                int pos = getListPosition(chatItem);
                if (pos == -1) {
//                    RosterTable table = new RosterTable();
//                    try {
//                        VCard vCard = getUserDetails(JidCreate.entityBareFrom(chatItem.getSender()));
//                        table.id = vCard.getFrom().asUnescapedString();
//                        table.nick_name = vCard.getNickName();
//                        table.name = vCard.getFirstName() + " " + vCard.getLastName();
//                        table.imageUrl = vCard.getField("URL");
//                        table.lastMessage = new Gson().toJson(chatItem);
//                        table.time = chatItem.getLognTime();
//                        table.status = 1;
//                        table.rosterName = "";
//                        table.unreadCount = 1;
//                        if (!table.id.contains(Constants.EVALY_NUMBER)) {
//                            AsyncTask.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    AppController.database.taskDao().addRoster(table);
//                                }
//                            });
//                        }
//                    } catch (XmppStringprepException e) {
//                        e.printStackTrace();
//                    }
                } else {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppController.database.taskDao().updateLastMessage(new Gson().toJson(chatItem), chatItem.getLognTime(), from.asUnescapedString(), 1);
                        }
                    });
                }
            }
            service.onNewMessage(new Gson().toJson(chatItem));
        }

        @Override
        public void stateChanged(Chat chat, ChatState state, Message message) {
            Logger.d(new Gson().toJson(message));
            Logger.d(chat.getXmppAddressOfChatPartner());
            if (debug)
                Log.e(TAG, "Chat State local: " + chat.getXmppAddressOfChatPartner() + ": " + state.name());

            if (state.toString().equals(ChatState.composing.toString())) {
                if (debug) Logger.e(TAG, "User is typing");
            } else if (state.toString().equals(ChatState.paused.toString())) {
                if (debug) Logger.e(TAG, "User is paused");
            } else if (state.toString().equals(ChatState.active.toString())) {
                if (debug) Logger.e(TAG, "User is active");
            } else if (state.toString().equals(ChatState.gone.toString())) {
                if (debug) Logger.e(TAG, "User is away");
            } else if (state.toString().equals(ChatState.inactive.toString())) {
                if (debug) Logger.e(TAG, "User is inactive");
            } else {
                if (debug) Logger.e(TAG, "User is nothing");
            }

            service.onChatStateChange(new ChatStateModel(chat.getXmppAddressOfChatPartner().asUnescapedString(), state));
        }
    }

    private class MyOutGoingChatManagerListener implements OutgoingChatMessageListener {


        @Override
        public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
//            Logger.d(message.getBody());
            ChatItem chatItem = new Gson().fromJson(message.getBody(), ChatItem.class);
//            Logger.d(new Gson().toJson(chatItem));
            service.onNewMessageSent(new Gson().toJson(chatItem));
        }

    }

    //Now our message Listener. We can now see how to parse XMPP messages received from other users or server.
    private class MyMessageListener implements MessageListener, ChatStateListener {

        private String TAG = getClass().getSimpleName();

        public MyMessageListener() {
        }


        // This is the method where we need to process the messages and parse it.

        private void addMessage(final ChatItem chatMessage) {
            service.onNewMessage(new Gson().toJson(chatMessage));
        }

        @Override
        public void stateChanged(Chat chat, ChatState state, Message message) {
            if (debug)
                Log.e(TAG, "Chat State local: " + chat.getXmppAddressOfChatPartner() + ": " + state.name());

            if (state.toString().equals(ChatState.composing.toString())) {
                if (debug) Log.e(TAG, "User is typing");
            } else if (state.toString().equals(ChatState.paused.toString())) {
                if (debug) Log.e(TAG, "User is paused");
            } else if (state.toString().equals(ChatState.active.toString())) {
                if (debug) Log.e(TAG, "User is active");
            } else if (state.toString().equals(ChatState.gone.toString())) {
                if (debug) Log.e(TAG, "User is away");
            } else if (state.toString().equals(ChatState.inactive.toString())) {
                if (debug) Log.e(TAG, "User is inactive");
            } else {
                if (debug) Log.e(TAG, "User is nothing");
            }

            service.onChatStateChange(new ChatStateModel(chat.getXmppAddressOfChatPartner().asUnescapedString(), state));
        }

        @Override
        public void processMessage(Message message) {
            Logger.d("New Message received");
            if (message.getType() == Message.Type.chat && message.getBody() != null) {

                if (debug) Log.d(TAG, "New Message received: " + message.getBody());

                //Make the newly received message as our chatitem that our listview can process
                ChatItem chatMessage = null;

                // Usually, you will code your XMPP implementation such that the sender sends a message
                // in a valid JSON representation. While this is a good practise, you may also not want to
                // miss any messages received from other sources (such as pidgin). They may not know your message
                // structure and message can be in raw string format. Thus, you can use this check if message was
                // from someone with your JSON structure, or is it from someone else. If latter is the case, you can
                // just log the message for debugging purpose, or ignore it ofcourse.

//                if(Utils.isJSONValid(message.getBody()))
//                    chatMessage = gson.fromJson(message.getBody(), ChatItem.class);
//                else{
//                    String currentDate = Utils.getCurrentDate();
//                    String currentTime = Utils.getCurrentTime();
//
//                    // Try to represent it in our message format.
//                    chatMessage = new ChatItem(message.getBody(), currentDate, currentTime, message.getFrom(), getCurrentUserDetails(),false);
//                }

                //Now our message is in our representation, we can send it to our list to add newly received message
                addMessage(chatMessage);
            }
        }
    }

    private class MyStanzaListener implements StanzaListener {

        @Override
        public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                final Jid fromJID = presence.getFrom();

                /* We got a request (subscription req). We need to send back "subscribe/subscribed"or "unsubscribe" based on
                 * user choice. We will show user asking him to "accept" or "reject".
                 */
                //@see: http://xmpp.org/rfcs/rfc6121.html#sub-request
                if (presence.getType() == Presence.Type.subscribe) {

                    if (debug) Log.e(TAG, "subscription request from - " + fromJID);
//                    confirmSubscription(fromJID.asBareJid(), true);
                    service.onRequestSubscribe(fromJID);

                }
            }
        }
    }

    private class MyRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<Jid> addresses) {
            if (debug) Log.e(TAG, "Entry added! ");

            Iterator addressIter = addresses.iterator();
            while (addressIter.hasNext()) {
                if (debug) Log.e(TAG, "Entry added: " + addressIter.next());
            }
        }

        @Override
        public void entriesUpdated(Collection<Jid> addresses) {
            if (debug) Log.e(TAG, "Entry updated! ");

            Iterator addressIter = addresses.iterator();
            while (addressIter.hasNext()) {
                if (debug) Log.e(TAG, "Entry updated: " + addressIter.next());
            }
        }

        @Override
        public void entriesDeleted(Collection<Jid> addresses) {

            if (debug) Log.e(TAG, "Entry deleted! ");

            Iterator addressIter = addresses.iterator();
            while (addressIter.hasNext()) {
                if (debug) Log.e(TAG, "Entry deleted: " + addressIter.next());
            }
        }

        /* This is a good place to know whenever a user went online/offline. Use this method
         * to call any of your singleton, pub-subs etc to let know your UI to change user presence
         */
        @Override
        public void presenceChanged(Presence presence) {
            if (debug)
                Log.e(TAG, "Presence changed: " + presence.getFrom() + " " + presence.getMode());
//            Logger.d(presence.getStatus() + "   " + presence.getMode() + "    " + presence.getType() + "    " + presence.getFrom());
            Presence.Mode mode = presence.getMode();
            int status = retreiveState(mode, presence.isAvailable());
            long lastActivity = 0;
            try {
                if (connection != null && isLoggedin()) {
                    lastActivity = LastActivityManager.getInstanceFor(connection).getLastActivity(presence.getFrom()).lastActivity;
                }
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PresenceModel presenceModel = new PresenceModel(presence.getFrom().asBareJid().toString(), presence.getStatus(), mode, status, lastActivity);
//            Logger.d(presenceModel.getUser());
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    AppController.database.taskDao().updateStatus(presenceModel.getUserStatus(), presenceModel.getUser());
//                }
//            });
            service.onPresenceChange(presenceModel);
        }
    }

    /*
     * Whenever a user sends a subscription request, you have to send him back two subscription requests:
     * 1. Presence.Type.subscribe
     * 2. Presence.Type.subscribed (you are now subscribed to user, at this point user can see your presence,
     *    but you can not see his presence)
     *
     */
    public void confirmSubscription(BareJid fromJID, boolean shouldSubscribe) {
        final RosterEntry newEntry = roster.getEntry(fromJID);
        //Prepare "subscribe" precense
        Presence subscribe = new Presence(Presence.Type.subscribe);
        subscribe.setTo(fromJID);

        //Prepare "subscribed" precense
        Presence subscribed = new Presence(Presence.Type.subscribed);
        subscribed.setTo(fromJID);

        //Prepare Unsubscribe
        Presence unsubscribe = new Presence(Presence.Type.unsubscribe);
        unsubscribe.setTo(fromJID);

//        Logger.d("{{{{{{{{{{{{{{{{");
        //Send both (or only subscribed, if user has already sent request)
        try {
            if (shouldSubscribe) {
                if (newEntry == null || newEntry.getType() == RosterPacket.ItemType.from) {
                    Logger.d("+++++++++++++");
                    connection.sendStanza(subscribed);
                    connection.sendStanza(subscribe);
                } else {
                    Logger.d("-----------");
                    connection.sendStanza(subscribed);
                }
            } else {
                Logger.d("UNSUBSCRIBE");
                connection.sendStanza(unsubscribe);
            }
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (shouldSubscribe){
            if (newEntry != null) {
                RosterTable table = new RosterTable();
                table.id = newEntry.getJid().asUnescapedString();
                table.rosterName = newEntry.getName();

                try {
                    VCard vCard = getUserDetails(newEntry.getJid().asEntityBareJidIfPossible());

                    table.name = vCard.getFirstName() + " " + vCard.getLastName();
                    table.imageUrl = vCard.getField("URL");
                    ChatItem chatItem = getLastMessage(newEntry.getJid());
                    table.lastMessage = new Gson().toJson(chatItem);
                    table.time = System.currentTimeMillis();
                    table.status = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int unreadCount = 0;
                try {
                    Stanza stanza = getUnreadMessage(newEntry.getJid(), mVcard.getFrom());
//                       Logger.d(new Gson().toJson(stanza));
                    XmlToJson xmlToJson = new XmlToJson.Builder(stanza.toXML("unseen_messages").toString())
                            .forceIntegerForPath("iq/unseen_messages/amount")
                            .build();
//                       Logger.d(xmlToJson);
                    JSONObject jsonObject = new JSONObject(xmlToJson.toString());
                    String value = jsonObject.getString("content").replace("<unseen-messages xmlns='urn:xmpp:mark_as_read' ", "").replaceAll("\\D", "");
//                       Logger.d(value);
                    unreadCount = Integer.valueOf(value);
                    table.unreadCount = unreadCount;

                    if (!table.id.contains(Constants.EVALY_NUMBER)) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
//                            Logger.d(new Gson().toJson(table));
                                AppController.database.taskDao().addRoster(table);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public List<ChatItem> getChatHistoryWithJID(Jid jid, int maxResults, boolean isPaginated) {
        List<ChatItem> chatMessageList = new ArrayList<>();

//        Logger.d(jid);

        try {
            mamManager = MamManager.getInstanceFor(connection);
            Logger.d(mamManager.isSupported());

//            mamManager.queryMostRecentPage(jid, maxResults).pagePrevious(maxResults);


            mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .setResultPageSize(maxResults)
                    .limitResultsToJid(jid)
                    .queryLastPage()
                    .build();

            mamQueryResult = mamManager.queryArchive(mamQueryArgs);
//            mamQueryResult.pageNext(maxResults);
//            List<Message> forwardedMessages;
//            if (isPaginated && !mamQueryResult.isComplete()) {
//                Logger.d(mamManager);
//                Logger.d("PAGINATED");
//                forwardedMessages = mamQueryResult.pagePrevious(maxResults);
//            } else {
//                Logger.d("NON PAGINATION");
//                forwardedMessages = mamQueryResult.getMessages();
//            }
            List<Message> forwardedMessages = mamQueryResult.getMessages();
//            Logger.d("||||||||||||||||||||");

            Iterator<Message> forwardedIterator = forwardedMessages.iterator();
//            Logger.d(forwardedMessages.size());
//            Logger.d(new Gson().toJson(forwardedMessages));

            while (forwardedIterator.hasNext()) {
                Message message = forwardedIterator.next();
//                Logger.d(message);

//                Logger.d(message.toXML("stanza-id"));
//                Logger.d(message.getBody());
//                Stanza stanza = forwarded.getBody();
//                if (stanza instanceof Message) {
//                    String messageId = stanza.getStanzaId();
//                    mMessageListener.processMessage((Message) stanza);
////                    xmppTcpConnection.processMessage((Message) stanza);
//                }

                ChatItem chatItem = new Gson().fromJson(message.getBody(), ChatItem.class);
                JSONObject jsonObject = new JSONObject(new Gson().toJson(message));

                JSONObject ob1 = jsonObject.getJSONObject("packetExtensions").getJSONObject("map");
                String json = ob1.toString();
//                Logger.d(json);
                String json1 = "{\"" + json.substring(json.indexOf("urn:xmpp:sid:0"));
                JSONObject object = new JSONObject(json1);
//                Logger.d(json1);
                JSONArray jsonArray = object.getJSONArray("urn:xmpp:sid:0");
                String id = jsonArray.getJSONObject(0).getString("id");
//                Logger.d(id+" ==========");
                chatItem.setMessageId(id);
                chatMessageList.add(chatItem);
            }
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Logger.d(new Gson().toJson(chatMessageList));
        return chatMessageList;
    }

    public List<ChatItem> getChatHistoryWithPagination(Jid jid, int maxResults, String uid) {
        List<ChatItem> chatMessageList = new ArrayList<>();

//        Logger.d(uid);

//        IQ iq = new IQ("query") {
//            @Override
//            protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
//                xml.rightAngleBracket();
//                xml.append("<iq type='set' id='"+CredentialManager.getUserName()+System.currentTimeMillis()+"'>" +
//                        "  <query xmlns='urn:xmpp:mam:2'>" +
//                        "    <x xmlns='jabber:x:data' type='submit'>" +
//                        "      <field var='FORM_TYPE' type='hidden'>" +
//                        "        <value>urn:xmpp:mam:2</value>" +
//                        "      </field>" +
//                        "     <field var='with'>" +
//                        "        <value>"+jid+"</value>" +
//                        "      </field>" +
//                        "    </x>" +
//                        "    <set xmlns='http://jabber.org/protocol/rsm'>" +
//                        "      <max>"+maxResults+"</max>" +
//                        "      <before>"+uid+"</before>" +
//                        "    </set>" +
//                        "  </query>" +
//                        "</iq>");
//                Logger.d(xml);
//                return xml;
//            }
//
//
////            "      <field var='start'>" +
////                    "        <value>"+uid+"</value>" +
////                    "      </field>" +
//        };

//        Logger.d(new Gson().toJson(iq));

//        connection.sendIqRequestAsync(IQ.createResultIQ(iq));


        try {
//            MamManager mamManager = MamManager.getInstanceFor(connection);
//            Logger.d(mamManager.isSupported());
////            mamManager.queryMostRecentPage(jid, maxResults).pageNext(maxResults);
//
//
            mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .beforeUid(uid)
                    .setResultPageSize(maxResults)
                    .limitResultsToJid(jid)
                    .build();
//
            mamQueryResult = mamManager.queryArchive(mamQueryArgs);
//            mamQueryResult.pagePrevious(maxResults);
//            mamQueryResult.pageNext(maxResults);
//            Logger.d(mamQueryResult.getMessageCount());


            Logger.d(mamQueryResult.getPage().getMessages().size());
            if (mamQueryResult.getPage().getMessages().size() != 0) {
                List<Message> forwardedMessages = mamQueryResult.pagePrevious(maxResults);

                Iterator<Message> forwardedIterator = forwardedMessages.iterator();
//            Logger.d(forwardedMessages.size());
//            Logger.d(new Gson().toJson(forwardedMessages));

                while (forwardedIterator.hasNext()) {
                    Message message = forwardedIterator.next();

//                Logger.d(message.toXML("stanza-id"));
//                Logger.d(message.getBody());
//                Stanza stanza = forwarded.getBody();
//                if (stanza instanceof Message) {
//                    String messageId = stanza.getStanzaId();
//                    mMessageListener.processMessage((Message) stanza);
////                    xmppTcpConnection.processMessage((Message) stanza);
//                }

                    ChatItem chatItem = new Gson().fromJson(message.getBody(), ChatItem.class);
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(message));

                    JSONObject ob1 = jsonObject.getJSONObject("packetExtensions").getJSONObject("map");
                    String json = ob1.toString();
//                Logger.d(json);
                    String json1 = "{\"" + json.substring(json.indexOf("urn:xmpp:sid:0"));
                    JSONObject object = new JSONObject(json1);
//                Logger.d(json1);
                    JSONArray jsonArray = object.getJSONArray("urn:xmpp:sid:0");
                    String id = jsonArray.getJSONObject(0).getString("id");
//                Logger.d(id+" ==========");
                    chatItem.setMessageId(id);
                    chatMessageList.add(chatItem);
                }
            }

        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Logger.d(new Gson().toJson(chatMessageList));
        return chatMessageList;
    }


    //Chat State events
    public void updateChatStatus(String receiver, ChatState chatState) {
        Logger.d(receiver);
        if (chat_created_for.get(receiver) == null)
            chat_created_for.put(receiver, false);

//        if (!chat_created_for.get(receiver)) {
//            mChat = ChatManager.getInstanceFor(connection).createChat(
//                    receiver,
//                    mMessageListener);
//
//            updateChatEntryMap(receiver);
//            chat_created_for.put(receiver,true);
//        }

        try {
            mChat = ChatManager.getInstanceFor(connection).chatWith(JidCreate.entityBareFrom(receiver));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }


        try {
            if (connection.isAuthenticated()) {
                Logger.d(chatState);
                Logger.d(mChat.getXmppAddressOfChatPartner());
                if (mChat == null) {
                    Logger.d("CHAT_NULL");
                }
                ChatStateManager.getInstance(connection).setCurrentState(chatState, mChat);
            }
        } catch (SmackException.NotConnectedException e) {
            if (debug) Log.e(TAG, "status Not sent!-Not Connected!");

        } catch (Exception e) {
            e.printStackTrace();
            if (debug) Log.e(TAG, "status Not sent!" + e.getMessage());
        }
    }
}
