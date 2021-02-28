package bd.com.evaly.evalyshop.ui.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

@SuppressLint("SetTextI18n")
public abstract class BaseFragment<DATA_BINDING extends ViewDataBinding, VIEW_MODEL extends ViewModel> extends Fragment {

    protected VIEW_MODEL viewModel;
    protected DATA_BINDING binding;
    protected Bundle bundle;
    private Class<VIEW_MODEL> viewModelClassType;
    @LayoutRes
    private int layoutId;
    protected NavController navController;

    public BaseFragment(Class<VIEW_MODEL> viewModelClassType, int layoutId) {
        this.viewModelClassType = viewModelClassType;
        this.layoutId = layoutId;
    }

    public BaseFragment() {

    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false);
        viewModel = new ViewModelProvider(this).get(viewModelClassType);
        binding.setVariable(BR.viewModel, viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.executePendingBindings();
        bundle = getArguments();
        navController = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        clickListeners();
        setupRecycler();
        liveEventsObservers();
    }

    protected View.OnClickListener backPressClickListener = view -> {
        if (getActivity() != null)
            getActivity().onBackPressed();
    };

    protected abstract void initViews();

    protected abstract void liveEventsObservers();

    protected abstract void clickListeners();

    protected void setupRecycler() {

    }
}
