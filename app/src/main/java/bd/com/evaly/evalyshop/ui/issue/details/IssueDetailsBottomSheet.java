package bd.com.evaly.evalyshop.ui.issue.details;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetIssueDetailsBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.issueNew.comment.IssueTicketCommentModel;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.rest.apiHelper.IssueApiHelper;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.issue.adapter.IssueReplyAdapter;
import bd.com.evaly.evalyshop.util.ImagePreview;
import bd.com.evaly.evalyshop.util.ScreenUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class IssueDetailsBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetIssueDetailsBinding binding;
    private IssueListModel issueModel;
    private IssueReplyAdapter adapter;
    private List<IssueTicketCommentModel> itemList = new ArrayList<>();

    public static IssueDetailsBottomSheet newInstance(IssueListModel issueModel) {
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
            issueModel = (IssueListModel) getArguments().getSerializable("model");
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

        if (issueModel.getStatus().equalsIgnoreCase("closed") ||
                issueModel.getStatus().equalsIgnoreCase("resolved") ||
                issueModel.getStatus().equalsIgnoreCase("rejected"))
            binding.commentHolder.setVisibility(View.GONE);

    }

    private void loadReplies() {


        IssueApiHelper.getIssueCommentList(issueModel.getId(), new ResponseListenerAuth<CommonDataResponse<List<IssueTicketCommentModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueTicketCommentModel>> response, int statusCode) {
                binding.progressContainer.setVisibility(View.GONE);
                itemList.addAll(response.getData());
                adapter.notifyDataSetChanged();

                if (itemList.size() == 0)
                    binding.not.setVisibility(View.VISIBLE);
                else
                    binding.not.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
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

        IssueApiHelper.createIssueComment(issueModel.getId(), binding.commentInput.getText().toString(), new ResponseListenerAuth<CommonDataResponse<IssueTicketCommentModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<IssueTicketCommentModel> response, int statusCode) {
                binding.commentInput.setText("");
                dialog.dismiss();
                if (response.getData() != null) {
                    binding.not.setVisibility(View.GONE);
                    itemList.add(response.getData());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.dismiss();
                ToastUtils.show(R.string.something_wrong);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    private void resolveIssue() {

        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.show();

        IssueApiHelper.resolveIssue("resolved", (int) issueModel.getId(), new ResponseListenerAuth<CommonDataResponse<IssueListModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<IssueListModel> response, int statusCode) {
                dialog.dismiss();
                if (response.getSuccess()) {
                    dismissAllowingStateLoss();
                    ToastUtils.show("Successfully marked the ticket as resolved");
                    if (getActivity() != null && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
                        if (getActivity() instanceof IssuesActivity)
                            ((IssuesActivity) getActivity()).refreshPage();
                    }
                } else
                    ToastUtils.show(response.getMessage());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.dismiss();
                ToastUtils.show(R.string.something_wrong);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    private void initCommentHeader(IssueListModel postModel) {

        binding.orderId.setText(issueModel.getInvoiceNumber());

        if (issueModel.getAttachments().size() > 0) {
            Glide.with(binding.getRoot())
                    .load(postModel.getAttachments().get(0))
                    .into(binding.postImage);
            binding.postImage.setVisibility(View.VISIBLE);
        } else
            binding.postImage.setVisibility(View.GONE);

        binding.text.setText(postModel.getAdditionalInfo());
        binding.date.setText(Utils.getTimeAgo(Utils.formattedDateFromStringToTimestampGMTIssue("", "", postModel.getCreatedAt())));

        String orderStatus = postModel.getStatus();

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

        if (postModel.getStatus().toLowerCase().equalsIgnoreCase("active") || postModel.getStatus().toLowerCase().equalsIgnoreCase("submitted")) {
            binding.tvIssueStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
        } else if (postModel.getStatus().toLowerCase().equalsIgnoreCase("rejected")) {
            binding.tvIssueStatus.setBackgroundColor(Color.parseColor("#d9534f"));
        } else {
            binding.tvIssueStatus.setBackgroundColor(Color.parseColor("#33d274"));
        }

        if (postModel.getStatus().equals("resolved") || postModel.getStatus().equals("rejected"))
            binding.tvResolveIssueHolder.setVisibility(View.GONE);
        else
            binding.tvResolveIssueHolder.setVisibility(View.VISIBLE);

        binding.issueType.setText(Utils.toFirstCharUpperAll(postModel.getCategory()));

        binding.postImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ImagePreview.class);
            intent.putExtra("image", postModel.getAttachments().get(0));
            startActivity(intent);
        });

        binding.tvResolveIssue.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext())
                    .setMessage("Are you sure you want to mark this ticket as solved?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("YES", (dialog, whichButton) -> resolveIssue())
                    .setNegativeButton("NO", null).show();
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
