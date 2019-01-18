package com.jlg.mobsms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mob.MobSDK;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * This class provides access to mobsms on the device.
 */
public class mobsms extends CordovaPlugin {

    // 填写从短信SDK应用后台注册得到的APPKEY
    //此APPKEY仅供测试使用，且不定期失效，请到mob.com后台申请正式APPKEY
    private String APPKEY = "29d025c53ffd1";//"f3fc6baa9ac4";

    // 填写从短信SDK应用后台注册得到的APPSECRET
    private String APPSECRET = "9d67b36379852a2a5cd3980ca95e44c5";//"7f3dedcb36d92deebcb373af921d635a";

    private String PhoneNumber = "";
    private String VerifyCode = "";


    private static String INITIALIZE = "INITIALIZE";
    private static String RequestVerifyCode = "RequestVerifyCode";
    private static String SubmitVerifyCode = "SubmitVerifyCode";
    //private static String UNREGISTER = "UNREGISTER";
    public static final String LOG_TAG = "jlgMobSMSPlugin";
    private static CallbackContext MobSMSContext;

    private static CallbackContext RequestVerifyCodeContext;
    private static CallbackContext SubmitVerifyCodeContext;
    private static CordovaWebView gWebView;
    private static Bundle gCachedExtras = null;
    private static boolean gForeground = false;

    /**
     * Gets the application context from cordova's main activity.
     *
     * @return the application context
     */
    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }

    EventHandler SMSSDKEeventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            super.afterEvent(event, result, data);
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功
                try {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        //SubmitVerifyCodeContext.success();
                        JSONObject obj = new JSONObject();
                        obj.put("status", 0);
                        obj.put("message", "verify OK");
                        PluginResult Pluginresult = new PluginResult(PluginResult.Status.OK, obj);
                        Pluginresult.setKeepCallback(true);
                        SubmitVerifyCodeContext.sendPluginResult(Pluginresult);

                    } else {
                        SubmitVerifyCodeContext.error(((Throwable) data).getMessage());
                    }
                } catch (JSONException e) {
                    SubmitVerifyCodeContext.error(e.getMessage());
                }
            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                try {
                    if (result == SMSSDK.RESULT_COMPLETE) {

                        JSONObject obj = new JSONObject();
                        obj.put("status", 0);
                        obj.put("message", "request OK");
                        PluginResult Pluginresult = new PluginResult(PluginResult.Status.OK, obj);
                        Pluginresult.setKeepCallback(true);
                        RequestVerifyCodeContext.sendPluginResult(Pluginresult);
                        //RequestVerifyCodeContext.success();
                    } else {
                        RequestVerifyCodeContext.error(((Throwable) data).getMessage());
                    }
                } catch (JSONException e) {
                    RequestVerifyCodeContext.error(e.getMessage());
                }
            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                //返回支持发送验证码的国家列表
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    MobSMSContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, data.toString()));
                } else {
                    sendError(((Throwable) data).getMessage());
                }
            }
        }
    };

    @Override
    public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) {
        Log.v(LOG_TAG, "execute: action=" + action);
        gWebView = this.webView;
        if (INITIALIZE.equals(action)) {
            MobSMSContext = callbackContext;
            JSONObject jo = null;
            Log.v(LOG_TAG, "execute: data=" + data.toString());
            try {
                jo = data.getJSONObject(0).getJSONObject("MobConfig");
                Log.v(LOG_TAG, "execute: jo=" + jo.toString());
                APPKEY = jo.getString("APPKEY");
                APPSECRET = jo.getString("APPSECRET");
                Log.v(LOG_TAG, "execute: APPKEY=" + APPKEY);
                Log.v(LOG_TAG, "execute: APPSECRET=" + APPSECRET);
                MobSDK.init(getApplicationContext(), APPKEY, APPSECRET);//初始化SDK
                SMSSDK.registerEventHandler(SMSSDKEeventHandler);//事件回调
                callbackContext.success();//无错返回
                return true;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "execute: Got JSON Exception " + e.getMessage());
                callbackContext.error(e.getMessage());
            }
        } else if (RequestVerifyCode.equals(action)) {//请求验证码
            try {
                RequestVerifyCodeContext = callbackContext;
                JSONObject jo = data.getJSONObject(0);
                PhoneNumber = jo.getString("PhoneNumber");
                Log.v(LOG_TAG, " excute RequestVerifyCode: PhoneNumber=" + PhoneNumber);
                SMSSDK.getVerificationCode("86", PhoneNumber);
                return true;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "execute: Got JSON Exception " + e.getMessage());
                callbackContext.error(e.getMessage());
            }
        } else if (SubmitVerifyCode.equals(action)) {//验证验证码
            try {
                SubmitVerifyCodeContext = callbackContext;
                JSONObject jo = data.getJSONObject(0);
                PhoneNumber = jo.getString("PhoneNumber");
                VerifyCode = jo.getString("VerifyCode");
                Log.v(LOG_TAG, " excute VerifyCode: PhoneNumber=" + PhoneNumber);
                Log.v(LOG_TAG, " excute VerifyCode: VerifyCode=" + VerifyCode);
                SMSSDK.submitVerificationCode("86", PhoneNumber, VerifyCode);
                return true;
            } catch (Exception e) {
                Log.e(LOG_TAG, "execute: Got JSON Exception " + e.getMessage());
                callbackContext.error(e.getMessage());
            }
        } else {
            Log.e(LOG_TAG, "Invalid action : " + action);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            return false;
        }
        return true;
    }

    public static void sendEvent(JSONObject _json) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, _json);
        pluginResult.setKeepCallback(true);
        if (MobSMSContext != null) {
            MobSMSContext.sendPluginResult(pluginResult);
        }
    }

    public static void sendError(String message) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, message);
        pluginResult.setKeepCallback(true);
        if (MobSMSContext != null) {
            MobSMSContext.sendPluginResult(pluginResult);
        }
    }

    /*
     * Sends the pushbundle extras to the client application.
     * If the client application isn't currently active, it is cached for later processing.
     */
    public static void sendExtras(Bundle extras) {
        if (extras != null) {
            if (gWebView != null) {
                //sendEvent(convertBundleToJson(extras));
            } else {
                Log.v(LOG_TAG, "sendExtras: caching extras to send at a later time.");
                gCachedExtras = extras;
            }
        }
    }

//    public static void setApplicationIconBadgeNumber(Context context, int badgeCount) {
//        if (badgeCount > 0) {
//            //ShortcutBadger.applyCount(context, badgeCount);
//        } else {
//            //ShortcutBadger.removeCount(context);
//        }
//    }

//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        super.initialize(cordova, webView);
////        SMSSDK.initSDK(cordova.getActivity().getApplicationContext(), APPKEY, APPSECRET);
//        MobSDK.init(getApplicationContext(), APPKEY, APPSECRET);//初始化SDK
//        EventHandler eh = new EventHandler() {
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    //回调完成
//                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                        //提交验证码成功
//                        PluginResult submitcodeResult = new PluginResult(PluginResult.Status.OK, data.toString());
//                        submitcodeResult.setKeepCallback(true);
//                        SubmitVerifyCodeContext.sendPluginResult(submitcodeResult);
//                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                        //获取验证码成功
//                        PluginResult requestcodeResult = new PluginResult(PluginResult.Status.OK, data.toString());
//                        requestcodeResult.setKeepCallback(true);
//                        SubmitVerifyCodeContext.sendPluginResult(requestcodeResult);
//
//                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
//                        //返回支持发送验证码的国家列表
//                    }
//                } else {
//                    ((Throwable) data).printStackTrace();
//
//                }
//            }
//        };
//        SMSSDK.registerEventHandler(eh); //注册短信回调
//    }


//            if (action.equals("RequestVerifyCode")) {
//            requestcodeCallbackContext = callbackContext;
//            this.RequestVerifyCode(args.getJSONObject(0).toString());
//        }
//        else if(action.equals("SubmitVerifyCode"))
//        {
//            submitcodeCallbackContext = callbackContext;
//            this.SubmitVerifyCode(args.getJSONObject(0).toString());
//        }
//        else if (action.equals("GetVerifyCode")) {
//            JSONObject r = new JSONObject();
//            r.put("Phone", mobsms.PhoneNumber);
//            r.put("VerifyCode", mobsms.VerifyCode);
//            callbackContext.success(r);
//        }
//        else {
//            return false;
//        }
//        return true;


//    @Override
//    public void onPause(boolean multitasking) {
//        super.onPause(multitasking);
//        gForeground = false;
//
//        SharedPreferences prefs = getApplicationContext().getSharedPreferences(COM_ADOBE_PHONEGAP_PUSH, Context.MODE_PRIVATE);
//        if (prefs.getBoolean(CLEAR_NOTIFICATIONS, true)) {
//            clearAllNotifications();
//        }
//    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        gForeground = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gForeground = false;
        gWebView = null;
    }

//    private void clearAllNotifications() {
//        final NotificationManager notificationManager = (NotificationManager) cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();
//    }

//    private void subscribeToTopics(JSONArray topics, String registrationToken) {
//        if (topics != null) {
//            String topic = null;
//            for (int i=0; i<topics.length(); i++) {
//                try {
//                    topic = topics.optString(i, null);
//                    if (topic != null) {
//                        Log.d(LOG_TAG, "Subscribing to topic: " + topic);
//                        GcmPubSub.getInstance(getApplicationContext()).subscribe(registrationToken, "/topics/" + topic, null);
//                    }
//                } catch (IOException e) {
//                    Log.e(LOG_TAG, "Failed to subscribe to topic: " + topic, e);
//                }
//            }
//        }
//    }
//
//    private void unsubscribeFromTopics(JSONArray topics, String registrationToken) {
//        if (topics != null) {
//            String topic = null;
//            for (int i=0; i<topics.length(); i++) {
//                try {
//                    topic = topics.optString(i, null);
//                    if (topic != null) {
//                        Log.d(LOG_TAG, "Unsubscribing to topic: " + topic);
//                        GcmPubSub.getInstance(getApplicationContext()).unsubscribe(registrationToken, "/topics/" + topic);
//                    }
//                } catch (IOException e) {
//                    Log.e(LOG_TAG, "Failed to unsubscribe to topic: " + topic, e);
//                }
//            }
//        }
//    }

    /*
     * serializes a bundle to JSON.
     */
//    private static JSONObject convertBundleToJson(Bundle extras) {
//        Log.d(LOG_TAG, "convert extras to json");
//        try {
//            JSONObject json = new JSONObject();
//            JSONObject additionalData = new JSONObject();
//
//            // Add any keys that need to be in top level json to this set
//            HashSet<String> jsonKeySet = new HashSet();
//            Collections.addAll(jsonKeySet, TITLE,MESSAGE,COUNT,SOUND,IMAGE);
//
//            Iterator<String> it = extras.keySet().iterator();
//            while (it.hasNext()) {
//                String key = it.next();
//                Object value = extras.get(key);
//
//                Log.d(LOG_TAG, "key = " + key);
//
//                if (jsonKeySet.contains(key)) {
//                    json.put(key, value);
//                }
//                else if (key.equals(COLDSTART)) {
//                    additionalData.put(key, extras.getBoolean(COLDSTART));
//                }
//                else if (key.equals(FOREGROUND)) {
//                    additionalData.put(key, extras.getBoolean(FOREGROUND));
//                }
//                else if ( value instanceof String ) {
//                    String strValue = (String)value;
//                    try {
//                        // Try to figure out if the value is another JSON object
//                        if (strValue.startsWith("{")) {
//                            additionalData.put(key, new JSONObject(strValue));
//                        }
//                        // Try to figure out if the value is another JSON array
//                        else if (strValue.startsWith("[")) {
//                            additionalData.put(key, new JSONArray(strValue));
//                        }
//                        else {
//                            additionalData.put(key, value);
//                        }
//                    } catch (Exception e) {
//                        additionalData.put(key, value);
//                    }
//                }
//            } // while
//
//            json.put(ADDITIONAL_DATA, additionalData);
//            Log.v(LOG_TAG, "extrasToJSON: " + json.toString());
//
//            return json;
//        }
//        catch( JSONException e) {
//            Log.e(LOG_TAG, "extrasToJSON: JSON exception");
//        }
//        return null;
//    }

    public static boolean isInForeground() {
        return gForeground;
    }

    public static boolean isActive() {
        return gWebView != null;
    }


}
