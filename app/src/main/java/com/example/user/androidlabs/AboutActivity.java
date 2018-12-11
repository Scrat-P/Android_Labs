package com.example.user.androidlabs;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class AboutActivity extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 123;
    private boolean shouldShowPermissionExplanation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        showVersion();

        int hasReadPhoneStatePermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (hasReadPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissionWithRationale();
        }
        else {
            showIMEI();
        }
    }

    private void showVersion() {
        String versionName = getResources().getString(R.string.version);
        TextView versionView = findViewById(R.id.versionView);
        versionView.setText(String.format("%s: %s", versionName, getVersion()));
    }

    private void showIMEI() {
        String imeiName = getResources().getString(R.string.imei);
        TextView imeiView = findViewById(R.id.imeiView);
        imeiView.setText(String.format("%s: %s", imeiName, getIMEI()));
    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(R.string.explanation);
            dialogBuilder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    requestPermissions();
                }
            });
            dialogBuilder.show();
        }
        else {
            requestPermissions();
        }
    }

    private String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(AboutActivity.this,
                new String[] { Manifest.permission.READ_PHONE_STATE },
                PERMISSIONS_REQUEST_READ_PHONE_STATE);
    }

    private String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager != null ? telephonyManager.getDeviceId() : "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showIMEI();
                }
                else if (shouldShowPermissionExplanation){
                    requestPermissionWithRationale();
                    shouldShowPermissionExplanation = true;
                }
            }
        }
    }
}