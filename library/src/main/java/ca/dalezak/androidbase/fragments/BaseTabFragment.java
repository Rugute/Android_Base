package ca.dalezak.androidbase.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseTabFragment<F extends BaseFragment>
        extends BaseFragment
        implements BaseFragment.Callback {

    private List<Integer> tabTitles = new ArrayList<>();
    private List<Class<? extends F>> tabClasses = new ArrayList<>();
    private TabsAdapter tabsAdapter;
    private int current = -1;

    @Control("view_pager")
    protected ViewPager viewPager;

    @Control("tab_strip")
    protected PagerTabStrip tabStrip;

    public BaseTabFragment() {
        super(R.layout.fragment_tabs);
    }

    public BaseTabFragment(int layout) {
        super(layout);
    }

    public BaseTabFragment(int layout, int menu) {
        super(layout, menu);
    }

    protected TabsAdapter getTabsAdapter() {
        return tabsAdapter;
    }

    protected int getCurrent() {
        return current;
    }

    protected ViewPager getViewPager() {
        return viewPager;
    }

    protected PagerTabStrip getTabStrip() {
        return tabStrip;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabsAdapter = new TabsAdapter(getFragmentManager());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Prefs.save(getActivity(), getSelectedKey(), viewPager.getCurrentItem());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (viewPager != null) {
            viewPager.setAdapter(tabsAdapter);
            viewPager.setOnPageChangeListener(tabsAdapter);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tabsAdapter.getCount() > 1 && tabStrip != null) {
            tabStrip.setVisibility(View.VISIBLE);
        }
        else if (tabStrip != null) {
            tabStrip.setVisibility(View.GONE);
        }
        if (Prefs.contains(getActivity(), getSelectedKey())) {
            int selected = Prefs.getInt(getActivity(), getSelectedKey());
            if (selected > 0 && selected < tabsAdapter.getCount()) {
                setTabSelected(selected, false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Prefs.remove(getActivity(), getClass().getName());
    }

    private String getSelectedKey() {
        return String.format("%s Selected Tab", getClass().getName());
    }

    public void addTab(int title, Class<? extends F> clazz) {
        tabTitles.add(title);
        tabClasses.add(clazz);
    }

    protected int getTabCount() {
        return tabTitles.size();
    }

    protected String getTabTitle(int position) {
        if (position > -1 && tabTitles.size() > position) {
            Integer title = tabTitles.get(position);
            return getString(title);
        }
        return null;
    }

    protected Class<? extends F> getTabClass(int position) {
        if (position > -1 && tabClasses.size() > position) {
            return tabClasses.get(position);
        }
        return null;
    }

    protected void setTabSelected(int position, boolean animated) {
        if (viewPager.getCurrentItem() == position) {
            F currentFragment = tabsAdapter.getItem(position);
            if (currentFragment.isAdded()) {
                onTabSelected(position, currentFragment);
                currentFragment.onSelected();
            }
        }
        else {
            viewPager.setCurrentItem(position, animated);
        }
    }

    @Override
    public void onFragmentCreated(BaseFragment fragment) {
        Log.i(this, "onFragmentCreated %s", fragment);
        if (current == -1) {
            F currentFragment = tabsAdapter.getItem(0);
            if (currentFragment.isAdded()) {
                onTabSelected(0, currentFragment);
                currentFragment.onSelected();
            }
            current = 0;
        }
        else if (current == tabsAdapter.getItemPosition(fragment)) {
            onTabSelected(current, (F)fragment);
            fragment.onSelected();
        }
    }

    protected abstract boolean onTabSelected(int position, F fragment);

    protected abstract boolean onTabUnselected(int position, F fragment);

    protected class TabsAdapter
            extends FragmentStatePagerAdapter
            implements ViewPager.OnPageChangeListener {

        protected Map<Integer, F> tabs = new HashMap<>();

        public TabsAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return getTabCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTabTitle(position);
        }

        public void refresh() {
            notifyDataSetChanged();
        }

        @Override
        public F getItem(int position) {
            if (position > -1) {
                F fragment = tabs.get(position);
                if (fragment == null) {
                    Class<? extends F> tabClass = getTabClass(position);
                    fragment = (F) Fragment.instantiate(getActivity(), tabClass.getName());
                    fragment.setCallback(BaseTabFragment.this);
                    tabs.put(position, fragment);
                }
                return fragment;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object != null) {
                for (Map.Entry<Integer, F> entry : tabs.entrySet()) {
                    if (entry.getValue().equals(object)) {
                        return entry.getKey();
                    }
                }
            }
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i(this, "destroyItem %d %s", position, object);
            try {
                tabs.remove(position);
                super.destroyItem(container, position, object);
            }
            catch (Exception exception) {
                Log.w(this, "Exception", exception);
            }
        }

        @Override
        public void onPageSelected(int position) {
            Log.i(this, "onPageSelected %d", position);
            if (current > -1) {
                F previousFragment = tabsAdapter.getItem(current);
                if (onTabUnselected(current, previousFragment)) {
                    previousFragment.onUnselected();
                    F currentFragment = tabsAdapter.getItem(position);
                    if (currentFragment != null && currentFragment.isAdded()) {
                        onTabSelected(position, currentFragment);
                        currentFragment.onSelected();
                    }
                    current = position;
                }
                else {
                    viewPager.setCurrentItem(current);
                    onTabSelected(current, previousFragment);
                }
            }
            else {
                F currentFragment = tabsAdapter.getItem(position);
                if (currentFragment != null && currentFragment.isAdded()) {
                    onTabSelected(position, currentFragment);
                    currentFragment.onSelected();
                }
                current = position;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }
}