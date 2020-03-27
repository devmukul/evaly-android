package bd.com.evaly.evalyshop.ui.newsfeed.createPost;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetCreatePostBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.Post;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.ScreenUtils;

import static android.app.Activity.RESULT_OK;

public class CreatePostBottomSheet extends BottomSheetDialogFragment {

    private CreatePostViewModel viewModel;
    private BottomSheetCreatePostBinding binding;
    private String selectedImage = "";
    private Context context;
    private NewsfeedPost postModel;
    private ProgressDialog progressDialogImage;

    public CreatePostBottomSheet() {
    }

    public static CreatePostBottomSheet newInstance(NewsfeedPost postModel) {
        CreatePostBottomSheet bottomSheet = new CreatePostBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("postModel", postModel);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        context = getContext();
        viewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);

        if (getArguments() != null)
            postModel = (NewsfeedPost) getArguments().getSerializable("postModel");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.shareBtn.setOnClickListener(v -> {
            createPost();
        });

        binding.addPhoto.setOnClickListener(v -> {
            openImageSelector();
        });

        binding.close.setOnClickListener(v -> {

            if (binding.text.getText().toString().equals(""))
                dismiss();
            else {
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to discard the status?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("YES", (dialog, whichButton) -> dismiss())
                        .setNegativeButton("NO", null).show();
            }
        });

        binding.cancelImage.setOnClickListener(v -> {

            selectedImage = "";
            binding.postImageHolder.setVisibility(View.GONE);
            binding.addPhotoText.setText("Add Photo");

        });

        if (!CredentialManager.getUserData().getGroups().contains("EvalyEmployee"))
            binding.spinnerHolder.setVisibility(View.GONE);

        viewModel.getImageUploadSuccess().observe(getViewLifecycleOwner(), imageDataModel -> {
            if (imageDataModel != null) {
                selectedImage = imageDataModel.getUrl();
                progressDialogImage.dismiss();
                setPostPic();
            }
        });

        viewModel.getImageUploadFailed().observe(getViewLifecycleOwner(), bol -> {
            progressDialogImage.dismiss();
            Toast.makeText(getActivity(), "Image upload failed, try again!", Toast.LENGTH_SHORT).show();
        });

        viewModel.getResponseListenerAuthMutableLiveData().observe(getViewLifecycleOwner(), responseViewModel -> {

            if (responseViewModel.getOnSuccess().equals("true")) {
                if (!CredentialManager.getUserData().getGroups().contains("EvalyEmployee"))
                    Toast.makeText(context, "Your post has successfully posted. It may take few hours to get approved.", Toast.LENGTH_LONG).show();

                dismiss();
            }
        });

        if (postModel != null) {
            binding.title.setText("Edit Post");
            binding.text.setText(postModel.getBody());

            if (postModel.getAttachment() != null) {

                selectedImage = postModel.getAttachment();
                binding.postImage.setVisibility(View.VISIBLE);
                binding.postImageHolder.setVisibility(View.VISIBLE);
                binding.addPhotoText.setText("Change Photo");

                Glide.with(this)
                        .asBitmap()
                        .load(selectedImage)
                        .skipMemoryCache(true)
                        .fitCenter()
                        .optionalCenterCrop()
                        .placeholder(R.drawable.half_dp_bg_light)
                        .apply(new RequestOptions().override(500, 500))
                        .into(binding.postImage);

            } else {

                binding.addPhotoText.setText("Add Photo");
                binding.postImageHolder.setVisibility(View.GONE);
                selectedImage = "";

            }
        }

    }

    public void createPost() {

        String postBody = binding.text.getText().toString().trim();

        if (postBody.equals("")) {
            Toast.makeText(context, "Please write something!", Toast.LENGTH_SHORT).show();
            return;
        }

        CreatePostModel model = new CreatePostModel();
        Post post = new Post();

        post.setBody(postBody);

        if (!(selectedImage.equals("") || selectedImage.equals("null")))
            post.setAttachment(selectedImage);

        if (CredentialManager.getUserData().getGroups().contains("Employee")) {

            String postType;

            int typePosition = binding.type.getSelectedItemPosition();
            if (typePosition == 0)
                postType = "ceo";
            else if (typePosition == 1)
                postType = "announcement";
            else
                postType = "public";

            post.setType(postType);

        } else {
            post.setType("public");
        }

        model.setPost(post);

        binding.shareBtn.setEnabled(false);


        String postSlug = null;

        if (postModel != null)
            postSlug = postModel.getSlug();

        viewModel.createPost(model, postSlug);

        viewModel.getResponseListenerAuthMutableLiveData().observe(getViewLifecycleOwner(), responseViewModel -> {
            if (responseViewModel.getOnSuccess().equals("true")) {
                if (!CredentialManager.getUserData().getGroups().contains("EvalyEmployee"))
                    Toast.makeText(context, "Your post has successfully posted. It may take few hours to get approved.", Toast.LENGTH_LONG).show();
                dismiss();
            }
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

    private void setPostPic() {

        if (selectedImage != null && isVisible()) {

            binding.postImage.setVisibility(View.VISIBLE);
            binding.postImageHolder.setVisibility(View.VISIBLE);

            binding.addPhotoText.setText("Change Photo");

            Glide.with(this)
                    .asBitmap()
                    .load(selectedImage)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .placeholder(R.drawable.half_dp_bg_light)
                    .apply(new RequestOptions().override(500, 500))
                    .into(binding.postImage);
        }

    }

    private void openImageSelector() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    8000);

        } else {
            openSelector();
        }
    }

    private void openSelector() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1001);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 8000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openSelector();
            else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImage = data.getData();
            //  String imagePath = RealPathUtil.getRealPath(getActivity(), selectedImage);


            String destinationDirectoryPath = getContext().getCacheDir().getPath() + File.separator + "images";

            try {


                File cImage = compressImage(data.getData(), Bitmap.CompressFormat.JPEG, 70, destinationDirectoryPath);

                Bitmap bitmap = BitmapFactory.decodeFile(destinationDirectoryPath);

                if (selectedImage != null && isVisible()) {

                    Glide.with(this)
                            .asBitmap()
                            .load(selectedImage)
                            .skipMemoryCache(true)
                            .fitCenter()
                            .optionalCenterCrop()
                            .placeholder(R.drawable.half_dp_bg_light)
                            .apply(new RequestOptions().override(500, 500))
                            .into(binding.postImage);
                }

                uploadProfilePicture(bitmap);


            } catch (Exception e) {

                Log.d("json image error", e.toString());
                Toast.makeText(context, "Error occurred while uploading image", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void uploadProfilePicture(Bitmap bitmap) {

        progressDialogImage = ProgressDialog.show(getActivity(), "",
                "Uploading image...", true);

        progressDialogImage.show();

        viewModel.uploadImage(bitmap);

    }


    private File compressImage(Uri path, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
        //FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {

            ImageUtils.getCorrectlyOrientedImage(getActivity(), path).compress(compressFormat, quality, fileOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(destinationPath);

    }


}
