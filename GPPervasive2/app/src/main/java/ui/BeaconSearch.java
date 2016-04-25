package ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.pervasivesystems.compasstest.R;

import java.util.List;
import java.util.UUID;

import request.blt.permission.BluetoothPermission;
import service.BeaconService;

public class BeaconSearch extends AppCompatActivity {

    BluetoothPermission bp;
    private String TAG_DEBUG = "BEACONSEARCH";
    private BeaconManager beaconManager;
    private static final String UUID_String = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private Region  region =  new Region("monitored region",UUID.fromString(UUID_String), null, null);
    private AppCompatActivity appCompatActivity;
    private boolean need_service = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_DEBUG,"onCreate()");
        setContentView(R.layout.activity_beacon_search);

        bp = new BluetoothPermission(BeaconSearch.this);
        stopService(new Intent(getBaseContext(), BeaconService.class));
        appCompatActivity = this;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG_DEBUG,"onResume()");
        bp.onResumeEnableBluetoothPermission();
        //stoppo il servizio
        stopService(new Intent(getBaseContext(), BeaconService.class));
        initializedBeaconManager();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG_DEBUG,"onPause()");
        //faccio partire il servizio, se ho già ottenuto i permessi, facciamo un check:
        if (bp.hasPermission() == PackageManager.PERMISSION_GRANTED) {
            //se il servizio è attivo bisogna stoppare il monitoring
            beaconManager.stopMonitoring(region);
            beaconManager.disconnect();
            //se vado in onPause perchè ho trovato un beacon non serve che faccio partire il servizio
            if(need_service)
                startService(new Intent(getBaseContext(), BeaconService.class));
        }
    }
    private void initializedBeaconManager() {
        if (bp.hasPermission() == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG_DEBUG, "initializedBeaconManager() ");
            //attivo il monitoring
            beaconManager = new BeaconManager(this);

            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManager.startMonitoring(region);
                }
            });

            beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
                @Override
                public void onEnteredRegion(Region region, List<Beacon> list) {
                    Log.d(TAG_DEBUG, "onEnteredRegion"+region.toString());
                    // Toast.makeText(context, "onEnteredRegion", Toast.LENGTH_SHORT).show();

                    //TODO chiama navigaziotion, passanto la region con cui sei entrato. Stringa UUID
                    Intent Navigation = new Intent(appCompatActivity, Navigation2.class);
                    Navigation.putExtra("Region", region.getProximityUUID());
                    Toast.makeText(appCompatActivity, "Abbiamo trovato un beacon, ti manderemo in automatico alla " +
                            "schermata di navigazione", Toast.LENGTH_LONG).show();
                    need_service = false;
                    appCompatActivity.startActivity(Navigation);
                }

                @Override
                public void onExitedRegion(Region region) {
                    // could add an "exit" notification too if you want (-:
                    Log.d(TAG_DEBUG, "onExitedRegion");

                    //TODO mmm, non so, forse neanche ci serve
                }
            });
        }else{
            Log.d(TAG_DEBUG,"NON ABBIAMO I PERMESSI PER IL BLT, DO ANNAMO?");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        bp.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        bp.onActivityResult(requestCode, resultCode, data);
    }

    //TODO Logica del monitoring
    //TODO Chiamata/chiusura del servizio in background

    /**
     * need_service serve perchè se vado in onPuase perchè ho trovato un beacon e chiamo Navigation
     *              non serve far partire il servizio
     *
     *
     */

}
