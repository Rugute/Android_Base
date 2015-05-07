package ca.dalezak.androidbase.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import ca.dalezak.androidbase.fragments.BaseCardsFragment;
import ca.dalezak.androidbase.utils.Log;

public abstract class BaseCardsActivity<F extends BaseCardsFragment>
        extends BaseActivity<F> {

    protected BaseCardsActivity(Class<F> fragment) {
        super(fragment);
    }

    protected BaseCardsActivity(Class<F> fragment, boolean showHome) {
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
            getFragment().setSearchText(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
            if (getFragment().hasSearchView()) {
                getFragment().getSearchView().setFocusable(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSearchRequested() {
        Log.i(this, "onSearchRequested");
        return false;
    }
}