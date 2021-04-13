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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

@SuppressLint("SetTextI18n")
public abstract class BaseDialogFragment<DATA_BINDING extends ViewDataBinding, VIEW_MODEL extends ViewModel> extends DialogFragment {

    protected VIEW_MODEL viewModel;
    protected DATA_BINDING binding;
    protected Bundle bundle;
    protected NavController navController;
    protected View.OnClickListener backPressClickListener = view -> {
        if (getActivity() != null)
            getActivity().onBackPressed();
    };
    private Class<VIEW_MODEL> viewModelClassType;
    @LayoutRes
    private int layoutId;

    public BaseDialogFragment(Class<VIEW_MODEL> viewModelClassType, int layoutId) {
        this.viewModelClassType = viewModelClassType;
        this.layoutId = layoutId;
    }

    public BaseDialogFragment() {

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
        try {
            navController = NavHostFragment.findNavController(this);
        } catch (Exception ignored) {

        }
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

    protected abstract void initViews();

    protected abstract void liveEventsObservers();

    protected abstract void clickListeners();

    protected void setupRecycler() {

    }
}
