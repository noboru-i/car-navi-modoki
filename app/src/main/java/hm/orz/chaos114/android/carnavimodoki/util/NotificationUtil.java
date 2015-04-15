package hm.orz.chaos114.android.carnavimodoki.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.R;
import hm.orz.chaos114.android.carnavimodoki.activity.MoviePlayActivity;
import hm.orz.chaos114.android.carnavimodoki.model.PlayingModel;

public class NotificationUtil {
    public static void startNotification(Service service) {
        PlayingModel playingModel = App.Models().getPlayingModel();
        String currentMediaId = playingModel.getCurrentEntity().getMovie().getMediaId();
        Intent notificationIntent = MoviePlayActivity.getIntent(service, currentMediaId);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(service.getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("CarNaviModoki")
                .setContentText("常駐中")
                .setContentIntent(pendingIntent)
                .build();
        service.startForeground(1, notification);
    }
}
