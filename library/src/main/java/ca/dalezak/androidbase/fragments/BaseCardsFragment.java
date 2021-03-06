package ca.dalezak.androidbase.fragments;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.adapters.BaseCardsAdapter;
import ca.dalezak.androidbase.animations.FadeIn;
import ca.dalezak.androidbase.animations.FadeOut;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Objects;
import ca.dalezak.androidbase.utils.Strings;
import ca.dalezak.androidbase.views.BaseCard;
import ca.dalezak.androidbase.annotations.Control;

public abstract class BaseCardsFragment<M extends BaseModel, C extends BaseCard, A extends BaseCardsAdapter<M, C>>
        extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, BaseCardsAdapter.OnAdapterListener<M, C> {

    @Control("swipe_loading")
    public TextView labelLoading;

    @Control("swipe_empty")
    public TextView labelEmpty;

    @Control("swipe_refresh")
    public SwipeRefreshLayout swipeLayout;

    @Control("recycler_view")
    public RecyclerView recyclerView;

    @Control("button_add")
    public FloatingActionButton buttonAdd;

    private int fab;
    private int empty;
    private A listAdapter;
    private GridLayoutManager layoutManager;
    private BaseScrollListener scrollListener;
    private SearchView searchView;
    private Class<A> listAdapterClass;

    private int phonePortrait = 1;
    private int phoneLandscape = 1;
    private int tabletPortrait = 1;
    private int tabletLandscape = 1;

    public BaseCardsFragment(Class<A> listAdapterClass) {
        super(R.layout.fragment_cards);
        this.listAdapterClass = listAdapterClass;
        this.empty = R.string.no_results;
    }

    public BaseCardsFragment(Class<A> listAdapterClass, int empty) {
        super(R.layout.fragment_cards);
        this.listAdapterClass = listAdapterClass;
        this.empty = empty;
    }

    public BaseCardsFragment(Class<A> listAdapterClass, int empty, int menu) {
        super(R.layout.fragment_cards, menu);
        this.listAdapterClass = listAdapterClass;
        this.empty = empty;
    }

    public BaseCardsFragment(Class<A> listAdapterClass, int empty, int menu, int fab) {
        super(R.layout.fragment_cards, menu);
        this.listAdapterClass = listAdapterClass;
        this.empty = empty;
        this.fab = fab;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listAdapter = Objects.createInstance(listAdapterClass, Context.class, getActivity());
        listAdapter.setAdapterListener(this);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setHasFixedSize(true);
        if (isTablet()) {
            if (isPortrait()) {
                layoutManager = new GridLayoutManager(getActivity(), tabletPortrait);
            }
            else {
                layoutManager = new GridLayoutManager(getActivity(), tabletLandscape);
            }
        }
        else {
            if (isPortrait()) {
                layoutManager = new GridLayoutManager(getActivity(), phonePortrait);
            }
            else {
                layoutManager = new GridLayoutManager(getActivity(), phoneLandscape);
            }
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(
                R.color.swipe_first,
                R.color.swipe_second,
                R.color.swipe_third,
                R.color.swipe_fourth);
        scrollListener = new BaseScrollListener();
        recyclerView.setOnScrollListener(scrollListener);
        if (labelEmpty != null) {
            labelEmpty.setText(this.empty);
        }
        if (fab != 0 && buttonAdd != null) {
            buttonAdd.setImageResource(fab);
            buttonAdd.setVisibility(View.VISIBLE);
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAddNew();
                }
            });
        }
        return view;
    }

    public void onLoadMore(int total, M last) {
        Log.i(this, "onLoadMore %d %s", total, last);
    }

    public void onAddNew() {
        Log.i(this, "onAddNew");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.refresh();
            listAdapter.filter(getSearchText());
        }
        scrollListener.reset(0, true);
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if (listAdapter != null) {
            listAdapter.refresh();
            listAdapter.filter(getSearchText());
        }
        scrollListener.reset(0, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
            swipeLayout.destroyDrawingCache();
            swipeLayout.clearAnimation();
        }
    }

    @Override
    public void onHidden() {
        super.onHidden();
    }

    public void onRefreshed() {
        Log.i(this, "onRefreshed %s", listAdapter);
        if (listAdapter != null) {
            listAdapter.refresh();
            listAdapter.filter(getSearchText());
        }
    }

    protected void setColumns(int phonePortrait, int phoneLandscape, int tabletPortrait, int tabletLandscape) {
        this.phonePortrait = phonePortrait;
        this.phoneLandscape = phoneLandscape;
        this.tabletPortrait = tabletPortrait;
        this.tabletLandscape = tabletLandscape;
    }

    protected FloatingActionButton getFloatingActionButton() {
        return buttonAdd;
    }

    public boolean hasSearchView() {
        return searchView != null;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    protected TextView getLabelLoading() {
        return labelLoading;
    }

    protected TextView getLabelEmpty() {
        return labelEmpty;
    }

    protected SwipeRefreshLayout getSwipeLayout() {
        return swipeLayout;
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    protected A getListAdapter() {
        return listAdapter;
    }

    public boolean hasSearchText() {
        return getIntent() != null &&
                getIntent().hasExtra(SearchManager.QUERY) &&
                getIntent().getStringExtra(SearchManager.QUERY).length() > 0;
    }

    public String getSearchText() {
        if (getIntent() != null && getIntent().hasExtra(SearchManager.QUERY)) {
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

    protected void onLoading() {
        if (labelEmpty != null) {
            labelEmpty.setText(R.string.loading_);
        }
    }

    protected void onLoaded() {
        if (labelEmpty != null) {
            labelEmpty.setText(this.empty);
        }
    }

    protected void onSearching() {
        if (labelEmpty != null) {
            labelEmpty.setText(R.string.searching_);
        }
    }

    protected void onSearched() {
        if (labelEmpty != null) {
            labelEmpty.setText(this.empty);
        }
    }

    public void showRefreshing() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
    }

    public void showRefreshing(int message) {
        if (isAdded()) {
            showRefreshing(getString(message));
        }
    }

    public void showRefreshing(int message, int total, int progress) {
        if (isAdded()) {
            showRefreshing(getString(message), total, progress);
        }
    }

    public void showRefreshing(String message) {
        showRefreshing(message, 0, 0);
    }

    public void showRefreshing(String message, int total, int progress) {
        if (isAdded()) {
            if (swipeLayout != null) {
                swipeLayout.setRefreshing(true);
            }
            if (labelLoading != null) {
                if (progress > 0 || total > 0) {
                    labelLoading.setText(String.format("%d %s %d %s", progress, getString(R.string.of), total, message));
                }
                else {
                    labelLoading.setText(message);
                }
                if (labelLoading.getVisibility() == View.GONE) {
                    labelLoading.setVisibility(View.INVISIBLE);
                    labelLoading.startAnimation(new FadeIn(labelLoading));
                }
            }
        }
    }

    public void hideRefreshing() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
        if (labelLoading != null) {
            if (labelLoading.getVisibility() == View.VISIBLE) {
                labelLoading.startAnimation(new FadeOut(labelLoading));
            }
            else if (labelLoading.getVisibility() == View.INVISIBLE) {
                labelLoading.startAnimation(new FadeOut(labelLoading));
            }
            else {
                labelLoading.setVisibility(View.GONE);
            }
        }
    }

    public void onCardRefreshed(int unfiltered, int filtered) {
        Log.i(this, "onCardRefreshed %d / %d", filtered, unfiltered);
        if (labelEmpty != null) {
            if (filtered == 0) {
                labelEmpty.setText(this.empty);
                labelEmpty.setVisibility(View.VISIBLE);
            }
            else {
                labelEmpty.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        for (int i = 0; i < menu.size(); i++) {
            final MenuItem menuItem = menu.getItem(i);
            View actionView = menu.getItem(i).getActionView();
            if (actionView != null && actionView instanceof SearchView) {
                Log.i(this, "SearchView %s", actionView);
                SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
                Log.i(this, "SearchManager %s", searchManager);
                SearchableInfo searchableInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
                Log.i(this, "SearchableInfo %s", searchableInfo);
                searchView = (SearchView)actionView;
                searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                searchView.setSearchableInfo(searchableInfo);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String text) {
                        if (listAdapter != null) {
                            listAdapter.filter(text);
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String text) {
                        if (listAdapter != null) {
                            listAdapter.filter(text);
                        }
                        return true;
                    }
                });
                searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean focussed) {
                        if (getActivity() != null && getActivity().getIntent() != null) {
                            Intent intent = getActivity().getIntent();
                            if (searchView.getQuery().length() > 0) {
                                CharSequence query = searchView.getQuery();
                                intent.putExtra(SearchManager.QUERY, query);
                            }
                            else if (!focussed) {
                                intent.removeExtra(SearchManager.QUERY);
                                if (menuItem != null && menuItem.isActionViewExpanded()) {
                                    menuItem.collapseActionView();
                                }
                            }
                        }
                    }
                });
                if (getActivity() != null && getActivity().getIntent() != null) {
                    Intent intent = getActivity().getIntent();
                    String query = intent.getStringExtra(SearchManager.QUERY);
                    if (!Strings.isNullOrEmpty(query)) {
                        if (menuItem != null) {
                            menuItem.expandActionView();
                        }
                        if (searchView != null) {
                            searchView.setQuery(query, false);
                        }
                    }
                }
            }
        }
    }

    private class BaseScrollListener extends RecyclerView.OnScrollListener {

        private boolean loading = true;
        private int totalItemCount = 0;
        private int previousItemCount = 0;
        private int visibleItemCount = 0;
        private int firstVisibleItem = 0;
        private int lastVisibleItem = 0;

        public BaseScrollListener() {}

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = getLayoutManager().getItemCount();
            firstVisibleItem = getLayoutManager().findFirstCompletelyVisibleItemPosition();
            lastVisibleItem = getLayoutManager().findLastCompletelyVisibleItemPosition();
            if (loading && (totalItemCount > previousItemCount)) {
                loading = false;
                previousItemCount = totalItemCount;
            }
            if (!loading && (lastVisibleItem == totalItemCount - 1)) {
                M model = listAdapter.getItem(totalItemCount - 1);
                if (model != null) {
                    onLoadMore(totalItemCount, model);
                    loading = true;
                }
            }
            swipeLayout.setEnabled(visibleItemCount > 0 && firstVisibleItem == 0);
        }

        public void reset(int previousItemCount, boolean loading) {
            this.previousItemCount = previousItemCount;
            this.loading = loading;
        }

        public boolean isLoading() {
            return loading;
        }
    }
}