package rezaei.mohammad.neo.minimalistanalogclock

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ConfigsActivity]
 */
class AnalogClass : AppWidgetProvider() {


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        /*if(intent?.action == Intent.ACTION_TIME_TICK){
            updateAppWidget(context!!, AppWidgetManager.getInstance(context),AppWidgetManager.INVALID_APPWIDGET_ID)
        }*/
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            clock = Clock(context)
            clock?.measure(400,400)
            clock?.layout(0,0,400,400)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            ConfigsActivity.deletePrefs(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        private var clock: Clock? = null

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            // Construct the RemoteViews object
            Handler().postDelayed({
                updateAppWidget(context,appWidgetManager,appWidgetId)
            }, 500)

            val views = RemoteViews(context.packageName, R.layout.analog_class)
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.hourDotColor)?.let {
                clock?.hourDotColor = it
            }
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.minuteDotColor)?.let {
                clock?.minuteDotColor = it
            }
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.secondDotColor)?.let {
                clock?.secondDotColor = it
            }
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.handsColor)?.let {
                clock?.handsColor = it
            }
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.datePrimaryColor)?.let {
                clock?.datePrimaryColor = it
            }
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.dateSecondaryColor)?.let {
                clock?.dateSecondaryColor = it
            }
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.centerCirclePrimaryColor)?.let {
                clock?.centerCircleSecondaryColor = it
            }
            ConfigsActivity.loadColorPref(context,appWidgetId,ConfigsActivity.centerCircleSecondaryColor)?.let {
                clock?.centerCircleSecondaryColor = it
            }
            clock?.showDate = ConfigsActivity.loadCheckBoxPref(context,appWidgetId,ConfigsActivity.showDate)
            clock?.showSecond = ConfigsActivity.loadCheckBoxPref(context,appWidgetId,ConfigsActivity.showSecond)

            clock?.isDrawingCacheEnabled = true
            var bitmap = clock?.drawingCache

            if(bitmap == null){
                bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.example_appwidget_preview)
            }

            views.setImageViewBitmap(R.id.clockHolder, bitmap)


            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

