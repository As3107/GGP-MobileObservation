package com.ggp.ai_ggpmobile.Notification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import com.ggp.ai_ggpmobile.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                /*if (SharedPrefManager.getInstance(getApplicationContext()).getMahasiswa().getNama()!=null){
                    sendPushNotification(json);
                } else if (SharedPrefManager.getInstance(getApplicationContext()).getDosen().getNama()!=null){
                    sendPushNotification(json);
                }*/
                sendPushNotification(json);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            String code = data.getString("code");

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            //creating an intent for the notification
            /*if(SharedPrefManager.getInstance(getApplicationContext()).getMahasiswa().getNama()!=null){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), DosenActivity.class);
                mNotificationManager.showSmallNotification(title, message, intent);
            }*/
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            mNotificationManager.showSmallNotification(title, message, intent);

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}