package com.downs.bakingbuddy.IdlingResource;

import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A very simple implementation of {@link IdlingResource}.
 * <p>
 * Consider using CountingIdlingResource from espresso-contrib package if you use this class from
 * multiple threads or need to keep a count of pending operations.
 */
public class SimpleIdlingResource implements IdlingResource {

    @Nullable private volatile ResourceCallback mCallback;

    // In the code above we also initiate an AtomicBoolean object to control
    // the state of idleness. This class provides us with a boolean variable that
    // can be read and written to automatically. AtomicBooleans are used when multiple
    // threads need to check and change the boolean.
    // This happens to be perfect for our situation.
    //
    //Remember that if idle is false there are pending operations in the background
    // and any testing operations should be paused. If idle is true all is clear and
    // testing operations can continue.
    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);


    // Override the 3 required methods.
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    /**
     * Sets the new idle state, if isIdleNow is true, it pings the {@link ResourceCallback}.
     * @param isIdleNow false if there are pending operations, true if idle.
     */
    public void setIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        if (isIdleNow && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }
}

