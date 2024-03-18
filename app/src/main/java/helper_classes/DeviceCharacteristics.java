package helper_classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class DeviceCharacteristics {

    public static int getWidthPx(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);

        return size.x;
    }

    public static int getHeightPx(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);

        return size.y;
    }

    public static int getStatusBarHeight(Context context) {
        @SuppressLint("InternalInsetResource")
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        } else { return 0; }
    }

    public static boolean hasNotch(Context context) {
        int statusBarHeight = DeviceCharacteristics.getStatusBarHeight(context);
        float defaultStatusBarHeight = Math.round(24 * (context.getResources().getDisplayMetrics().densityDpi / 160f));

        return statusBarHeight > defaultStatusBarHeight;
    }
}
