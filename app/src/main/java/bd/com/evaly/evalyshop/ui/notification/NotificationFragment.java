package bd.com.evaly.evalyshop.ui.notification;

import android.net.Uri;
import android.view.View;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentNotificationBinding;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.notification.adapter.NotificationAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationFragment extends BaseFragment<FragmentNotificationBinding, NotificationViewModel> implements NotificationAdapter.ClickListener {

    @Inject
    ApiRepository apiRepository;

    @Inject
    PreferenceRepository preferenceRepository;

    private NotificationAdapter adapter;
    private ArrayList<NotificationItem> notifications;

    public NotificationFragment() {
        super(NotificationViewModel.class, R.layout.fragment_notification);
    }

    @Override
    protected void initViews() {
        setUpToolbar();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), list -> {
            if (list.size() == 0) {
                binding.empty.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                notifications.clear();
                notifications.addAll(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications, getContext());
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setUpToolbar() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        binding.toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            viewModel.markAsRead();
            return false;
        });
    }

    @Override
    protected void clickListeners() {

    }

    @Override
    public void onClick(String url) {
        try {
            NavHostFragment.findNavController(this).navigate(Uri.parse(url));
        } catch (Exception e) {

        }
    }
}
