package bd.com.evaly.evalyshop.ui.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentAppointmentBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppointmentFragment extends Fragment {

    @Inject
    AppointmentViewModel viewModel;
    private FragmentAppointmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clickListeners();
    }

    private void clickListeners() {
        binding.createAppointment.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.createAppointmentBottomSheet);
        });
    }


}
