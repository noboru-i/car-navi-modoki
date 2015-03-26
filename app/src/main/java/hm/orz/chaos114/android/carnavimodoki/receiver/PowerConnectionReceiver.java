package hm.orz.chaos114.android.carnavimodoki.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_POWER_CONNECTED:
                Toast.makeText(context, "接続！" + intent.getExtras(), Toast.LENGTH_LONG).show();
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                Toast.makeText(context, "切断！" + intent.getExtras(), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
