/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.sync;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import es.ugr.swad.swadroid.Constants;

/**
 * Class for launch a periodic sync request
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public final class PeriodicSyncReceiver extends BroadcastReceiver {

    private static Intent createIntent(Context context, String authority, Bundle extras) {
        Intent intent = new Intent(context, PeriodicSyncReceiver.class);
        intent.putExtra(Constants.AUTHORITY, authority);
        return intent;
    }

    public static PendingIntent createPendingIntent(Context context, String authority, Bundle extras) {
        int requestCode = 0;
        Intent intent = createIntent(context, authority, extras);
        int flags = 0;
        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String authority = intent.getStringExtra(Constants.AUTHORITY);

        ContentResolver.requestSync(null, authority, new Bundle());
    }
}
