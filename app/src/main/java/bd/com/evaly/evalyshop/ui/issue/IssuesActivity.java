package bd.com.evaly.evalyshop.ui.issue;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.models.issue.IssuesModel;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class IssuesActivity extends BaseActivity implements RecyclerViewOnItemClickListener {

    @BindView(R.id.rvIssues)
    RecyclerView rvIssues;
    @BindView(R.id.noIssue) TextView noIssue;

    IssuesAdapter adapter;

    private List<IssuesModel> list;
    String invoice;

    private ViewDialog dialog;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        ButterKnife.bind(this);

        invoice = getIntent().getStringExtra("invoice");
        if (invoice == null){
            invoice = getIntent().getExtras().getString("resource_id");
        }

        getSupportActionBar().setTitle(invoice);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();
        dialog = new ViewDialog(this);

        adapter = new IssuesAdapter(this, list, this);
        rvIssues.setLayoutManager(new LinearLayoutManager(this));
        rvIssues.setAdapter(adapter);

        dialog.showDialog();
        AuthApiHelper.getIssueList(invoice, new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                dialog.hideDialog();
                if (response.body()!=null) {

                    List<IssuesModel> models = new Gson().fromJson(response.body().get("results"), new TypeToken<List<IssuesModel>>() {
                    }.getType());
                    list.addAll(models);
                    adapter.notifyDataSetChanged();

                    if (list.size() == 0) {
                        noIssue.setVisibility(View.VISIBLE);
                    } else {
                        noIssue.setVisibility(View.GONE);
                    }
                } else {

                    noIssue.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(int status) {
                dialog.hideDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecyclerViewItemClicked(Object object) {
        IssuesModel model = (IssuesModel) object;

        bottomSheetDialog = new BottomSheetDialog(IssuesActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.issue_reply_bottom_view);

        EditText etDescription = bottomSheetDialog.findViewById(R.id.etDescription);
        Button btnSubmit = bottomSheetDialog.findViewById(R.id.btnSubmit);
        ImageView ivIssueImage = bottomSheetDialog.findViewById(R.id.ivIssueImage);
        RecyclerView rvReply = bottomSheetDialog.findViewById(R.id.rvReply);
        TextView tvDate = bottomSheetDialog.findViewById(R.id.tvDate);
        TextView tvBody = bottomSheetDialog.findViewById(R.id.tvBody);

        List<String> options = new ArrayList<>();
        List<OrderIssueModel> list = Constants.getDelivaryIssueList();
        for (int i = 0; i<list.size(); i++){
            options.add(list.get(i).getDescription());
        }

        List<IssuesModel.ReplyModel> repliedList = model.getIssue_replies();

        rvReply.setLayoutManager(new LinearLayoutManager(this));
        IssueReplyAdapter adapter = new IssueReplyAdapter(this, repliedList);
        rvReply.setAdapter(adapter);

        tvDate.setText(Utils.getTimeAgo(Utils.formattedDateFromStringToTimestampGMT("yyyy-MM-dd'T'HH:mm:ss","",model.getCreated_at())));

        tvBody.setText(model.getDescription());
        if (model.getAttachment() != null){
            ivIssueImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(model.getAttachment())
                    .into(ivIssueImage);
        }

        btnSubmit.setOnClickListener(view -> {
            if (etDescription.getText().toString().trim().isEmpty()){
                etDescription.setError("Required");
                return;
            }

            AuthApiHelper.replyIssue(etDescription.getText().toString(), model.getId()+"", new DataFetchingListener<Response<JsonObject>>() {
                @Override
                public void onDataFetched(Response<JsonObject> response) {
                    etDescription.setText("");
                    if (response.code() == 200 || response.code() == 201){
                        IssuesModel.ReplyModel replyModel = new Gson().fromJson(response.body(), IssuesModel.ReplyModel.class);
                        repliedList.add(replyModel);
                        adapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailed(int status) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            });
//                model.setDescription(etDescription.getText().toString());
//
//                model.setAttachment(imageUrl);
//                dialog.showDialog();
//                submitIssue(model, bottomSheetDialog);
        });


        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        new KeyboardUtil(IssuesActivity.this, bottomSheetInternal);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    Logger.d("=---==========------");
                    view.post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
//        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }
}
