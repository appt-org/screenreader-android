package app.screenreader.views.gestures

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat
import app.screenreader.R
import app.screenreader.extensions.isStart
import app.screenreader.helpers.Accessibility
import app.screenreader.model.Gesture
import app.screenreader.model.Touch
import kotlin.math.atan2

interface GestureViewCallback {
    fun correct(gesture: Gesture)
    fun incorrect(gesture: Gesture, feedback: String)
}

/**
 * Created by Jan Jaap de Groot on 12/10/2020
 * Copyright 2020 Stichting Appt
 */
abstract class GestureView(val gesture: Gesture, context: Context) : View(context) {

    private val TAG = "GestureView"
    private val CLASS_NAME = GestureView::class.java.name

    private val paint: Paint by lazy {
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.primary)
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = resources.getDimension(R.dimen.stroke_width)
        paint.isAntiAlias = true
        paint
    }

    var touches = mutableMapOf<Int, ArrayList<Touch>>()
    var correct = false

    /** Drawing **/

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        touches.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        touches.entries.forEach { entry ->
            val touches = entry.value

            // Draw circle(s) around first touch.
            touches.firstOrNull()?.let { touch ->
                drawCircle(canvas, paint, touch)
            }

            // Draw lines.
            if (touches.size >= 5) {
                drawLines(canvas, paint, touches)
                drawArrowHead(canvas, paint, touches)
            }
        }
    }

    private fun drawCircle(
        canvas: Canvas,
        paint: Paint,
        touch: Touch,
        size: Float = paint.strokeWidth * 2
    ) {
        val offset = paint.strokeWidth / 2
        val x = touch.x.toFloat() - offset
        val y = touch.y.toFloat() - offset

        var i = 0
        while(i < touch.taps) {
            val radius = size * (i+1)

            if (i == 0 && touch.longPress) {
                paint.style = Paint.Style.FILL
            } else {
                paint.style = Paint.Style.STROKE
            }
            canvas.drawCircle(x, y, radius, paint)

            i++
        }
    }

    private fun drawLines(
        canvas: Canvas,
        paint: Paint,
        touches: List<Touch>
    ) {
        paint.style = Paint.Style.STROKE

        touches.forEachIndexed { index, t1 ->
            if (index < touches.size - 1) {
                val t2 = touches[index+1]

                drawLine(canvas, paint, t1, t2)
            }
        }
    }

    private fun drawLine(
        canvas: Canvas,
        paint: Paint,
        touch1: Touch,
        touch2: Touch
    ) {
        val offset = paint.strokeWidth / 2

        paint.style = Paint.Style.STROKE
        canvas.drawLine(
            touch1.x.toFloat() - offset,
            touch1.y.toFloat() - offset,
            touch2.x.toFloat() - offset,
            touch2.y.toFloat() - offset,
            paint
        )
    }

    private fun drawArrowHead(
        canvas: Canvas,
        paint: Paint,
        touches: List<Touch>,
        arrowSize: Float = paint.strokeWidth * 3,
        arrowAngle: Float = 45f
    ) {
        // Use a subset of 10 points
        val subset = touches.takeLast(10)
        if (subset.size < 10) {
            return
        }
        val t1 = subset.first()
        val t2 = subset.last()

        // Determine start and end coordinates
        val offset = paint.strokeWidth / 2
        val x1 = t1.x - offset
        val y1 = t1.y - offset
        val x2 = t2.x - offset
        val y2 = t2.y - offset

        // Calculate points of left and right side of arrow
        val leftPoints = floatArrayOf(x2 - arrowSize, y2, x2, y2)
        val rightPoints = floatArrayOf(x2, y2, x2, y2 + arrowSize)

        // Calculate angle
        val angle = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble()) * 180 / Math.PI + arrowAngle

        // Rotate the matrix around the angle
        val matrix = Matrix()
        matrix.setRotate(angle.toFloat(), x2, y2)
        matrix.mapPoints(leftPoints)
        matrix.mapPoints(rightPoints)

        // Draw arrow
        paint.style = Paint.Style.STROKE
        canvas.drawLine(leftPoints[0], leftPoints[1], leftPoints[2], leftPoints[3], paint)
        canvas.drawLine(rightPoints[0], rightPoints[1], rightPoints[2], rightPoints[3], paint)
    }

    /** Touches **/

    fun showTouches(event: MotionEvent?, taps: Int = 1, longPress: Boolean = false) {
        if (event != null) {
            val touches = mutableMapOf<Int, ArrayList<Touch>>()
            for (i in 0 until event.pointerCount) {
                val id = event.getPointerId(i)
                val coordinates = touches[id] ?: arrayListOf()

                val x = event.getX(i).toInt()
                val y = event.getY(i).toInt()

                coordinates.add(Touch(x, y, taps, longPress))
                touches[id] = coordinates
            }
            showTouches(touches)
        }
    }

    fun showTouches(touches: MutableMap<Int, ArrayList<Touch>>) {
        this.touches = touches
        invalidate()
    }

    /** Motion events **/

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onGenericMotionEvent: $event")
        return super.onGenericMotionEvent(event)
    }

    override fun onHoverEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onHoverEvent: $event")

        if (Accessibility.screenReader(context) && event != null) {
            when (event.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    event.action = MotionEvent.ACTION_DOWN
                }
                MotionEvent.ACTION_HOVER_MOVE -> {
                    event.action = MotionEvent.ACTION_MOVE
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    event.action = MotionEvent.ACTION_UP
                }
            }
            return onTouchEvent(event)
        }

        return super.onHoverEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (correct) {
            return true
        }

        Log.d(TAG, "onTouchEvent: $event")

        if (event != null && event.pointerCount > 0) {
            if (event.isStart()) {
                touches.clear()
            }

            for (i in 0 until event.pointerCount) {
                val id = event.getPointerId(i)
                val coordinates = touches[id] ?: arrayListOf()

                val x = event.getX(i).toInt()
                val y = event.getY(i).toInt()

                if (coordinates.size > 0) {
                    val lastTouch = coordinates.last()
                    if (lastTouch.x == x && lastTouch.y == y) {
                        Log.d(TAG, "Duplicate touch ignored")
                        continue
                    }
                }

                Log.d(TAG, "Point index $i with id $id touches position ($x, $y)")

                coordinates.add(Touch(x, y))
                this.touches[id] = coordinates
            }
            invalidate()
        }

        return super.onTouchEvent(event)
    }

    /** Accessibility events **/

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent?) {
        event?.className = CLASS_NAME
        super.onInitializeAccessibilityEvent(event)
    }

    override fun onPopulateAccessibilityEvent(event: AccessibilityEvent?) {
        event?.className = CLASS_NAME
        super.onPopulateAccessibilityEvent(event)
    }

    abstract fun onAccessibilityGesture(gesture: Gesture)

    /** Callback **/

    var callback: GestureViewCallback? = null

    open fun correct() {
        Log.d(TAG, "Correct")

        if (!correct) {
            correct = true
            callback?.correct(gesture)
        }
    }

    open fun incorrect(feedback: String = "Geen feedback") {
        Log.d(TAG, "Incorrect: $feedback")

        if (!correct) {
            callback?.incorrect(gesture, feedback)
        }
    }
}