package com.excel.onetimesetup.secondgen;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.excel.configuration.ConfigurationReader;
import com.excel.customitems.CustomItems;
import com.excel.excelclasslibrary.UtilMisc;
import com.excel.excelclasslibrary.UtilNetwork;

public class MainActivity extends AppCompatActivity {

    ConfigurationReader configurationReader;

    LinearLayout ll_select_language, ll_select_country, ll_select_timezone, ll_select_city, ll_set_room_no, ll_select_network, ll_set_cms_ip;
    TextView tv_select_country, tv_select_timezone, tv_select_city, tv_room_no, tv_select_network, tv_cms_ip, tv_select_language;
    TextView tv_country_value, tv_timezone_value, tv_city_value, tv_room_no_value, tv_network_value, tv_cms_ip_value, tv_language_value;

    Context context = this;
    public static final String TAG = "MainActivity";

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        configurationReader = ConfigurationReader.reInstantiate();

        // Set Network Selection Value
        setNetworkValue();
    }

    public void initViews(){
        hideActionBar();

        configurationReader = ConfigurationReader.getInstance();

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
    }

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
            interface_name = "Network Dis";
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
}
