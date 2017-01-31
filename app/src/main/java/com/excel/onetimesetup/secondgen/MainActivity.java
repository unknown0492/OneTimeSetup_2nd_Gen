package com.excel.onetimesetup.secondgen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.excel.configuration.ConfigurationReader;
import com.excel.configuration.ConfigurationWriter;
import com.excel.customitems.CustomItems;
import com.excel.excelclasslibrary.UtilArray;
import com.excel.excelclasslibrary.UtilFile;
import com.excel.excelclasslibrary.UtilMisc;
import com.excel.excelclasslibrary.UtilNetwork;
import com.excel.excelclasslibrary.UtilShell;
import com.excel.excelclasslibrary.UtilURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ConfigurationReader configurationReader;
    ConfigurationWriter configurationWriter;

    LinearLayout ll_select_language, ll_select_country, ll_select_timezone, ll_select_city, ll_set_room_no, ll_select_network, ll_set_cms_ip;
    TextView tv_select_country, tv_select_timezone, tv_select_city, tv_room_no, tv_select_network, tv_cms_ip, tv_select_language;
    TextView tv_country_value, tv_timezone_value, tv_city_value, tv_room_no_value, tv_network_value, tv_cms_ip_value, tv_language_value;

    Context context = this;
    public static final String TAG = "MainActivity";
    Button bt_verify_settings;

    String[] languages;
    String[] language_codes;
    HashMap<String,String> language_map;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        init();
    }

    public void init(){
        initViews();

        // Set Default Values on the Text Views (reading the default values from /system/appstv_data/configuration
        setDefaultValues();

        // Select Network
        selectNetworkListener();

        // Set Room Number Click
        roomNumberClickListener();

        // Set CMS IP
        setCMSIpListener();

        // Verify Settings Button
        verifySettings();
    }

    @Override
    protected void onResume() {
        super.onResume();

        configurationReader = ConfigurationReader.reInstantiate();

        // Set Network Selection Value
        setNetworkValue();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.d( TAG, "onBackPressed()" );

    }

    public void initViews(){
        hideActionBar();

        configurationReader = ConfigurationReader.getInstance();

        checkIfOTSAlreadyCompleted();
        initLanguages();


        ll_select_language = (LinearLayout) findViewById( R.id.ll_select_language );
        ll_select_country = (LinearLayout) findViewById( R.id.ll_select_country );
        ll_select_timezone = (LinearLayout) findViewById( R.id.ll_select_timezone );
        ll_select_city = (LinearLayout) findViewById( R.id.ll_select_city );
        ll_set_room_no = (LinearLayout) findViewById( R.id.ll_set_room_no );
        ll_select_network = (LinearLayout) findViewById( R.id.ll_select_network );
        ll_set_cms_ip = (LinearLayout) findViewById( R.id.ll_set_cms_ip );
        tv_select_language = (TextView) findViewById( R.id.tv_select_language );
        tv_select_country = (TextView) findViewById( R.id.tv_select_country );
        tv_select_timezone = (TextView) findViewById( R.id.tv_select_timezone );
        tv_select_city = (TextView) findViewById( R.id.tv_select_city );
        tv_room_no = (TextView) findViewById( R.id.tv_room_no );
        tv_select_network = (TextView) findViewById( R.id.tv_select_network );
        tv_cms_ip = (TextView) findViewById( R.id.tv_cms_ip );
        tv_language_value = (TextView) findViewById( R.id.tv_language_value );
        tv_country_value = (TextView) findViewById( R.id.tv_country_value );
        tv_timezone_value = (TextView) findViewById( R.id.tv_timezone_value );
        tv_city_value = (TextView) findViewById( R.id.tv_city_value );
        tv_room_no_value = (TextView) findViewById( R.id.tv_room_no_value );
        tv_network_value = (TextView) findViewById( R.id.tv_network_value );
        tv_cms_ip_value = (TextView) findViewById( R.id.tv_cms_ip_value );
        bt_verify_settings = (Button) findViewById( R.id.bt_verify_settings );
    }

    public void checkIfOTSAlreadyCompleted(){
        String is_ots_comleted = configurationReader.getIsOtsCompleted();
        if( is_ots_comleted.equals( "1" ) ){
            Log.d( TAG, "Exiting OTS, already completed !" );
            finish();
        }


    }



    public void selectNetworkListener(){

        ll_select_network.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {

                UtilMisc.startApplicationUsingPackageName( context, "com.mbx.settingsmbox" );

            }

        });
    }

    public void setNetworkValue(){
        String ip = UtilNetwork.getLocalIpAddressIPv4( context );
        String interface_name = UtilNetwork.getConnectedNetworkInterfaceName( context );

        if( interface_name == null )
            interface_name = "Network Disconnected : ";
        else
            interface_name = interface_name + " Connected : ";
        if( ip == null )
            ip = "Invalid IP Address";
        else
            ip = "IPv4 address - " + ip;

        tv_network_value.setText( interface_name + ip );
    }

    public void roomNumberClickListener(){

        ll_set_room_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder( context );
                ab.setTitle( "Set Room Number" );
                ab.setMessage( "Note : Type only numbers without the word `ROOM`" );

                final EditText et = new EditText( context );
                et.setHint( "1013" );
                et.setInputType( InputType.TYPE_CLASS_NUMBER );

                String room_no = "";

                ab.setView( et );
                ab.setPositiveButton( "Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        String room_no = et.getText().toString().trim();
                        if( room_no.equals( "" ) ){
                            CustomItems.showCustomToast( context, "error", "Room Number cannot be empty", 3000 );
                            return;
                        }

                        tv_room_no_value.setText( "ROOM" + room_no );
                    }

                });

                ab.setNegativeButton( "Cancel", null );

                ab.show();
            }

        });
    }

    public void setCMSIpListener(){

        ll_set_cms_ip.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                AlertDialog.Builder ab = new AlertDialog.Builder( context );
                ab.setTitle( "Set CMS IP" );
                ab.setMessage( "Note : Type each character carefully. Enter only IP address without any protocol name and without port number" );

                final EditText et = new EditText( context );
                et.setHint( "192.168.1.2" );

                String cms_ip = tv_cms_ip_value.getText().toString();
                et.setText( cms_ip );

                ab.setView( et );
                ab.setPositiveButton( "Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        String cms_ip = et.getText().toString().trim();
                        if( cms_ip.equals( "" ) ){
                            CustomItems.showCustomToast( context, "error", "CMS IP cannot be empty !", 3000 );
                            return;
                        }

                        tv_cms_ip_value.setText( cms_ip );
                    }

                });

                ab.setNegativeButton( "Cancel", null );

                ab.show();
            }

        });

    }

    public void verifySettings(){
        bt_verify_settings.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                AlertDialog.Builder ab = new AlertDialog.Builder( context );
                ab.setTitle( "Confirm Settings" );
                ab.setMessage( "Are you sure you want to submit with these settings ?" );
                ab.setNegativeButton( "Cancel", null );
                ab.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick( DialogInterface dialog, int which ) {

                        // Validate the fields
                        if( ! UtilNetwork.isConnectedToInternet( context ) ){
                            CustomItems.showCustomToast( context, "error", "Seems that you are not connected to the Network", 5000 );
                            return;
                        }

                        String room_no = tv_room_no_value.getText().toString();
                        if( ! ( room_no.startsWith( "ROOM" ) ) ){
                            CustomItems.showCustomToast( context, "error", "Please enter a valid Room Number !", 5000 );
                            return;
                        }

                        submitSettingsToCMS();
                    }

                });
                ab.show();
            }

        });
    }

    public void submitSettingsToCMS(){
        final String cms_ip = tv_cms_ip_value.getText().toString();
        final String room_no = tv_room_no_value.getText().toString();
        final String ip_address = UtilNetwork.getLocalIpAddressIPv4( context );
        final String mac_address = UtilNetwork.getMacAddress( context );

        new AsyncTask< Void, Void, String >(){

            @Override
            protected String doInBackground( Void... params ) {
                String URL = UtilURL.getWebserviceURL( cms_ip );
                String url_params = UtilURL.getURLParamsFromPairs(
                        new String[][]{
                                { "what_do_you_want", "register_appstv" },
                                { "mac_address", mac_address },
                                { "room_no", room_no },
                                { "ip_address", ip_address },
                                { "client", "appstv" },
                                { "api_key", UtilMisc.md5String( mac_address ) },
                        } );
                Log.d( TAG, URL + "-----" +url_params );
                return UtilNetwork.makeRequestForData( URL, "POST", url_params );
            }

            @Override
            protected void onPostExecute( String s ) {
                super.onPostExecute( s );

                if( s == null ){
                    //CustomItems.showCustomToast( context, "error", "Incorrect CMS Server IP or Network Disconnected. Failed to register !", 5000 );
                    CustomItems.showCustomToast( context, "error", "Incorrect CMS Server IP or Network Disconnected !", 5000 );

                    Log.e( TAG, "Null ! Incorrect CMS Server IP or Network Disconnected !" );
                    return;
                }

                Log.d( TAG, s );

                String info = null;
                try{
                    JSONArray jsonArray = new JSONArray( s );
                    JSONObject jsonObject = jsonArray.getJSONObject( 0 );
                    String type = jsonObject.getString( "type" );
                    info = jsonObject.getString( "info" );

                    if( type.equals( "error" ) ){
                        CustomItems.showCustomToast( context, "error", info, 5000 );
                        return;
                    }

                }
                catch ( Exception e ){
                    e.printStackTrace();
                    CustomItems.showCustomToast( context, "error", "JSONException Occurred !", 5000 );
                    return;
                }


                // configurationWriter = ConfigurationWriter.getInstance( context );
                File appstv_data = new File( ConfigurationWriter.getAppstvDataDirectorypath() );
                if( ! appstv_data.exists() )
                    appstv_data.mkdirs();

                if( ! ConfigurationWriter.writeAllConfigurations( context, info ) ){
                    CustomItems.showCustomToast( context, "error", "OTS was not successful. Contact Technical Team !", 5000 );
                    return;
                }

                // Apply the configuration settings on the box, as received from the CMS Server
                configurationReader = ConfigurationReader.reInstantiate();

                // Set TimeZone
                setTimeZone( configurationReader.getTimezone() );

                // Set Language
                setLocaleLanguage( configurationReader.getLanguage() );

                // Install pre-install apps
                installPreInstallApps();

                CustomItems.showCustomToast( context, "success", "Configuration settings saved successfully !", 5000 );
                // finish();


            }
        }.execute();

    }



    /*******************************************************
    /* Not so important functions. Not updated frequently */
    /*******************************************************/

    private void hideActionBar(){
        ActionBar ab = getSupportActionBar();
        ab.hide();
    }

    public void setDefaultValues(){
        tv_city_value.setText( configurationReader.getLocation() );
        tv_cms_ip_value.setText( configurationReader.getCmsIp() );
        tv_country_value.setText( configurationReader.getCountry() );
        tv_language_value.setText( configurationReader.getLanguage() );
        tv_timezone_value.setText( configurationReader.getTimezone() );

        setNetworkValue();
    }

    public void initLanguages(){
        Locale[] langs = Locale.getAvailableLocales();
        language_codes = new String[ langs.length ];
        for( int i = 0; i < langs.length ; i++ ){
            language_codes[ i ] = langs[ i ].getLanguage();
        }
        language_codes = UtilArray.removeDuplicates( language_codes );

        // get Language Names from language codes
        languages = new String[ language_codes.length ];
        for( int i = 0; i < language_codes.length ; i++ ){
            languages[ i ] = new Locale( language_codes[ i ] ).getDisplayLanguage();
        }

        language_map = new HashMap<String,String>();
        for( int i = 0; i < language_codes.length ; i++ ){
            language_map.put( languages[ i ], language_codes[ i ] );
        }
        languages = new String[ language_codes.length ];

    }

    public void setTimeZone( String timeZone ){
        UtilShell.executeShellCommandWithOp( "setprop persist.sys.timezone " + timeZone );
    }

    public void setLocaleLanguage( String language ){
        Log.d( TAG, ":" + language + ":" + language_map.get( language ) + ":" );
        UtilShell.executeShellCommandWithOp( "setprop persist.sys.language  " + language_map.get( language ) );
    }

    public String[] getPreinstallAppsPackageNames(){
        UtilShell.executeShellCommandWithOp( "chmod -R 777 /system/appstv_data/preinstall" );
        String package_names =  UtilFile.readData( new File( "/system/appstv_data/preinstall_apps" ) );
        String p[] = package_names.trim().split( "," );
        String packages[] = new String[ p.length / 3 ];
        int i = 0;
        for( int j = 0 ; j < p.length - 2 ; j = j + 3 ){
            packages[ i++ ] = p[ j ];
        }
        return packages;
    }

    public void installPreInstallApps(){
        /*final Dialog ab = new Dialog( context );
        ab.setTitle( "Installing apps...Do not Interrupt !" );
        ab.setContentView( new ProgressBar( context ));
        ab.setCancelable( false );
        ab.show();

        // Show Progress Bar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] package_names = getPreinstallAppsPackageNames();
                for( int i = 0 ; i < package_names.length ; i++ ){
                    // Log.d( TAG, "pm install /system/preinstall/" + package_names[ i ] + ".apk" );
                    UtilShell.executeShellCommandWithOp( "pm install /system/preinstall/" + package_names[ i ] + ".apk" );
                    CustomItems.showCustomToast( context, "success", String.format( "Installing app %s of %s", i + 1, package_names.length ), 3000 );
                }
                ab.dismiss();
                finishOTS();
            }
        }, 2000 );*/

        new AsyncTask< Void, Void, Void >(){

            Dialog ab = new Dialog( context );
            int i = 0;
            TextView message;
            LayoutInflater lf;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                lf = (LayoutInflater) context.getSystemService( LAYOUT_INFLATER_SERVICE );
                RelativeLayout rl = (RelativeLayout) lf.inflate( R.layout.app_installation_progress, null );
                message = (TextView) rl.findViewById( R.id.tv_message );

                ab.setTitle( "Installing apps...Do not Interrupt !" );
                ab.setContentView( rl );
                ab.setCancelable( false );

                ab.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                final String[] package_names = getPreinstallAppsPackageNames();
                for(  i = 0 ; i < package_names.length ; i++ ){
                    // Log.d( TAG, "pm install /system/preinstall/" + package_names[ i ] + ".apk" );
                    MainActivity.this.runOnUiThread(new Runnable() {

                        public void run() {
                            Log.d( TAG, String.format( "Installing app %s of %s", i+1, package_names.length ) );
                            message.setText( String.format( "Installing app %s of %s", i+1, package_names.length ) );
                            //CustomItems.showCustomToast( context, "success", String.format( "Installing app %s of %s", i + 1, package_names.length ), 3000 );
                        }

                    });
                    UtilShell.executeShellCommandWithOp( "pm install /system/preinstall/" + package_names[ i ] + ".apk" );

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                ab.dismiss();

                AlertDialog.Builder abb = new AlertDialog.Builder( context );
                abb.setTitle( "Success" );
                abb.setMessage( "OTS completed successfully !" );
                abb.setCancelable( false );
                abb.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishOTS();
                    }

                });
                abb.show();


            }


        }.execute();


    }

    public void finishOTS(){
        finish();
    };
    /*******************************************************
     /* Not so important functions. Not updated frequently */
    /*******************************************************/
}
