# Storage (Vault) Sample app

The Storage sample app that introduces you to the vault features of the ContextHub Android SDK.

### Table of Contents

1. **[Purpose](#purpose)**
2. **[ContextHub Use Case](#contexthub-use-case)**
3. **[Background](#background)**
4. **[Getting Started](#getting-started)**
5. **[Running the Sample App](#running-the-sample-app)**
6. **[Developer Portal](#developer-portal)**
7. **[ADB Logcat](#adb-logcat)**
8. **[Sample Code](#sample-code)**
9. **[Usage](#usage)**
  - **[Creating a Vault Item](#creating-a-vault-item)**
  - **[Retrieving Vault Items by Tag](#retrieving-vault-items-by-tag)**
  - **[Retrieving Vault Items by KeyPath](#retrieving-vault-items-by-keypath)**
  - **[Retrieving a Vault Item by ID](#retrieving-vault-items-by-id)**
  - **[Updating a Vault Item](#updating-a-vault-item)**
  - **[Deleting a Vault Item](#deleting-a-vault-item)**
10. **[Final Words](#final-words)**

## Purpose

This sample application will show you how to create, retrieve, update, delete (CRUD) and perform key path vault search on vault items in ContextHub.

## ContextHub Use Case

In this sample application, we use ContextHub to CRUD vault items on the server then list them in a simple ListView. ContextHub allows you to store small amounts of data on the server which can be accessed by all devices without starting up your own database server.

## Background

The "vault" in ContextHub allows developers to store JSON-compliant data structures on a server to be accessed by all devices with your app and most importantly context rules. Data stored in the vault can be simple key-values as well as nested structures of arrays and serializable objects. It is important to note that "vault" is not a relational database and is not meant to store millions of records that need relational queries performed on them. A proper database is still necessary for those kinds of scenarios.

## Getting Started

1. Get started by either forking or cloning the Storage repo. Visit [GitHub Help](https://help.github.com/articles/fork-a-repo) if you need help.
2. Go to [ContextHub](http://app.contexthub.com) and create a new application.
3. Find the app id associated with the application you just created. Its format looks something like this: `13e7e6b4-9f33-4e97-b11c-79ed1470fc1d`.
4. Open up your project and put the app id into the `ContextHub.init(this, "YOUR-APP-ID-HERE")` method call in the `StorageApp` class.
5. Build and run the project on your device.
6. You should see a blank list view (no vault items have been entered yet)

## Running the Sample App

1. In the app, tap the "+" button, to create a new vault item. Enter values for each item then tap "Done".
2. Your vault item should now appear in the list showing that it's now persisted on the server.
3. Enter either the first or last name in the search bar to find a record with that value (needs to be entered exactly).

## Developer Portal

1. In the [developer portal](http://app.contexthub.com), go to "Vault" and click on the item you just created.
2. You should now see the JSON-representation of your data present on the server. Go ahead and change a value now (changes that are not JSON-compliant will not be saved).
3. Stop and restart the app to see the change reflected!


## ADB Logcat

1. This sample app will log responses from the ContextHub Android SDK as you create, retrieve, update, delete and search for vault items in the app.
2. Use the logged statements to get an idea of the structures returned from each of these API calls to become more familiar with them.

## Sample Code

In this sample, most of the important code that deals with CRUDing vault items can be found in the `VaultItemListFragment` and `EditVaultItemFragment` fragments. Each method goes though a single operation you'll need to use the `VaultProxy` class. The stored objects must implement the `serializable` interface so they can be serialized when stored in vault on ContextHub. Use the `transient` keyword to exclude fields you don't want to be persisted. When retrieving items from the vault, the ContextHub Android SDK will automatically deserialize data into the specified class type.

## Usage

##### Creating a Vault Item
```java
class Person implements Serializable {

  String name;
  String title;
  ArrayList<String> nicknames;
  int heightInInches;
  int age;

  public Person(String name, String title, ArrayList<String> nicknames, int heightInInches, int age) {
    this.name = name;
    this.title = title;
    this.nicknames = nicknames;
    this.heightInInches = heightInInches;
    this.age = age;
  }

  @Override
  public String toString() {
    return String.format("name: %s\ntitle: %s\nnicknames: %s\nheightInInches: %s\nage: %s",
        name, title, nicknames, heightInInches, age);
  }
}

//...

// Add Kramer's information to the vault
ArrayList<String> nicknames = new ArrayList<String>(Arrays.asList(new String[]{"Hipster Doofus", "K-Man"}));
Person person = new Person("Cosmo Kramer", "CEO, Kramerica Industries", nicknames, 75, 32);
VaultProxy<Person> proxy = new VaultProxy<Person>();
proxy.createDocument(person, Person.class, new String[]{"sample"}, new VaultCallback<Person>() {
    @Override
    public void onSuccess(VaultDocument<Person> result) {
      Log.d(TAG, result.getDataObject().toString());
    }

    @Override
    public void onFailure(Exception e) {
      Log.d(TAG, e.getMessage());
    }
});
```

##### Retrieving Vault Items by Tag
```java
// Get all vault documents with the tag "sample" and log the results
VaultProxy<Person> proxy = new VaultProxy<Person>();
proxy.listDocuments(new String[]{"sample"}, Person.class, new VaultListingCallback<Person>() {
    @Override
    public void onSuccess(VaultDocument<Person>[] result) {
      for(VaultDocument<Person> document : result) {}
        Log.d(TAG, document.getDataObject().toString());
      }
    }

    @Override
    public void onFailure(Exception e) {
      Log.d(TAG, e.getMessage());
    }
});
```

##### Retrieving Vault Items by KeyPath
```java
// Get all vault documents with the tag "sample" and match "name = 'Cosmo Kramer'"
VaultProxy<Person> proxy = new VaultProxy<Person>();
proxy.listDocuments("name", "Cosmo Kramer", new String[]{"sample"}, Person.class, new VaultListingCallback<Person>() {
    @Override
    public void onSuccess(VaultDocument<Person>[] result) {
      for(VaultDocument<Person> document : result) {}
        Log.d(TAG, document.getDataObject().toString());
      }
    }

    @Override
    public void onFailure(Exception e) {
      Log.d(TAG, e.getMessage());
    }
});
```

##### Retrieving Vault Items by ID
```java
// Getting a vault item with a specific ID
VaultProxy<Person> proxy = new VaultProxy<Person>();
proxy.getDocument("41D8C25F-0464-41C5-A2E3-AAC234F240E4", Person.class, new VaultCallback<Person>() {
    @Override
    public void onSuccess(VaultDocument<Person> result) {
      Log.d(TAG, result.getDataObject().toString());
    }

    @Override
    public void onFailure(Exception e) {
      Log.d(TAG, e.getMessage());
    }
});
```

##### Updating a Vault Item
```java
// Updating a vault item with the first name "Darin" and adding the tag "employee"
// Update *replaces* a vault record with new contents so you must have a copy of the previous record if you want to make a change to a single value
person.setName("Darin");
VaultProxy<Person> proxy = new VaultProxy<Person>();
proxy.updateDocument(person, Person.class, "B89ECE55-9A3B-4998-AD58-2927F99802B7", new String[]{"kramerica", "employee"}, new VaultCallback<Person>() {
    @Override
    public void onSuccess(VaultDocument<Person> result) {
      Log.d(TAG, result.getDataObject().toString());
    }

    @Override
    public void onFailure(Exception e) {
      Log.d(TAG, e.getMessage());
    }
});

```

##### Deleting a Vault Item
```java
// Deleting a vault item
final String id = "B89ECE55-9A3B-4998-AD58-2927F99802B7";
VaultProxy<Person> proxy = new VaultProxy<Person>();
proxy.deleteDocument(id, new Callback<Object>() {
    @Override
    public void onSuccess(Object result) {
      Log.d(TAG, String.format("Successfully deleted item id %s", id));
    }

    @Override
    public void onFailure(Exception e) {
      Log.d(TAG, e.getMessage());
    }
});
```

##### Final Words
That's it! Hopefully this sample application showed you how easy it is to work with vault items in ContextHub to easily store information accessible from all devices and context rules.
