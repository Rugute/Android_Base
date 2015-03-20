package ca.dalezak.androidbase.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.adapters.BaseListAdapter;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Objects;
import ca.dalezak.androidbase.utils.Strings;

public abstract class BaseListFragment<M extends BaseModel, A extends BaseListAdapter>
        extends BaseFragment
        implements AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    @Control(id=android.R.id.empty)
    public TextView labelEmpty;

    @Control("swipe_label")
    public TextView labelLoading;

    @Control("swipe_refresh")
    public SwipeRefreshLayout swipeLayout;

    @Control("button_add")
    public ImageButton buttonAdd;

    @Control(id=android.R.id.list)
    public ListView listView;

    private int fab;
    private A listAdapter;
    private SearchView searchView;
    private Class<A> listAdapterClass;

    public BaseListFragment(Class<A> listAdapterClass) {
        super(R.layout.fragment_list);
        this.listAdapterClass = listAdapterClass;
    }

    public BaseListFragment(Class<A> listAdapterClass, int menu) {
        super(R.layout.fragment_list, menu);
        this.listAdapterClass = listAdapterClass;
    }

    public BaseListFragment(Class<A> listAdapterClass, int menu, int fab) {
        super(R.layout.fragment_list, menu);
        this.listAdapterClass = listAdapterClass;
        this.fab = fab;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listAdapter = Objects.createInstance(listAdapterClass, Activity.class, getActivity());
        listView.setAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (labelEmpty != null) {
            listView.setEmptyView(labelEmpty);
        }
        listView.setOnItemClickListener(this);
        listView.setFocusable(true);
        listView.setFocusableInTouchMode(true);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(
                R.color.swipe_first,
                R.color.swipe_second,
                R.color.swipe_third,
                R.color.swipe_fourth);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }

            @Override
            public void onScroll(AbsListView v, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (listView != null && listView.getChildCount() > 0) {
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeLayout.setEnabled(enable);
            }
        });
        if (fab != 0 && buttonAdd != null) {
            buttonAdd.setImageResource(fab);
            buttonAdd.setVisibility(View.VISIBLE);
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlphaAnimation animation = new AlphaAnimation(1F, 0.5F);
                    animation.setDuration(400);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            onAddNew();
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    view.startAnimation(animation);
                }
            });
        }
        return view;
    }

    public void onAddNew() {
        Log.i(this, "onAddNew");
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        for (int i = 0; i < menu.size(); i++) {
            final MenuItem menuItem = menu.getItem(i);
            View actionView = menu.getItem(i).getActionView();
            if (actionView != null && actionView instanceof SearchView) {
                SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
                searchView = (SearchView)actionView;
                searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String text) {
                        listAdapter.filter(text);
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String text) {
                        listAdapter.filter(text);
                        return true;
                    }
                });
                searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (searchView.getQuery().length() > 0) {
                            getActivity().getIntent().putExtra(SearchManager.QUERY, searchView.getQuery());
                        }
                        else if (!hasFocus) {
                            getActivity().getIntent().removeExtra(SearchManager.QUERY);
                            menuItem.collapseActionView();
                        }
                    }
                });
                if (getActivity().getIntent().hasExtra(SearchManager.QUERY)) {
                    String query = getActivity().getIntent().getStringExtra(SearchManager.QUERY);
                    if (!Strings.isNullOrEmpty(query)) {
                        menuItem.expandActionView();
                        searchView.setQuery(query, false);
                    }
                }
            }
        }
    }

    protected ImageButton getButtonAdd() {
        return buttonAdd;
    }

    public boolean hasSearchView() {
        return searchView != null;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    protected A getListAdapter() {
        return listAdapter;
    }

    protected SwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    protected TextView getLabelLoading() {
        return labelLoading;
    }

    protected TextView getLabelEmpty() {
        return labelEmpty;
    }

    protected ListView getListView() {
        return listView;
    }

    protected void onLoading() {
        if (labelEmpty != null) {
            labelEmpty.setText(R.string.loading_);
        }
    }

    protected void onLoaded() {
        if (labelEmpty != null) {
            labelEmpty.setText(R.string.no_results);
        }
    }

    protected void onSearching() {
        if (labelEmpty != null) {
            labelEmpty.setText(R.string.searching_);
        }
    }

    protected void onSearched() {
        if (labelEmpty != null) {
            labelEmpty.setText(R.string.no_results);
        }
    }

    public void setEmpty(String message) {
        if (labelEmpty != null) {
            labelEmpty.setText(message);
        }
    }

    public void setEmpty(int message) {
        if (labelEmpty != null) {
            labelEmpty.setText(message);
        }
    }

    public void hideRefreshing() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
        if (labelLoading != null) {
            final AlphaAnimation fade_out = new AlphaAnimation(1.0f, 0.0f);
            fade_out.setDuration(300);
            fade_out.setAnimationListener(new Animation.AnimationListener(){
                public void onAnimationStart(Animation anim){}
                public void onAnimationRepeat(Animation anim){}
                public void onAnimationEnd(Animation anim){
                    labelLoading.setText(null);
                    labelLoading.setVisibility(View.GONE);
                }
            });
            labelLoading.startAnimation(fade_out);
        }
    }

    public void showRefreshing() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
    }

    public void showRefreshing(String message) {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        if (labelLoading != null) {
            labelLoading.setText(message);
            if (labelLoading.getVisibility() == View.GONE) {
                labelLoading.setVisibility(View.INVISIBLE);
                final AlphaAnimation fade_in = new AlphaAnimation(0.0f, 1.0f);
                fade_in.setDuration(300);
                fade_in.setAnimationListener(new Animation.AnimationListener(){
                    public void onAnimationStart(Animation anim){}
                    public void onAnimationRepeat(Animation anim){}
                    public void onAnimationEnd(Animation anim) {
                        labelLoading.setVisibility(View.VISIBLE);
                    }
                });
                labelLoading.startAnimation(fade_in);
            }
        }
    }

    public void onRefreshed() {
        Log.i(this, "onRefreshed %s", listAdapter);
        if (listAdapter != null) {
            listAdapter.refresh();
            listAdapter.filter(getSearchText());
        }
    }

    public boolean hasSearchText() {
        return getIntent().hasExtra(SearchManager.QUERY) &&
               getIntent().getStringExtra(SearchManager.QUERY).length() > 0;
    }

    public String getSearchText() {
        if (getIntent().hasExtra(SearchManager.QUERY)) {
            return getIntent().getStringExtra(SearchManager.QUERY);
        }
        return null;
    }

    public void setSearchText(String text) {
        onSearching();
        if (listAdapter != null) {
            listAdapter.filter(text);
        }
        if (searchView != null) {
            searchView.setQuery(text, false);
        }
        onSearched();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        onSearching();
        listAdapter.filter(getSearchText());
        onSearched();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

}