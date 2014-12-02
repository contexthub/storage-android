package com.contexthub.storageapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.chaione.contexthub.sdk.model.VaultDocument;
import com.contexthub.storageapp.fragments.AboutFragment;
import com.contexthub.storageapp.fragments.EditVaultItemFragment;
import com.contexthub.storageapp.fragments.VaultItemListFragment;
import com.contexthub.storageapp.models.Person;


public class MainActivity extends ActionBarActivity implements VaultItemListFragment.Listener, FragmentManager.OnBackStackChangedListener {

    private MenuItem menuSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new VaultItemListFragment())
                    .commit();
            getSupportFragmentManager().addOnBackStackChangedListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        setupSearchView(menu.findItem(R.id.action_search));
        return true;
    }

    private void setupSearchView(final MenuItem menuSearch) {
        this.menuSearch = menuSearch;

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuSearch);
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setHint(R.string.search_hint);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                menuSearch.collapseActionView();
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(android.R.id.content, VaultItemListFragment.newInstance(query))
                        .commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isMainFragment = getSupportFragmentManager().getBackStackEntryCount() <= 0;
        menu.findItem(R.id.action_search).setVisible(isMainFragment);
        menu.findItem(R.id.action_add).setVisible(isMainFragment);
        menu.findItem(R.id.action_about).setVisible(isMainFragment);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuSearch.collapseActionView();

        switch(item.getItemId()) {
            case R.id.action_add:
                launchEditVaultItemFragment(null);
                return true;
            case R.id.action_about:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(android.R.id.content, new AboutFragment())
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchEditVaultItemFragment(VaultDocument<Person> document) {
        EditVaultItemFragment fragment = document == null ? new EditVaultItemFragment() :
                EditVaultItemFragment.newInstance(document);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(android.R.id.content, fragment)
                .commit();
    }

    @Override
    public void onItemClick(VaultDocument<Person> document) {
        menuSearch.collapseActionView();
        launchEditVaultItemFragment(document);
    }

    @Override
    public void onBackStackChanged() {
        supportInvalidateOptionsMenu();
    }
}
