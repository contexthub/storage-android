package com.contexthub.storageapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaione.contexthub.sdk.VaultProxy;
import com.chaione.contexthub.sdk.callbacks.Callback;
import com.chaione.contexthub.sdk.callbacks.VaultListingCallback;
import com.chaione.contexthub.sdk.model.VaultDocument;
import com.contexthub.storageapp.R;
import com.contexthub.storageapp.models.Person;
import com.contexthub.storageapp.models.PersonComparator;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by andy on 10/14/14.
 */
public class VaultItemListFragment extends Fragment implements VaultListingCallback<Person>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String ARG_QUERY = "query";

    @InjectView(android.R.id.list) ListView list;
    @InjectView(android.R.id.empty) TextView empty;

    VaultProxy<Person> proxy = new VaultProxy<Person>();
    Listener listener;

    public interface Listener {
        public void onItemClick(VaultDocument<Person> document);
    }

    public static VaultItemListFragment newInstance(String query) {
        VaultItemListFragment fragment = new VaultItemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        if(activity instanceof Listener) {
            listener = (Listener) activity;
        }
        else {
            throw new IllegalArgumentException("Activity must implement Listener interface");
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vault_item_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadItems();
    }

    private void loadItems() {
        getActivity().setProgressBarIndeterminateVisibility(true);
        String[] tags = new String[]{"sample"};
        if(getArguments() != null && getArguments().containsKey(ARG_QUERY)) {
            String query = getArguments().getString(ARG_QUERY);
            getActivity().getActionBar().setTitle(getString(R.string.search_for_title, query));
            // Query ContextHub for vault documents with a "sample" tag and name value matching the search string
            proxy.listDocuments("name", query, tags, Person.class, this);
        }
        else {
            // Query ContextHub for vault documents with a "sample" tag
            getActivity().getActionBar().setTitle(R.string.vault_items);
            proxy.listDocuments(tags, Person.class, this);
        }
    }

    /**
     * Called after successfully fetching vault documents from ContextHub
     * @param vaultDocuments the resulting documents
     */
    @Override
    public void onSuccess(VaultDocument<Person>[] vaultDocuments) {
        getActivity().setProgressBarIndeterminateVisibility(false);
        Arrays.sort(vaultDocuments, new PersonComparator());
        PersonAdapter adapter = new PersonAdapter(getActivity(), vaultDocuments);
        list.setAdapter(adapter);
        list.setEmptyView(empty);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);
    }

    /**
     * Called when an error occurs fetching vault documents from ContextHub
     * @param e the exception details
     */
    @Override
    public void onFailure(Exception e) {
        getActivity().setProgressBarIndeterminateVisibility(false);
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        VaultDocument<Person> document = (VaultDocument<Person>) adapterView.getAdapter().getItem(i);
        listener.onItemClick(document);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        VaultDocument<Person> document = (VaultDocument<Person>) adapterView.getAdapter().getItem(i);
        showDeleteConfirmDialog(document);
        return true;
    }

    private void showDeleteConfirmDialog(final VaultDocument<Person> document) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().setProgressBarIndeterminateVisibility(true);
                        // Submit a request to ContextHub to delete the specified vault document
                        proxy.deleteDocument(document.getVaultInfo().getId(), deleteCallback);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private Callback<Object> deleteCallback = new Callback<Object>() {

        /**
         * Called after successfully deleting a vault document from ContextHub
         * @param o
         */
        @Override
        public void onSuccess(Object o) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            Toast.makeText(getActivity(), R.string.vault_item_deleted, Toast.LENGTH_SHORT).show();
            loadItems();
        }

        /**
         * Called when an error occurs deleting a vault document from ContextHub
         * @param e the exception details
         */
        @Override
        public void onFailure(Exception e) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    class PersonAdapter extends ArrayAdapter<VaultDocument<Person>> {

        public PersonAdapter(Context context, VaultDocument<Person>[] objects) {
            super(context, -1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.vault_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Person person = getItem(position).getDataObject();
            holder.name.setText(person.getName());
            holder.title.setText(person.getTitle());
            holder.age.setText(getString(R.string.age_years_old, person.getAge()));
            holder.height.setText(getString(R.string.height_inches, person.getHeightInInches()));
            holder.nicknames.setText(TextUtils.join(", ", person.getNicknames()));

            return convertView;
        }
    }

    class ViewHolder {
        @InjectView(R.id.name) TextView name;
        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.age) TextView age;
        @InjectView(R.id.height) TextView height;
        @InjectView(R.id.nicknames) TextView nicknames;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
