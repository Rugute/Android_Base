package ca.dalezak.android.base.fragments;

public abstract class BaseEditFragment extends BaseFragment {

    public BaseEditFragment() {}

    public BaseEditFragment(int layout) {
        super(layout);
    }

    public BaseEditFragment(int layout, int menu) {
        super(layout, menu);
    }

    public abstract boolean onValidate();

    public abstract boolean onSave();

}
