package com.example.mobile_computing_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_map.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fab_addOpened = false

        fab_add.setOnClickListener{

            if (!fab_addOpened) {

                fab_addOpened = true
                fab_add.animate().translationY(-resources.getDimension((R.dimen.standard_116)))
                fab_log.animate().translationY(-resources.getDimension((R.dimen.standard_66)))

            } else {

                fab_addOpened = false
                fab_add.animate().translationY(0f)
                fab_log.animate().translationY(0f)

            }
        }

        button_log.setOnClickListener {
            startActivity(Intent(applicationContext, TimeActivity::class.java))

        }

        button_sign.setOnClickListener {
            startActivity(Intent(applicationContext, MapActivity::class.java))

        }

        button_location.setOnClickListener {
            startActivity(Intent(applicationContext, MapNavigateActivity::class.java))

        }



    }

    override fun onResume() {
        super.onResume()
        refreshlist()
    }


    private fun refreshlist() {
        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders")
                .build()
            val reminders = db.reminderDao().getReminders()
            db.close()

            uiThread {

                if (reminders.isNotEmpty()) {
                    val adapter = ReminderAdapter(applicationContext, reminders)
                    list.adapter = adapter

                } else {
                    toast("No reminder yet")
                }
            }
        }
    }

    companion object {
        const val CHANNEL_ID="REMINDER_CHANNEL_ID"
        var NotificationID=1567

        fun showNotification(context: Context, message:String) {
            val notificationBuilder=NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .setContentTitle(context.getString(R.string.app_name)).setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT).apply { description = context.getString(R.string.app_name) }

                notificationManager.createNotificationChannel(channel)

            }
            val notification= NotificationID+ Random(NotificationID).nextInt(from = 1, until = 30)
            notificationManager.notify(notification, notificationBuilder.build())
        }
    }
}

