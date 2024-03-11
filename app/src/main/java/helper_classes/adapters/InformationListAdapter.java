package helper_classes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;

import constants.Constants;
import helper_classes.Information;
import helper_classes.scale_layout.ScaledLayoutVariables;
import vz.apps.dailyevents.R;

public class InformationListAdapter extends ArrayAdapter<Information> {

    private final Context context;
    private final int resource;
    private final boolean isDayFragment;

    static class ViewHolder {
        ScrollView detailsScroll;
        TextView details;
        TextView dayDate;
        TextView time;
        TextView notifications;
        TextView numeration;
        Animation animation;
        boolean notificationIsActive;
    }

    public InformationListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Information> objects, boolean isDayFragment) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.isDayFragment = isDayFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String details = getItem(position).getDetails();
        String dayDate = getItem(position).getDate() + " (" + getItem(position).getWeekDay() + ")";
        String time = context.getString(R.string.at_oclock, getItem(position).getTime());
        boolean notificationIsActive = getItem(position).getEv_notif().isActive();

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();

            holder.detailsScroll = convertView.findViewById(R.id.details_ScrollView);
            holder.details = convertView.findViewById(R.id.details_TextView);
            holder.dayDate = convertView.findViewById(R.id.dayDate_TextView);
            holder.time = convertView.findViewById(R.id.time_TextView);
            holder.notifications = convertView.findViewById(R.id.notifications_TextView);
            holder.numeration = convertView.findViewById(R.id.numeration_TextView);
            holder.animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
            holder.notificationIsActive = !notificationIsActive;

            scaleCardHeight(convertView);
            holder.notifications.setCompoundDrawablePadding((int) context.getResources().getDimension(R.dimen.card_notification_icon_padding));

            ConstraintLayout cardConstraintLayout = convertView.findViewById(R.id.card_ConstraintLayout);
            if (Constants.GRADIENT_ANGLE == 0 || Constants.GRADIENT_ANGLE == 45 || Constants.GRADIENT_ANGLE == 315) cardConstraintLayout.setBackgroundResource(R.drawable.red_black_gradient_0);
            else { cardConstraintLayout.setBackgroundResource(R.drawable.red_black_gradient_180); }

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) holder.detailsScroll.getLayoutParams();
            layoutParams.height = holder.details.getLineHeight() * Constants.MAX_INFO_LINES;
            holder.detailsScroll.setLayoutParams(layoutParams);

            Rect bounds = new Rect();
            holder.details.getPaint().getTextBounds(holder.details.getText().toString(), 0, holder.details.getText().toString().length(), bounds);
            holder.details.setPadding(0, (int) (holder.details.getPaint().getFontMetrics().top - bounds.top - 0.5), 0, 0);

            if (isDayFragment) {
                holder.dayDate.setVisibility(View.GONE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(cardConstraintLayout);

                constraintSet.connect(R.id.notifications_TextView, ConstraintSet.TOP, R.id.guideline6, ConstraintSet.BOTTOM);
                constraintSet.connect(R.id.notifications_TextView, ConstraintSet.BOTTOM, R.id.guideline7, ConstraintSet.TOP);
                constraintSet.connect(R.id.time_TextView, ConstraintSet.TOP, R.id.guideline7, ConstraintSet.BOTTOM);
                constraintSet.connect(R.id.time_TextView, ConstraintSet.BOTTOM, R.id.guideline8, ConstraintSet.TOP);

                constraintSet.applyTo(cardConstraintLayout);
            }

            convertView.setOnLongClickListener(v -> false);

            simulateLongClickUnderScrollView(convertView, holder);
            convertView.setTag(holder);
        } else { holder = (ViewHolder) convertView.getTag(); }

        if (!holder.details.getText().toString().equals(details)) holder.details.setText(details);
        if (!holder.dayDate.getText().toString().equals(dayDate)) holder.dayDate.setText(dayDate);
        if (!holder.time.getText().toString().equals(time)) holder.time.setText(time);
        if (!holder.numeration.getText().toString().equals(String.valueOf(position + 1))) holder.numeration.setText(String.valueOf(position + 1));

        if (holder.notificationIsActive != notificationIsActive) {
            holder.notificationIsActive = notificationIsActive;

            if (notificationIsActive) holder.notifications.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_active, 0);
            else { holder.notifications.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_off, 0); }
        }

        convertView.startAnimation(holder.animation);

        return convertView;
    }

    private void scaleCardHeight(View convertView) {
        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        layoutParams.height = ScaledLayoutVariables.DAY_FRAGMENT_CARD_HEIGHT;
        convertView.setLayoutParams(layoutParams);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void simulateLongClickUnderScrollView(View convertView, ViewHolder holder) {
        final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) { return true; }

            @Override
            public void onShowPress(MotionEvent e) { }

            @Override
            public boolean onSingleTapUp(MotionEvent e) { return true; }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return true; }

            @Override
            public void onLongPress(MotionEvent e) {
                convertView.performLongClick(holder.detailsScroll.getX() + e.getX(), holder.detailsScroll.getY() + e.getY() - holder.detailsScroll.getScrollY());
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return true; }
        });

        holder.detailsScroll.findViewById(R.id.details_TextView).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
}
