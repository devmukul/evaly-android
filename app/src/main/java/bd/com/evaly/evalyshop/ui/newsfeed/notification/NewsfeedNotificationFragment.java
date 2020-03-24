package bd.com.evaly.evalyshop.ui.newsfeed.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.NewsfeedNotificationFragmentBinding;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;
import bd.com.evaly.evalyshop.ui.newsfeed.notification.adapter.NewsfeedNotificationAdapter;

public class NewsfeedNotificationFragment extends Fragment {

    private NewsfeedNotificationViewModel viewModel;
    private NewsfeedNotificationFragmentBinding binding;
    private List<NotificationItem> itemList = new ArrayList<>();
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = false;
    private int currentPage = 1;


    public static NewsfeedNotificationFragment newInstance() {
        return new NewsfeedNotificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = NewsfeedNotificationFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });


        viewModel = new ViewModelProvider(this).get(NewsfeedNotificationViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);

        NewsfeedNotificationAdapter adapter = new NewsfeedNotificationAdapter(itemList, getContext());
        binding.recyclerView.setAdapter(adapter);

        itemList.clear();

        viewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), notifications -> {

            itemList.addAll(notifications);
            adapter.notifyItemRangeInserted(itemList.size() - notifications.size(), notifications.size());

            loading = false;

            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.progressContainer.setVisibility(View.GONE);

        });


        currentPage = 1;

        viewModel.getNotification(currentPage++);
        viewModel.markAsRead();


        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                            binding.progressBar.setVisibility(View.VISIBLE);
                            viewModel.getNotification(currentPage);
                            currentPage++;
                            loading = true;
                        }
                    }
                }
            }
        });
    }

}
