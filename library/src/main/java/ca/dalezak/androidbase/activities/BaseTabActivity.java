package ca.dalezak.androidbase.activities;

import android.os.Bundle;

import ca.dalezak.androidbase.fragments.BaseTabFragment;

public abstract class BaseTabActivity<F extends BaseTabFragment>
        extends BaseActivity<F> {

    protected BaseTabActivity(Class<F> fragment) {
        super(fragment);
    }

    protected BaseTabActivity(Class<F> fragment, int menu) {
        super(fragment, menu);
    }

    protected BaseTabActivity(Class<F> fragment, boolean showHome) {
        super(fragment, showHome);
    }

    protected BaseTabActivity(Class<F> fragment, int menu, boolean showHome) {
        super(fragment, menu, showHome);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
}