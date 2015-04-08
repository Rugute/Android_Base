package ca.dalezak.androidbase.utils;

import android.content.Context;

public class ProgressDialog extends android.app.ProgressDialog {

    private CharSequence message;

    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public CharSequence getMessage() {
        return message;
    }

    @Override
    public void setMessage(CharSequence message) {
        try {
            super.setMessage(message);
            this.message = message;
        }
        catch(Exception ex) {
            Log.w(this, "Exception %s", ex);
        }
    }
}
