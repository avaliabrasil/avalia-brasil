package org.avaliabrasil.avaliabrasil.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Pedro on 29/02/2016.
 */
public class AvbAuthenticatorService extends Service {
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