package hm.orz.chaos114.android.carnavimodoki.receiver;

import android.content.Context;
import android.content.Intent;

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // 端末管理者画面から無効にする際の、confirmメッセージ
        return "バッテリー駆動になった時に、画面を消すことができなくなります。\n本当によろしいですか？";
    }
}
