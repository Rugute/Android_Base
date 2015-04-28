package ca.dalezak.androidbase.app.activites;

import android.os.Bundle;

import ca.dalezak.androidbase.activities.BaseActivity;
import ca.dalezak.androidbase.app.fragments.MainFragment;

public class MainActivity extends BaseActivity<MainFragment> {

    public MainActivity() {
        super(MainFragment.class, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}