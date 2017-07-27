package com.hems.socketio.client.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.hems.socketio.client.provider.DatabaseContract;

import java.io.IOException;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by planet on 7/25/2017.
 */

public class SyncUtil {

    // Constants
    public static final String ACCOUNT_TYPE = "com.hems.socketio.client.datasync";
    private static final String TAG = SyncUtil.class.getSimpleName();
    private static final long SYNC_FREQUENCY = 60 * 60;
    // Instance fields
    static Account mAccount;

    // Incoming Intent key for extended data\
    public static final String KEY_SYNC_REQUEST =
            "com.hems.socketio.client.datasync.KEY_SYNC_REQUEST";

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context, String accountName) {
        boolean accountCreated = false;
        // Create the account type and default account
        mAccount = new Account(
                accountName, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(mAccount, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(mAccount, DatabaseContract.AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(mAccount, DatabaseContract.AUTHORITY, true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(mAccount, DatabaseContract.AUTHORITY, Bundle.EMPTY, SYNC_FREQUENCY);
            Log.w(TAG, "account created!");
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            accountCreated = true;
        } else {
            Log.w(TAG, "account creation failed!");
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        if (accountCreated) {
            // TODO check later if required
            //SyncAdapter.performSync();
        }
        return mAccount;
    }

    public static void DeleteSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        for (Account account : accountManager.getAccounts()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (accountManager.removeAccountExplicitly(account)) {
                    Log.w(TAG, "account deleted");
                }
            } else {
                accountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future) {
                        try {
                            if (future.getResult()) {
                                Log.w(TAG, "account deleted");
                            }
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
            }
        }

    }
}
