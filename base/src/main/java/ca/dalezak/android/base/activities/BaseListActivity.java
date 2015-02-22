package ca.dalezak.android.base.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import ca.dalezak.android.base.fragments.BaseListFragment;

public abstract class BaseListActivity<F extends BaseListFragment>
        extends BaseActivity<F> {

    protected BaseListActivity(Class<F> fragment) {
        super(fragment);
    }

    protected BaseListActivity(Class<F> fragment, boolean showHome) {
        super(fragment, showHome);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            fragment.setSearchText(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
            if (fragment.searchView != null) {
                fragment.searchView.setFocusable(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSearchRequested() {
        return false;
    }
}