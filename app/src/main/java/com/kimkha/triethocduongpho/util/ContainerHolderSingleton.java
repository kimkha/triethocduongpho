package com.kimkha.triethocduongpho.util;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/19/15
 */

import com.google.android.gms.tagmanager.ContainerHolder;

/**
 * Singleton to hold the GTM Container (since it should be only created once
 * per run of the app).
 */
public class ContainerHolderSingleton {
    private static ContainerHolder containerHolder;

    /**
     * Utility class; don't instantiate.
     */
    private ContainerHolderSingleton() {
    }

    public static ContainerHolder getContainerHolder() {
        return containerHolder;
    }

    public static void setContainerHolder(ContainerHolder c) {
        containerHolder = c;
    }
}