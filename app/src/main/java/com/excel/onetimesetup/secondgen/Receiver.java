package com.excel.onetimesetup.secondgen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.excel.configuration.ConfigurationReader;

/**
 * Created by Sohail on 08-11-2016.
 */

public class Receiver extends BroadcastReceiver {

    final static String TAG = "Receiver";

    @Override
    public void onReceive( Context context, Intent intent ) {
        String action = intent.getAction();
        Log.d( TAG, "action : " + action );

        //if( action.equals( "connectivity_change" ) || action.equals( "android.net.conn.CONNECTIVITY_CHANGE" ) ){
        if( action.equals( "connectivity_change" ) || action.equals( "android.net.conn.CONNECTIVITY_CHANGE" ) ){
            if( ! isOtsCompleted() )
                startOTS( context );
        }
        else if( action.equals( "android.intent.action.BOOT_COMPLETED" ) || action.equals( "boot_completed" ) ){
            /*if( ! isOtsCompleted() )
                startOTS( context );*/
        }

    }

    private void startOTS( Context context ){
        Intent in = new Intent( context, MainActivity.class );
        in.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( in );
    }

    public boolean isOtsCompleted(){
        ConfigurationReader configurationReader = ConfigurationReader.reInstantiate();
        String is_ots_comleted = configurationReader.getIsOtsCompleted();
        return (is_ots_comleted.equals( "1" ))?true:false;
    }
}
