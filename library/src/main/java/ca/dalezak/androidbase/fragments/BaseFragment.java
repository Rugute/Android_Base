package ca.dalezak.androidbase.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import ca.dalezak.androidbase.BaseApplication;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Controls;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.ProgressDialog;
import ca.dalezak.androidbase.utils.Strings;

public abstract class BaseFragment extends android.app.Fragment {

    public interface Callback {
        public void onFragmentCreated(BaseFragment fragment);
    }

    protected BaseApplication App;
    protected ProgressDialog dialog;
    protected int layout;
    protected int menu;
    private Callback callback;

    public BaseFragment() {
    }

    public BaseFragment(int layout) {
        this.layout = layout;
    }

    public BaseFragment(int layout, int menu) {
        this.layout = layout;
        this.menu = menu;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        Log.i(this, "onInflate");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(this, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(this, "onCreateView");
        View view = inflater.inflate(layout, container, false);
        Class clazz = getClass();
        while (clazz != null && clazz != BaseFragment.class) {
            Controls.load(getActivity(), this, view, clazz);
            clazz = clazz.getSuperclass();
        }
        setHasOptionsMenu(menu != 0);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(this, "onViewCreated");
        if (callback != null) {
            callback.onFragmentCreated(this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.App = (BaseApplication)getActivity().getApplication();
        Log.i(this, "onActivityCreated");
    }

    public void onSelected() {
        Log.i(this, "onSelected");
    }

    public void onUnselected() {
        Log.i(this, "onUnselected");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(this, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(this, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(this, "onPause");
        hideKeyboard();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(this, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(this, "onDestroyView");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(this, "onConfigurationChanged");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(this, "onDestroy");
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(this, "onDetach");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (this.menu != 0) {
            inflater.inflate(this.menu, menu);
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                if (menuItem.getIcon() != null) {
                    if (menuItem.isEnabled()) {
                        menuItem.getIcon().setAlpha(255);
                    }
                    else {
                        menuItem.getIcon().setAlpha(50);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        Log.i(this, "onBackPressed");
    }

    protected boolean showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            view.requestFocus();
            return inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
        return inputMethodManager.showSoftInput(null, InputMethodManager.SHOW_IMPLICIT);
    }

    protected boolean hideKeyboard() {
        return hideKeyboard(getActivity().getCurrentFocus());
    }

    protected boolean hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            view.clearFocus();
            return inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return inputMethodManager.hideSoftInputFromWindow(null, 0);
    }

    protected Intent getIntent() {
        return getActivity() != null ? getActivity().getIntent() : null;
    }

    protected void showLoading(int message) {
        showLoading(getString(message));
    }

    protected void showLoading(int message, int total, int progress) {
        showLoading(getString(message), total, progress);
    }

    protected void showLoading(final CharSequence message) {
        if (dialog != null && !Strings.areEqual(dialog.getMessage(), message)) {
            dialog.dismiss();
            dialog = null;
        }
        if (dialog == null && getActivity() != null) {
            dialog = new ProgressDialog(getActivity());
        }
        if (dialog != null) {
            dialog.setMessage(message);
            dialog.setIndeterminate(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }
    }

    protected void showLoading(final CharSequence message, final int total, final int progress) {
        if (dialog != null && dialog.isIndeterminate()) {
            dialog.dismiss();
            dialog = null;
        }
        if (dialog == null && getActivity() != null) {
            dialog = new ProgressDialog(getActivity());
        }
        if (dialog != null) {
            dialog.setMessage(message);
            dialog.setMax(total);
            dialog.setProgress(progress);
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
        }
    }

    protected void hideLoading() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected String getStringExtra(String name) {
        return getIntent().getStringExtra(name);
    }

    protected boolean hasExtra(Class<? extends BaseModel> clazz) {
        return getIntent().hasExtra(clazz.getName());
    }

    protected String getStringExtra(Class<? extends BaseModel> clazz) {
        return getIntent().getStringExtra(clazz.getName());
    }

    protected void setTitle(String title) {
        getActivity().setTitle(title);
    }

    protected LinearLayout.LayoutParams getLayoutParams(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    protected void scaleDrawables(EditText editText, double scale) {
        try {
            for (Drawable drawable : editText.getCompoundDrawables()) {
                if (drawable != null) {
                    int right = (int)(drawable.getIntrinsicWidth()*scale);
                    int bottom = (int)(drawable.getIntrinsicHeight()*scale);
                    int left = 0;
                    int top = drawable.getIntrinsicHeight() - bottom;
                    drawable.setBounds(left, top, right, bottom);
                }
            }
        }
        catch (Exception ex) {
            Log.w(this, "scaleDrawables", ex);
        }
    }

    protected boolean isDebug() {
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            String packageName = getActivity().getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(this, "PackageManager.NameNotFoundException", e);
        }
        return true;
    }

    protected boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    protected boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}