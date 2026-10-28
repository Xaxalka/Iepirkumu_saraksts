package com.example.iepirkumu_saraksts;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

/** Shows the reminder notification, and re-arms alarms after a reboot. */
public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            List<Product> products = AppDatabase.getInstance(context).productDao().getWithReminders();
            for (Product p : products) ReminderScheduler.schedule(context, p);
            return;
        }

        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.reminder_channel), NotificationManager.IMPORTANCE_HIGH);
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        int id = intent.getIntExtra(ReminderScheduler.EXTRA_ID, 0);
        String name = intent.getStringExtra(ReminderScheduler.EXTRA_NAME);
        PendingIntent open = PendingIntent.getActivity(context, id,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(context.getString(R.string.reminder_title))
                .setContentText(name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(open)
                .setAutoCancel(true);
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
