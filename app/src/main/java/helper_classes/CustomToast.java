package helper_classes;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vz.apps.dailyevents.R;

import es.dmoral.toasty.Toasty;

public class CustomToast {

    public static void showSuccess(Context context, String message, int duration) {
        Toast toast = Toasty.custom(
                context,
                message,
                R.drawable.ic_success,
                R.color.colorSuccessToast,
                duration,
                true,
                true
        );

        View view = toast.getView();
        TextView textView = view.findViewById(R.id.toast_text);
        Typeface typeface = Typeface.create(context.getString(R.string.app_font), Typeface.BOLD_ITALIC);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.toast_text_size));
        textView.setTypeface(typeface);

        toast.show();
    }

    public static void showError(Context context, String message, int duration) {
        Toast toast = Toasty.custom(
                context,
                message,
                R.drawable.ic_error,
                R.color.colorErrorToast,
                duration,
                true,
                true
        );

        View view = toast.getView();
        TextView textView = view.findViewById(R.id.toast_text);
        Typeface typeface = Typeface.create(context.getString(R.string.app_font), Typeface.BOLD_ITALIC);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.toast_text_size));
        textView.setTypeface(typeface);

        toast.show();
    }

    public static void showWarning(Context context, String message, int duration) {
        Toast toast = Toasty.custom(
                context,
                message,
                R.drawable.ic_warning,
                R.color.colorWarningToast,
                duration,
                true,
                true
        );

        View view = toast.getView();
        TextView textView = view.findViewById(R.id.toast_text);
        Typeface typeface = Typeface.create(context.getString(R.string.app_font), Typeface.BOLD_ITALIC);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.toast_text_size));
        textView.setTypeface(typeface);

        toast.show();
    }

    public static void showInfo(Context context, String message, int duration) {
        Toast toast = Toasty.custom(
                context,
                message,
                R.drawable.ic_info,
                R.color.colorInfoToast,
                duration,
                true,
                true
        );

        View view = toast.getView();
        TextView textView = view.findViewById(R.id.toast_text);
        Typeface typeface = Typeface.create(context.getString(R.string.app_font), Typeface.BOLD_ITALIC);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.toast_text_size));
        textView.setTypeface(typeface);

        toast.show();
    }

}
