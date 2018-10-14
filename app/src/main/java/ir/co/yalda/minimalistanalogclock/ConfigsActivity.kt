package ir.co.yalda.minimalistanalogclock

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.analog_class_configure.*

/**
 * The configuration screen for the [AnalogClass] AppWidget.
 */
class ConfigsActivity : AppCompatActivity(),ColorSeekBar.OnColorChangeListener, CompoundButton.OnCheckedChangeListener {

    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@ConfigsActivity


        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        AnalogClass.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    override fun onColorChangeListener(color: Int, colorSeekBar: ColorSeekBar) {
        when(colorSeekBar.id){
            R.id.pickerHourDotColor -> {
                clock.hourDotColor = color
                saveColorPref(this,mAppWidgetId, hourDotColor,color)
            }
            R.id.pickerMinuteDotColor -> {
                clock.minuteDotColor = color
                saveColorPref(this,mAppWidgetId, minuteDotColor,color)
            }
            R.id.pickerHandsColor -> {
                clock.handsColor = color
                saveColorPref(this,mAppWidgetId, handsColor,color)
            }
            R.id.pickerSecondDotColor -> {
                clock.secondDotColor = color
                saveColorPref(this,mAppWidgetId, secondDotColor,color)
            }
            R.id.pickerDatePrimaryColor -> {
                clock.datePrimaryColor = color
                saveColorPref(this,mAppWidgetId, datePrimaryColor,color)
            }
            R.id.pickerDateSecondaryColor -> {
                clock.dateSecondaryColor = color
                saveColorPref(this,mAppWidgetId, dateSecondaryColor,color)
            }
            R.id.pickerCenterCirclePrimaryColor -> {
                clock.centerCirclePrimaryColor = color
                saveColorPref(this,mAppWidgetId, centerCirclePrimaryColor,color)
            }
            R.id.pickerCenterCircleSecondaryColor -> {
                clock.centerCircleSecondaryColor = color
                saveColorPref(this,mAppWidgetId, centerCircleSecondaryColor,color)
            }
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when(p0?.id){
            R.id.chShowDate -> {
                clock.showDate = p1
                saveCheckBoxPref(this,mAppWidgetId, showDate,p1)
            }
            R.id.chShowSecond -> {
                clock.showSecond = p1
                saveCheckBoxPref(this,mAppWidgetId, showSecond,p1)
            }
        }
    }


    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.analog_class_configure)
        findViewById<View>(R.id.add_button).setOnClickListener(mOnClickListener)

        pickerHourDotColor.setOnColorChangeListener(this)
        pickerMinuteDotColor.setOnColorChangeListener(this)
        pickerHandsColor.setOnColorChangeListener(this)
        pickerSecondDotColor.setOnColorChangeListener(this)
        pickerDatePrimaryColor.setOnColorChangeListener(this)
        pickerDateSecondaryColor.setOnColorChangeListener(this)
        pickerCenterCirclePrimaryColor.setOnColorChangeListener(this)
        pickerCenterCircleSecondaryColor.setOnColorChangeListener(this)
        chShowDate.setOnCheckedChangeListener(this)
        chShowSecond.setOnCheckedChangeListener(this)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }



        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

    }

    companion object {

        private val PREFS_NAME = "ir.co.yalda.minimalistanalogclock.AnalogClass"
        const val hourDotColor = "hourDotColor"
        const val minuteDotColor = "minuteDotColor"
        const val handsColor = "handsColor"
        const val secondDotColor = "secondDotColor"
        const val showDate = "showDate"
        const val showSecond = "showSecond"
        const val datePrimaryColor = "datePrimaryColor"
        const val dateSecondaryColor = "dateSecondaryColor"
        const val centerCirclePrimaryColor = "centerCirclePrimaryColor"
        const val centerCircleSecondaryColor = "centerCircleSecondaryColor"

        internal fun saveColorPref(context: Context, appWidgetId: Int,colorName: String, colorValue: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putInt(colorName + appWidgetId, colorValue)
            prefs.apply()
        }

        internal fun deletePrefs(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(hourDotColor + appWidgetId)
            prefs.remove(minuteDotColor + appWidgetId)
            prefs.remove(handsColor + appWidgetId)
            prefs.remove(secondDotColor + appWidgetId)
            prefs.remove(showDate + appWidgetId)
            prefs.remove(showSecond + appWidgetId)
            prefs.remove(datePrimaryColor + appWidgetId)
            prefs.remove(dateSecondaryColor + appWidgetId)
            prefs.remove(centerCircleSecondaryColor + appWidgetId)
            prefs.remove(centerCirclePrimaryColor + appWidgetId)
            prefs.apply()
        }

        internal fun saveCheckBoxPref(context: Context, appWidgetId: Int,checkName: String, value: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putBoolean(checkName + appWidgetId, value)
            prefs.apply()
        }

        internal fun loadColorPref(context: Context, appWidgetId: Int,colorName: String): Int? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val value = prefs.getInt(colorName + appWidgetId, 0)
            return if(value != 0)
                value
            else
                null
        }

        internal fun loadCheckBoxPref(context: Context, appWidgetId: Int,checkName: String): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getBoolean(checkName + appWidgetId, true)
        }
    }
}

