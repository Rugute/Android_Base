package ca.dalezak.android.base.activities;

import android.os.Bundle;

import ca.dalezak.android.base.fragments.BaseEditFragment;

public abstract class BaseEditActivity<F extends BaseEditFragment>
        extends BaseActivity<F> {

    protected BaseEditActivity(Class<F> fragment) {
        super(fragment);
    }

    protected BaseEditActivity(Class<F> fragment, boolean showHome) {
        super(fragment, showHome);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
