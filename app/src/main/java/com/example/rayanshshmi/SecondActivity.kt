package com.example.rayanshshmi

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    // Section data
    private val boxWidths = arrayOf(300, 300, 300)
    private val boxHeights = arrayOf(200, 200, 200)
    private val ballSizes = arrayOf(40, 40, 40)  // 3 borders only
    private val ballCounts = arrayOf(1, 1, 1, 1) // 4 balls (last is for fourth border)

    private var thirdBallView: View? = null  // Third ball inside first border

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Overlay Permission
        findViewById<Button>(R.id.btnOverlayPermission).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Permission already granted!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Overlay permission not required for this Android version.", Toast.LENGTH_SHORT).show()
            }
        }

        // Section setup (no third border)
        setupSection(findViewById(R.id.section1), 0, "red_border", true)  // first section with 2 balls (including third ball)
        setupSection(findViewById(R.id.section2), 1, "blue_border", false)
        setupSection(findViewById(R.id.section4), 2, "black_border", false)
        setupThirdSection(findViewById(R.id.section3))  // Only controls, no border

        // Start Overlay button
        findViewById<Button>(R.id.btnStartOverlay).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
                Toast.makeText(this, "Please allow overlay permission!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prepare configs for 3 borders only
            val configs = ArrayList<OverlayConfig>()
            configs.add(OverlayConfig(boxWidths[0], boxHeights[0], ballSizes[0], 0)) // First border (red)
            configs.add(OverlayConfig(boxWidths[1], boxHeights[1], ballSizes[1], 1)) // Second border (blue)
            configs.add(OverlayConfig(boxWidths[2], boxHeights[2], ballSizes[2], 2)) // Fourth border (black)

            // Ball counts: [first, second, third (in first), fourth]
            val ballCountsInt = intArrayOf(ballCounts[0], ballCounts[1], ballCounts[2], ballCounts[3])

            val intent = Intent(this, OverlayService::class.java)
            intent.putExtra("configs", configs)
            intent.putExtra("ballCounts", ballCountsInt)
            androidx.core.content.ContextCompat.startForegroundService(this, intent)
        }

        // Next Page Button
        findViewById<Button>(R.id.btnNextPage).setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        // Start/Stop buttons
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            Toast.makeText(this, "START clicked!", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            Toast.makeText(this, "Overlay stopped!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSection(section: View, index: Int, borderDrawable: String, hasThirdBall: Boolean) {
        val seekBoxWidth = section.findViewById<SeekBar>(R.id.seekBoxWidth)
        val seekBoxHeight = section.findViewById<SeekBar>(R.id.seekBoxHeight)
        val seekBallSize = section.findViewById<SeekBar>(R.id.seekBallSize)
        val tvBallCount = section.findViewById<TextView>(R.id.tvBallCount)
        val btnSetBall = section.findViewById<Button>(R.id.btnSetBall)
        val boxContainer = section.findViewById<FrameLayout>(R.id.boxContainer)
        val ballView = section.findViewById<View>(R.id.ballView)

        // Set border color
        val resId = resources.getIdentifier(borderDrawable, "drawable", packageName)
        boxContainer.setBackgroundResource(resId)

        // Set initial values
        seekBoxWidth.progress = boxWidths[index]
        seekBoxHeight.progress = boxHeights[index]
        seekBallSize.progress = ballSizes[index]
        tvBallCount.text = ballCounts[index].toString()

        // Set initial size
        val paramsBox = boxContainer.layoutParams
        paramsBox.width = boxWidths[index]
        paramsBox.height = boxHeights[index]
        boxContainer.layoutParams = paramsBox

        val paramsBall = ballView.layoutParams
        paramsBall.width = ballSizes[index]
        paramsBall.height = ballSizes[index]
        ballView.layoutParams = paramsBall

        // SeekBar listeners
        seekBoxWidth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                boxWidths[index] = progress
                val p = boxContainer.layoutParams
                p.width = progress
                boxContainer.layoutParams = p
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBoxHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                boxHeights[index] = progress
                val p = boxContainer.layoutParams
                p.height = progress
                boxContainer.layoutParams = p
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        seekBallSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ballSizes[index] = progress
                val p = ballView.layoutParams
                p.width = progress
                p.height = progress
                ballView.layoutParams = p

                // Update third ball size if exists and this is first section
                if (hasThirdBall && thirdBallView != null) {
                    val paramsThirdBall = thirdBallView!!.layoutParams
                    paramsThirdBall.width = ballSizes[2]  // third ball size
                    paramsThirdBall.height = ballSizes[2]
                    thirdBallView!!.layoutParams = paramsThirdBall
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSetBall.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val input = EditText(this)
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            dialog.setTitle("Set Ball Count")
            dialog.setView(input)
            dialog.setPositiveButton("OK") { _, _ ->
                val count = input.text.toString().toIntOrNull() ?: 1

                ballCounts[index] = count
                tvBallCount.text = count.toString()

                // Update third ball count if this is first section
                if (hasThirdBall && thirdBallView != null) {
                    thirdBallView!!.visibility = if (count > 0) View.VISIBLE else View.GONE
                }
            }
            dialog.setNegativeButton("Cancel", null)
            dialog.show()
        }

        // Add third ball inside first section's boxContainer
        if (hasThirdBall) {
            if (thirdBallView == null) {
                thirdBallView = View(this)
                thirdBallView!!.id = View.generateViewId()
                thirdBallView!!.setBackgroundResource(R.drawable.ball_shape)
                val size = ballSizes[2]  // third ball size
                val layoutParams = FrameLayout.LayoutParams(size, size)
                layoutParams.marginStart = 0
                layoutParams.topMargin = 0
                thirdBallView!!.layoutParams = layoutParams
                boxContainer.addView(thirdBallView)
            }
        }
    }

    private fun setupThirdSection(section: View) {
        val seekBallSize = section.findViewById<SeekBar>(R.id.seekBallSize)
        val tvBallCount = section.findViewById<TextView>(R.id.tvBallCount)
        val btnSetBall = section.findViewById<Button>(R.id.btnSetBall)

        // Set initial values
        seekBallSize.progress = ballSizes[2]
        tvBallCount.text = ballCounts[2].toString()

        // SeekBar listener
        seekBallSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ballSizes[2] = progress

                // Update third ball size in first section
                thirdBallView?.let {
                    val params = it.layoutParams
                    params.width = progress
                    params.height = progress
                    it.layoutParams = params
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSetBall.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val input = EditText(this)
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            dialog.setTitle("Set Ball Count")
            dialog.setView(input)
            dialog.setPositiveButton("OK") { _, _ ->
                val count = input.text.toString().toIntOrNull() ?: 1
                ballCounts[2] = count
                tvBallCount.text = count.toString()

                // Show/hide third ball in first section based on count
                thirdBallView?.visibility = if (count > 0) View.VISIBLE else View.GONE
            }
            dialog.setNegativeButton("Cancel", null)
            dialog.show()
        }
    }
}