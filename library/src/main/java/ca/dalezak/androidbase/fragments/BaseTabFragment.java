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

    private final String SELECTED_TAB = "Selected Tab";

    protected TabsAdapter tabsAdapter;

    protected int current = -1;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabsAdapter = new TabsAdapter(getFragmentManager());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Prefs.save(getActivity(), SELECTED_TAB, viewPager.getCurrentItem());
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
        if (Prefs.contains(getActivity(), SELECTED_TAB)) {
            int selectedTab = Prefs.getInt(getActivity(), SELECTED_TAB);
            if (tabsAdapter.getCount() > selectedTab) {
                current = selectedTab;
                viewPager.setCurrentItem(selectedTab, false);
                onTabSelected(selectedTab, false);
            }
            else {
                viewPager.setCurrentItem(0, false);
            }
        }
        else {
            viewPager.setCurrentItem(0, false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(this, "onBackPressed");
        Prefs.remove(getActivity(), SELECTED_TAB);
    }

    public void addTab(int title, Class<? extends F> clazz) {
        tabTitles.add(title);
        tabClasses.add(clazz);
    }

    protected int getTabCount() {
        return tabTitles.size();
    }

    protected String getTabTitle(int position) {
        Integer title = tabTitles.get(position);
        return getString(title);
    }

    protected Class<? extends F> getTabClass(int position) {
        return tabClasses.get(position);
    }

    protected void onTabSelected(int position, boolean animated) {
//        F fragment = tabsAdapter.getItem(position);
//        onTabSelected(position, fragment);
//        current = position;
//        viewPager.setCurrentItem(position, animated);
        F previousFragment = tabsAdapter.getItem(current);
        if (previousFragment != null && onTabUnselected(current, previousFragment)) {
            F currentFragment = tabsAdapter.getItem(position);
            viewPager.setCurrentItem(position, animated);
            onTabSelected(position, currentFragment);
            current = position;
        }
        else {
            viewPager.setCurrentItem(current, animated);
            onTabSelected(current, previousFragment);
        }
    }

    @Override
    public void onFragmentCreated(BaseFragment fragment) {
        Log.i(this, "onFragmentCreated %s", fragment);
        if (current == -1) {
            onTabSelected(0, tabsAdapter.getItem(0));
            current = 0;
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
            F fragment = tabs.get(position);
            if (fragment == null) {
                Class<? extends F> clazz = getTabClass(position);
                fragment = (F) Fragment.instantiate(getActivity(), clazz.getName());
                fragment.setCallback(BaseTabFragment.this);
                tabs.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object != null && tabs.containsValue((F)object)) {
                return super.getItemPosition(object);
            }
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i(this, "destroyItem %d %s", position, object);
            tabs.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public void onPageSelected(int position) {
            Log.i(this, "onPageSelected %d", position);
            if (current > -1) {
                F previousFragment = tabsAdapter.getItem(current);
                if (onTabUnselected(current, previousFragment)) {
                    F currentFragment = tabsAdapter.getItem(position);
                    onTabSelected(position, currentFragment);
                    current = position;
                }
                else {
                    viewPager.setCurrentItem(current);
                    onTabSelected(current, previousFragment);
                }
            }
            else {
                F currentFragment = tabsAdapter.getItem(position);
                onTabSelected(position, currentFragment);
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