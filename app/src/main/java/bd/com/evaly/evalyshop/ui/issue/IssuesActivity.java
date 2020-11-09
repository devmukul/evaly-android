package bd.com.evaly.evalyshop.ui.issue;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.rest.apiHelper.IssueApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IssuesActivity extends BaseActivity implements RecyclerViewOnItemClickListener {

    @BindView(R.id.rvIssues)
    RecyclerView rvIssues;
    @BindView(R.id.noIssue)
    TextView noIssue;

    IssuesAdapter adapter;
    String invoice;
    BottomSheetDialog bottomSheetDialog;
    private List<IssueListModel> list;
    private ViewDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        ButterKnife.bind(this);

        invoice = getIntent().getStringExtra("invoice");

        getSupportActionBar().setTitle(invoice);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();
        dialog = new ViewDialog(this);

        adapter = new IssuesAdapter(this, list, this);
        rvIssues.setLayoutManager(new LinearLayoutManager(this));
        rvIssues.setAdapter(adapter);

        dialog.showDialog();

        getIssuesList();

    }

    public void refreshPage(){
        list.clear();
        getIssuesList();
    }

    private void getIssuesList() {
        IssueApiHelper.getIssueList(invoice, new ResponseListenerAuth<CommonDataResponse<List<IssueListModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueListModel>> response, int statusCode) {
                dialog.hideDialog();
                if (response != null) {

                    list.addAll(response.getData());
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
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show(errorBody);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecyclerViewItemClicked(Object object) {
        IssueListModel model = (IssueListModel) object;
        model.setInvoiceNumber(invoice);

        IssueDetailsBottomSheet detailsBottomSheet = IssueDetailsBottomSheet.newInstance(model);
        detailsBottomSheet.show(getSupportFragmentManager(), "Issue Bottom Sheet");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }
}
