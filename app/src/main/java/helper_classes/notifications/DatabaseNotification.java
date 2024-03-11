package helper_classes.notifications;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

@Keep
public class DatabaseNotification {

    private boolean active;
    private int index1;
    private int index2;

    public DatabaseNotification(boolean active, int index1, int index2) {
        this.active = active;
        this.index1 = index1;
        this.index2 = index2;
    }

    @SuppressWarnings("unused")
    public DatabaseNotification() { }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getIndex1() {
        return index1;
    }

    public void setIndex1(int index1) {
        this.index1 = index1;
    }

    public int getIndex2() {
        return index2;
    }

    public void setIndex2(int index2) {
        this.index2 = index2;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        DatabaseNotification notification = (DatabaseNotification) obj;

        return notification.isActive() == this.active && notification.getIndex1() == this.index1 && notification.getIndex2() == this.index2;
    }
}
