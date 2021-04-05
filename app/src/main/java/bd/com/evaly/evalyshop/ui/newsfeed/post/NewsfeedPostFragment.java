package bd.com.evaly.evalyshop.ui.newsfeed.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanhua.skeleton.SkeletonScreen;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.NewsfeedTabsFragmentBinding;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.newsfeed.createPost.CreatePostBottomSheet;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewsfeedPostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    PreferenceRepository preferenceRepository;
    private NewsfeedPostViewModel viewModel;
    private MainViewModel mainViewModel;
    private String type;
    private NewsfeedTabsFragmentBinding binding;
    private NewsfeedPostAdapter adapter;
    private List<NewsfeedPost> itemsList;
    private int currentPage = 1;
    private boolean isLoading = false;
    private SkeletonScreen skeletonScreen;
    private boolean isFirstLoad = true;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = NewsfeedTabsFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(NewsfeedPostViewModel.class);
        if (getActivity() != null)
            mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
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
            Intent in = new Intent(Intent.ACTION_SEND);
            in.setType("text/plain");
            in.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
            in.putExtra(Intent.EXTRA_TEXT, "https://evaly.com.bd/feeds/" + object.getSlug());
            startActivity(Intent.createChooser(in, "Share Post"));
        }, preferenceRepository);

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
    public void onDestroyView() {
        super.onDestroyView();
        adapter.submitList(null);
        binding.recyclerView.setAdapter(null);
        binding = null;
    }

}
