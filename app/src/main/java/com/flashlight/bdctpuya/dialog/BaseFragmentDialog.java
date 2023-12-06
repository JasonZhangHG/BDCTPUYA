package com.flashlight.bdctpuya.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.flashlight.bdctpuya.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public abstract class BaseFragmentDialog extends DialogFragment {

    private boolean mDisableBackPress;
    private boolean mEnableTouchCancel;
    private boolean mIsShowing;
    private boolean mIsAdded;
    private List<Integer> mImgList;
    private OnDismissListener mDismissListener;

    public interface OnDismissListener {
        void onDismiss(BaseFragmentDialog dialog);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mDismissListener = listener;
    }

    protected abstract int getLayoutResId();

    public void disableBackPress(boolean disableBackPress) {
        this.mDisableBackPress = disableBackPress;
    }

    public void setEnableTouchCancel(boolean mEnableTouchCancel) {
        this.mEnableTouchCancel = mEnableTouchCancel;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        mIsAdded = true;
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View rootView = getView();
        if (rootView != null) {
            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mEnableTouchCancel) {
                        dismiss();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = getDialogAnimation();
        // listener for back press
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return (mDisableBackPress && keyCode == KeyEvent.KEYCODE_BACK);  // disable back presse
            }
        });

        return dialog;
    }

    public void tryRecover(FragmentManager fm) {
        if (mIsShowing) {
            tryShow(fm);
        } else {
            tryHide();
        }
    }

    public void tryShow(FragmentManager fm) {
        if (!hasSavedInstanceState() && !mIsAdded) {
            try {
                show(fm, getPrivateTag());
                mIsAdded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mIsShowing = true;
    }

    public void tryHide() {
        if (!hasSavedInstanceState() && mIsAdded) {
            mIsAdded = false;
            super.dismiss();
        }
        mIsShowing = false;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mIsAdded = false;
        mIsShowing = false;
        if (mDismissListener != null) {
            mDismissListener.onDismiss(this);
        }
        super.onDismiss(dialog);
    }

    @Override
    public void dismiss() {
        tryHide();
    }

    protected boolean hasSavedInstanceState() {
        return false;
    }

    private String getPrivateTag() {
        return getClass().toString();
    }


    @Override
    public void onDestroyView() {
        mIsAdded = false;
        super.onDestroyView();
    }

    protected int getDialogAnimation() {
        return R.style.DialogScaleAnimation;
    }
}
