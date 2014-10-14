package com.contexthub.storageapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;

import com.chaione.contexthub.sdk.model.VaultDocument;
import com.contexthub.storageapp.fragments.AboutFragment;
import com.contexthub.storageapp.fragments.EditVaultItemFragment;
import com.contexthub.storageapp.fragments.VaultItemListFragment;
import com.contexthub.storageapp.models.Person;


public class MainActivity extends FragmentActivity implements VaultItemListFragment.Listener, FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setProgressBarIndeterminate(true);

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
        SearchView searchView = (SearchView) menuSearch.getActionView();

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchText = (AutoCompleteTextView) searchPlate.findViewById(searchTextId);
        if (searchText!=null) {
            searchText.setHint(R.string.search_hint);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        launchEditVaultItemFragment(document);
    }

    @Override
    public void onBackStackChanged() {
        supportInvalidateOptionsMenu();
    }
}
