package bd.com.evaly.evalyshop.ui.issue;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.BottomSheetIssueDetailsBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.issue.IssueBodyModel;
import bd.com.evaly.evalyshop.models.issue.IssuesModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.Utils;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class IssueDetailsBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetIssueDetailsBinding binding;
    private IssuesModel issueModel;
    private IssueReplyAdapter adapter;
    private List<IssuesModel.ReplyModel> itemList = new ArrayList<>();

    public static IssueDetailsBottomSheet newInstance(IssuesModel issueModel) {
        IssueDetailsBottomSheet bottomSheet = new IssueDetailsBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", issueModel);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);

        if (getArguments() == null)
            dismiss();
        else {
            issueModel = (IssuesModel) getArguments().getSerializable("model");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetIssueDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new IssueReplyAdapter(getActivity(), itemList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);

        initCommentHeader(issueModel);
        binding.submitComment.setOnClickListener(v -> {
            submitReply();
        });

        loadReplies();

        if (issueModel.getStatus().equalsIgnoreCase("closed") || issueModel.getStatus().equalsIgnoreCase("resolved"))
            binding.commentHolder.setVisibility(View.GONE);

    }

    private void loadReplies() {
        binding.progressContainer.setVisibility(View.GONE);
        itemList.addAll(issueModel.getIssue_replies());
        adapter.notifyDataSetChanged();

        if (itemList.size() == 0)
            binding.not.setVisibility(View.VISIBLE);
        else
            binding.not.setVisibility(View.GONE);
    }


    private void submitReply() {

        if (binding.commentInput.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Please write something...", Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager img = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        assert img != null;
        img.hideSoftInputFromWindow(binding.commentInput.getWindowToken(), 0);

        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.show();

        IssueBodyModel model = new IssueBodyModel();
        model.setBody(binding.commentInput.getText().toString());

        AuthApiHelper.replyIssue(binding.commentInput.getText().toString(), issueModel.getId() + "", new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                binding.commentInput.setText("");
                dialog.dismiss();
                if (response.code() == 200 || response.code() == 201) {
                    binding.not.setVisibility(View.GONE);
                    IssuesModel.ReplyModel replyModel = new Gson().fromJson(response.body(), IssuesModel.ReplyModel.class);
                    itemList.add(replyModel);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), AppController.getmContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(int status) {
                dialog.dismiss();
                Toast.makeText(getContext(), AppController.getmContext().getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void initCommentHeader(IssuesModel postModel) {

        binding.orderId.setText(issueModel.getOrder_invoice());

        if (issueModel.getAttachment() != null) {
            Glide.with(binding.getRoot())
                    .load(postModel.getAttachment())
                    .into(binding.postImage);
            binding.postImage.setVisibility(View.VISIBLE);
        } else
            binding.postImage.setVisibility(View.GONE);

        binding.text.setText(postModel.getDescription());
        binding.date.setText(Utils.getTimeAgo(Utils.formattedDateFromStringToTimestampGMT("yyyy-MM-dd'T'HH:mm:ss", "", postModel.getCreated_at())));

        String orderStatus = postModel.getOrder_status();

        if (orderStatus.toLowerCase().equalsIgnoreCase("cancel")) {
            binding.tvStatus.setBackgroundColor(Color.parseColor("#d9534f"));
            binding.tvStatus.setText("Cancelled");
        } else if (orderStatus.toLowerCase().equalsIgnoreCase("delivered")) {
            binding.tvStatus.setText("Delivered");
            binding.tvStatus.setBackgroundColor(Color.parseColor("#4eb950"));

        } else if (orderStatus.toLowerCase().equalsIgnoreCase("pending")) {
            binding.tvStatus.setText("Pending");
            binding.tvStatus.setBackgroundColor(Color.parseColor("#e79e03"));

        } else if (orderStatus.toLowerCase().equalsIgnoreCase("confirmed")) {
            binding.tvStatus.setText("Confirmed");
            binding.tvStatus.setBackgroundColor(Color.parseColor("#7abcf8"));

        } else if (orderStatus.toLowerCase().equalsIgnoreCase("shipped")) {
            binding.tvStatus.setText("Shipped");
            binding.tvStatus.setBackgroundColor(Color.parseColor("#db9803"));

        } else if (orderStatus.toLowerCase().equalsIgnoreCase("picked")) {
            binding.tvStatus.setText("Picked");
            binding.tvStatus.setBackgroundColor(Color.parseColor("#3698db"));

        } else if (orderStatus.toLowerCase().equalsIgnoreCase("processing")) {
            binding.tvStatus.setText("Processing");
            binding.tvStatus.setBackgroundColor(Color.parseColor("#5ac1de"));
        }

        binding.tvIssueStatus.setText(Utils.toFirstCharUpperAll(postModel.getStatus()));

        if (postModel.getStatus().toLowerCase().equalsIgnoreCase("active")) {
            binding.tvIssueStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
        } else {
            binding.tvIssueStatus.setBackgroundColor(Color.parseColor("#33d274"));
        }

        binding.issueType.setText(Utils.toFirstCharUpperAll(postModel.getIssue_type()));

        binding.postImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ImagePreview.class);
            intent.putExtra("image", postModel.getAttachment());
            startActivity(intent);
        });
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
