package com.google.android.exoplayer2.english_mp3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.nw.JoinWebAsyncTask;
import com.google.android.exoplayer2.nw.NotificationReceiver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.android.exoplayer2.english_mp3.NotificationHelper.GROUP_KEY;
import static com.google.android.exoplayer2.english_mp3.NotificationHelper.OFF;
import static com.google.android.exoplayer2.english_mp3.NotificationHelper.ON;
import static com.google.android.exoplayer2.english_mp3.NotificationHelper.CLOSED;

public class BleActivity extends AppCompatActivity {

    boolean Connection = false;

    public boolean isConnection() {
        return Connection;
    }

    public void setConnection(boolean connection) {
        Connection = connection;
    }

    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Handler handler = null;

    Set<BluetoothDevice> mDevices;
    BluetoothDevice mRemoteDevie;
    String TAG = "BLE_LOG_RCV";
    RemoteViews contentview;

    // 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
    BluetoothAdapter mBluetoothAdapter;
    BleActivity bleActivity;

    public static BluetoothSocket mSocket = null;

    public static BluetoothSocket getmSocket() {
        return mSocket;
    }

    public static void setmSocket(BluetoothSocket mSocket) {
        mSocket = mSocket;
    }

    MediaPlayer music;
    AudioManager mAudioManager;
    TextView ttt;
    Button btn1;
    Button btn2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_main);
        bleActivity = this;
        handler = new Handler();

        ttt = (TextView) findViewById(R.id.ttt);
        ttt.setText("연결장비 : " + getPreferencesName());

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);

        checkBluetooth();



    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();


        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(mBroadcastReceiverDevice, filter1);
        registerReceiver(mBroadcastReceiverDevice, filter2);
        registerReceiver(bluetoothReceiverBLEOnOff, filter3);

        setSound();

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        setNoti();

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        checkBtnColor();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setNoti() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannelGroup group1 = new NotificationChannelGroup("channel_group_id", "channel_group_name");
            notificationManager.createNotificationChannelGroup(group1);
            NotificationChannel notificationChannel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("channel description");
            notificationChannel.setGroup("channel_group_id");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int notificationId = 111;//new Random().nextInt();

        Intent intentAction = new Intent(bleActivity, BleActivity.class);
        intentAction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent open = PendingIntent.getActivity(this, notificationId, intentAction, PendingIntent.FLAG_ONE_SHOT);

        Intent positive = new Intent(this, NotificationReceiver.class);
        positive.putExtra("notiID", notificationId);
        positive.setAction(ON);
        PendingIntent pIntent_positive = PendingIntent.getBroadcast(this, notificationId, positive, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent negative = new Intent(this, NotificationReceiver.class);
        negative.putExtra("notiID", notificationId);
        negative.setAction(OFF);
        PendingIntent pIntent_negative = PendingIntent.getBroadcast(this, notificationId, negative, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent closed = new Intent(this, NotificationReceiver.class);
        closed.putExtra("notiID", notificationId);
        closed.setAction(CLOSED);
        PendingIntent pIntent_closed = PendingIntent.getBroadcast(this, notificationId, closed, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(bleActivity);
        notification.setAutoCancel(false);
        notification.setGroup(GROUP_KEY);
        //notification.setContentTitle("블루투스연결된 장비:\n" + getPreferencesName() + "\n");
        //notification.setContentText("블루투스 이벤트 장치선택 변경/수정/모니터링");
        notification.setSmallIcon(R.drawable.logo_penut);
        notification.setAutoCancel(false);
        notification.setOngoing(true);

        //notification.setColor(ContextCompat.getColor(bleActivity, colorID));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId("channel_id");
        }
        notification.setPriority(Notification.PRIORITY_HIGH);
        notification.setContentIntent(open);


        //notification.addAction(new NotificationCompat.Action(R.drawable.logo, "켜기", pIntent_positive));
        //notification.addAction(new NotificationCompat.Action(R.drawable.logo, "끄기", pIntent_negative));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(notificationId, mNotificationManager.build());

        contentview = new RemoteViews(getPackageName(), R.layout.removeview_ble);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                //contentview.setImageViewResource(R.id.ons, android.R.drawable.ic_delete);
                contentview.setViewVisibility(R.id.offs, View.VISIBLE);
                contentview.setViewVisibility(R.id.ons, View.GONE);

            } else {
                contentview.setViewVisibility(R.id.offs, View.GONE);
                contentview.setViewVisibility(R.id.ons, View.VISIBLE);
                //contentview.setImageViewResource(R.id.offs, android.R.drawable.ic_lock_power_off);

            }
        }

        //타이틀 설정
        contentview.setTextViewText(R.id.title, "블루투스 장비명 : " + getPreferencesName());
        contentview.setOnClickPendingIntent(R.id.closed, pIntent_closed);
        //contentview.setOnClickPendingIntent(R.id.ons, pIntent_positive);
        //contentview.setOnClickPendingIntent(R.id.offs, pIntent_negative);

        notification.setContent(contentview);
        mNotificationManager.notify(notificationId, notification.build());
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    setConnection(false);
                    Log.d(TAG, "통화상태 아님");
                    //Toast.makeText(getApplicationContext(), "통화상태 아님~~~"+incomingNumber, Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    setConnection(true);
                    Log.d(TAG, "통화중");
                    Toast.makeText(getApplicationContext(), "통화중~~~" + incomingNumber, Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    setConnection(false);
                    Log.d(TAG, "전화벨링");
                    //Toast.makeText(getApplicationContext(), "전화벨링~~~"+incomingNumber, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mBroadcastReceiverDevice);
        //unregisterReceiver(bluetoothReceiverBLEOnOff);
        setNoti();
    }

    private void setSound() {

       /* NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, 20001);

        } else {
            if (mAudioManager == null) {
                mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);

            } else if (mAudioManager != null) {

                if (mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
                }
            }
        }*/
    }

    BroadcastReceiver bluetoothReceiverBLEOnOff = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (mBluetoothAdapter.isEnabled()) {
                    //on
                    btn1.setBackgroundColor(Color.rgb(255, 255, 255));
                    btn2.setBackgroundColor(Color.rgb(255, 2, 2));
                } else {
                    //off
                    btn1.setBackgroundColor(Color.rgb(255, 2, 2));
                    btn2.setBackgroundColor(Color.rgb(255, 255, 255));
                }
                setNoti();
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //selectDevice();
                        Log.i(TAG, "@@@ MYSERVICE 블루투스 STATE_TURNING_ON ");
                        if (!isConnection()) {

                        }

                        Toast.makeText(getApplicationContext(), "블루투스 STATE_TURNING_ON", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG, "@@@ MYSERVICE 블루투스 STATE_TURNING_OFF ");
                        if (!isConnection()) {
                            //music = MediaPlayer.create(getApplicationContext(), R.raw.voice_bell);
                            //music.setLooping(false);
                            //music.start();
                        }


                        Toast.makeText(getApplicationContext(), "블루투스 STATE_TURNING_OFF", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        }
    };


    //브로드캐스트리시버를 이용하여 블루투스 장치가 연결이 되고, 끊기는 이벤트를 받아 올 수 있다.
    private final BroadcastReceiver mBroadcastReceiverDevice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // 장치가 연결이 되었으면

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:

                    if (!isConnection() && getPreferencesAddress().equals(device.getAddress())) {
                        Log.d(TAG, "연결된 장치 : " + device.getName().toString() + device.getAddress().toString());
                        Toast.makeText(getApplicationContext(), "장치연결됨 : " + device.getName().toString(), Toast.LENGTH_LONG).show();

                    }

                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    if (!isConnection() && getPreferencesAddress().equals(device.getAddress())) {

                        Log.d(TAG, "연결해제");
                        Toast.makeText(getApplicationContext(), "장치연결해제 :" + device.getName().toString(), Toast.LENGTH_LONG).show();

                        music = MediaPlayer.create(getApplicationContext(), R.raw.voice_bell);
                        music.setLooping(false);
                        music.start();
                    }

                    break;
            }
        }
    };


    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않음", Toast.LENGTH_LONG).show();
            // 앱종료
            //finish();
        } else {

            // 블루투스 지원하며 비활성 상태인 경우.
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();


            }

            checkBtnColor();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    // 블루투스 활성화 상태
                    selectDevice();

                } else if (resultCode == RESULT_CANCELED) {
                    // 블루투스 비활성화 상태
                }

                break;
            case 20001:
                if (requestCode == 20001) {
                    setSound();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 블루투스 지원하며 활성 상태인 경우.
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        mDevices = mBluetoothAdapter.getBondedDevices();
        final List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
        List<String> listItems = new ArrayList<String>();

        //페어링된 장치가 있으면
        if (mDevices.size() > 0) {
            mPariedDeviceCount = mDevices.size();
            int i = 0;
            for (BluetoothDevice device : mDevices) {
                //페어링된 장치 이름과, MAC주소를 가져올 수 있다.
                deviceList.add(device);
                listItems.add(device.getName());
                Log.d(TAG, "페어링된 장치 " + (++i) + " : " + device.getName().toString() + device.getAddress().toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), "먼저 휴대폰에서 블루투스 연결을 해 주세요", Toast.LENGTH_LONG).show();
            return;
        }

        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치선택 \n(현재선택장비:" + (getPreferencesName() == null ? "없음" : getPreferencesName()) + ")");

        // 취소 항목 추가.
        listItems.add("취소");
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, final int item) {
                // TODO Auto-generated method stub
                if (item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "장치연결취소", Toast.LENGTH_LONG).show();
                    //finish();
                } else {

                    // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                    BluetoothDevice device = deviceList.get(item);
                    Log.d(TAG, "리스트 선택 " + item + "  name = " + device.getName() + "  address=" + device.getAddress());

                    try {
                        //선택한 디바이스 페어링 요청
                        Method method = device.getClass().getMethod("createBond", (Class[]) null);
                        method.invoke(device, (Object[]) null);

                        savePreferences(device.getName(), device.getAddress());
                        setNoti();
                        ttt.setText("연결장비 : " + device.getName());

                        JoinWebAsyncTask boardAsyncTask = new JoinWebAsyncTask(bleActivity, "connectToSelectedDevice");
                        boardAsyncTask.execute(device.getAddress());

                        //selectDevice = position;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void savePreferences(String name, String address) {
        SharedPreferences pref = getSharedPreferences("ble_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("address", address);
        editor.putString("name", name);
        editor.commit();
    }

    private String getPreferencesAddress() {
        SharedPreferences pref = getSharedPreferences("ble_pref", MODE_PRIVATE);
        return pref.getString("address", "") == null ? "" : pref.getString("address", "");
    }


    private String getPreferencesName() {
        SharedPreferences pref = getSharedPreferences("ble_pref", MODE_PRIVATE);
        return pref.getString("name", "") == null ? "" : pref.getString("name", "");
    }

    public void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = java.util.UUID.fromString("00001108-0000-1000-8000-00805F9B34FB");

        try {
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            Toast.makeText(getApplicationContext(), "통신연결", Toast.LENGTH_LONG).show();
            //info.setText("통신연결 수신중....");
            Log.d(TAG, "통신연결 성공");

        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            Log.d(TAG, e.toString());
            Toast.makeText(getApplicationContext(), "블루투스 장비 연결 중 오류", Toast.LENGTH_LONG).show();
        }
    }

    // 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드.
    BluetoothDevice getDeviceFromBondedList(String address) {
        BluetoothDevice selectedDevice = null;
        for (BluetoothDevice deivce : mDevices) {
            if (address.equals(deivce.getAddress())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    public void mOnBluetoothSearch(View view) {
        selectDevice();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void OFF(View view) {
        mBluetoothAdapter.disable();

    }

    public void ON(View view) {
        mBluetoothAdapter.enable();

    }

    private void checkBtnColor() {
        if (mBluetoothAdapter.isEnabled()) {
            //on
            btn1.setBackgroundColor(Color.rgb(255, 255, 255));
            btn2.setBackgroundColor(Color.rgb(255, 2, 2));
        } else {
            //off
            btn1.setBackgroundColor(Color.rgb(255, 2, 2));
            btn2.setBackgroundColor(Color.rgb(255, 255, 255));
        }
    }


}
