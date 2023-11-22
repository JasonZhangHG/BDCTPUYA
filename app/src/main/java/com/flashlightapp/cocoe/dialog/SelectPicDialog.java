package com.flashlightapp.cocoe.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.flashlightapp.cocoe.R;
import com.flashlightapp.cocoe.http.PermissionHelper;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;


public class SelectPicDialog extends BaseFragmentDialog {

    LinearLayout mPhotoLibrary;
    LinearLayout mCamera;

    TextView mCancelView;
    private File mSourceFile;
    private OnItemClickedListener mListener;

    public void setListener(OnItemClickedListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPhotoLibrary = view.findViewById(R.id.ll_album);
        mCamera = view.findViewById(R.id.ll_camera);
        mCancelView = view.findViewById(R.id.tv_cancel);
        mCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancelClicked();
                }
                tryHide();
            }
        });

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClicked();
            }
        });

        mPhotoLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoLibraryClicked();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideFromTopToMiddleAnimation;
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_change_avatar;
    }

    public void setData(File sourceFile) {
        this.mSourceFile = sourceFile;
    }

    public void onCameraClicked() {
        PermissionHelper.requestCameraAndAllMedia()
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                        shouldRequest.again(true);
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        startAvatarCameraActivity(getActivity(), mSourceFile);
                        tryHide();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtils.launchAppDetailsSettings();
                        }
                    }
                }).request();
    }

    public static void startAvatarCameraActivity(Activity activity, File dstFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri mImageUri = Uri.fromFile(dstFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageUri = FileProvider.getUriForFile(activity, "com.flashlightapp.cocoe.fileprovider", dstFile);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        activity.startActivityForResult(intent, AppConstant.ActivityRequestCode.REQUEST_CAMERA_IMG);
    }


    public void onPhotoLibraryClicked() {
        PermissionHelper.requestCameraAndAllMedia()
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                        shouldRequest.again(true);
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        startGalleryActivity(getActivity());
                        tryHide();
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtils.launchAppDetailsSettings();
                        }
                    }
                }).request();
    }

    public void startGalleryActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), AppConstant.ActivityRequestCode.REQUEST_ALBUM_IMG);
    }

    public interface OnItemClickedListener {
        void onCancelClicked();
    }

}
