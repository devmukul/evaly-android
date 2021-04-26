package bd.com.evaly.evalyshop.ui.issue;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityIssuesBinding;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.issue.adapter.IssuesAdapter;
import bd.com.evaly.evalyshop.ui.issue.details.IssueDetailsBottomSheet;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IssuesActivity extends BaseActivity<ActivityIssuesBinding, IssueViewModel> implements RecyclerViewOnItemClickListener {

    private IssuesAdapter adapter;
    private String invoice;
    private List<IssueListModel> list;
    private ViewDialog dialog;

    public IssuesActivity() {
        super(IssueViewModel.class, R.layout.activity_issues);
    }

    @Override
    protected void initViews() {
        invoice = getIntent().getStringExtra("invoice");

        getSupportActionBar().setTitle(invoice);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ViewDialog(this);
        dialog.showDialog();

        list = new ArrayList<>();
        adapter = new IssuesAdapter(this, list, this);
        binding.rvIssues.setLayoutManager(new LinearLayoutManager(this));
        binding.rvIssues.setAdapter(adapter);
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.loadingBar.observe(this, aBoolean -> {
            if (aBoolean)
                dialog.showDialog();
            else
                dialog.hideDialog();
        });

        viewModel.liveList.observe(this, issueListModels -> {
            list.clear();
            list.addAll(issueListModels);
            adapter.notifyDataSetChanged();
            if (list.size() == 0) {
                binding.noIssue.setVisibility(View.VISIBLE);
            } else {
                binding.noIssue.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void clickListeners() {

    }

    public void refreshPage() {
        list.clear();
        viewModel.getIssuesList();
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
