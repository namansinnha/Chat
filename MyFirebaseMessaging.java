public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived (remoteMessage);

        String sent = remoteMessage.getData ().get ("sent");
        String user = remoteMessage.getData ().get ("user");

        SharedPreferences preferences = getSharedPreferences ("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString ("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        if (firebaseUser != null && sent.equals (firebaseUser.getUid ())) {

            if (!currentUser.equals (user)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    sendOreoNotification (remoteMessage);

                } else {

                    sendNotification (remoteMessage);

                }
            }
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData ().get ("user");
        String icon = remoteMessage.getData ().get ("icon");
        String title = remoteMessage.getData ().get ("title");
        String body = remoteMessage.getData ().get ("body");
        int j = Integer.parseInt (user.replaceAll ("[\\D]", ""));

        RemoteMessage.Notification notification = remoteMessage.getNotification ();
        Intent intent = new Intent (this, MessageActivity.class);

        Bundle bundle = new Bundle ();
        bundle.putString ("userid", user);
        intent.putExtras (bundle);
        intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity (this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri (RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification (this);
        Notification.Builder builder = oreoNotification.getOreoNotification (title, body, pendingIntent, defaultSound, icon);


        int i = 0;
        if (j > 0) {

            i=j;

        }

        oreoNotification.getNotificationManager ().notify (i, builder.build ());
    }

    private void sendNotification (RemoteMessage remoteMessage) {

        String user = remoteMessage.getData ().get ("user");
        String icon = remoteMessage.getData ().get ("icon");
        String title = remoteMessage.getData ().get ("title");
        String body = remoteMessage.getData ().get ("body");
        int j = Integer.parseInt (user.replaceAll ("[\\D]", ""));

        RemoteMessage.Notification notification = remoteMessage.getNotification ();
        Intent intent = new Intent (this, MessageActivity.class);

        Bundle bundle = new Bundle ();
        bundle.putString ("userid", user);
        intent.putExtras (bundle);
        intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity (this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri (RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder (this)
                .setSmallIcon (Integer.parseInt (icon))
                .setContentTitle (title)
                .setContentText (body)
                .setAutoCancel (true)
                .setSound (defaultSound)
                .setContentIntent (pendingIntent);

        NotificationManager noti = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0) {

            i=j;

        }

        noti.notify (i, builder.build ());

    }

}
