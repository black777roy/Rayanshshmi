package com.example.rayanshshmi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etInput: EditText
    private lateinit var keyboardGrid: GridLayout
    private var isCaps = false

    companion object {
        val keyButtonMap = mutableMapOf<Char, Button>()

        fun pressKeyEffect(char: Char) {
            val btn = keyButtonMap[char.uppercaseChar()]
            btn?.let {
                it.isPressed = true
                it.invalidate()
                Handler(Looper.getMainLooper()).postDelayed({
                    it.isPressed = false
                    it.invalidate()
                }, 120)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etInput = findViewById(R.id.etInput)
        keyboardGrid = findViewById(R.id.keyboardGrid)

        findViewById<Button>(R.id.btnSetDefaultKeyboard).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        findViewById<Button>(R.id.btnAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.btnNextPage).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnTypeRandomWord).setOnClickListener {
            val word = getRandomWord()
            if (word != null) {
                typeWordHumanLike(word)
            } else {
                Toast.makeText(this, "Word list empty!", Toast.LENGTH_SHORT).show()
            }
        }

        setupKeyboard()
    }

    private fun setupKeyboard() {
        keyboardGrid.removeAllViews()
        keyButtonMap.clear()

        val keys = arrayOf(
            arrayOf("1","2","3","4","5","6","7","8","9","0"),
            arrayOf("Q","W","E","R","T","Y","U","I","O","P"),
            arrayOf("A","S","D","F","G","H","J","K","L"),
            arrayOf("⇪","Z","X","C","V","B","N","M","⌫","🔍")
        )

        for (row in keys) {
            for (key in row) {
                val btn = Button(this)
                btn.text = if (isCaps && key.length == 1 && key[0].isLetter()) key.uppercase() else key
                btn.setTextColor(Color.WHITE)
                btn.textSize = 18f
                btn.setPadding(0, 0, 0, 0)
                btn.setAllCaps(false)
                btn.background = ContextCompat.getDrawable(this, R.drawable.key_button_bg)

                if (key.length == 1 && key[0].isLetterOrDigit()) {
                    keyButtonMap[key[0].uppercaseChar()] = btn
                }

                if (key == "⇪") {
                    if (isCaps) {
                        btn.setBackgroundColor(Color.parseColor("#B388FF"))
                        btn.setTextColor(Color.BLACK)
                    } else {
                        btn.setBackgroundColor(Color.parseColor("#444444"))
                        btn.setTextColor(Color.WHITE)
                    }
                }

                val params = GridLayout.LayoutParams()
                params.width = 0
                params.height = GridLayout.LayoutParams.WRAP_CONTENT
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                params.setMargins(6, 6, 6, 6)
                btn.layoutParams = params

                when (key) {
                    "⌫" -> {
                        btn.setOnClickListener {
                            val text = etInput.text.toString()
                            if (text.isNotEmpty()) {
                                etInput.setText(text.substring(0, text.length - 1))
                                etInput.setSelection(etInput.text.length)
                            }
                        }
                        btn.setOnLongClickListener {
                            etInput.setText("")
                            true
                        }
                    }
                    "⇪" -> {
                        btn.setOnClickListener {
                            isCaps = !isCaps
                            setupKeyboard()
                        }
                    }
                    "🔍" -> {
                        btn.setOnClickListener {
                            // Search ka logic yahan daal sakte ho
                        }
                    }
                    else -> {
                        btn.setOnClickListener {
                            val toAdd = if (isCaps && key.length == 1 && key[0].isLetter()) key.uppercase() else key
                            etInput.append(toAdd)
                        }
                    }
                }
                keyboardGrid.addView(btn)
            }
        }
    }

    private fun getRandomWord(): String? {
        val wordList = Utils.loadWordList(this)
        if (wordList.isNotEmpty()) {
            return wordList.random()
        }
        return null
    }

    private fun typeWordHumanLike(word: String, onDone: (() -> Unit)? = null) {
        val timeList = Utils.loadTimeList(this, "time_list_1")
        if (timeList.isEmpty()) {
            Toast.makeText(this, "Section 1 timing list empty!", Toast.LENGTH_SHORT).show()
            return
        }
        etInput.setText("")
        var i = 0

        fun typeNext() {
            if (i >= word.length) {
                onDone?.invoke()
                return
            }
            pressKeyEffect(word[i])
            etInput.append(word[i].toString())
            i++
            val delayData = timeList.random()
            val delay = delayData.second * 1000L + delayData.millisecond.toLong()
            etInput.postDelayed({ typeNext() }, delay)
        }
        typeNext()
    }
}