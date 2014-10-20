package com.contexthub.storageapp.models;

import com.chaione.contexthub.sdk.model.VaultDocument;

import java.util.Comparator;

/**
 * Created by andy on 10/20/14.
 */
public class PersonComparator implements Comparator<VaultDocument<Person>> {

    @Override
    public int compare(VaultDocument<Person> personVaultDocument, VaultDocument<Person> personVaultDocument2) {
        return personVaultDocument.getDataObject().getName().compareTo(
                personVaultDocument2.getDataObject().getName());
    }
}
