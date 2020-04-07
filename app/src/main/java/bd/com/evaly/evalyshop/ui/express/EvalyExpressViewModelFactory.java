package bd.com.evaly.evalyshop.ui.express;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory;

public class EvalyExpressViewModelFactory extends NewInstanceFactory{

    private String serviceSlug;

    public EvalyExpressViewModelFactory(String serviceSlug) {
        super();
        this.serviceSlug = serviceSlug;
    }


    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EvalyExpressViewModel.class)) {
            return (T) new EvalyExpressViewModel(serviceSlug);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
