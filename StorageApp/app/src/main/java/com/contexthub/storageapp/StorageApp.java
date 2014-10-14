package com.contexthub.storageapp;

import android.app.Application;

import com.chaione.contexthub.sdk.ContextHub;

/**
 * Created by andy on 10/13/14.
 */
public class StorageApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register the app id of the application you created on https://app.contexthub.com
        ContextHub.init(this, "YOUR-APP-ID-HERE");
    }
}
