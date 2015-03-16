package ca.dalezak.androidbase.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import ca.dalezak.androidbase.BaseApplication;
import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.fragments.BaseFragment;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;

public abstract class BaseActivity<F extends BaseFragment>
        extends android.support.v7.app.ActionBarActivity {

    private Class<F> fragmentClass;
    private boolean showHome;
    private int menuId;
    private F fragment;
    private ProgressDialog dialog;
    private Toolbar toolbar;

    protected BaseActivity(Class<F> fragmentClass) {
        this(fragmentClass, 0, false);
    }

    protected BaseActivity(Class<F> fragmentClass, boolean showHome) {
        this(fragmentClass, 0, showHome);
    }

    protected BaseActivity(Class<F> fragmentClass, int menuId) {
        this(fragmentClass, menuId, false);
    }

    protected BaseActivity(Class<F> fragmentClass, int menuId, boolean showHome) {
        this.menuId = menuId;
        this.showHome = showHome;
        this.fragmentClass = fragmentClass;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this, "onCreate");
        BaseApplication baseApplication = (BaseApplication)getApplication();
        baseApplication.setActivity(this);
        setContentView(R.layout.activity_main);
        try {
            FragmentManager fragmentManager = getFragmentManager();
            if (savedInstanceState != null) {
                fragment = (F)fragmentManager.findFragmentByTag(fragmentClass.getName());
            }
            if (fragment == null) {
                fragment = fragmentClass.newInstance();
            }
            Log.i(this, "Fragment %s", fragment.getClass().getSimpleName());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.layout_content, fragment, fragmentClass.getName());
            fragmentTransaction.commit();
        }
        catch (InstantiationException e) {
            Log.w(this, "InstantiationException", e);
        }
        catch (IllegalAccessException e) {
            Log.w(this, "IllegalAccessException", e);
        }
        toolbar = (Toolbar)findViewById(R.id.layout_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHome);
            toolbar.setTitle(getTitle());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(this, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(this, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(this, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(this, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(this, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(this, "onDestroy");
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(this, "onActivityResult");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(this, "onCreateOptionsMenu %s", menu);
        if (this.menuId != 0) {
            getMenuInflater().inflate(this.menuId, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(this, "onOptionsItemSelected %s", item.getTitle());
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.i(this, "KeyEvent.KEYCODE_BACK");
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(this, "onNewIntent %s", intent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.i(this, "onBackPressed");
        fragment.onBackPressed();
    }

    protected String getStringExtra(String name) {
        return getIntent().getStringExtra(name);
    }

    protected String getStringExtra(Class<? extends BaseModel> clazz) {
        return getIntent().getStringExtra(clazz.getName());
    }

    protected void showLoading(int message) {
        showLoading(getString(message));
    }

    protected void showLoading(int total, int progress) {
        if (dialog != null) {
            dialog.setIndeterminate(false);
            dialog.setMax(total);
            dialog.setProgress(progress);
        }
    }

    protected void showLoading(CharSequence message) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        dialog.setMessage(message);
        dialog.show();
    }

    protected void hideLoading() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected F getFragment() {
        return fragment;
    }
}