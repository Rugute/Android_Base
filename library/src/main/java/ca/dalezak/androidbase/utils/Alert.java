package ca.dalezak.androidbase.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ca.dalezak.androidbase.R;

public class Alert {

    private AlertDialog.Builder builder;

    public Alert(Context context) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
        }
    }

    public Alert(Context context, int title) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
        }
    }

    public Alert(Context context, String title) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
        }
    }

    public Alert(Context context, Exception exception) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(Strings.splitCamelCase(exception.getClass().getSimpleName()));
            if (!Strings.isNullOrEmpty(exception.getLocalizedMessage())) {
                builder.setMessage(exception.getLocalizedMessage());
            }
            else if (!Strings.isNullOrEmpty(exception.getMessage())) {
                builder.setMessage(exception.getMessage());
            }
            else if (exception.getCause() != null && !Strings.isNullOrEmpty(exception.getCause().getLocalizedMessage())) {
                builder.setMessage(exception.getCause().getLocalizedMessage());
            }
            else if (exception.getCause() != null && !Strings.isNullOrEmpty(exception.getCause().getMessage())) {
                builder.setMessage(exception.getCause().getMessage());
            }
            else {
                builder.setMessage(R.string.unknown_exception_description);
            }
        }
    }

    public Alert(Context context, int title, int message) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
        }
    }

    public Alert(Context context, int title, String message) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
        }
    }

    public Alert(Context context, String title, String message) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
        }
    }

    public Alert(Context context, String title, int message) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
        }
    }

    public Alert(Context context, int title, boolean cancelable) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(cancelable);
        }
    }

    public Alert(Context context, String title, boolean cancelable) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(cancelable);
        }
    }

    public Alert(Context context, int title, int message, boolean cancelable) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(cancelable);
        }
    }

    public Alert(Context context, int title, String message, boolean cancelable) {
        if (context != null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(cancelable);
        }
    }

    protected Alert setOk(int text) {
        if (builder != null) {
            builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    ok();
                }
            });
        }
        return this;
    }

    protected Alert setCancel(int text) {
        if (builder != null) {
            builder.setNeutralButton(text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    cancel();
                }
            });
        }
        return this;
    }

    protected Alert setDiscard(int text) {
        if (builder != null) {
            builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    discard();
                }
            });
        }
        return this;
    }

    public AlertDialog showOkCancelDiscard(int ok, int cancel, int discard) {
        if (builder != null) {
            setOk(ok);
            setCancel(cancel);
            setDiscard(discard);
            return builder.show();
        }
        return null;
    }

    public AlertDialog showOkCancel(int ok, int cancel) {
        if (builder != null) {
            setOk(ok);
            setCancel(cancel);
            return builder.show();
        }
        return null;
    }

    public AlertDialog showOkDiscard(int ok, int discard) {
        if (builder != null) {
            setOk(ok);
            setDiscard(discard);
            return builder.show();
        }
        return null;
    }

    public AlertDialog showOk(int ok) {
        if (builder != null) {
            setOk(ok);
            return builder.show();
        }
        return null;
    }

    public AlertDialog showDiscard(int discard) {
        if (builder != null) {
            setDiscard(discard);
            return builder.show();
        }
        return null;
    }

    public AlertDialog showCancelDiscard(int cancel, int discard) {
        if (builder != null) {
            setCancel(cancel);
            setDiscard(discard);
            return builder.show();
        }
        return null;
    }

    public AlertDialog showItems(final String[] items) {
        if (builder != null) {
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int index) {
                    selected(index, items[index]);
                }
            });
            return builder.show();
        }
        return null;
    }

    public AlertDialog showItems(final String[] items, final boolean[] checks) {
        if (builder != null) {
            builder.setMultiChoiceItems(items, checks, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int index, boolean checked) {
                    selected(index, checked);
                }
            });
            return builder.show();
        }
        return null;
    }

    public void ok() {}

    public void cancel() {}

    public void discard() {}

    public void selected(int index, String item) {}

    public void selected(int index, boolean checked) {}

}
