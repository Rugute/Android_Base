package ca.dalezak.androidbase.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.adapters.BaseCardAdapter;
import ca.dalezak.androidbase.animations.FadeIn;
import ca.dalezak.androidbase.animations.FadeOut;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Objects;
import ca.dalezak.androidbase.utils.Strings;
import ca.dalezak.androidbase.views.BaseCard;
import ca.dalezak.androidbase.annotations.Control;

public abstract class BaseCardsFragment<M extends BaseModel, C extends BaseCard, A extends BaseCardAdapter<M, C>>
        extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, BaseCardAdapter.OnAdapterListener<M, C> {

    private int empty;
    protected A listAdapter;

    private LinearLayoutManager layoutManager;

    @Control("swipe_loading")
    public TextView labelLoading;

    @Control("swipe_empty")
    public TextView labelEmpty;

    @Control("swipe_refresh")
    public SwipeRefreshLayout swipeLayout;

    @Control("recycler_view")
    public RecyclerView recyclerView;

    public SearchView searchView;
    private Class<A> listAdapterClass;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutManager = new LinearLayoutManager(getActivity());
        listAdapter = Objects.createInstance(listAdapterClass, Context.class, getActivity());
        listAdapter.setAdapterListener(this);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setHasFixedSize(true);
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
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean enable = false;
                int totalItemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                    BaseCardsFragment.this.onScrolled(firstVisibleItem, visibleItemCount, totalItemCount, true);
                }
                else {
                    BaseCardsFragment.this.onScrolled(firstVisibleItem, visibleItemCount, totalItemCount, false);
                }
                if (recyclerView.getChildCount() > 0) {
                    boolean firstItemVisible = firstVisibleItem == 0;
                    boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeLayout.setEnabled(enable);
            }
        });
        if (labelEmpty != null) {
            labelEmpty.setText(this.empty);
        }
        return view;
    }

    public void onScrolled(int topPosition, int visibleCount, int totalCount, boolean isLast) {
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.refresh();
    }

    public void onRefreshed() {
        Log.i(this, "onRefreshed %s", listAdapter);
        if (listAdapter != null) {
            listAdapter.refresh();
            listAdapter.filter(getSearchText());
        }
    }

    protected LinearLayoutManager getLayoutManager() {
        return layoutManager;
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
        showRefreshing(getString(message));
    }

    public void showRefreshing(String message) {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(true);
        }
        if (labelLoading != null) {
            labelLoading.setText(message);
            if (labelLoading.getVisibility() == View.GONE) {
                labelLoading.setVisibility(View.INVISIBLE);
                labelLoading.startAnimation(new FadeIn(labelLoading));
            }
        }
    }

    public void hideRefreshing() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
        if (labelLoading != null) {
            labelLoading.startAnimation(new FadeOut(labelLoading));
        }
    }

    public void onCardRefreshed(int unfiltered, int filtered) {
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
}