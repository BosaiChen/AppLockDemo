package bosaichen.com.pollingtestapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_OPEN_USAGE = 1;
    public static final String ACTION_USAGE_ACCESS_SETTINGS = "android.settings.USAGE_ACCESS_SETTINGS";
    private TextView mTVGrantUsage;
    public static final String PERMISSION_PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";

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
        checkUsagePermission();
    }

    private void checkUsagePermission() {
        if (Utils.hasUsageAccessPermission(getApplicationContext())) {
            mTVGrantUsage.setVisibility(View.GONE);
        } else {
            mTVGrantUsage.setVisibility(View.VISIBLE);
            mTVGrantUsage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ACTION_USAGE_ACCESS_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, REQUEST_OPEN_USAGE);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_USAGE) {
            checkUsagePermission();
        }
    }
}
