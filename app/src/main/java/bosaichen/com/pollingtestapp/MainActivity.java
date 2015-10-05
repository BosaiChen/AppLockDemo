package bosaichen.com.pollingtestapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTVGrantUsage;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final String PERMISSION_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTVGrantUsage = (TextView) findViewById(R.id.grant_usage_access);

        TextView apiLevelText = (TextView) findViewById(R.id.device_api_level);
        apiLevelText.setText(String.format(getString(R.string.device_api_level), Build.VERSION.SDK_INT, Utils.getSDKVersionName(this)));

        Button startPolling = (Button) findViewById(R.id.btn_start_polling);
        startPolling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().startService(new Intent(getApplicationContext(), PollingService.class));
                Toast.makeText(MainActivity.this, "polling started", Toast.LENGTH_SHORT).show();
            }
        });

        Button stopPolling = (Button) findViewById(R.id.btn_stop_polling);
        stopPolling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().stopService(new Intent(getApplicationContext(), PollingService.class));
                Toast.makeText(MainActivity.this, "polling stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.PACKAGE_USAGE_STATS");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mTVGrantUsage.setVisibility(View.GONE);
        } else {
            mTVGrantUsage.setVisibility(View.VISIBLE);
            mTVGrantUsage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{PERMISSION_USAGE_STATS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            });
        }
    }
}
