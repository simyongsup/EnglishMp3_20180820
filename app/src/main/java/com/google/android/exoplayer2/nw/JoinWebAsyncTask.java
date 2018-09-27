package com.google.android.exoplayer2.nw;

/**
 * Created by Administrator on 2016-04-30.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.VO.DetailVO;
import com.google.android.exoplayer2.VO.ListVO;
import com.google.android.exoplayer2.dialog.CommonMusicStopDialog;
import com.google.android.exoplayer2.dialog.CommonPermissionChecckDialog;
import com.google.android.exoplayer2.english_mp3.BleActivity;
import com.google.android.exoplayer2.english_mp3.MainActivity;
import com.google.android.exoplayer2.english_mp3.PlayerSpeedActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class JoinWebAsyncTask extends AsyncTask<String, Void, Object> {
    Context mContext;
    String commandType;
    MainActivity mainActivity;
    PlayerSpeedActivity playerSpeedActivity;
    CommonPermissionChecckDialog commonPermissionChecckDialog;
    BleActivity bleActivity;

    String params[] = null;
    String param;

    public JoinWebAsyncTask(MainActivity mainActivity, String commandType) {
        this.commandType = commandType;
        this.mainActivity = mainActivity;
    }

    public JoinWebAsyncTask(PlayerSpeedActivity playerSpeedActivity, String commandType) {
        this.commandType = commandType;
        this.playerSpeedActivity = playerSpeedActivity;
    }

    public JoinWebAsyncTask(CommonPermissionChecckDialog commonPermissionChecckDialog, String commandType) {
        this.commonPermissionChecckDialog = commonPermissionChecckDialog;
        this.commandType = commandType;
    }

    public JoinWebAsyncTask(BleActivity bleActivity, String commandType) {
        this.commandType = commandType;
        this.bleActivity = bleActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Object doInBackground(String... params) {
        HttpClientManager manager = new HttpClientManager();

        if (commandType.equals("getStudyList") && mainActivity != null) {
            Log.d("LOGDA", "getStudyList");
            mainActivity.progress.sendEmptyMessage(0);
        }

        if (commandType.equals("getFileDownLoad")) {
            this.params = params;
            getFileDownLoadProcess();
            return params;

        } else if (commandType != null && commandType.equals("permisionInfoThreadDialog")) {
            return 0;

        } else {
            if (params != null && params.length > 1) {
                return manager.request(params[0], params[1], params[2]);
            } else {
                param = params[0];
                return param;
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (commandType.equals("getFileDownLoad") && playerSpeedActivity != null) {

            Message msg = playerSpeedActivity.progressDlgUpdate.obtainMessage();
            msg.what = Integer.parseInt(getValue());
            playerSpeedActivity.progressDlgUpdate.sendMessage(msg);

        }
    }

    @Override
    protected void onPostExecute(Object obj) {

        switch (commandType) {
            case "getStudyList":
                getStudyList(obj);
                break;

            case "getStudySentence":
                getStudySentence(obj);
                break;

            case "getFileDownLoad":
                getFileDownLoadFinish(obj);
                break;

            case "getVersion":
                getVersion(obj);
                break;

            case "registerLike":
                registerLike(obj);
                break;

            case "UnRegisterLike":
                UnRegisterLike(obj);
                break;

            case "registerLikePlayerActivity":
                registerLikePlayerActivity(obj);
                break;

            case "UnRegisterLikePlayerActivity":
                UnRegisterLikePlayerActivity(obj);
                break;

            case "permisionInfoThreadDialog":
                permisionInfoThreadDialog(obj);
                break;

            case "connectToSelectedDevice":
                connectToSelectedDevice(obj);
                break;

            case "connectToSelectedDeviceMyService":
                connectToSelectedDeviceMyService(obj);
                break;

        }

        if (commandType.equals("getStudyList") && mainActivity != null)
            mainActivity.progressFinish.sendEmptyMessage(0);
    }


    private void connectToSelectedDeviceMyService(Object obj) {

        //if(myService !=null){
        //myService.connectToSelectedDevice(param);
        //}
    }

    private void connectToSelectedDevice(Object obj) {

        if (bleActivity != null) {
            bleActivity.connectToSelectedDevice(param);
        }
    }


    private void permisionInfoThreadDialog(Object obj) {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt = 4;
                while (true) {
                    Message msg = commonPermissionChecckDialog.handler.obtainMessage();
                    --cnt;
                    msg.arg1 = cnt;
                    commonPermissionChecckDialog.handler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
    }

    private void UnRegisterLikePlayerActivity(Object obj) {
        if (obj != null) {
            String rs = (String) obj;
            playerSpeedActivity.showToastOkUnRegister(rs);
        }
    }

    private void registerLikePlayerActivity(Object obj) {
        if (obj != null) {
            String rs = (String) obj;
            playerSpeedActivity.showToastOkRegister(rs);
        }
    }

    private void UnRegisterLike(Object obj) {
        if (obj != null) {
            String rs = (String) obj;
            mainActivity.showToastUnRegister(rs);
        }
    }

    private void registerLike(Object obj) {
        if (obj != null) {
            String rs = (String) obj;
            mainActivity.showToast(rs);
        }
    }

    private void getVersion(Object obj) {
        if (obj != null) {
            String rs = (String) obj;
            int result = Integer.parseInt(rs);
            int versionCode = 0;
            try {
                versionCode = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Log.d("LOGDA", "JoinWebAsyncTask @@ versionCode =" + versionCode);
            if (result > 0 && versionCode < result) {
                Log.d("LOGDA", "버전업데이트 알림");
                mainActivity.ShowUpdateVersion.sendEmptyMessage(0);
                //Intent intent = new Intent(mainActivity, ShowUpdateActivity.class);
                //mainActivity.startActivity(intent);
            } else {
                Log.d("LOGDA", "버전업데이트 정상");
                mainActivity.afterCheckVersion();
            }
        }
    }

    private void getFileDownLoadFinish(Object obj) {
        //playerSpeedActivity.go = Save_Path + "/" + File_Name;
        //playerSpeedActivity.uri = Uri.parse(playerSpeedActivity.go);
        Log.e("LOGDA_REPEAT", "getFileDownLoadFinish  uri =" + playerSpeedActivity.uri);
        MediaPlayer mp = MediaPlayer.create(playerSpeedActivity, playerSpeedActivity.uri);
        if (mp == null) {
            Log.e("LOGDA", "파일 null");
            //playerSpeedActivity.progressDlgFinishNullFile.sendEmptyMessage(0);
            Message msg = playerSpeedActivity.progressDlgFinishNullFile.obtainMessage();
            Bundle bd = new Bundle();
            bd.putString("id", params[1]);
            bd.putString("file_name", params[2]);
            msg.setData(bd);
            playerSpeedActivity.progressDlgFinishNullFile.sendMessage(msg);

        } else {
            Log.e("LOGDA", "파일 not null");
            playerSpeedActivity.progressDlgFinish.sendEmptyMessage(0);
        }
    }

    String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    String File_Name = "";
    String File_extend = ".mp3";
    String fileURL = ""; // URL
    String Save_Path = "";
    String Save_folder = "/EnglishAppData";

    private void getFileDownLoadProcess() {
        Log.e("LOGDA", "파일다운로드!!!");
        // 다운로드 경로를 외장메모리 사용자 지정 폴더로 함.
        File_Name = params[2];
        Save_Path = path + Save_folder;
        File dir = new File(Save_Path);
        fileURL = "http://ilove14.cafe24.com/ESTD/Download.php?token=12345&ID=" + params[1] +
                "&UUID_KEY=" + playerSpeedActivity.getDevicesUUID() +
                "&VERSION=Android" + Build.VERSION.RELEASE;

        // 폴더가 존재하지 않을 경우 폴더를 만듦
        if (!dir.exists()) {
            dir.mkdir();
        }

        playerSpeedActivity.go = Save_Path + "/" + File_Name;
        playerSpeedActivity.uri = Uri.parse(playerSpeedActivity.go);

        // 다운로드 폴더에 동일한 파일명이 존재하는지 확인해서
        // 없으면 다운받고 있으면 해당 파일 실행시킴.
        if (new File(Save_Path + "/" + File_Name).exists() == false) {
            Log.e("LOGDA_REPEAT", "파일 다운로드 경로에 없음!!!");

            try {
                playerSpeedActivity.progressDlg.sendEmptyMessage(0);
                serverDownload(fileURL, Save_Path + "/" + File_Name);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("LOGDA", "파일 다운로드 경로에있음!!!");

        }
    }

    private void serverDownload(String ServerUrl, String LocalPath) {
        URL mp3url;
        int Read;
        try {
            mp3url = new URL(ServerUrl);
            HttpURLConnection conn = (HttpURLConnection) mp3url.openConnection();
            int len = conn.getContentLength();
            byte[] tmpByte = new byte[len];
            InputStream is = conn.getInputStream();
            File file = new File(LocalPath);
            FileOutputStream fos = new FileOutputStream(file);

            long total = 0;
            for (; ; ) {
                Read = is.read(tmpByte);
                if (Read <= 0) {
                    break;
                }
                total += Read;
                setValue("" + (int) ((total * 100) / len));
                Log.d("LOGDA", "valeu = " + getValue());
                publishProgress();
                fos.write(tmpByte, 0, Read);
            }
            is.close();
            fos.close();
            conn.disconnect();

        } catch (MalformedURLException e) {
            Log.e("LOGDA", e.getMessage());

        } catch (IOException e) {
            Log.e("LOGDA", e.getMessage());
            e.printStackTrace();

        }
    }

    private void getStudySentence(Object obj) {
        if (obj != null) {
            ArrayList<DetailVO> list = (ArrayList<DetailVO>) obj;
            Log.d("LOGDA_REPEAT", "setDetailList list.size() = " + list.size());
            playerSpeedActivity.setDetailList(list);
        }
    }

    private void getStudyList(Object obj) {
        Log.d("LOGD", "@@@ getMyPointList");
        ArrayList<ListVO> list = (ArrayList<ListVO>) obj;
        if (list != null && list.size() > 0) {
            //등록셔틀기사유저
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); ++i) {
                    ListVO vo = list.get(i);
                    mainActivity.adapter.addItemString(vo);
                }
            }
        } else {
            //mainActivity.b_more.setText("마지막 입니다");
            //Toast.makeText(mainActivity,"마지막 입니다.",Toast.LENGTH_LONG).show();
        }
        mainActivity.progressFinish.sendEmptyMessage(0);
        mainActivity.progressFinish.sendEmptyMessage(0);

    }


}
