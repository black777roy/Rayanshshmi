package com.example.rayanshshmi

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlin.random.Random

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private val overlayViews = mutableListOf<FrameLayout>()
    private var startStopLayout: View? = null

    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private val dragDone = mutableListOf<Boolean>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "overlay_service_channel"
            val channelName = "Overlay Service"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(channel)
            }
            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Overlay Running")
                .setContentText("Overlay service is active")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
            startForeground(1, notification)
        }

        removeOverlays()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        @Suppress("DEPRECATION")
        val configs = intent?.getSerializableExtra("configs") as? ArrayList<OverlayConfig> ?: arrayListOf()

        overlayViews.clear()
        dragDone.clear()
        configs.forEachIndexed { i, config ->
            dragDone.add(false)
            val overlayView = createOverlayView(config, i)
            val params = WindowManager.LayoutParams(
                config.boxWidth,
                config.boxHeight,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.TOP or Gravity.START
            params.x = 100 * (i + 1)
            params.y = 200 * (i + 1)
            windowManager?.addView(overlayView, params)
            overlayViews.add(overlayView)
        }

        addStartStopOverlay()
        return START_NOT_STICKY
    }

    private fun createOverlayView(config: OverlayConfig, sectionIndex: Int): FrameLayout {
        val context = this
        val frame = FrameLayout(context)
        val borderRes = when (config.borderType) {
            0 -> R.drawable.red_border
            1 -> R.drawable.blue_border
            2 -> R.drawable.black_border
            else -> R.drawable.red_border
        }
        frame.setBackgroundResource(borderRes)

        // Main ball (always present, black)
        val ball = TextView(context)
        val size = config.ballSize
        val lp = FrameLayout.LayoutParams(size, size)
        lp.leftMargin = 0
        lp.topMargin = 0
        ball.layoutParams = lp
        ball.setBackgroundResource(R.drawable.ball_shape)
        ball.text = (sectionIndex + 1).toString()
        ball.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        ball.textSize = 18f
        ball.gravity = Gravity.CENTER
        frame.addView(ball)
        frame.tag = ball

        // Third ball (red) only in first border
        if (sectionIndex == 0) {
            val thirdBall = TextView(context)
            val thirdBallSize = size
            val thirdLp = FrameLayout.LayoutParams(thirdBallSize, thirdBallSize)
            thirdLp.leftMargin = 0
            thirdLp.topMargin = 0
            thirdBall.layoutParams = thirdLp
            thirdBall.setBackgroundResource(R.drawable.ball_shape_clicked) // Red color
            thirdBall.text = "3"
            thirdBall.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            thirdBall.textSize = 18f
            thirdBall.gravity = Gravity.CENTER
            thirdBall.visibility = View.GONE // Hide by default
            frame.setTag(R.id.third_ball_tag, thirdBall)
            frame.addView(thirdBall)
        }

        // DRAG SUPPORT
        frame.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                if (dragDone[sectionIndex]) return false
                val params = v?.layoutParams as? WindowManager.LayoutParams ?: return false
                try {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            windowManager?.updateViewLayout(v, params)
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            dragDone[sectionIndex] = true
                            makeOverlayTouchThrough(v)
                            return true
                        }
                    }
                } catch (_: Exception) {}
                return false
            }
        })

        return frame
    }

    private fun makeOverlayTouchThrough(view: View?) {
        view ?: return
        val params = view.layoutParams as? WindowManager.LayoutParams ?: return
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        windowManager?.updateViewLayout(view, params)
    }

    private fun addStartStopOverlay() {
        val context = this
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        val btnStart = Button(context)
        btnStart.text = "START"
        btnStart.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
        val btnStop = Button(context)
        btnStop.text = "STOP"
        btnStop.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light))

        layout.addView(btnStart)
        layout.addView(btnStop)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.END
        params.x = 50
        params.y = 200

        btnStart.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                handler.postDelayed({ startStrictSequence() }, 500)
            }
        }
        btnStop.setOnClickListener {
            isRunning = false
            stopBallClickSequence()
        }

        windowManager?.addView(layout, params)
        startStopLayout = layout
    }

    // Helper to get random position after layout is drawn
    private fun getRandomPosition(frame: FrameLayout, ballSize: Int, onReady: (Int, Int) -> Unit) {
        frame.post {
            val boxWidth = frame.width
            val boxHeight = frame.height
            val maxX = boxWidth - ballSize
            val maxY = boxHeight - ballSize
            val randX = if (maxX > 0) Random.nextInt(0, maxX) else 0
            val randY = if (maxY > 0) Random.nextInt(0, maxY) else 0
            onReady(randX, randY)
        }
    }

    // Ball click and show (real click, hold for random seconds, then remove from border)
    private fun clickSectionHoldAndRemove(sectionIndex: Int, sectionTimeKey: String, onDone: () -> Unit) {
        if (!isRunning) { onDone(); return }
        if (sectionIndex >= overlayViews.size) { onDone(); return }

        val frame = overlayViews[sectionIndex]
        val ball = frame.tag as TextView
        val ballSize = ball.width

        getRandomPosition(frame, ballSize) { randX, randY ->
            val animBall = TextView(this)
            val lp = FrameLayout.LayoutParams(ballSize, ballSize)
            lp.leftMargin = randX
            lp.topMargin = randY
            animBall.layoutParams = lp
            animBall.setBackgroundResource(R.drawable.ball_shape_clicked)
            animBall.text = (sectionIndex + 1).toString()
            animBall.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            animBall.textSize = 18f
            animBall.gravity = Gravity.CENTER

            frame.addView(animBall)

            val location = IntArray(2)
            frame.getLocationOnScreen(location)
            val clickX = location[0] + randX + ballSize / 2
            val clickY = location[1] + randY + ballSize / 2

            MyClickAccessibilityService.instance?.performClick(clickX, clickY) {
                val timeList = Utils.loadTimeList(this, sectionTimeKey)
                val delayData = if (timeList.isNotEmpty()) timeList.random() else TimeData(1, 0)
                val delay = delayData.second * 1000L + delayData.millisecond
                handler.postDelayed({
                    try { frame.removeView(animBall) } catch (_: Exception) {}
                    onDone()
                }, delay)
            }
        }
    }

    // Word type karne ka function (Section 1 ke random delay ke saath)
    private fun typeWordHumanLike(word: String, onDone: () -> Unit) {
        val timeList = Utils.loadTimeList(this, "first_border_time_list")
        var i = 0
        fun typeNext() {
            if (i >= word.length) { onDone(); return }
            handler.post {
                val ic = MyKeyboardService.inputConnectionGlobal
                val char = word[i].toString()
                ic?.commitText(char, 1)
                val delayData = if (timeList.isNotEmpty()) timeList.random() else TimeData(0, 150)
                val delay = delayData.millisecond.toLong()
                handler.postDelayed({ i++; typeNext() }, delay)
            }
        }
        typeNext()
    }

    // Third ball click (red) only in first border, after second ball
    private fun clickThirdBallInFirstBorder(onDone: () -> Unit) {
        val frame = overlayViews[0]
        val thirdBall = frame.getTag(R.id.third_ball_tag) as? TextView ?: run { onDone(); return }
        val ballSize = thirdBall.width

        getRandomPosition(frame, ballSize) { randX, randY ->
            val lp = thirdBall.layoutParams as FrameLayout.LayoutParams
            lp.leftMargin = randX
            lp.topMargin = randY
            thirdBall.layoutParams = lp
            thirdBall.visibility = View.VISIBLE

            val location = IntArray(2)
            frame.getLocationOnScreen(location)
            val clickX = location[0] + randX + ballSize / 2
            val clickY = location[1] + randY + ballSize / 2

            val timeList = Utils.loadTimeList(this, "third_border_time_list")
            val delayData = if (timeList.isNotEmpty()) timeList.random() else TimeData(1, 0)
            val delay = delayData.second * 1000L + delayData.millisecond

            MyClickAccessibilityService.instance?.performClick(clickX, clickY) {
                handler.postDelayed({
                    thirdBall.visibility = View.GONE
                    onDone()
                }, delay)
            }
        }
    }

    // Backspace se word clear karne ka function (Section 4 ke random delay ke saath)
    private fun clearWordHumanLike(length: Int, onDone: () -> Unit) {
        val timeList = Utils.loadTimeList(this, "fourth_border_time_list")
        var i = 0
        fun backspaceNext() {
            if (i >= length) { onDone(); return }
            handler.post {
                val ic = MyKeyboardService.inputConnectionGlobal
                ic?.deleteSurroundingText(1, 0)
                val delayData = if (timeList.isNotEmpty()) timeList.random() else TimeData(0, 150)
                val delay = delayData.millisecond.toLong()
                handler.postDelayed({ i++; backspaceNext() }, delay)
            }
        }
        backspaceNext()
    }

    private fun startStrictSequence() {
        if (!isRunning) return

        clickSectionHoldAndRemove(0, "first_border_time_list") {
            val wordList = Utils.loadWordList(this)
            val word = if (wordList.isNotEmpty()) wordList.random() else "Hello"
            typeWordHumanLike(word) {
                clickSectionHoldAndRemove(1, "second_border_time_list") {
                    clickThirdBallInFirstBorder {
                        clickSectionHoldAndRemove(3, "fourth_border_time_list") {
                            clearWordHumanLike(word.length) {
                                handler.postDelayed({ startStrictSequence() }, 500)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun stopBallClickSequence() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun removeOverlays() {
        overlayViews.forEach {
            try { windowManager?.removeView(it) } catch (_: Exception) {}
        }
        overlayViews.clear()
    }
}