package com.google.android.exoplayer2.auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.english_mp3.R;

public class AuthCheckShowActivity extends Activity {
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1004;
    private final int OVERLAY_REQUEST = 1000;
    public final int REQUEST_CODE_SETTING_ACTIVITY = 1040;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                requestOverlayPermission();
            } else {
                checkPermission();
            }
        } else {
            afterCheck();
        }
    }

    @SuppressLint("WrongConstant")
    @TargetApi(23)
    public void checkPermission() {

        int permissionResult = 0;
        boolean check = true;

        //스토리지 접근권한
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == -1 ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == -1 ||
                //콜상태
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == -1 ||
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == -1
                //checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == -1 ||
                //checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == -1

                ) {
            Log.d("LOGDA", "@@ checkPermission 권한없음");
            requestPermissions(new String[]{
                            //스토리지 접근권한
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,

                            //콜상태, UUID, 전화번호 접근
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE
                            //Manifest.permission.ACCESS_COARSE_LOCATION,
                            //Manifest.permission.ACCESS_FINE_LOCATION,
                            //Manifest.permission.CALL_PHONE
                    },
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {
            Log.d("CUSTOMER", " this @@ 권한있음");
            afterCheck();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:

                boolean check[] = new boolean[grantResults.length];
                int rs = 1;

                for (int i = 0; i < grantResults.length; ++i) {
                    Log.d("CUSTOMER", "grantResults[i]=" + grantResults[i] + "   PackageManager.PERMISSION_GRANTED=" + PackageManager.PERMISSION_GRANTED);
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        check[i] = true;
                    } else {
                        check[i] = false;
                    }
                }

                for (int i = 0; i < check.length; ++i) {
                    if (!check[i]) {
                        rs = 0;
                    }
                }

                if (rs > 0) {
                    if (!Settings.canDrawOverlays(getApplicationContext())) {
                        requestOverlayPermission();
                    } else {
                        afterCheck();
                    }
                } else {
                    Log.d("CUSTOMER", " onRequestPermissionsResult 권한거부");
                    checkPermission();
                }

                return;
        }
    }

    /************************* 오버레이권한 *************************/
    @TargetApi(Build.VERSION_CODES.M)
    public void requestOverlayPermission() {
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, OVERLAY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_SETTING_ACTIVITY:

                System.exit(0);
                break;
            case OVERLAY_REQUEST:
                Log.d("CUSTOMER", "OVERLAY_REQUEST~~~~");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        checkPermission();
                    } else {
                        requestOverlayPermission();
                        Toast.makeText(this, "다른앱위에그리기 거부", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    private void afterCheck() {
        setResult(8888, getIntent());
        finish();
    }

    public void close(View view) {
        //finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
