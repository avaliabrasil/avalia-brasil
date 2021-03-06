package org.avaliabrasil.avaliabrasil2.avb.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Pedro on 29/02/2016.
 */
public class AvbAuthenticatorService extends Service {
    public final String LOG_TAG = this.getClass().getSimpleName();
    // Instance field that stores the authenticator object
    private AvbAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new AvbAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}