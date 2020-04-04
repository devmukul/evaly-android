package bd.com.evaly.evalyshop.ui.newsfeed.replies;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ReplyBottomSheetFragmentBinding;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class ReplyBottomSheet extends BottomSheetDialogFragment {

    private ReplyViewModel viewModel;
    private ReplyBottomSheetFragmentBinding binding;
    private CommentItem commentModel;
    private String postSlug;
    private ReplyAdapter adapter;
    private List<RepliesItem> itemList = new ArrayList<>();

    public static ReplyBottomSheet newInstance(CommentItem commentModel, String postSlug) {
        ReplyBottomSheet bottomSheet = new ReplyBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("commentModel", commentModel);
        bundle.putString("postSlug", postSlug);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);

        viewModel = new ViewModelProvider(this).get(ReplyViewModel.class);

        if (getArguments() == null)
            dismiss();
        else {
            commentModel = (CommentItem) getArguments().getSerializable("commentModel");
            postSlug = getArguments().getString("postSlug");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ReplyBottomSheetFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getReplyCreatedLiveData().observe(getViewLifecycleOwner(), jsonObject -> {

            binding.commentInput.setText("");
            binding.commentInput.setEnabled(true);
            binding.submitComment.setEnabled(true);
            binding.progressContainer.setVisibility(View.GONE);

            viewModel.loadReplies(1, postSlug, commentModel.getId());

        });

        viewModel.getReplyListLiveData().observe(getViewLifecycleOwner(), commentItems -> {


            if (commentItems.size() == 0) {
                binding.not.setVisibility(View.VISIBLE);
            } else {
                binding.not.setVisibility(View.GONE);
                binding.progressContainer.setVisibility(View.GONE);
                itemList.addAll(commentItems);
                adapter.notifyItemRangeInserted(itemList.size() - commentItems.size(), commentItems.size());
            }
        });

        adapter = new ReplyAdapter(itemList, getContext(), viewModel);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);

        if (itemList.size() == 0)
            binding.not.setVisibility(View.VISIBLE);


        initCommentHeader(commentModel);


        viewModel.loadReplies(1, postSlug, commentModel.getId());


        binding.submitComment.setOnClickListener(v -> {

            if (!isVisible())
                return;
            else if (binding.commentInput.getText().toString().trim().equals(""))
                Toast.makeText(getContext(), "Write something first before posting reply.", Toast.LENGTH_SHORT).show();

            itemList.clear();
            adapter.notifyDataSetChanged();

            binding.not.setVisibility(View.GONE);
            binding.progressContainer.setVisibility(View.VISIBLE);

            binding.commentInput.setEnabled(false);
            binding.submitComment.setEnabled(false);

            JsonObject parameters = new JsonObject();
            JsonObject parametersPost = new JsonObject();

            parameters.addProperty("body", binding.commentInput.getText().toString());
            parametersPost.add("comment", parameters);

            viewModel.createReply(parametersPost, postSlug, commentModel.getId());

        });

    }


    private void initCommentHeader(CommentItem postModel) {

        if (getContext() != null) {

            binding.userName.setText(postModel.getAuthor().getFullName());
            binding.text.setText(postModel.getBody());

            String timeAgo = Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS", "hh:mm aa - d',' MMMM", postModel.getCreatedAt()));
            binding.date.setText(timeAgo);

            if (postModel.getAuthor().getIsAdmin()) {
                int sizeInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.newsfeed_verified_icon);
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_evaly_verified_logo_filled);
                img.setBounds(0, 0, sizeInPixel, sizeInPixel);
                binding.userName.setCompoundDrawables(null, null, img, null);
                binding.userName.setCompoundDrawablePadding(15);
                binding.date.setText(timeAgo);

            } else {
                binding.userName.setCompoundDrawables(null, null, null, null);
                binding.date.setText(timeAgo);
            }

            Glide.with(getContext())
                    .load(postModel.getAuthor().getCompressedImage())
                    .fitCenter()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.user_image)
                    .apply(new RequestOptions().override(200, 200))
                    .into(binding.picture);

        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialogz -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialogz;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (getContext() != null && bottomSheet != null) {
                ScreenUtils screenUtils = new ScreenUtils(getContext());

                LinearLayout dialogLayoutReply = dialog.findViewById(R.id.container2);
                assert dialogLayoutReply != null;
                dialogLayoutReply.setMinimumHeight(screenUtils.getHeight());

                //BottomSheetBehavior.from(bottomSheet).setPeekHeight(screenUtils.getHeight());
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }

        });
        return bottomSheetDialog;
    }


}
