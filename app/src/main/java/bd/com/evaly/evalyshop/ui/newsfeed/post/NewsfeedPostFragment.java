package bd.com.evaly.evalyshop.ui.newsfeed.post;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.NewsfeedTabsFragmentBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.FeedShareModel;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.ui.chat.invite.ContactShareAdapter;
import bd.com.evaly.evalyshop.ui.chat.viewmodel.RoomWIthRxViewModel;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.newsfeed.createPost.CreatePostBottomSheet;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;


public class NewsfeedPostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private NewsfeedPostViewModel viewModel;
    private MainViewModel mainViewModel;
    private RoomWIthRxViewModel chatViewModel;
    private String type;
    private NewsfeedTabsFragmentBinding binding;
    private NewsfeedPostAdapter adapter;
    private List<NewsfeedPost> itemsList;
    private int currentPage = 1;
    private boolean isLoading = false;
    private SkeletonScreen skeletonScreen;
    private boolean isFirstLoad = true;

    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    XMPPEventReceiver xmppEventReceiver;

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onLoggedIn() {
            xmppHandler = AppController.getmService().xmpp;
        }

        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
        }

        public void onLoginFailed(String msg) {
            if (msg.contains("already logged in")) {

            }
        }

    };

    public static NewsfeedPostFragment newInstance(String type) {

        NewsfeedPostFragment fragment = new NewsfeedPostFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getString("type");
        }

        xmppEventReceiver = mChatApp.getEventReceiver();

    }

    private void startXmppService() {
        if (!XMPPService.isServiceRunning) {
            Intent intent = new Intent(getActivity(), XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
        } else {
            xmppHandler = AppController.getmService().xmpp;
            if (!xmppHandler.isConnected()) {
                xmppHandler.connect();
            } else {
                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                xmppHandler.login();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = NewsfeedTabsFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(NewsfeedPostViewModel.class);
        if (getActivity() != null)
            mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(RoomWIthRxViewModel.class);
        viewModel.setType(type);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.swipeLayout.setOnRefreshListener(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NewsfeedPostAdapter(getContext(), viewModel, getParentFragmentManager(), object -> {
            if (!CredentialManager.getToken().equals(""))
                shareWithContacts(object);
            else
                Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();

        });

        viewModel.getPostLiveData().observe(getViewLifecycleOwner(), newsfeedPosts -> {
            adapter.submitList(newsfeedPosts);
        });

        viewModel.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {

            if (isFirstLoad && networkState == NetworkState.LOADING_FIRST) {

                binding.shimmer.startShimmer();
                binding.shimmer.setVisibility(View.VISIBLE);

            } else if (networkState == NetworkState.LOADED) {

                currentPage++;
                binding.shimmer.startShimmer();
                binding.shimmer.setVisibility(View.GONE);
            }

            isFirstLoad = false;

            if (currentPage == 2 && adapter.getItemCount() == 0)
                binding.not.setVisibility(View.VISIBLE);
            else
                binding.not.setVisibility(View.GONE);

            //   adapter.setNetworkState(networkState);

//            if (networkState == NetworkState.LOADING_FIRST)
//                skeletonScreen = Skeleton.bind(binding.recyclerView)
//                        .adapter(adapter)
//                        .color(R.color.fff)
//                        .load(R.layout.item_skeleton_feed_post)
//                        .show();
//
//            if (networkState == NetworkState.LOADED)
//                if (skeletonScreen != null)
//                    skeletonScreen.hide();

        });


//        viewModel.getDataSourceLiveData().observe(getViewLifecycleOwner(), newsfeedPostDataSource -> newsfeedPostDataSource.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {
//            adapter.setNetworkState(networkState);
//
//            if (networkState == NetworkState.LOADING_FIRST)
//                skeletonScreen = Skeleton.bind(binding.recyclerView)
//                        .adapter(adapter)
//                        .color(R.color.fff)
//                        .load(R.layout.item_skeleton_feed_post)
//                        .show();
//
//            if (networkState == NetworkState.LOADED)
//                if (skeletonScreen != null)
//                    skeletonScreen.hide();
//        }));


        binding.recyclerView.setAdapter(adapter);

        viewModel.getEditPostLiveData().observe(getViewLifecycleOwner(), model -> {
            CreatePostBottomSheet createPostBottomSheet = CreatePostBottomSheet.newInstance(model);
            createPostBottomSheet.show(getChildFragmentManager(), model.getSlug());
        });

        mainViewModel.getUpdateNewsfeed().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                viewModel.refreshData();
        });
    }

    @Override
    public void onRefresh() {
        viewModel.refreshData();
        binding.swipeLayout.setRefreshing(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        xmppEventReceiver.setListener(xmppCustomEventListener);
    }

    private void shareWithContacts(NewsfeedPost model) {

        if (xmppHandler != null) {
            if (!xmppHandler.isLoggedin() || !xmppHandler.isConnected()) {
                startXmppService();
            }
        } else {
            startXmppService();
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.share_with_contact_view);

        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        assert bottomSheetInternal != null;
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(getActivity(), bottomSheetInternal);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                    bottomSheet.post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        bottomSheetDialog.setCanceledOnTouchOutside(false);
        RecyclerView rvContacts = bottomSheetDialog.findViewById(R.id.rvContacts);
        ImageView ivBack = bottomSheetDialog.findViewById(R.id.back);
        EditText etSearch = bottomSheetDialog.findViewById(R.id.etSearch);
        TextView tvCount = bottomSheetDialog.findViewById(R.id.tvCount);
        LinearLayout llSend = bottomSheetDialog.findViewById(R.id.llSend);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        assert rvContacts != null;
        rvContacts.setLayoutManager(layoutManager);
        chatViewModel.loadRosterList(CredentialManager.getUserName(), 1, 10000);
        chatViewModel.rosterList.observe(this, rosterTables -> {
            List<RosterTable> selectedRosterList = new ArrayList<>();
            ContactShareAdapter contactShareAdapter = new ContactShareAdapter(getActivity(), rosterTables, (object, status) -> {
                RosterTable table = (RosterTable) object;

                if (status && !selectedRosterList.contains(table)) {
                    selectedRosterList.add(table);
                } else {
                    selectedRosterList.remove(table);
                }

                assert tvCount != null;
                tvCount.setText(String.format(Locale.ENGLISH, "(%d) ", selectedRosterList.size()));
            });

            assert llSend != null;
            llSend.setOnClickListener(view -> {
                FeedShareModel feedShareModel = new FeedShareModel(model.getSlug(), model.getBody(), model.getAttachment(), model.getCommentsCount() + "", model.getFavoritesCount() + "", model.getAuthorFullName());

                if (xmppHandler.isLoggedin()) {
                    try {
                        for (RosterTable rosterTable : selectedRosterList) {
                            ChatItem chatItem = new ChatItem(new Gson().toJson(feedShareModel), CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), CredentialManager.getUserData().getImage_sm(), CredentialManager.getUserData().getFirst_name(), System.currentTimeMillis(), CredentialManager.getUserName() + "@" + Constants.XMPP_HOST, rosterTable.id, Constants.TYPE_FEED, true, "");
                            xmppHandler.sendMessage(chatItem);
                        }
                        for (int i = 0; i < rosterTables.size(); i++) {
                            rosterTables.get(i).isSelected = false;
                        }
                        contactShareAdapter.notifyDataSetChanged();
                        selectedRosterList.clear();
                        assert tvCount != null;
                        tvCount.setText(String.format(Locale.ENGLISH, "(%d) ", selectedRosterList.size()));
                        Toast.makeText(getActivity(), "Sent!", Toast.LENGTH_LONG).show();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    }
                }
            });

            rvContacts.setAdapter(contactShareAdapter);
            assert etSearch != null;
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    rvContacts.getRecycledViewPool().clear();
                    contactShareAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    rvContacts.getRecycledViewPool().clear();
                    contactShareAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        });

        assert ivBack != null;
        ivBack.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.submitList(null);
        binding.recyclerView.setAdapter(null);
        binding = null;
    }

}
