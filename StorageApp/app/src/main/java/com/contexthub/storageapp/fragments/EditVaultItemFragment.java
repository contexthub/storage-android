package com.contexthub.storageapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaione.contexthub.sdk.VaultProxy;
import com.chaione.contexthub.sdk.callbacks.VaultCallback;
import com.chaione.contexthub.sdk.model.VaultDocument;
import com.contexthub.storageapp.models.Person;
import com.contexthub.storageapp.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by andy on 10/14/14.
 */
public class EditVaultItemFragment extends Fragment implements VaultCallback<Person> {

    private static final String ARG_VAULT_ID = "vault_id";
    private static final String ARG_PERSON = "person";

    @InjectView(R.id.name) EditText name;
    @InjectView(R.id.title) EditText title;
    @InjectView(R.id.age_seekbar) SeekBar ageSeekBar;
    @InjectView(R.id.age_value) TextView ageValue;
    @InjectView(R.id.height_seekbar) SeekBar heightSeekBar;
    @InjectView(R.id.height_value) TextView heightValue;
    @InjectView(R.id.nicknames) EditText nicknames;

    String vaultId = null;
    Person person;

    public static EditVaultItemFragment newInstance(VaultDocument<Person> document) {
        EditVaultItemFragment fragment = new EditVaultItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VAULT_ID, document.getVaultInfo().getId());
        args.putParcelable(ARG_PERSON, Parcels.wrap(document.getDataObject()));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_vault_item, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(R.string.edit_vault_item);
        if(getArguments() != null && getArguments().containsKey(ARG_PERSON) && getArguments().containsKey(ARG_VAULT_ID)) {
            vaultId = getArguments().getString(ARG_VAULT_ID);
            person = Parcels.unwrap(getArguments().getParcelable(ARG_PERSON));
            bindPerson();
        }
        else {
            person = new Person();
        }

        ageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ageValue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                heightValue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void bindPerson() {
        name.setText(person.getName());
        title.setText(person.getTitle());
        ageSeekBar.setProgress(person.getAge());
        ageValue.setText(String.valueOf(person.getAge()));
        heightSeekBar.setProgress(person.getHeightInInches());
        heightValue.setText(String.valueOf(person.getHeightInInches()));
        nicknames.setText(TextUtils.join(",", person.getNicknames()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_vault_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                hideSoftKeyboard();
                saveVaultItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
    }

    private void saveVaultItem() {
        if(!isValid()) return;

        person.setName(name.getText().toString());
        person.setTitle(title.getText().toString());
        person.setAge(ageSeekBar.getProgress());
        person.setHeightInInches(heightSeekBar.getProgress());
        person.setNicknames(parseNicknames());

        getActivity().setProgressBarIndeterminateVisibility(true);
        VaultProxy<Person> proxy = new VaultProxy<Person>();
        if(vaultId == null) {
            // Submit a request to ContextHub to create the document
            proxy.createDocument(person, Person.class, new String[]{"sample"}, this);
        }
        else {
            // Submit a request to ContextHub to update the specified document
            proxy.updateDocument(person, Person.class, vaultId, new String[]{"sample"}, this);
        }
    }

    private boolean isValid() {
        name.setError(null);
        title.setError(null);

        boolean isValid = true;
        if(TextUtils.isEmpty(name.getText())) {
            name.setError(getString(R.string.name_required));
            isValid = false;
        }
        if(TextUtils.isEmpty(title.getText())) {
            title.setError(getString(R.string.title_required));
            isValid = false;
        }
        return isValid;
    }

    private ArrayList<String> parseNicknames() {
        if(TextUtils.isEmpty(nicknames.getText())) return new ArrayList<String>();
        String trimmed = nicknames.getText().toString().replace(", ", ",");
        return new ArrayList<String>(Arrays.asList(trimmed.split(",")));
    }

    /**
     * Called after successfully creating or updating a vault document on the ContextHub server
     * @param personVaultDocument the document that was created or updated
     */
    @Override
    public void onSuccess(VaultDocument<Person> personVaultDocument) {
        getActivity().setProgressBarIndeterminateVisibility(false);
        Toast.makeText(getActivity(), vaultId == null ? R.string.vault_item_created :
                R.string.vault_item_updated, Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    /**
     * Called when an error occurs creating or updating a vault document on the ContextHub server
     * @param e the exception details
     */
    @Override
    public void onFailure(Exception e) {
        getActivity().setProgressBarIndeterminateVisibility(false);
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
