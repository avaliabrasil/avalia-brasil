package org.avaliabrasil.avaliabrasil.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Pedro on 29/02/2016.
 */
public class AvbSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static AvbSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new AvbSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}
