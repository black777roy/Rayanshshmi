package com.example.rayanshshmi

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyClickAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MyClickAccessibilityService? = null
    }

    var isGestureRunning = false
    var onGestureComplete: (() -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private var gestureTimeoutRunnable: Runnable? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("MyClickAccessibilityService", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun performClick(x: Int, y: Int, onComplete: (() -> Unit)? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isGestureRunning) {
            isGestureRunning = true
            onGestureComplete = onComplete
            val path = Path()
            path.moveTo(x.toFloat(), y.toFloat())
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 250)) // 250ms duration for better reliability
                .build()
            Log.d("MyClickAccessibilityService", "Dispatching gesture at: $x, $y")

            // Timeout: agar callback nahi aaya toh 1.5 sec baad gesture free kar do
            gestureTimeoutRunnable?.let { handler.removeCallbacks(it) }
            gestureTimeoutRunnable = Runnable {
                if (isGestureRunning) {
                    isGestureRunning = false
                    Log.d("MyClickAccessibilityService", "Gesture timeout at: $x, $y")
                    onGestureComplete?.invoke()
                }
            }
            handler.postDelayed(gestureTimeoutRunnable!!, 1500)

            dispatchGesture(gesture, object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    isGestureRunning = false
                    Log.d("MyClickAccessibilityService", "Gesture completed at: $x, $y")
                    handler.removeCallbacks(gestureTimeoutRunnable!!)
                    onGestureComplete?.invoke()
                }
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    isGestureRunning = false
                    Log.d("MyClickAccessibilityService", "Gesture cancelled at: $x, $y")
                    handler.removeCallbacks(gestureTimeoutRunnable!!)
                    onGestureComplete?.invoke()
                }
            }, null)
        } else {
            Log.d("MyClickAccessibilityService", "Gesture not dispatched: running=$isGestureRunning")
        }
    }
}