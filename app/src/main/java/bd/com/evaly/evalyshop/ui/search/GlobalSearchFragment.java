package bd.com.evaly.evalyshop.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import bd.com.evaly.evalyshop.databinding.FragmentGlobalSearchBinding;
import bd.com.evaly.evalyshop.models.search.AlgoliaParams;

public class GlobalSearchFragment extends Fragment {

    private FragmentGlobalSearchBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGlobalSearchBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initSearchViews();
        intRecyclerView();
        liveEventObservers();
    }

    private void initSearchViews() {

    }

    private void initViews() {
        AlgoliaParams params = new AlgoliaParams();
        Log.d("hmtz", params.getParams());
    }

    private void liveEventObservers() {

    }

    private void intRecyclerView() {

    }
}
