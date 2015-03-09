package ca.dalezak.androidbase.activities;

import android.app.Activity;
import android.os.Bundle;

import ca.dalezak.androidbase.R;

public class ShutdownActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown);
        finish();
    }
}