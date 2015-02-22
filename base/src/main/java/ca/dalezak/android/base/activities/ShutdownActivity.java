package ca.dalezak.android.base.activities;

import android.app.Activity;
import android.os.Bundle;

import ca.dalezak.android.base.R;

public class ShutdownActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown);
        finish();
    }
}