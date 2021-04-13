package bd.com.evaly.evalyshop.ui.notification;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentNotificationBinding;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.notification.adapter.NotificationAdapter;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationFragment extends Fragment implements NotificationAdapter.ClickListener {

    @Inject
    ApiRepository apiRepository;

    @Inject
    PreferenceRepository preferenceRepository;

    private NotificationAdapter adapter;
    private ArrayList<NotificationItem> notifications;
    private FragmentNotificationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications, getContext());
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);
        getNotifications();
        setUpToolbar();
    }

    private void setUpToolbar() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        binding.toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            markAsRead();
            return false;
        });
    }

    public void getNotifications() {

        apiRepository.getNotification(preferenceRepository.getToken(), 1, new ResponseListener<CommonResultResponse<List<NotificationItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<NotificationItem>> response, int statusCode) {
                if (response.getCount() == 0) {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    notifications.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void markAsRead() {

        apiRepository.markNotificationAsRead(preferenceRepository.getToken(), new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                ToastUtils.show("Marked as read!");
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    @Override
    public void onClick(String url) {
        try {
            NavHostFragment.findNavController(this).navigate(Uri.parse(url));
        } catch (Exception e) {

        }
    }
}
