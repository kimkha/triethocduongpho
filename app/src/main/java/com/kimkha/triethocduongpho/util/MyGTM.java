package com.kimkha.triethocduongpho.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;
import com.kimkha.triethocduongpho.R;

import java.util.concurrent.TimeUnit;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/19/15
 */
public class MyGTM {
    private static final String CONTAINER_ID = "GTM-XXXX";

    public static void init(Context context) {
        TagManager tagManager = TagManager.getInstance(context);
        PendingResult<ContainerHolder> pending = tagManager.loadContainerPreferNonDefault(CONTAINER_ID, R.raw.gtm_container);

        // The onResult method will be called as soon as one of the following happens:
//     1. a saved container is loaded
//     2. if there is no saved container, a network container is loaded
//     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("CuteAnimals", "failure loading container");
                    //displayErrorToUser(R.string.load_error);
                    return;
                }
                ContainerHolderSingleton.setContainerHolder(containerHolder);
//                ContainerLoadedCallback.registerCallbacksForContainer(container);
//                containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
//                startMainActivity();
            }
        }, 2, TimeUnit.SECONDS);
    }
}
