package ir.co.yalda.minimalistanalogclock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View

import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign


internal class Clock : View {

    var hourDotColor = 0
    var minuteDotColor = 0
    var handsColor = 0
    var secondDotColor = 0
    var showDate = true
    var showSecond = true
    var datePrimaryColor = 0
    var dateSecondaryColor = 0
    var centerCirclePrimaryColor = 0
    var centerCircleSecondaryColor = 0

    /** The coordinates used to paint the clock hands.  */
    var xHandSec: Int = 0
    var yHandSec: Int = 0
    var xHandMin: Int = 0
    var yHandMin: Int = 0
    var xHandHour: Int = 0
    var yHandHour: Int = 0

    /** The size of the clock.  */
    private val WIDTH = 400
    private val HEIGHT = 400
    private var SCALE = 1f
    private var RADIUS = 0

    private var mBottom: Int = 0
    private var mTop: Int = 0
    private var mLeft: Int = 0
    private var mRight: Int = 0

    /** The length of the clock hands relative to the clock size.  */
    private val secondHandLength = WIDTH / 2 - 30
    private val minuteHandLength = WIDTH / 2 - 100
    private val hourHandLength = WIDTH / 2 - 130

    /** The distance of the dots from the origin (center of the clock).  */
    private val DISTANCE_DOT_FROM_ORIGIN = WIDTH / 2 - 30

    private val DIAMETER_BIG_DOT = 5
    private val DIAMETER_SMALL_DOT = 3
    private val DIAMETER_SECOND_DOT = 8
    private val DIAMETER_CENTER_BIG_DOT = 14
    private val DIAMETER_CENTER_SMALL_DOT = 6


    private lateinit var mCalendar: Calendar
    private var mContext: Context




    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mContext = context
        handleAttr(context,attrs)
        init(Calendar.getInstance())
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        handleAttr(context,attrs)
        init(Calendar.getInstance())
    }

    constructor(context: Context) : super(context) {
        mContext = context
        handleAttr(context,null)
        init(Calendar.getInstance())

    }

    @SuppressLint("Recycle")
    private fun handleAttr(context: Context, attrs: AttributeSet?){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.mac, 0, 0)

        try {
            hourDotColor = typedArray.getColor(R.styleable.mac_hourDotColor,Color.parseColor("#53cfff"))
            minuteDotColor = typedArray.getColor(R.styleable.mac_minuteDotColor,Color.WHITE)
            handsColor = typedArray.getColor(R.styleable.mac_handsColor,Color.WHITE)
            secondDotColor = typedArray.getColor(R.styleable.mac_secondDotColor,Color.parseColor("#fe6f70"))
            showDate = typedArray.getBoolean(R.styleable.mac_showDate,true)
            showSecond = typedArray.getBoolean(R.styleable.mac_showSecond,true)
            datePrimaryColor = typedArray.getColor(R.styleable.mac_datePrimaryColor,Color.WHITE)
            dateSecondaryColor = typedArray.getColor(R.styleable.mac_dateSecondaryColor,Color.argb(120,0,0,0))
            centerCirclePrimaryColor = typedArray.getColor(R.styleable.mac_centerCirclePrimaryColor,Color.parseColor("#0e5876"))
            centerCircleSecondaryColor = typedArray.getColor(R.styleable.mac_centerCircleSecondaryColor,Color.parseColor("#fe6f70"))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }

    }

    private fun init(calendar: Calendar) {
        mCalendar = calendar
        RADIUS = Math.min(WIDTH,HEIGHT) / 2
        getTime()
        Handler().postDelayed({ init(Calendar.getInstance()) }, 500)
    }

    /**
     * At each iteration we recalculate the coordinates of the clock hands,
     * and repaint everything.
     */
    private fun getTime() {
        val currentSecond = mCalendar.get(Calendar.SECOND)
        val currentMinute = mCalendar.get(Calendar.MINUTE)
        val currentHour = mCalendar.get(Calendar.HOUR)

        xHandSec = minToLocation(currentSecond, secondHandLength).x
        yHandSec = minToLocation(currentSecond, secondHandLength).y
        xHandMin = minToLocation(currentMinute, minuteHandLength).x
        yHandMin = minToLocation(currentMinute, minuteHandLength).y
        xHandHour = minToLocation(currentHour * 5 + getRelativeHour(currentMinute), hourHandLength).x
        yHandHour = minToLocation(currentHour * 5 + getRelativeHour(currentMinute), hourHandLength).y
        invalidate()
    }


    private fun getRelativeHour(min: Int): Int {
        return min / 12
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val availW = mRight - mLeft
        val availH = mBottom - mTop

        val wScale = availW.div(WIDTH)
        val hScale = availH.div(HEIGHT)
        val scale = Math.min(wScale,hScale)
        SCALE = scale.toFloat()

        val cX = availW / 2
        val cY = availH / 2

        val w = (WIDTH * SCALE).toInt()
        val h = (HEIGHT * SCALE).toInt()

        var scaled = false

        if (availW < w || availH < h) {
            scaled = true
            val scale = Math.min(availW.toFloat() / w.toFloat(),
                    availH.toFloat() / h.toFloat())
            canvas.save()
            canvas.scale(scale, scale, cX.toFloat(), cY.toFloat())
        }

        val paint = Paint()
        paint.isAntiAlias = true

        // Draw the dots
        for (i in 0..59) {

            val dotCoordinates = minToLocation(i, DISTANCE_DOT_FROM_ORIGIN)

            if (i % 5 == 0) {
                // big dot
                paint.color = hourDotColor
                canvas.drawCircle((dotCoordinates.x - DIAMETER_BIG_DOT.times(SCALE) / 2),
                        (dotCoordinates.y - DIAMETER_BIG_DOT.times(SCALE) / 2),
                        DIAMETER_BIG_DOT.times(SCALE),
                        paint)
            } else {
                // small dot
                paint.color = minuteDotColor
                canvas.drawCircle((dotCoordinates.x - DIAMETER_SMALL_DOT.times(SCALE) / 2),
                        (dotCoordinates.y - DIAMETER_SMALL_DOT.times(SCALE) / 2),
                        DIAMETER_SMALL_DOT.times(SCALE),
                        paint)
            }
        }

        //Draw clock second hands
        if(showSecond) {
            paint.color = Color.WHITE
            canvas.drawCircle((xHandSec - DIAMETER_SECOND_DOT.times(SCALE) / 2)
                    , (yHandSec - DIAMETER_SECOND_DOT.times(SCALE) / 2)
                    , (DIAMETER_SECOND_DOT.times(SCALE) + 2)
                    , paint)
            paint.color = secondDotColor
            canvas.drawCircle((xHandSec - DIAMETER_SECOND_DOT.times(SCALE) / 2)
                    , (yHandSec - DIAMETER_SECOND_DOT.times(SCALE) / 2)
                    , DIAMETER_SECOND_DOT.times(SCALE)
                    , paint)
        }


        //Draw Date
        if(showDate) {
            paint.isFakeBoldText = true
            val typeface = ResourcesCompat.getFont(mContext, R.font.k2d)
            paint.typeface = typeface
            val DISTANCE_FROM_BOTTM = 60.times(SCALE)
            //Draw year
            paint.color = datePrimaryColor
            val yearSize = Rect()
            val yearName = mCalendar.get(Calendar.YEAR).toString()
            paint.textSize = 25f.times(SCALE)
            paint.getTextBounds(yearName, 0, yearName.length, yearSize)
            val yearX = WIDTH.times(SCALE) / 2 - yearSize.width() / 2 - yearSize.left
            val yearY = HEIGHT.times(SCALE) - DISTANCE_FROM_BOTTM
            canvas.drawText(yearName, yearX, yearY, paint)
            //Draw day
            paint.color = dateSecondaryColor
            val daySize = Rect()
            val dayName = to2Digit(mCalendar.get(Calendar.DAY_OF_MONTH))
            paint.textSize = 35f.times(SCALE)
            paint.getTextBounds(dayName, 0, dayName.length, daySize)
            val dayX = WIDTH.times(SCALE) / 2 - daySize.width() / 2 - daySize.left
            val dayY = HEIGHT.times(SCALE) - DISTANCE_FROM_BOTTM - yearSize.height() - 10
            canvas.drawText(dayName, dayX, dayY, paint)
            //Draw Month
            paint.color = datePrimaryColor
            paint.alpha = 255
            val monthSize = Rect()
            val monthName = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US).toUpperCase()
            paint.textSize = 25f.times(SCALE)
            paint.getTextBounds(monthName, 0, monthName.length, monthSize)
            val monthX = WIDTH.times(SCALE) / 2 - monthSize.width() / 2 - monthSize.left
            val monthY = HEIGHT.times(SCALE) - DISTANCE_FROM_BOTTM - (daySize.height() + yearSize.height()) - 20
            canvas.drawText(monthName, monthX, monthY, paint)
        }


        // Draw the clock hands
        paint.color = handsColor
        paint.strokeWidth = 8f.times(SCALE)
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine((WIDTH.times(SCALE) / 2)
                , (HEIGHT.times(SCALE) / 2)
                , xHandMin.toFloat(), yHandMin.toFloat(), paint)
        canvas.drawLine((WIDTH.times(SCALE) / 2)
                , (HEIGHT.times(SCALE) / 2)
                , xHandHour.toFloat()
                , yHandHour.toFloat(), paint)

        //Draw center circle
        paint.color = centerCirclePrimaryColor
        canvas.drawCircle((WIDTH.times(SCALE) / 2)
                , (HEIGHT.times(SCALE) / 2)
                , DIAMETER_CENTER_BIG_DOT.times(SCALE)
                , paint)
        paint.color = centerCircleSecondaryColor
        canvas.drawCircle((WIDTH.times(SCALE) / 2)
                , (HEIGHT.times(SCALE) / 2)
                , DIAMETER_CENTER_SMALL_DOT.times(SCALE)
                , paint)

        if (scaled) {
            canvas.restore()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val finalRadius = (WIDTH * SCALE).toInt()
        setMeasuredDimension(finalRadius, finalRadius)
    }

    override fun getSuggestedMinimumHeight(): Int {
        return (HEIGHT * SCALE).toInt()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return (WIDTH * SCALE).toInt()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int,
                          bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mRight = right
        mLeft = left
        mTop = top
        mBottom = bottom
    }

    /**
     * Converts current second/minute/hour to x and y coordinates.
     * @param radius The radius length
     * @return the coordinates point
     */
    var xIsPos = true
    var yIsPos = true
    private fun minToLocation(timeStep: Int, radius: Int): Point {
        val t =  2.0 * Math.PI * (timeStep - 15).toDouble() / 60
        when (timeStep) {
            in 0..15 -> {
                xIsPos = true
                yIsPos = true
            }
            in 16..30 -> {
                xIsPos = true
                yIsPos = false
            }
            in 31..45 -> {
                xIsPos = false
                yIsPos = false
            }
            in 46..60 -> {
                xIsPos = false
                yIsPos = true
            }
        }

        val xDegree = if(xIsPos) Math.cos(t).absoluteValue.pow(9.0/11)
        else Math.cos(t).absoluteValue.pow(9.0/11).times(-1)

        val yDegree = - if(yIsPos) Math.sin(t).absoluteValue.pow(9.0/11)
        else Math.sin(t).absoluteValue.pow(9.0/11).times(-1)


        val x = (WIDTH.times(SCALE) / 2 + radius.times(SCALE) * xDegree)
        val y = (HEIGHT.times(SCALE) / 2 + radius.times(SCALE) * yDegree)


        return Point(x.toInt(),y.toInt())
    }

    private fun to2Digit(number: Number): String {
        return String.format(Locale.US, "%02d", number)
    }

}