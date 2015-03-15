package ca.dalezak.androidbase.utils;

import android.content.Context;
import android.content.DialogInterface;

public class AlertDialog extends android.app.AlertDialog.Builder {

    public AlertDialog(Context context) {
        super(context);
    }

    public AlertDialog(Context context, int title) {
        super(context);
        setTitle(title);
    }

    public AlertDialog(Context context, String title) {
        super(context);
        setTitle(title);
    }

    public AlertDialog(Context context, int title, int message) {
        super(context);
        setTitle(title);
        setMessage(message);
    }

    public AlertDialog(Context context, int title, String message) {
        super(context);
        setTitle(title);
        setMessage(message);
    }

    public AlertDialog(Context context, String title, String message) {
        super(context);
        setTitle(title);
        setMessage(message);
    }

    public AlertDialog(Context context, String title, int message) {
        super(context);
        setTitle(title);
        setMessage(message);
    }

    public AlertDialog(Context context, int title, boolean cancelable) {
        super(context);
        setTitle(title);
        setCancelable(cancelable);
    }

    public AlertDialog(Context context, String title, boolean cancelable) {
        super(context);
        setTitle(title);
        setCancelable(cancelable);
    }

    public AlertDialog(Context context, int title, int message, boolean cancelable) {
        super(context);
        setTitle(title);
        setMessage(message);
        setCancelable(cancelable);
    }

    public AlertDialog(Context context, int title, String message, boolean cancelable) {
        super(context);
        setTitle(title);
        setMessage(message);
        setCancelable(cancelable);
    }

    public AlertDialog setOk(int text) {
        setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ok();
            }
        });
        return this;
    }

    public AlertDialog setCancel(int text) {
        setNeutralButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                cancel();
            }
        });
        return this;
    }

    public AlertDialog setDiscard(int text) {
        setNegativeButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                discard();
            }
        });
        return this;
    }

    public AlertDialog showOkCancelDiscard(int ok, int cancel, int discard) {
        setOk(ok);
        setCancel(cancel);
        setDiscard(discard);
        show();
        return this;
    }

    public AlertDialog showOkCancel(int ok, int cancel) {
        setOk(ok);
        setCancel(cancel);
        show();
        return this;
    }

    public AlertDialog showOkDiscard(int ok, int discard) {
        setOk(ok);
        setDiscard(discard);
        show();
        return this;
    }

    public AlertDialog showOk(int ok) {
        setOk(ok);
        show();
        return this;
    }

    public AlertDialog showDiscard(int discard) {
        setDiscard(discard);
        show();
        return this;
    }

    public AlertDialog showCancelDiscard(int cancel, int discard) {
        setCancel(cancel);
        setDiscard(discard);
        show();
        return this;
    }

    public AlertDialog showItems(final String[] items) {
        setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                selected(index, items[index]);
            }
        });
        show();
        return this;
    }

    public AlertDialog showItems(final String[] items, final boolean[] checks) {
        setMultiChoiceItems(items, checks, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index, boolean checked) {
                selected(index, checked);
            }
        });
        show();
        return this;
    }

    public void ok() {}

    public void cancel() {}

    public void discard() {}

    public void selected(int index, String item) {}

    public void selected(int index, boolean checked) {}

}
