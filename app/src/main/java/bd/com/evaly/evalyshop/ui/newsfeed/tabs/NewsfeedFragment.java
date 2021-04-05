package bd.com.evaly.evalyshop.ui.newsfeed.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import bd.com.evaly.evalyshop.ui.newsfeed.NewsfeedPendingFragment;
import bd.com.evaly.evalyshop.ui.newsfeed.adapters.NewsfeedTabPager;
import bd.com.evaly.evalyshop.ui.newsfeed.createPost.CreatePostBottomSheet;
import bd.com.evaly.evalyshop.ui.newsfeed.post.NewsfeedPostFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewsfeedFragment extends Fragment {

    private NewsfeedViewModel viewModel;
    private MainViewModel mainViewModel;
    private NewsfeedTabPager pager;
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

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        binding.toolbar.inflateMenu(R.menu.badge_newsfeed_menu);
        final View menu_hotlist = binding.toolbar.getMenu().findItem(R.id.menu_messages).getActionView();
        TextView ui_hot = menu_hotlist.findViewById(R.id.hotlist_hot);

        ui_hot.setVisibility(View.INVISIBLE);
        menu_hotlist.setOnClickListener(viw -> {
            if (CredentialManager.getToken().equals(""))
                Toast.makeText(getContext(), "You need to login first.", Toast.LENGTH_SHORT).show();
            else
                navController.navigate(R.id.newsfeedNotificationFragment);
        });

        viewModel.getNotificationCountLiveData().observe(getViewLifecycleOwner(), integer -> {
            if (integer == null || isDetached() || isRemoving())
                return;

            ui_hot.setVisibility(View.VISIBLE);
            if (integer == 0)
                ui_hot.setVisibility(View.GONE);
            else if (integer > 99)
                ui_hot.setText("99");
            else
                ui_hot.setText(String.format("%d", integer));
        });

        viewModel.getLogoutLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                AppController.getInstance().logout(getActivity());
        });

        pager = new NewsfeedTabPager(getChildFragmentManager(), getLifecycle());
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setAdapter(pager);

        binding.tabLayout.setSmoothScrollingEnabled(true);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(pager.getTitle(position))
        ).attach();

        NewsfeedPostFragment publicFragment = NewsfeedPostFragment.newInstance("public");
        pager.addFragment(publicFragment, "PUBLIC");

        NewsfeedPostFragment announcementFragment = NewsfeedPostFragment.newInstance("announcement");
        pager.addFragment(announcementFragment, "ANNOUNCEMENT");

        NewsfeedPostFragment dailyNewsFragment = NewsfeedPostFragment.newInstance("daily-news");
        pager.addFragment(dailyNewsFragment, "DAILY NEWS");


        NewsfeedPostFragment ceoFragment = NewsfeedPostFragment.newInstance("ceo");
        pager.addFragment(ceoFragment, "CEO");

        if (!CredentialManager.getToken().equals("")) {
            NewsfeedPostFragment myFragment = NewsfeedPostFragment.newInstance("my");
            pager.addFragment(myFragment, "MY POSTS");
        }

        if (CredentialManager.getUserData() != null &&
                (CredentialManager.getUserData().getGroups().contains("EvalyEmployee"))) {
            NewsfeedPendingFragment pendingFragment = NewsfeedPendingFragment.newInstance("pending");
            pager.addFragment(pendingFragment, getString(R.string.pending));
        }

        pager.notifyDataSetChanged();

        if (CredentialManager.getToken().equals(""))
            binding.createPost.setVisibility(View.GONE);

        binding.createPost.setOnClickListener(v -> {
            CreatePostBottomSheet createPostBottomSheet = new CreatePostBottomSheet();
            createPostBottomSheet.show(getParentFragmentManager(), "Create Post");
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (viewModel != null && !CredentialManager.getToken().equals(""))
            viewModel.updateNotificationCount();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.viewPager.setAdapter(null);
        binding = null;
    }
}
