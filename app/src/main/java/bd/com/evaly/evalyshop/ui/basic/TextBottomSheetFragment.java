package bd.com.evaly.evalyshop.ui.basic;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetBasicTextBinding;
import bd.com.evaly.evalyshop.ui.basic.textBottomSheet.TextListController;

public class TextBottomSheetFragment extends BottomSheetDialogFragment {

    private BottomSheetBasicTextBinding binding;

    public static TextBottomSheetFragment newInstance(String title, String text) {
        TextBottomSheetFragment fragment = new TextBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", text);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static TextBottomSheetFragment newInstance(String text) {
        TextBottomSheetFragment fragment = new TextBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetBasicTextBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("text")) {

            String text = getArguments().getString("text");
            try {
                JsonArray jsonArray = JsonParser.parseString(text).getAsJsonArray();
                binding.text.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

                TextListController controller = new TextListController();
                binding.recyclerView.setAdapter(controller.getAdapter());

                List<String> list = new ArrayList<>();
                for (int i=0; i<jsonArray.size(); i++)
                    list.add( jsonArray.get(i).getAsString());
                controller.setList(list);

            } catch (Exception e) {
                binding.text.setText(Html.fromHtml(text));
                binding.text.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }

        }

        if (getArguments() != null && getArguments().containsKey("title"))
            binding.title.setText(getArguments().getString("title"));
        else
            binding.title.setVisibility(View.GONE);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });
        return bottomSheetDialog;
    }

}
