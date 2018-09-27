package com.google.android.exoplayer2.nw;

/**
 * Created by Administrator on 2016-04-30.
 */

import android.util.Log;

import com.google.android.exoplayer2.VO.DetailVO;
import com.google.android.exoplayer2.VO.ListVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpClientManager {
    String TAG;
    String path;
    URL url;
    HttpURLConnection cons;
    BufferedWriter buffw;
    BufferedReader buffr;
    //BufferedReader rd;

    public HttpClientManager() {
        TAG = this.getClass().getName();
    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object request(String path, String params, String type) {
        Log.d("LOGDA_HTTP", "@@ path=" + path + " , params=" + params + " , type=" + type);
        Object resultValue = null;
        this.path = path;
        try {
            url = new URL(path);
            trustAllHosts();

            cons = (HttpURLConnection) url.openConnection();
            /*cons.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });*/

            cons.setRequestMethod("POST");
            cons.setReadTimeout(15000);
            cons.setConnectTimeout(5000);
            cons.setDoInput(true);
            cons.setDoOutput(true);
            cons.connect();

            buffw = new BufferedWriter(new OutputStreamWriter(cons.getOutputStream(), "UTF-8"));
            buffw.write(params);
            buffw.flush();

            Log.d("LOGDA_HTTP", "@@ ok ? " + cons.getResponseCode());

            if (cons.getResponseCode() == HttpURLConnection.HTTP_OK) {
                /*요청에 대한 응답을 받아온다!!*/

                buffr = new BufferedReader(new InputStreamReader(cons.getInputStream(), "UTF-8"));
                String data = null;
                StringBuilder sb = new StringBuilder();

                while ((data = buffr.readLine()) != null) {
                    sb.append(data);
                }

                Log.d("LOGDA_HTTP", "@@ 결과 string>" + sb.toString());

                /*서버측으로부터 받아온 텍스트 데이터를 제이슨으로 파싱해본다*/
                JSONObject jsonObject = new JSONObject(sb.toString());

               /*    "ID": "27",
                    "TITLE_NAME": "Audio English_하나의 신",
                    "IMG_NAME": "http://ilove14.cafe24.com/Image/Tn/button_image_green.jpg",
                    "TOT_TIME": "02:25.57",
                    "FILE_NAME": "AE_00014.mp3",
                    "FILE_DOWN_YN": "N"
               */

                switch (type) {
                    case "getStudyList":
                        resultValue = getStudyList(jsonObject);
                        break;
                    case "getStudySentence":
                        resultValue = getStudySentence(jsonObject);
                        break;
                    case "getVersion":
                        resultValue = getVersion(jsonObject);
                        break;
                    case "registerLike":
                        resultValue = registerLike(jsonObject);
                        break;
                    case "UnRegisterLike":
                        resultValue = UnRegisterLike(jsonObject);
                        break;

                    //
                }


            } else {
                Log.d("LOGDA_HTTP", "요청처리에 문제가 발생 하였습니다.");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (buffr != null) {
                try {
                    buffr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
/*
            if (buffw != null) {
                try {
                    buffw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
*/

            if (cons != null) {
                cons.disconnect();
                cons = null;
            }
        }

        Log.d("LOGDA_HTTP", "@@ resultValue =>" + resultValue);

        return resultValue;
    }

    private Object registerLike(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("response");
        String result = "";
        Log.d("LOGDA_HTTP", "size = " + array.length());

        for (int i = 0; i < array.length(); i++) {
            jsonObject = (JSONObject) array.get(i);
            result = jsonObject.getString("result");
            Log.d("LOGDA_HTTP", "result = " + result);
        }

        return result;
    }


    private Object UnRegisterLike(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("response");
        String result = "";
        Log.d("LOGDA_HTTP", "size = " + array.length());

        for (int i = 0; i < array.length(); i++) {
            jsonObject = (JSONObject) array.get(i);
            result = jsonObject.getString("result");
            Log.d("LOGDA_HTTP", "result = " + result);
        }

        return result;
    }

    private Object getVersion(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("response");
        String version = "";
        Log.d("LOGDA_HTTP", "size = " + array.length());

        for (int i = 0; i < array.length(); i++) {
            jsonObject = (JSONObject) array.get(i);
            version = jsonObject.getString("APP_VERSION");
            Log.d("LOGDA_HTTP", "version = " + version);
        }

        return version;
    }

    private Object getStudySentence(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("response");
        ArrayList<DetailVO> list = new ArrayList<>();

        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                DetailVO dto = new DetailVO();
                JSONObject obj = (JSONObject) array.get(i);

                dto.setIdx(obj.getString("ID"));
                dto.setOrder_seq(obj.getString("ORD_SEQ"));
                dto.setStart_loc_msec(obj.getString("START_LOC_MSEC"));
                dto.setEnd_loc_msec(obj.getString("END_LOC_MSEC"));
                dto.setStart_loc(obj.getString("START_LOC"));
                dto.setEnd_loc(obj.getString("END_LOC"));
                dto.setForeign_sentence(obj.getString("FOREIGN_SENTENCE"));
                dto.setKor_sentence(obj.getString("KOR_SENTENCE"));
                dto.setTot_time(obj.getString("TOT_TIME"));
                dto.setFile_name(obj.getString("FILE_NAME"));

                //Log.d("LOGDA_HTTP", "img_name = " + dto.getImg_name() + " setFile_name = " + dto.getFile_name() + " setFile_down_yn = " + dto.getFile_down_yn());

                list.add(dto);
            }
        }


        return list;
    }

    private Object getStudyList(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("response");
        ArrayList<ListVO> list = new ArrayList<>();

        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                ListVO dto = new ListVO();
                JSONObject obj = (JSONObject) array.get(i);

                dto.setIdx(obj.getString("ID"));
                //Log.d("LOGDA_HTTP", i + "-" + 1 + " " + obj.getString("ORD_SEQ"));
                dto.setTitle_name(obj.getString("TITLE_NAME"));
                //Log.d("LOGDA_HTTP", i + "-" + 2);
                dto.setImg_name(obj.getString("IMG_NAME"));
                //Log.d("LOGDA_HTTP", i + "-" + 3);
                dto.setTot_time(obj.getString("TOT_TIME"));
                //Log.d("LOGDA_HTTP", i + "-" + 4);
                dto.setFile_name(obj.getString("FILE_NAME"));
                //Log.d("LOGDA_HTTP", i + "-" + 5);
                dto.setFile_down_yn(obj.getString("FILE_DOWN_YN"));
                //Log.d("LOGDA_HTTP", i + "-" + 6);
                dto.setFavorite_yn(obj.getString("FAVORITE_YN"));
                //Log.d("LOGDA_HTTP", i + "-" + 7);
                dto.setKor_script_yn(obj.getString("KOR_SCRIPT_YN"));
                //Log.d("LOGDA_HTTP", i + "-" + 8);
                dto.setForeign_script_yn(obj.getString("FOREIGN_SCRIPT_YN"));
                //Log.d("LOGDA_HTTP", i + "-" + 9);//LANG_TYPE

                dto.setLang_type(obj.getString("LANG_TYPE"));

                //Log.d("LOGDA_HTTP", "img_name = " + dto.getImg_name() + " setFile_name = " + dto.getFile_name() + " setFile_down_yn = " + dto.getFile_down_yn());

                list.add(dto);
            }
        }


        return list;
    }
/*

    private Object setAgree(JSONObject jsonObject) throws JSONException {
        String rs = "";
        try {
            String jj = jsonObject.getString("iWorkOutPut");
            jsonObject = new JSONObject(jj);
            rs = jsonObject.getString("iStatus");

            Log.d("LOGDA_HTTP", "rs = " + rs);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private Object checkUserYn(JSONObject jsonObject) throws JSONException {
        String rs = "";
        try {
            String jj = jsonObject.getString("iWorkOutPut");
            jsonObject = new JSONObject(jj);
            rs = jsonObject.getString("iStatus");

            Log.d("LOGDA_HTTP", "rs = " + rs);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rs;
    }

    private Object getShoppingList(JSONObject jsonObject) throws JSONException {

        ArrayList<DeliVO> list = new ArrayList<>();

        try {

            String Form = jsonObject.getString("Form");
            String domain = jsonObject.getString("Domain");

            String file_name = "";
            String file_path = "";

            if (Form.equals("Simple")) {
                jsonObject = jsonObject.getJSONObject("Results");
                DeliVO dto = new DeliVO();

                dto.setIdx(jsonObject.getString("Code"));
                file_name = jsonObject.getString("M_FileName") == null ? "" : jsonObject.getString("M_FileName");
                file_path = jsonObject.getString("M_FilePath") == null ? "" : jsonObject.getString("M_FilePath");
                //로고이미지
                if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                    dto.setTitle_img("");
                } else {
                    dto.setTitle_img(domain + "/" + file_path + "/" + file_name);
                }

                dto.setTitle(jsonObject.getString("Name"));
                dto.setContent(jsonObject.getString("ShortContents"));
                dto.setDiscount_per(jsonObject.getString("DisRate"));
                dto.setDiscount_price(jsonObject.getString("DisPrice"));
                dto.setOriginal_price(jsonObject.getString("Price"));

                list.add(dto);

            } else {
                JSONArray array = jsonObject.getJSONArray("Results");

                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        DeliVO dto = new DeliVO();
                        JSONObject obj = (JSONObject) array.get(i);

                        dto.setIdx(obj.getString("Code"));
                        file_name = obj.getString("M_FileName") == null ? "" : obj.getString("M_FileName");
                        file_path = obj.getString("M_FilePath") == null ? "" : obj.getString("M_FilePath");
                        //로고이미지
                        if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                            dto.setTitle_img("");
                        } else {
                            dto.setTitle_img(domain + "/" + file_path + "/" + file_name);
                        }

                        dto.setTitle(obj.getString("Name"));
                        dto.setContent(obj.getString("ShortContents"));
                        dto.setDiscount_per(obj.getString("DisRate"));
                        dto.setDiscount_price(obj.getString("DisPrice"));
                        dto.setOriginal_price(obj.getString("Price"));

                        list.add(dto);
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Object getStampListSafe(JSONObject jsonObject) {
        */
/*int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGD", "@@ getStampList 결과 cnt=" + cnt);

        ArrayList<StampVO> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("getStampListSafe");
            if (cnt > 0) {
                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    dto.setShop_name(obj.getString("shop_name"));
                    dto.setShop_title(obj.getString("shop_title"));
                    dto.setShop_img_file(obj.getString("shop_img_files"));
                    dto.setShop_img_file2(obj.getString("shop_img_file2s"));
                    dto.setShop_img_file2_tel(obj.getString("shop_img_file2_tels"));
                    dto.setDeli_dong(obj.getString("deli_dong"));

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;*//*



        ArrayList<StampVO> list = new ArrayList<>();
        try {

            String domain = "";

            domain = jsonObject.getString("Domain");
            Log.d("LOGDA_HTTP", "@@iWorkOutPut =" + domain);

            JSONArray array = jsonObject.getJSONArray("Results");

            Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

            String file_name = "";
            String file_path = "";

            if (array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    //dto.setStamp("100");

                    //dto.setStamp(obj.getString("stamp"));
                    dto.setIdx(obj.getString("Code"));
                    dto.setDeli_dong(obj.getString("DeliveryArea"));
                    dto.setShop_name(obj.getString("Name"));
                    dto.setShop_title(obj.getString("Benefit"));

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    file_name = obj.getString("M_FileName") == null ? "" : obj.getString("M_FileName");
                    file_path = obj.getString("M_FilePath") == null ? "" : obj.getString("M_FilePath");

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    file_name = obj.getString("D_FileName") == null ? "" : obj.getString("D_FileName");
                    file_path = obj.getString("D_FilePath") == null ? "" : obj.getString("D_FilePath");

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file2("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file2(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    dto.setShop_img_file2_tel(obj.getString("Tel").replaceAll(" ", ""));
                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Object getStampListCash(JSONObject jsonObject) {
        */
/*int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGD", "@@ getStampList 결과 cnt=" + cnt);

        ArrayList<StampVO> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("getStampListCash");
            if (cnt > 0) {
                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    dto.setShop_name(obj.getString("shop_name"));
                    dto.setShop_title(obj.getString("shop_title"));
                    dto.setShop_img_file(obj.getString("shop_img_files"));
                    dto.setShop_img_file2(obj.getString("shop_img_file2s"));
                    dto.setShop_img_file2_tel(obj.getString("shop_img_file2_tels"));
                    dto.setDeli_dong(obj.getString("deli_dong"));

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;*//*


        ArrayList<StampVO> list = new ArrayList<>();
        try {

            String domain = "";

            domain = jsonObject.getString("Domain");
            Log.d("LOGDA_HTTP", "@@iWorkOutPut =" + domain);

            JSONArray array = jsonObject.getJSONArray("Results");

            Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

            String file_name = "";
            String file_path = "";

            if (array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    dto.setStamp("100");

                    //dto.setStamp(obj.getString("stamp"));
                    dto.setIdx(obj.getString("Code"));
                    dto.setDeli_dong(obj.getString("DeliveryArea"));
                    dto.setShop_name(obj.getString("Name"));
                    dto.setShop_title(obj.getString("Benefit"));

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    file_name = obj.getString("M_FileName") == null ? "" : obj.getString("M_FileName");
                    file_path = obj.getString("M_FilePath") == null ? "" : obj.getString("M_FilePath");

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    file_name = obj.getString("D_FileName") == null ? "" : obj.getString("D_FileName");
                    file_path = obj.getString("D_FilePath") == null ? "" : obj.getString("D_FilePath");

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file2("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file2(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    dto.setShop_img_file2_tel(obj.getString("Tel").replaceAll(" ", ""));
                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Object getStampListTime(JSONObject jsonObject) {
        */
/*int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGD", "@@ getStampList 결과 cnt=" + cnt);

        ArrayList<StampVO> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("getStampListTime");
            if (cnt > 0) {
                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    dto.setShop_name(obj.getString("shop_name"));
                    dto.setShop_title(obj.getString("shop_title"));
                    dto.setShop_img_file(obj.getString("shop_img_files"));
                    dto.setShop_img_file2(obj.getString("shop_img_file2s"));
                    dto.setShop_img_file2_tel(obj.getString("shop_img_file2_tels"));
                    dto.setDeli_dong(obj.getString("deli_dong"));

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;*//*


        ArrayList<StampVO> list = new ArrayList<>();
        try {

            String domain = "";

            domain = jsonObject.getString("Domain");
            Log.d("LOGDA_HTTP", "@@iWorkOutPut =" + domain);

            JSONArray array = jsonObject.getJSONArray("Results");

            Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

            String file_name = "";
            String file_path = "";

            if (array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    //dto.setStamp("100");

                    //dto.setStamp(obj.getString("Amount"));
                    dto.setIdx(obj.getString("Code"));
                    dto.setDeli_dong(obj.getString("DeliveryArea"));
                    dto.setShop_name(obj.getString("Name"));
                    dto.setShop_title(obj.getString("Benefit"));

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    file_name = obj.getString("M_FileName") == null ? "" : obj.getString("M_FileName");
                    file_path = obj.getString("M_FilePath") == null ? "" : obj.getString("M_FilePath");

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    file_name = obj.getString("D_FileName") == null ? "" : obj.getString("D_FileName");
                    file_path = obj.getString("D_FilePath") == null ? "" : obj.getString("D_FilePath");

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file2("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file2(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    dto.setShop_img_file2_tel(obj.getString("Tel").replaceAll(" ", ""));
                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Object getStampList(JSONObject jsonObject) {
        */
/*int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGD", "@@ getStampList 결과 cnt=" + cnt);

        ArrayList<StampVO> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("getStampList");
            if (cnt > 0) {
                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    dto.setStamp(obj.getString("stamp"));
                    dto.setShop_name(obj.getString("shop_name"));
                    dto.setShop_title(obj.getString("shop_title"));
                    dto.setShop_img_file(obj.getString("shop_img_files"));
                    dto.setShop_img_file2(obj.getString("shop_img_file2s"));
                    dto.setShop_img_file2_tel(obj.getString("shop_img_file2_tels"));
                    dto.setDeli_dong(obj.getString("deli_dong"));


                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

*//*

        ArrayList<StampVO> list = new ArrayList<>();
        try {

            String domain = "";

            domain = jsonObject.getString("Domain");
            Log.d("LOGDA_HTTP", "@@iWorkOutPut =" + domain);

            JSONArray array = jsonObject.getJSONArray("Results");

            Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

            String file_name = "";
            String file_path = "";

            if (array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    //dto.setStamp("100");

                    dto.setStamp(obj.getString("Amount"));
                    dto.setIdx(obj.getString("Code"));
                    dto.setDeli_dong(obj.getString("DeliveryArea"));
                    dto.setShop_name(obj.getString("Name"));
                    dto.setShop_title(obj.getString("Benefit"));

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    file_name = obj.getString("M_FileName") == null ? "" : obj.getString("M_FileName");
                    file_path = obj.getString("M_FilePath") == null ? "" : obj.getString("M_FilePath");

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    file_name = obj.getString("D_FileName") == null ? "" : obj.getString("D_FileName");
                    file_path = obj.getString("D_FilePath") == null ? "" : obj.getString("D_FilePath");

                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    //로고이미지
                    if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                        dto.setShop_img_file2("");
                        Log.d("LOGDA_HTTP", "@@결과 IN");
                    } else {
                        dto.setShop_img_file2(domain + "/" + file_path + "/" + file_name);
                        Log.d("LOGDA_HTTP", "@@결과 OUT");
                    }

                    dto.setShop_img_file2_tel(obj.getString("Tel").replaceAll(" ", ""));
                    Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Object getStampListPop(JSONObject jsonObject) throws JSONException {
        ArrayList<StampVO> list = new ArrayList<>();
        try {

            String domain = jsonObject.getString("Domain");
            String Form = jsonObject.getString("Form");
            Log.d("LOGDA_HTTP", "@@iWorkOutPut =" + domain);
            JSONArray array = null;
            String file_name = "";
            String file_path = "";
            StampVO dto = new StampVO();

            if (Form.equals("Simple")) {

                jsonObject = jsonObject.getJSONObject("Results");

                dto.setIdx(jsonObject.getString("Code"));
                dto.setDeli_dong(jsonObject.getString("DeliveryArea"));
                dto.setShop_name(jsonObject.getString("Name"));
                dto.setShop_title(jsonObject.getString("Benefit"));

                file_name = jsonObject.getString("M_FileName") == null ? "" : jsonObject.getString("M_FileName");
                file_path = jsonObject.getString("M_FilePath") == null ? "" : jsonObject.getString("M_FilePath");

                //로고이미지
                if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                    dto.setShop_img_file("");
                } else {
                    dto.setShop_img_file(domain + "/" + file_path + "/" + file_name);
                }

                file_name = jsonObject.getString("D_FileName") == null ? "" : jsonObject.getString("D_FileName");
                file_path = jsonObject.getString("D_FilePath") == null ? "" : jsonObject.getString("D_FilePath");


                //로고이미지
                if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                    dto.setShop_img_file2("");
                } else {
                    dto.setShop_img_file2(domain + "/" + file_path + "/" + file_name);
                }

                dto.setShop_img_file2_tel(jsonObject.getString("Tel").replaceAll(" ", ""));

                list.add(dto);
            } else {
                array = jsonObject.getJSONArray("Results");
                if (array.length() > 0) {

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject obj = (JSONObject) array.get(i);

                        dto.setIdx(obj.getString("Code"));
                        dto.setDeli_dong(obj.getString("DeliveryArea"));
                        dto.setShop_name(obj.getString("Name"));
                        dto.setShop_title(obj.getString("Benefit"));
                        file_name = obj.getString("M_FileName") == null ? "" : obj.getString("M_FileName");
                        file_path = obj.getString("M_FilePath") == null ? "" : obj.getString("M_FilePath");

                        //로고이미지
                        if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                            dto.setShop_img_file("");

                        } else {
                            dto.setShop_img_file(domain + "/" + file_path + "/" + file_name);

                        }

                        file_name = obj.getString("D_FileName") == null ? "" : obj.getString("D_FileName");
                        file_path = obj.getString("D_FilePath") == null ? "" : obj.getString("D_FilePath");

                        //로고이미지
                        if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                            dto.setShop_img_file2("");

                        } else {
                            dto.setShop_img_file2(domain + "/" + file_path + "/" + file_name);

                        }

                        dto.setShop_img_file2_tel(obj.getString("Tel").replaceAll(" ", ""));

                        list.add(dto);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Object getMyPointList(JSONObject jsonObject) {
        ArrayList<PointVO> list = new ArrayList<>();
        try {

            JSONArray array = jsonObject.getJSONArray("Results");
            Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

            if (array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    PointVO dto = new PointVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    dto.setCompCode(obj.getString("CompCode"));
                    dto.setCompName(obj.getString("CompName"));
                    dto.setPoint(obj.getString("Amount"));

                    dto.setGbn(obj.getString("TypeName"));
                    dto.setInput_date(obj.getString("RequestDate"));

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Object sendPoint(JSONObject jsonObject) {
        String rs = "";
        try {
            String jj = jsonObject.getString("iWorkOutPut");
            jsonObject = new JSONObject(jj);
            rs = jsonObject.getString("iStatus");

            Log.d("LOGDA_HTTP", "cnt = " + rs);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rs;
    }


    private Object getVersion(JSONObject jsonObject) {
        int cnt = 0;
        try {
            String jj = jsonObject.getString("Results");
            jsonObject = new JSONObject(jj);
            cnt = jsonObject.getInt("AppVersion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    //배달책자 조회
    private Object getDeliListFind(JSONObject jsonObject) throws JSONException {
        String domain = "";
        try {

            Log.d("LOGDA_HTTP", "@@jsonObject =" + jsonObject.toString());
            domain = jsonObject.getString("Domain");
            Log.d("LOGDA_HTTP", "@@Domain =" + domain);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<DeliVO> list = new ArrayList<>();
        JSONArray array = jsonObject.getJSONArray("Results");

        Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

        String file_name = "";
        String file_path = "";

        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {

                DeliVO dto = new DeliVO();
                JSONObject obj = (JSONObject) array.get(i);

                Log.d("LOGDA_HTTP", array.get(i).toString());

                dto.setIdx(obj.getString("Code"));
                dto.setDeli_dong(obj.getString("DeliveryArea"));
                dto.setShop_name(obj.getString("Name"));
                dto.setShop_title(obj.getString("Benefit"));

                Log.d("LOGDA_HTTP", "@@결과 사이즈 = " + array.length());

                file_name = obj.getString("M_FileName") == null ? "" : obj.getString("M_FileName");
                file_path = obj.getString("M_FilePath") == null ? "" : obj.getString("M_FilePath");

                //로고이미지
                if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                    dto.setShop_img_file("");
                    //Log.d("LOGDA_HTTP", "@@결과 IN");
                } else {
                    dto.setShop_img_file(domain + "/" + file_path + "/" + file_name);
                    //Log.d("LOGDA_HTTP", "@@결과 OUT");
                }


                file_name = obj.getString("D_FileName") == null ? "" : obj.getString("D_FileName");
                file_path = obj.getString("D_FilePath") == null ? "" : obj.getString("D_FilePath");

                //로고이미지
                if (file_name == null || file_name.equals("") || file_path == null || file_path.equals("")) {
                    dto.setShop_img_file2("");
                    //Log.d("LOGDA_HTTP", "@@결과 IN");
                } else {
                    dto.setShop_img_file2(domain + "/" + file_path + "/" + file_name);
                    //Log.d("LOGDA_HTTP", "@@결과 OUT");
                }

                dto.setShop_img_file2_tel(obj.getString("Tel").replaceAll(" ", ""));
                dto.setInput_date("");
                dto.setTotal_cnt(obj.getString("CompCount"));


                //다운로드 여부
                dto.setDownyn("");
                list.add(dto);
            }
        }

        return list;
    }


    private Object getMyPoint(JSONObject jsonObject) {
        String rs = "";
        try {
            String jj = jsonObject.getString("Results");
            jsonObject = new JSONObject(jj);
            rs = jsonObject.getString("Amount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rs;
    }


    private Object getMyStamp(JSONObject jsonObject) {
        String rs = "";
        try {
            String jj = jsonObject.getString("Results");
            jsonObject = new JSONObject(jj);
            rs = jsonObject.getString("Amount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rs;
    }


    private Object payNow(JSONObject jsonObject) {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cnt;
    }


    private Object getUserInfo(JSONObject jsonObject) throws JSONException {

        LoginVO dto = new LoginVO();
        JSONObject js = (JSONObject) jsonObject.get("getUserInfo");

        dto.setJoin(js.getString("joins"));
        dto.setResult_cnt(js.getString("result_cnt"));

        return dto;
    }

    private Object join(JSONObject jsonObject) {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    private Object registerID(JSONObject jsonObject) {
        return null;
    }

    private Object sendSmsDaeli(JSONObject jsonObject) {
        return null;
    }

    private Object getDeliShowUpList3(JSONObject jsonObject) throws JSONException {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGDA_HTTP", "@@결과");

        ArrayList<DeliVO3> list = new ArrayList<>();
        JSONArray array = jsonObject.getJSONArray("getDeliShowUpList3");

        if (cnt > 0) {
            for (int i = 0; i < array.length(); i++) {
                DeliVO3 dto = new DeliVO3();
                JSONObject obj = (JSONObject) array.get(i);

                dto.setIdx(obj.getString("idx"));
                dto.setNo(obj.getString("no"));
                dto.setName(obj.getString("name"));
                dto.setName_tel(obj.getString("name_tel"));

                list.add(dto);
            }
        }

        return list;
    }

    private Object getShowImageName(JSONObject jsonObject) throws JSONException {

        String name[] = new String[2];
        name[0] = jsonObject.getString("img");
        name[1] = jsonObject.getString("img_tel");
        Log.d("LOGDA_HTTP", "@@결과 name[0] =" + name[0] + " name[1] =" + name[1]);

        return name;
    }

    private Object getOrderList3(JSONObject jsonObject) throws JSONException {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGDA_HTTP", "@@결과");

        ArrayList<OrderVO> list = new ArrayList<>();
        JSONArray array = jsonObject.getJSONArray("getOrderList3");

        if (cnt > 0) {
            for (int i = 0; i < array.length(); i++) {
                OrderVO dto = new OrderVO();
                JSONObject obj = (JSONObject) array.get(i);

                dto.setIdx(obj.getString("idx"));
                dto.setName(obj.getString("name"));
                dto.setText(obj.getString("text"));
                dto.setInput_date(obj.getString("input_date"));
                dto.setUpdate_date(obj.getString("update_date"));
                dto.setMobile_no(obj.getString("mobile_no"));
                dto.setId(obj.getString("id"));
                dto.setPwd(obj.getString("pwd"));

                dto.setPrice(obj.getString("price"));
                dto.setAddress(obj.getString("address"));
                dto.setShop(obj.getString("shop"));
                dto.setCustomer_no(obj.getString("customer_no"));

                dto.setDay(obj.getString("day"));
                dto.setPay_yn(obj.getString("pay_yn"));

                dto.setStamp(obj.getString("stamp"));
                dto.setPoint(obj.getString("point"));

                list.add(dto);
            }
        }

        return list;
    }

    private Object getPointSum(JSONObject jsonObject) {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("getPointSum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    private Object getPointList(JSONObject jsonObject) {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGD", "@@ getPointList 결과 cnt=" + cnt);

        ArrayList<PointVO> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("getPointList");
            if (cnt > 0) {
                for (int i = 0; i < array.length(); i++) {
                    PointVO dto = new PointVO();
                    JSONObject obj = (JSONObject) array.get(i);
                    dto.setShop(obj.getString("shop"));
                    dto.setPoint(obj.getString("point"));
                    dto.setPointSum(obj.getString("pointSum"));
                    dto.setShop_tel(obj.getString("shop_tel"));
                    dto.setInput_date(obj.getString("input_date"));
                    dto.setMobile_no(obj.getString("mobile_no"));
                    dto.setText(obj.getString("text"));

                    Log.d("LOGD", "@@ mobile_no " + obj.getString("mobile_no"));
                    dto.setAddsum(obj.getString("addsum"));
                    dto.setUse_yn(obj.getString("use_yn"));

                    Log.d("LOGD", "@@ pointsum " + obj.getString("addsum"));
                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Object sendAuthSMS(JSONObject jsonObject) {
        String cnt[] = new String[2];
        try {
            cnt[0] = jsonObject.getString("result_cnt");
            cnt[1] = jsonObject.getString("auth");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cnt;
    }


    private Object sendAuthSMSUpdate(JSONObject jsonObject) {
        String cnt[] = new String[2];
        try {
            cnt[0] = jsonObject.getString("result_cnt");
            cnt[1] = jsonObject.getString("auth");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cnt;
    }


    //인증하기
    private Object authCheck(JSONObject jsonObject) {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    private Object getTownShopList(JSONObject jsonObject) {
        int cnt = 0;
        try {
            cnt = jsonObject.getInt("result_cnt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("LOGD", "@@ getTownShopList 결과 cnt=" + cnt);

        ArrayList<StampVO> list = new ArrayList<>();
        JSONArray array = null;
        try {
            array = jsonObject.getJSONArray("getTownShopList");
            if (cnt > 0) {
                for (int i = 0; i < array.length(); i++) {
                    StampVO dto = new StampVO();
                    JSONObject obj = (JSONObject) array.get(i);

                    dto.setStamp(obj.getString("stamp"));
                    dto.setShop_name(obj.getString("shop_name"));
                    dto.setShop_title(obj.getString("shop_title"));
                    dto.setShop_img_file(obj.getString("shop_img_files"));
                    dto.setShop_img_file2(obj.getString("shop_img_file2s"));
                    dto.setShop_img_file2_tel(obj.getString("shop_img_file2_tels"));

                    dto.setDeli_dong(obj.getString("deli_dong"));
                    Log.d("LOGD", "@@ getTownShopList list.add cnt=" + cnt);

                    list.add(dto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return list;
    }
*/


}
