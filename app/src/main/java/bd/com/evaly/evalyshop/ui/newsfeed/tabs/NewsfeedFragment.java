package bd.com.evaly.evalyshop.ui.newsfeed.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.tabs.TabLayoutMediator;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.NewsfeedFragmentBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.newsfeed.adapters.NewsfeedPager;


public class NewsfeedFragment extends Fragment {

    private NewsfeedViewModel viewModel;
    private MainViewModel mainViewModel;
    private NewsfeedPager pager;
    private NewsfeedFragmentBinding binding;
    private NavController navController;

    public static NewsfeedFragment newInstance() {
        return new NewsfeedFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.newsfeed_fragment, container, false);

        navController = NavHostFragment.findNavController(this);

        viewModel = new ViewModelProvider(this).get(NewsfeedViewModel.class);

        if (getActivity() != null)
            mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getNotificationCountLiveData().observe(getViewLifecycleOwner(), integer -> updateHotCount(integer));

        viewModel.getLogoutLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                AppController.logout(getActivity());
        });


        binding.notificationHolder.setOnClickListener(v -> {
            if (CredentialManager.getToken().equals(""))
                Toast.makeText(getContext(), "You need to login first.", Toast.LENGTH_SHORT).show();
            else
                navController.navigate(R.id.notificationFragment);
        });

        pager = new NewsfeedPager(getChildFragmentManager(), getLifecycle());
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setAdapter(pager);

        binding.tabLayout.setSmoothScrollingEnabled(true);

        binding.homeSearch.setOnClickListener(v -> Toast.makeText(getContext(), "Search is coming soon!", Toast.LENGTH_SHORT).show());

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(pager.getTitle(position))
        ).attach();

        NewsfeedPostFragment publicFragment = NewsfeedPostFragment.newInstance("public");
        pager.addFragment(publicFragment, "PUBLIC");

        NewsfeedPostFragment announcementFragment = NewsfeedPostFragment.newInstance("announcement");
        pager.addFragment(announcementFragment, "ANNOUNCEMENT");

        NewsfeedPostFragment ceoFragment = NewsfeedPostFragment.newInstance("ceo");
        pager.addFragment(ceoFragment, "CEO");


        if (!CredentialManager.getToken().equals("")) {
            NewsfeedPostFragment myFragment = NewsfeedPostFragment.newInstance("my");
            pager.addFragment(myFragment, "MY POSTS");
        }

        pager.notifyDataSetChanged();

    }


    public void updateHotCount(final int new_hot_number) {
        if (binding.hotlistHot == null) return;

        if (new_hot_number == 0)
            binding.hotlistHot.setVisibility(View.INVISIBLE);
        else {
            binding.hotlistHot.setVisibility(View.VISIBLE);
            if (new_hot_number > 99)
                binding.hotlistHot.setText("99");
            else
                binding.hotlistHot.setText(Integer.toString(new_hot_number));
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (viewModel != null && !CredentialManager.getToken().equals(""))
            viewModel.updateNotificationCount();

    }
}
