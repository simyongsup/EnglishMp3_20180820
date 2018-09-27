/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.english_mp3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.VO.ListVO;
import com.google.android.exoplayer2.dialog.CommonCancelDialog;
import com.google.android.exoplayer2.dialog.CommonDialog;
import com.google.android.exoplayer2.dialog.CommonPermissionChecckDialog;
import com.google.android.exoplayer2.dialog.CommonPreDataDialog;
import com.google.android.exoplayer2.dialog.CommonUpdateDialog;
import com.google.android.exoplayer2.nw.JoinWebAsyncTask;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    public static String host = "http://ilove14.cafe24.com/ESTD/ContentsList.php";
    String host_version = "http://ilove14.cafe24.com/ESTD/Info.php";
    String host_register = "http://ilove14.cafe24.com/ESTD/RegFavorite.php";
    String host_unregister = "http://ilove14.cafe24.com/ESTD/UnRegFavorite.php";
    String host_search = "http://ilove14.cafe24.com/ESTD/Search.php";

    MainActivity mainActivity;

    CommonUpdateDialog commonUpdateDialog;
    CommonCancelDialog cancelYnDialog;
    CommonPreDataDialog commonPreDataDialog;
    CommonDialog dialog;
    EditText e_data;

    LinearLayout RelativeFind;
    LinearLayout top;
    ImageView findButton;
    TextView title;
    Button likeBtn;
    Button unLikeBtn;

    LinearLayout close;
    ProgressDialog progressDialog;
    RecyclerView rv;
    public static SimpleStringRecyclerViewAdapter adapter;
    SwipeRefreshLayout layout_order;
    NavigationView navigationView;
    TextView openWeb;

    int PageCnt = 1;
    ImageView home_btn;

    public void reflesh(View view) {
        PageCnt = 1;
        adapter.clear();

        EditText data = (EditText) findViewById(R.id.data);
        data.setText("");

        if (like == true) {
            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
            //boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/SearchSelected.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");
            boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/FavoriteList.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

        } else {
            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
            boardAsyncTask.execute(host, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

        }
    }

    /************************* onCreate *************************/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        mainActivity = this;

        top = (LinearLayout) findViewById(R.id.top);
        rv = (RecyclerView) findViewById(R.id.point_list);
        //layout_order = (SwipeRefreshLayout) findViewById(R.id.layout_order);
        findButton = (ImageView) findViewById(R.id.findButton);
        title = (TextView) findViewById(R.id.title);
        RelativeFind = (LinearLayout) findViewById(R.id.RelativeFind);

        likeBtn = (Button) findViewById(R.id.like);
        unLikeBtn = (Button) findViewById(R.id.unlike);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like = true;
                PageCnt = 1;
                adapter.clear();

                Log.d("LOGDA_SCROLL", "page = " + PageCnt);
                EditText data = (EditText) findViewById(R.id.data);
                data.setText("");
                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/FavoriteList.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                unLikeBtn.setTextColor(Color.parseColor("#cccccc"));
                likeBtn.setTextColor(Color.WHITE);

                likeBtn.setBackgroundColor(Color.parseColor("#585858"));
                unLikeBtn.setBackgroundColor(Color.parseColor("#2A0A12"));//2A0A12
            }
        });

        unLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like = false;
                PageCnt = 1;
                adapter.clear();

                EditText data = (EditText) findViewById(R.id.data);
                data.setText("");
                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                boardAsyncTask.execute(host, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                likeBtn.setTextColor(Color.parseColor("#cccccc"));
                unLikeBtn.setTextColor(Color.WHITE);

                likeBtn.setBackgroundColor(Color.parseColor("#2A0A12"));
                unLikeBtn.setBackgroundColor(Color.parseColor("#585858"));
            }
        });

        e_data = (EditText) findViewById(R.id.data);

        e_data.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:

                        String dataStr = e_data.getText().toString();
                        Log.i("LOGDA_SCROLL", "Bottom of list   data = " + dataStr + " like = " + like + "  PageCnt =" + PageCnt);


                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(e_data.getWindowToken(), 0);

                        PageCnt = 1;
                        mainActivity.adapter.clear();
                        if (like == true) {
                            if (dataStr != null && !dataStr.equals("")) {
                                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                                boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/SearchFavorite.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt + "&SearchData=" + dataStr, "getStudyList");

                            } else {
                                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                                boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/FavoriteList.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                            }

                        } else {
                            if (dataStr != null && !dataStr.equals("")) {
                                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                                boardAsyncTask.execute(host_search, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt + "&SearchData=" + dataStr, "getStudyList");

                            } else {
                                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                                boardAsyncTask.execute(host, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                            }
                        }

                        break;
                    default:
                        //Toast.makeText(getApplicationContext(), "기본", Toast.LENGTH_LONG).show();
                        return false;
                }
                return true;
            }
        });


        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                top.setVisibility(View.GONE);
                RelativeFind.setVisibility(View.VISIBLE);

                e_data.requestFocus();
                //키보드 보이게 하는 부분
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        setupRecyclerView(rv);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rv.setOnScrollChangeListener(new RecyclerView.OnScrollChangeListener() {

                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    checkScroll();
                }
            });
        } else {
            rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    checkScroll();
                }
            });
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BleActivity.class);
                startActivity(intent);
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        //openWeb = (TextView) findViewById(R.id.openWeb);
       /* openWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.note21c.com"));
                startActivity(intent);
            }
        });*/

       /* layout_order.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                PageCnt = 1;
                adapter.clear();

                EditText data = (EditText) findViewById(R.id.data);
                data.setText("");

                if (like == true) {
                    JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                    boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/SearchSelected.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                } else {
                    JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                    boardAsyncTask.execute(host, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                }

                layout_order.setRefreshing(false);

            }
        });*/

        registerReceiver(broadNetwork, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (getIntent() != null) {
            processIntent(getIntent());
        }

        Intent start = new Intent(MainActivity.this, startActivity.class);
        startActivity(start);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerLayout.setDrawerListener(this);

        home_btn = (ImageView) findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!

                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT); //OPEN Nav Drawer!

                }
            }
        });

        mDrawerLayout.openDrawer(Gravity.LEFT); //OPEN Nav Drawer!
        mDrawerLayout.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!


      /*  if (navigationView != null) {
            setupDrawerContent(navigationView);
        }*/
        //setNoti();
    }





    public void openWeb(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.note21c.com"));
        startActivity(intent);
    }

    private DrawerLayout mDrawerLayout;

  /*  private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }*/

    private void checkScroll() {
        if (!rv.canScrollVertically(-1)) {
            Log.i("LOGDA", "Top of list");
        } else if (!rv.canScrollVertically(1)) {

            ++PageCnt;
            try {


                String dataStr = e_data.getText().toString();
                Log.i("LOGDA_SCROLL", "Bottom of list   data = " + dataStr + " like = " + like + "  PageCnt =" + PageCnt);

                if (like == true) {
                    if (dataStr != null && !dataStr.equals("")) {
                        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                        boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/SearchFavorite.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt + "&SearchData=" + dataStr, "getStudyList");

                    } else {
                        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                        boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/FavoriteList.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                    }

                } else {
                    if (dataStr != null && !dataStr.equals("")) {
                        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                        boardAsyncTask.execute(host_search, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt + "&SearchData=" + dataStr, "getStudyList");

                    } else {
                        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                        boardAsyncTask.execute(host, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.i("LOGDA", "idle");
        }
    }

    boolean start = true;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("LOGDA_CALL", "onresume start =" + start);
        if (start == true) {
            PermisionCheck();
            start = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadNetwork);
    }

    BroadcastReceiver broadNetwork = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (isDataConnected() > 0) {

            }
        }
    };


    private void afterCheck() {
        try {

            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getVersion");
            boardAsyncTask.execute(host_version, "token=12345", "getVersion");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void PermisionCheck() {

        //Log.d("LOGDA", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ PermisionCheck SDK_INT=" + Build.VERSION.SDK_INT + "   Build.VERSION_CODES.M=" + Build.VERSION_CODES.M);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//마시멜로 미만버전
            afterCheck();
        } else {//마시멜로이상

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    //콜상태
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                Log.d("CUSTOMER", "#$%#$%#$%#$%#$%#$%#$%#$%#$%#$%#$%#$%#$%#$%" + Build.VERSION.SDK_INT);
                //Intent intent = new Intent(MainActivity.this, AuthCheckShowActivity.class);
                //startActivityForResult(intent, 8888);

                //checkFinal();
                if (commonPermissionChecckDialog != null && commonPermissionChecckDialog.isShowing())
                    commonPermissionChecckDialog.dismiss();

                commonPermissionChecckDialog = new CommonPermissionChecckDialog(MainActivity.this, closed);
                commonPermissionChecckDialog.show();

            } else {
                afterCheck();
            }
        }
    }

    CommonPermissionChecckDialog commonPermissionChecckDialog;

    public View.OnClickListener closed = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onClick(View v) {
            commonPermissionChecckDialog.dismiss();
            Log.d("CUSTOMER", "@@@@@@@@@@@@@@ check final");
            checkFinal();
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFinal() {
        Log.d("CUSTOMER", "@@@@@@@@@@@@@@ check final");

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                //콜상태
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                ) {
            Log.d("LOGDA", "@@ checkPermission 권한없음");
            requestPermissions(new String[]{
                            //스토리지 접근권한
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,

                            //콜상태, UUID, 전화번호 접근
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    },
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


        } else {
            Log.d("CUSTOMER", " this @@ 권한있음");
            afterCheck();
        }
    }

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 12345;

    /************************* 오버레이권한 *************************/
    @TargetApi(Build.VERSION_CODES.M)
    public void requestOverlayPermission() {
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, OVERLAY_REQUEST);
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
                    PermisionCheck();
                }

                return;
        }
    }


    public View.OnClickListener dismis_finish = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
            finish();
        }
    };

    private int isDataConnected() {
        //Log.d("LOGDA", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ isDataConnected ");
        int rs = 0;
        try {

            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);//데이타 연결
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//와이파이

            if (mobile.isConnected()) {

                rs = 1;
            } else if (wifi.isConnected()) {

                rs = 2;
            } else if (!mobile.isConnected() && !wifi.isConnected()) {
                //if (dialog != null && dialog.isShowing()) dialog.dismiss();
                //dialog = new CommonDialog(this, "인터넷 연결 실패 !!\n앱 종료 됩니다.\n인터넷 연결 후 재시도 하세요", "확인", dismis_finish);
                //dialog.show();
                rs = 0;
            }

        } catch (Exception e) {
            return 0;
        }

        //Log.d("LOGDA", "네트워크 확인 = " + rs);
        return rs;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            processIntent(intent);
        }
    }

    //TDES des = new TDES();
    private void processIntent(Intent intent) {

    }

    private String getDevicesUUID() {
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SimpleStringRecyclerViewAdapter(MainActivity.this);

        recyclerView.setAdapter(adapter);

    }

    public void searchKeyWord(View view) {
        String dataStr = e_data.getText().toString();

        PageCnt = 1;
        if (dataStr != null && !dataStr.equals("")) {


            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(e_data.getWindowToken(), 0);

            adapter.clear();

            if (like == true) {
                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/SearchFavorite.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt + "&SearchData=" + dataStr, "getStudyList");

            } else {
                JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
                boardAsyncTask.execute(host_search, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt + "&SearchData=" + dataStr, "getStudyList");

            }

        } else {
            Toast.makeText(mainActivity, "검색어를 입력해 주세요.", Toast.LENGTH_LONG).show();
        }
    }

    public void afterCheckVersion() {
        if (PageCnt == 1 && like == false) {
            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
            boardAsyncTask.execute(host, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");
        }

    }

    public final Handler ShowUpdateVersion = new Handler() {
        public void handleMessage(Message msg) {
            commonUpdateDialog = new CommonUpdateDialog(mainActivity, updateClick);
            commonUpdateDialog.show();
        }
    };

    ProgressDialog asyncDialog;
    public final Handler progress = new Handler() {
        public void handleMessage(Message msg) {
            asyncDialog = new ProgressDialog(MainActivity.this);
            asyncDialog.show();

        }
    };

    public final Handler progressFinish = new Handler() {
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();

            if (asyncDialog != null && asyncDialog.isShowing()) {
                asyncDialog.dismiss();
            }
        }
    };

    String CHECK_PACKAGE_NAME = "com.google.android.exoplayer2.english_mp3";
    public View.OnClickListener updateClick = new View.OnClickListener() {
        public void onClick(View v) {
            //commonUpdateDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + CHECK_PACKAGE_NAME));
            startActivity(intent);
            commonUpdateDialog.dismiss();
        }
    };

    public void showToast(String rs) {
        Toast.makeText(mainActivity, "즐겨듣기 등록", Toast.LENGTH_LONG).show();
        Drawable start2 = getResources().getDrawable(R.drawable.star2);
        downView.setImageDrawable(start2);
    }

    public void showToastUnRegister(String rs) {
        Toast.makeText(mainActivity, "즐겨듣기 제외", Toast.LENGTH_LONG).show();
        Drawable start2 = getResources().getDrawable(R.drawable.star_empty);
        downView.setImageDrawable(start2);
    }

    public ImageView downView;
    public static boolean like = false;


    public void returnBack(View view) {
        EditText data = (EditText) findViewById(R.id.data);
        data.setText("");
        top.setVisibility(View.VISIBLE);
        RelativeFind.setVisibility(View.GONE);
    }

    public void goRenwal(View view) {

        PageCnt = 1;
        adapter.clear();

        EditText data = (EditText) findViewById(R.id.data);
        data.setText("");

        if (like == true) {
            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
            boardAsyncTask.execute("http://ilove14.cafe24.com/ESTD/FavoriteList.php", "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

        } else {
            JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "getStudyList");
            boardAsyncTask.execute(host, "token=12345&UUID_KEY=" + getDevicesUUID() + "&PageCnt=" + PageCnt, "getStudyList");

        }

    }

    public class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<ListVO> mValues;

        public List<ListVO> getMValue() {
            return mValues;
        }

        public void clear() {
            mValues.clear();
        }

        //내부클래스 시작
        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView idx;
            public final TextView title;
            public final TextView tot_time;
            public final TextView file_name;
            public final TextView scriptYN;
            public final ImageView downYn;
            public final ImageView title_img;
            public final LinearLayout registerLike;
            public final LinearLayout popOpenLayout;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                idx = (TextView) view.findViewById(R.id.idx);
                title = (TextView) view.findViewById(R.id.title);
                tot_time = (TextView) view.findViewById(R.id.tot_time);
                file_name = (TextView) view.findViewById(R.id.file_name);
                scriptYN = (TextView) view.findViewById(R.id.scriptYN);
                downYn = (ImageView) view.findViewById(R.id.downYn);
                title_img = (ImageView) view.findViewById(R.id.title_img);
                //layout = (LinearLayout) view.findViewById(R.id.layout);
                popOpenLayout = (LinearLayout) view.findViewById(R.id.popOpenLayout);
                registerLike = (LinearLayout) view.findViewById(R.id.registerLike);
            }

           /* @Override
            public String toString() {
                return super.toString() + " '" + shop_name.getText();
            }*/

        }//내부클래스 종료

        public void addItemString(ListVO item) {
            mValues.add(item);
        }

        public SimpleStringRecyclerViewAdapter(Context context) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = new ArrayList<ListVO>();
        }

        @Override
        public SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_point_list, parent, false);
            view.setBackgroundResource(mBackground);
            return new SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            //holder.title_img.setText(mValues.get(position).getImg_name());
            holder.idx.setText(mValues.get(position).getIdx());
            holder.title.setText(mValues.get(position).getTitle_name());
            holder.file_name.setText(mValues.get(position).getFile_name());
            holder.tot_time.setText(mValues.get(position).getTot_time());

            //holder.downYn.setText(mValues.get(position).getFile_down_yn());

            //북마크
            if (mValues.get(position).getFavorite_yn() != null && mValues.get(position).getFavorite_yn().equals("Y")) {
                Drawable start2 = getResources().getDrawable(R.drawable.star2);
                //holder.downYn.setBackgroundResource(R.drawable.star2);
                holder.downYn.setImageDrawable(start2);
            } else {
                Drawable star_empty = getResources().getDrawable(R.drawable.star_empty);
                //holder.downYn.setBackgroundResource(R.drawable.star_empty);
                holder.downYn.setImageDrawable(star_empty);
            }

            //holder.scriptYN.setText(mValues.get(position).getKor_script_yn());
            if (mValues.get(position).getForeign_script_yn().equals("Y")) {
                holder.scriptYN.setText("SCRIPT");
                holder.scriptYN.setTextColor(Color.parseColor("#ffffff"));
            } else {
                holder.scriptYN.setText("No-SCRIPT");
                holder.scriptYN.setTextColor(Color.parseColor("#808080"));
            }

            Glide.with(MainActivity.this)
                    .load(mValues.get(position).getImg_name())
                    .fitCenter()
                    .placeholder(R.drawable.logo_penut)
                    .into(holder.title_img);

            holder.popOpenLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (like == true) {
                        PageCnt = 1;
                    }

                    Log.d("LOGDA_REPEAT", "setOnClickListener idx = " + mValues.get(position).getIdx());
                    Intent i = new Intent(MainActivity.this, PlayerSpeedActivity.class);
                    i.putExtra("uuid", getDevicesUUID());
                    i.putExtra("file_down", mValues.get(position).getFile_down_yn());
                    i.putExtra("file_name", mValues.get(position).getFile_name());
                    i.putExtra("idx", mValues.get(position).getIdx());
                    i.putExtra("tot_time", mValues.get(position).getTot_time());
                    i.putExtra("title_name", mValues.get(position).getTitle_name());
                    i.putExtra("scriptKorYN", mValues.get(position).getKor_script_yn());
                    i.putExtra("scriptForYN", mValues.get(position).getForeign_script_yn());
                    i.putExtra("favorit", mValues.get(position).getFavorite_yn());
                    i.putExtra("number", "" + position);
                    i.putExtra("LANG", "" + mValues.get(position).getLang_type());
                    startActivityForResult(i, 100000);
                }
            });

            holder.registerLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downView = holder.downYn;

                    if (mValues.get(position).getFavorite_yn() != null && mValues.get(position).getFavorite_yn().equals("Y")) {
                        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "UnRegisterLike");
                        boardAsyncTask.execute(host_unregister, "token=12345&UUID_KEY=" + getDevicesUUID() + "&ID=" + mValues.get(position).getIdx(), "UnRegisterLike");
                        mValues.get(position).setFavorite_yn("N");

                    } else {
                        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(MainActivity.this, "registerLike");
                        boardAsyncTask.execute(host_register, "token=12345&UUID_KEY=" + getDevicesUUID() + "&ID=" + mValues.get(position).getIdx(), "registerLike");
                        mValues.get(position).setFavorite_yn("Y");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }


    int end = 0;

    @Override
    public void onBackPressed() {
        ++end;
        if (end >= 2) {
            if (mainActivity != null && mainActivity != null && !mainActivity.isFinishing()) {
                cancelYnDialog = new CommonCancelDialog(mainActivity, dismis_cancel);
                cancelYnDialog.show();
            }
            //finish();
        }
        //finish();
    }

    public View.OnClickListener dismis_cancel = new View.OnClickListener() {
        public void onClick(View v) {
            cancelYnDialog.dismiss();
            finish();
        }
    };

    public void close(View view) {
        finish();
    }


    public String getDivNo() {
        String phoneNum = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                if (telManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                    // 유심이 없는 경우
                    phoneNum = "";
                } else {
                    // 유심이 존재하는 경우
                    phoneNum = telManager.getLine1Number();//전화번호
                    if (phoneNum != null) {
                        if (phoneNum.startsWith("+82")) {
                            phoneNum = phoneNum.replace("+82", "0");
                        } else if (phoneNum.startsWith("82")) {
                            phoneNum = phoneNum.replace("82", "0");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "전화 번호가 없습니다", Toast.LENGTH_LONG).show();
                    }
                }

                return phoneNum;
            } else {
                return "";
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (telManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                    // 유심이 없는 경우
                    phoneNum = "";
                } else {
                    // 유심이 존재하는 경우
                    phoneNum = telManager.getLine1Number();//전화번호
                    if (phoneNum != null) {
                        if (phoneNum.startsWith("+82")) {
                            phoneNum = phoneNum.replace("+82", "0");
                        } else if (phoneNum.startsWith("82")) {
                            phoneNum = phoneNum.replace("82", "0");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "전화 번호가 없습니다", Toast.LENGTH_LONG).show();
                    }
                }
            }
            return phoneNum;
        }
    }

    final int OVERLAY_REQUEST = 12341;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("LOGDA", "@@ requestCode=" + requestCode);
        switch (requestCode) {
            case 8888:
                //Log.d("LOGDA", "data intent~~~~~~~ 1003");
                afterCheck();
                break;
            case OVERLAY_REQUEST:
                Log.d("CUSTOMER", "OVERLAY_REQUEST~~~~");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        PermisionCheck();
                    } else {
                        requestOverlayPermission();
                        Toast.makeText(this, "다른앱위에그리기 거부", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case 100000:
                if (data != null) {
                    String end = data.getStringExtra("param");
                    Log.d("LOGDA_CALL", "end = " + end);

                    if (end != null && end.equals("end")) {
                        finish();
                    }

                }
                break;

        }
    }


}
