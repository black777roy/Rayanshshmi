package com.example.rayanshshmi

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class MyKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    companion object {
        var inputConnectionGlobal: InputConnection? = null
        var keyboardViewGlobal: KeyboardView? = null
    }

    private lateinit var kv: KeyboardView
    private lateinit var keyboard: Keyboard
    private var isCaps = false

    // Custom long press logic
    private var isBackspacePressed = false
    private val handler = Handler(Looper.getMainLooper())
    private val longPressRunnable = object : Runnable {
        override fun run() {
            if (isBackspacePressed) {
                val ic = currentInputConnection
                ic.deleteSurroundingText(Int.MAX_VALUE, Int.MAX_VALUE)
                isBackspacePressed = false
            }
        }
    }

    override fun onCreateInputView(): View {
        kv = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        keyboard = Keyboard(this, R.xml.qwerty)
        kv.keyboard = keyboard
        kv.setOnKeyboardActionListener(this)
        keyboardViewGlobal = kv

        // Custom touch listener for backspace long press
        kv.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                val key = getKeyFromTouch(event)
                if (key != null && key.codes[0] == Keyboard.KEYCODE_DELETE) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        isBackspacePressed = true
                        handler.postDelayed(longPressRunnable, 500) // 500ms for long press
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        if (isBackspacePressed) {
                            handler.removeCallbacks(longPressRunnable)
                            isBackspacePressed = false
                        }
                    }
                }
            }
            false
        }

        return kv
    }

    private fun getKeyFromTouch(event: MotionEvent): Keyboard.Key? {
        val keys = keyboard.keys
        for (key in keys) {
            if (event.x >= key.x && event.x < key.x + key.width &&
                event.y >= key.y && event.y < key.y + key.height) {
                return key
            }
        }
        return null
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
            Keyboard.KEYCODE_SHIFT -> {
                isCaps = !isCaps
                keyboard.isShifted = isCaps
                kv.invalidateAllKeys()
            }
            32 -> ic.commitText(" ", 1) // Space
            44 -> ic.commitText(",", 1) // Comma
            46 -> ic.commitText(".", 1) // Dot
            10 -> {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
            }
            else -> {
                var code = primaryCode.toChar()
                if (isCaps && code.isLetter()) code = code.uppercaseChar()
                ic.commitText(code.toString(), 1)
            }
        }
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        inputConnectionGlobal = currentInputConnection
        keyboardViewGlobal = kv
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        inputConnectionGlobal = null
        keyboardViewGlobal = null
    }
}
