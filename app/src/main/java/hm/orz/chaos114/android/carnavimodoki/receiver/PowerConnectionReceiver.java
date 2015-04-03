package hm.orz.chaos114.android.carnavimodoki.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import hm.orz.chaos114.android.carnavimodoki.App;
import hm.orz.chaos114.android.carnavimodoki.activity.MainActivity;
import hm.orz.chaos114.android.carnavimodoki.service.MusicService;

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_POWER_CONNECTED:
                startApplication(context);
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                // ディスプレイを切る
                ComponentName cn = new ComponentName(context, DeviceAdminReceiver.class);
                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (dpm.isAdminActive(cn)) {
                    dpm.lockNow();
                }

                // 音楽を止める
                App.Bus().post(MusicService.ControlEvent.PAUSE);
                break;
        }
    }

    private void startApplication(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
