package com.example.mywidget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

class MyWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            Log.i("MY_WIDGET", "appWidgetId : $appWidgetId")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val action = intent?.action
        if (action.equals("switchClockFormat")){
                context?.let {
                    val sp = it.getSharedPreferences("SP", Context.MODE_PRIVATE)
                    val is24ClockFormat = sp.getBoolean("VALUE", false)?:false
                    sp.edit().putBoolean("VALUE", !is24ClockFormat).apply()
                    updateWidgets(it)
                }
        }

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
//        for (appWidgetId in appWidgetIds) {
//            deleteTitlePref(context, appWidgetId)
//        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled

        Log.i("MY_WIDGET", "onDisabled")
    }

    private fun pendingIntent(
        context: Context?,
        action: String,
    ): PendingIntent?{
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun updateWidgets(context: Context){
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))
        ids.forEach { id ->
            updateAppWidget(context, manager, id)
        }
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.my_widget)

        // get update value
        val sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE)
        val is24ClockFormat = sp.getBoolean("VALUE", false)?:false

        if (is24ClockFormat){
            views.setImageViewResource(R.id.clockFormatChangeImgBtn, R.drawable.off_24)
        }else{
            views.setImageViewResource(R.id.clockFormatChangeImgBtn, R.drawable.on_12)
        }

        //views.setTextViewText(R.id.appwidget_text, value.toString())

        views.setOnClickPendingIntent(R.id.clockFormatChangeImgBtn, pendingIntent(context, "switchClockFormat"))

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

        Log.i("MY_WIDGET", "updateAppWidget : $is24ClockFormat")
    }
}
