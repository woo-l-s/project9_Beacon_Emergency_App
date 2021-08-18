package com.example.smbeaconclient.firebase;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.smbeaconclient.EmergencyActivity;
import com.example.smbeaconclient.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "MyFirebaseMessagingServicelog";
    Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        //message will contain the Push Message
        String message = remoteMessage.getData().get("message");
        //imageUri will contain URL of the image to be displayed with Notification
        String imageUri = remoteMessage.getData().get("image");
//        int floorUser = Integer.parseInt(remoteMessage.getData().get("floor"));
//        int floorFire = Integer.parseInt(remoteMessage.getData().get("fire_floor"));
        String floorUser = (remoteMessage.getData().get("floor"));
        String floorFire = (remoteMessage.getData().get("fire_floor"));
        String people1f = (remoteMessage.getData().get("floor_1_people"));
        String people2f = (remoteMessage.getData().get("floor_2_people"));
        Log.d(TAG, "imageUri: " + imageUri);


        //To get a Bitmap image from the URL received
        bitmap = getBitmapfromUrl(imageUri);

//        //start; 00 broadcast intent; it works
//        final Intent intent2 = new Intent(getApplicationContext(), FcmBroadcastReceiver.class); // Receiver 설정
//        intent2.setAction("example.test.broadcast");
//        sendBroadcast(intent2);
//        Log.d(TAG, "SendBroadcast");
////        sendNotification(message, bitmap, TrueOrFlase, imageUri);
//        //end; 00


        //start; activity intent; work
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// | Intent.FLAG_ACTIVITY_NEW_TASK); //error
        intent.putExtra("imageUri", imageUri);
        intent.putExtra("floorUser", floorUser);
        intent.putExtra("floorFire", floorFire);
        intent.putExtra("people1f", people1f);
        intent.putExtra("people2f", people2f);
        ComponentName cn = new ComponentName(this, EmergencyActivity.class);
        intent.setComponent(cn);
        startActivity(intent);
        sendNotification(message, bitmap, imageUri);
        //end; activity intent; work

//        //start; not work
//        Intent fullScreenIntent = new Intent(this, EmergencyActivity.class);
//        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
//                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        String CHANNEL_ID = "one-channel";
//        String channelName = "My Channel One1";
//        String channelDescription = "My Channel One Description";
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_flame)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setFullScreenIntent(fullScreenPendingIntent, true);
//
////        NotificationManager notificationManager =
////                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////
////        notificationManager.notify(0 /* ID of notification */, builder.build());
//
//        Notification incomingCallNotification = notificationBuilder.build();
//        //end; 02


        //
//        final PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        final AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        /* 설정된 시각에 알람 설정
//        인자 : 알람매니저 타입 / 언제 이 알람이 발동되야하는지 그 시간을 long값으로 줘야함 / 뭐가 실행될지는 펜딩인덴트로 동작하게 함. */
//        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, , pendingIntent); //setExact()과 동일하만 절전모드에서도 동작하는 함수
////        manager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarm.getTIME(), pendingIntent), pendingIntent);
//
//        TimeUtils.printTimeStringDebug("알람서비스등록", alarm.getTIME(), alarm.getRequestCode());



    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */
//    public void showNotificationMessage(String msgTitle, String msgContent) {
//        Log.i("### noti msgTitle : ", msgTitle);
//        Log.i("### noti msgContent : ", msgContent);
//        String toastText = String.format("[Notification 메시지] title: %s => content: %s", msgTitle, msgContent);
//        Looper.prepare();
//        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
//        Looper.loop();
//    }

//    @Override
//    public void onMessageReceived(RemoteMessage msg) {
//        Log.d(TAG, "onMessageReceived called");
//        Log.i("### msg : ", msg.toString());
//        if (msg.getData().isEmpty()) {
//            showNotificationMessage(msg.getNotification().getTitle(), msg.getNotification().getBody());  // Notification으로 받을 때
//        } else {
//            showDataMessage(msg.getData().get("title"), msg.getData().get("content"));  // Data로 받을 때
//        }

    private void sendNotification(String messageBody, Bitmap image, String imageUri) {
        ((BitmapDrawable)(ContextCompat.getDrawable(this,R.drawable.ic_flame))).getBitmap();

//        Intent intent = new Intent(this, EmergencyActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("AnotherActivity", TrueOrFalse);
//        intent.putExtra("imageUri", imageUri);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,new Intent(), // add this
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "one-channel";
        String channelName = "My Channel One1";
        String channelDescription = "My Channel One Description";


//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_flame)
//                .setSmallIcon(IconCompat.createWithBitmap(image))
//                .setLargeIcon(image) //not work
//                .setContentTitle(messageBody)
                .setContentTitle("zz")
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(image))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        //Save token on the phone internal memory
        SharedPreferences sharedPreferences = getSharedPreferences("Token", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
        editor.putString("token", token); // key,value 형식으로 저장
        editor.apply();    //최종 커밋. 커밋을 해야 저장이 된다.

        //get permission

//        //권한이 부여가 안돼있다면
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        String phoneNum = "zz";
        Log.d(TAG, "포하하");


        // write token document on the db //파베 db에다 이 놈의 토큰 문서를 저장함, token field 만 채움
//        Workplace workplace = new Workplace(token, false, phoneNum);
        Workplace workplace = new Workplace(token, false);
        MyFirestore.getWorkplaceColRef().document(token).set(workplace).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "SUCCESS");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG, "failed");
                Log.d(TAG, String.valueOf(e));
            }
        });
    }
}
