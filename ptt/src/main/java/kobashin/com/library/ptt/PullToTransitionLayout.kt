package kobashin.com.library.ptt

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import android.widget.FrameLayout
import kotlin.math.PI
import kotlin.math.asin
import android.graphics.Bitmap
import android.util.Log
import kobashin.com.library.ptt.ext.dp


interface OnTransitionEvent {
    fun whetherStartTransition(v: View, event: MotionEvent, startX: Float, startY: Float): Boolean
    fun onDragging(v: View, event: MotionEvent)
    fun onFinishTransition()
    fun onCancelTransition()
}

enum class DraggingStatus(val isDragging: Boolean, val isConsumedEvent: Boolean) {
    DEFAULT(false, true),
    START(false, false), // whetherStartTransition == trueになったとき
    STARTED(true, true), // drag用のBitmapが用意できたとき
    LOCATION(true, true),
    DROP(false, false),
    ENDED(false, false),
    EXITED(false, false);
}


class PullToTransitionLayout : FrameLayout {

    private var targetRid: Int = 0
    private var finishDistance: Int? = null
    private lateinit var targetView: View
    private var draggingBitmap: Bitmap? = null
    private val drawPaint = Paint()
    private var draggingStatus: DraggingStatus = DraggingStatus.DEFAULT
    private var startX: Float = 0.0F
    private var startY: Float = 0.0F
    private var prevX: Float = 0.0F
    private var prevY: Float = 0.0F


    var callback: OnTransitionEvent? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PullToTransitionLayout, 0, 0)

        targetRid = if (a.hasValue(R.styleable.PullToTransitionLayout_pull_target)) {
            a.getResourceId(R.styleable.PullToTransitionLayout_pull_target, 0)
        } else 0
        require(targetRid != 0)

        finishDistance = if (a.hasValue(R.styleable.PullToTransitionLayout_finish_distance)) {
            a.getDimensionPixelSize(R.styleable.PullToTransitionLayout_finish_distance, 100.dp)
        } else 100.dp
        require(finishDistance != null)
        a.recycle()
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        targetView = findViewById<View>(targetRid)
        attachView()
    }

    private fun attachView() {
        targetView.setOnTouchListener { v, event ->
            Log.i("koba", event.toString())
            var dirty = false
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (draggingStatus.isDragging) {
                        if (event.y - startY > finishDistance!!) {
                            callback?.onFinishTransition()
                        } else {
                            callback?.onCancelTransition()
                            dirty = true
                        }
                        resetState()
                    }

                    startX = 0.0F
                    startY = 0.0F
                    prevX = 0.0F
                    prevY = 0.0F

                }
                MotionEvent.ACTION_DOWN -> {
                    draggingStatus = DraggingStatus.DEFAULT

                    startX = event.x
                    startY = event.y
                    prevX = event.x
                    prevY = event.y

                    dirty = true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (draggingStatus.isDragging) {
                        draggingStatus = DraggingStatus.LOCATION
                    } else if (draggingStatus == DraggingStatus.DEFAULT) {
                        // 初回のDOWNが来ずにMOVEから始まるケースが存在する
                        if (startX == 0F && startY == 0F) {
                            startX = event.x
                            startY = event.y
                        }
                        draggingStatus = checkState(v, event, startX, startY)

                        if (draggingStatus == DraggingStatus.START) {
                            generateDraggingView()
                        }
                    }
                    
                    if (event.y < startY) {
                        resetState()
                        callback?.onCancelTransition()
                    }
                    
                    prevX = event.x
                    prevY = event.y
                    callback?.onDragging(v, event)
                    dirty = true
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (draggingStatus.isDragging) {
                        resetState()
                    }
                    startX = 0.0F
                    startY = 0.0F
                    prevX = 0.0F
                    prevY = 0.0F
                    dirty = true
                }
                else -> {
                    dirty = true
                }
            }

            // drag中はイベントを消費する
            if (dirty) {
                invalidate()
            }
            draggingStatus.isConsumedEvent
        }
    }

    private fun resetState() {
        draggingStatus = DraggingStatus.DEFAULT
    }

    private fun checkState(
        v: View,
        event: MotionEvent,
        startX: Float,
        startY: Float
    ): DraggingStatus = if (draggingStatus == DraggingStatus.DEFAULT) {
            if (callback?.whetherStartTransition(v, event, startX, startY) == true) {
                DraggingStatus.START
            } else DraggingStatus.DEFAULT
        } else draggingStatus


    private fun generateDraggingView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getBitmapFromView(targetView) {
                draggingBitmap = it
                draggingStatus = if (it != null && draggingStatus == DraggingStatus.START) DraggingStatus.STARTED else DraggingStatus.DEFAULT
            }
        } else {
            draggingBitmap = Bitmap.createBitmap(targetView.width, targetView.height, Bitmap.Config.ARGB_8888)
            draggingStatus = if (draggingBitmap != null) {
                val canvas = Canvas(draggingBitmap!!)
                targetView.draw(canvas)
                DraggingStatus.STARTED
            } else {
                DraggingStatus.DEFAULT
            }
        }
    }

    private fun calculateScale(): Float {
        val dy = prevY - startY
        val calcDistance = finishDistance!! * 2
        return when {
            dy <= 0 -> 1.0F
            dy >= calcDistance -> 0.8F
            else -> 1.0F - (asin(dy.toDouble() / calcDistance) * 2 / PI).toFloat() * 0.2F
        }
    }

    // Step 4, draw the children
    override fun dispatchDraw(canvas: Canvas?) {
        if (!draggingStatus.isDragging) {
            super.dispatchDraw(canvas)
        } else if (draggingBitmap != null) {
            val matrix = Matrix().apply {
                val sc = calculateScale()
                postScale(sc, sc, prevX, prevY)
                postTranslate(prevX - startX, prevY - startY)
            }
            canvas?.drawBitmap(draggingBitmap!!, matrix, drawPaint)
        }
    }

    // Step 6, draw decorations (foreground, scrollbars)
    override fun onDrawForeground(canvas: Canvas?) {
        if (!draggingStatus.isDragging) {
            super.onDrawForeground(canvas)
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun getBitmapFromView(view: View, callback: (Bitmap) -> Unit) {
        (context as? Activity)?.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(
                    window,
                    Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width,
                        locationOfViewInWindow[1] + view.height
                    ),
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            callback(bitmap)
                        }
                    },
                    Handler()
                )
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }
    }
}
