package com.example.iepirkumu_saraksts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public final class ReminderScheduler {
    static final String EXTRA_NAME = "name";
    static final String EXTRA_ID = "id";

    private ReminderScheduler() {}

    private static PendingIntent pending(Context context, int id, String name) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_NAME, name);
        return PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /** Schedules (or cancels, if there is no future reminder) the alarm for this product. */
    public static void schedule(Context context, Product product) {
        cancel(context, product);
        if (product.reminderTime <= System.currentTimeMillis() || product.isChecked) return;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Inexact alarm: needs no special permission; may fire a few minutes late.
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, product.reminderTime,
                pending(context, product.id, product.name));
    }

    public static void cancel(Context context, Product product) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pending(context, product.id, product.name));
    }
}
