package ca.dalezak.androidbase.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.utils.Log;

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
            F currentFragment = tabsAdapter.getFragment(position);
            if (currentFragment.isAdded()) {
                onTabSelected(position, currentFragment);
            }
        }
        else {
            viewPager.setCurrentItem(position, animated);
        }
    }

    protected abstract boolean onTabSelected(int position, F fragment);

    protected abstract boolean onTabUnselected(int position, F fragment);

    @Override
    public void onFragmentInflate(BaseFragment fragment) {}

    @Override
    public void onFragmentAttach(BaseFragment fragment) {}

    @Override
    public void onFragmentCreate(BaseFragment fragment) {}

    @Override
    public void onFragmentViewCreated(BaseFragment fragment) {}

    @Override
    public void onFragmentActivityCreated(BaseFragment fragment) {}

    @Override
    public void onFragmentConfigurationChanged(BaseFragment fragment) {}

    @Override
    public void onFragmentStart(BaseFragment fragment) {}

    @Override
    public void onFragmentResume(BaseFragment fragment) {
        if (current == -1) {
            Log.i(this, "onFragmentResume -1 > 0 %s", fragment);
            F currentFragment = tabsAdapter.getFragment(0);
            if (currentFragment.isAdded()) {
                onTabSelected(0, currentFragment);
            }
            current = 0;
        }
        else if (current == viewPager.getCurrentItem()) {
            Log.i(this, "onFragmentResume %d %s", current, fragment);
            F currentFragment = tabsAdapter.getFragment(current);
            if (currentFragment.isAdded()) {
                onTabSelected(current, currentFragment);
            }
        }
    }

    @Override
    public void onFragmentVisible(BaseFragment fragment) {
        fragment.setHasOptionsMenu(fragment.menuResource != 0);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onFragmentPause(BaseFragment fragment) {}

    @Override
    public void onFragmentStop(BaseFragment fragment) {}

    @Override
    public void onFragmentDestroy(BaseFragment fragment) {}

    @Override
    public void onFragmentDetach(BaseFragment fragment) {}

    @Override
    public void onFragmentHidden(BaseFragment fragment) {
        fragment.setHasOptionsMenu(false);
        getActivity().invalidateOptionsMenu();
    }

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
            Log.i(this, "getItem %d", position);
            if (position > -1) {
                Class<? extends F> tabClass = getTabClass(position);
                if (tabClass != null) {
                    F fragment = (F)Fragment.instantiate(getActivity(), tabClass.getName());
                    fragment.setCallback(BaseTabFragment.this);
                    tabs.put(position, fragment);
                    return fragment;
                }
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            F fragment = (F)object;
            if (fragment != null) {
                for (Map.Entry<Integer, F> entry : tabs.entrySet()) {
                    if (entry.getValue().equals(fragment)) {
                        Log.i(this, "getItemPosition %d %s", entry.getKey(), fragment);
                        return entry.getKey();
                    }
                }
            }
            Log.i(this, "getItemPosition POSITION_NONE %s", fragment);
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i(this, "instantiateItem %d", position);
            F fragment = (F)super.instantiateItem(container, position);
            tabs.put(position, fragment);
            return fragment;
        }

        @Override
        public Parcelable saveState() {
            Log.i(this, "saveState");
            tabs.clear();
            return super.saveState();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
            Log.i(this, "restoreState");
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i(this, "destroyItem %d %s", position, object);
            tabs.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.i(this, "setPrimaryItem %d %s", position, object);
        }

        public F getFragment(int position) {
            if (position > -1) {
                F fragment = tabs.get(position);
                if (fragment == null) {
                    fragment = getItem(position);
                    Log.i(this, "getFragment %d New %s", position, fragment);
                }
                else {
                    Log.i(this, "getFragment %d Exists %s", position, fragment);
                }
                return fragment;
            }
            return null;
        }

        @Override
        public void onPageSelected(int position) {
            if (current == position) {
                Log.i(this, "onPageSelected %d == %d", current, position);
                F currentFragment = tabsAdapter.getFragment(position);
                Log.i(this, "Next %d %s", position, currentFragment);
                if (currentFragment != null && currentFragment.isAdded()) {
                    onTabSelected(position, currentFragment);
                }
            }
            else if (current > -1) {
                Log.i(this, "onPageSelected %d > %d", current, position);
                F previousFragment = tabsAdapter.getFragment(current);
                Log.i(this, "Previous %d %s", current, previousFragment);
                if (onTabUnselected(current, previousFragment)) {
                    F nextFragment = tabsAdapter.getFragment(position);
                    Log.i(this, "Next %d %s", position, nextFragment);
                    if (nextFragment != null && nextFragment.isAdded()) {
                        onTabSelected(position, nextFragment);
                    }
                    current = position;
                }
                else if (viewPager.getCurrentItem() != current) {
                    Log.i(this, "Return %d != %d %s", viewPager.getCurrentItem(), current, previousFragment);
                    viewPager.setCurrentItem(current, false);
                    if (previousFragment != null && previousFragment.isAdded()) {
                        onTabSelected(current, previousFragment);
                    }
                }
                else {
                    Log.i(this, "Return %d == %d %s", viewPager.getCurrentItem(), current, previousFragment);
                    tabsAdapter.notifyDataSetChanged();
                }
            }
            else {
                Log.i(this, "onPageSelected %d", position);
                F nextFragment = tabsAdapter.getFragment(position);
                Log.i(this, "Next %d %s", position, nextFragment);
                if (nextFragment != null && nextFragment.isAdded()) {
                    onTabSelected(position, nextFragment);
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