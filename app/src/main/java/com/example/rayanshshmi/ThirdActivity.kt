package com.example.rayanshshmi

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ThirdActivity : AppCompatActivity() {

    private lateinit var wordList: MutableList<String>
    private lateinit var timeList1: MutableList<TimeData>
    private lateinit var timeList2: MutableList<TimeData>
    private lateinit var timeList3: MutableList<TimeData>
    private lateinit var timeList4: MutableList<TimeData>
    private lateinit var firstBorderTimeList: MutableList<TimeData>
    private lateinit var secondBorderTimeList: MutableList<TimeData>
    private lateinit var thirdBorderTimeList: MutableList<TimeData>
    private lateinit var fourthBorderTimeList: MutableList<TimeData>

    private lateinit var wordAdapter: WordAdapter
    private lateinit var timeAdapter1: TimeAdapter
    private lateinit var timeAdapter2: TimeAdapter
    private lateinit var timeAdapter3: TimeAdapter
    private lateinit var timeAdapter4: TimeAdapter
    private lateinit var firstBorderTimeAdapter: TimeAdapter
    private lateinit var secondBorderTimeAdapter: TimeAdapter
    private lateinit var thirdBorderTimeAdapter: TimeAdapter
    private lateinit var fourthBorderTimeAdapter: TimeAdapter

    private lateinit var etFirstBorderSec: EditText
    private lateinit var etFirstBorderMs: EditText
    private lateinit var btnAddFirstBorderTime: Button
    private lateinit var rvFirstBorderTime: RecyclerView

    private lateinit var etSecondBorderSec: EditText
    private lateinit var etSecondBorderMs: EditText
    private lateinit var btnAddSecondBorderTime: Button
    private lateinit var rvSecondBorderTime: RecyclerView

    private lateinit var etThirdBorderSec: EditText
    private lateinit var etThirdBorderMs: EditText
    private lateinit var btnAddThirdBorderTime: Button
    private lateinit var rvThirdBorderTime: RecyclerView

    private lateinit var etFourthBorderSec: EditText
    private lateinit var etFourthBorderMs: EditText
    private lateinit var btnAddFourthBorderTime: Button
    private lateinit var rvFourthBorderTime: RecyclerView

    private lateinit var etSecond4: EditText
    private lateinit var etMilli4: EditText
    private lateinit var btnAddTime4: Button
    private lateinit var rvTimeList4: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        etFirstBorderSec = findViewById(R.id.etFirstBorderSec)
        etFirstBorderMs = findViewById(R.id.etFirstBorderMs)
        btnAddFirstBorderTime = findViewById(R.id.btnAddFirstBorderTime)
        rvFirstBorderTime = findViewById(R.id.rvFirstBorderTime)

        etSecondBorderSec = findViewById(R.id.etSecondBorderSec)
        etSecondBorderMs = findViewById(R.id.etSecondBorderMs)
        btnAddSecondBorderTime = findViewById(R.id.btnAddSecondBorderTime)
        rvSecondBorderTime = findViewById(R.id.rvSecondBorderTime)

        etThirdBorderSec = findViewById(R.id.etThirdBorderSec)
        etThirdBorderMs = findViewById(R.id.etThirdBorderMs)
        btnAddThirdBorderTime = findViewById(R.id.btnAddThirdBorderTime)
        rvThirdBorderTime = findViewById(R.id.rvThirdBorderTime)

        etFourthBorderSec = findViewById(R.id.etFourthBorderSec)
        etFourthBorderMs = findViewById(R.id.etFourthBorderMs)
        btnAddFourthBorderTime = findViewById(R.id.btnAddFourthBorderTime)
        rvFourthBorderTime = findViewById(R.id.rvFourthBorderTime)

        etSecond4 = findViewById(R.id.etSecond4)
        etMilli4 = findViewById(R.id.etMilli4)
        btnAddTime4 = findViewById(R.id.btnAddTime4)
        rvTimeList4 = findViewById(R.id.rvTimeList4)

        wordList = Utils.loadWordList(this)
        timeList1 = Utils.loadTimeList(this, "time_list_1")
        timeList2 = Utils.loadTimeList(this, "time_list_2")
        timeList3 = Utils.loadTimeList(this, "time_list_3")
        timeList4 = Utils.loadTimeList(this, "time_list_4")
        firstBorderTimeList = Utils.loadTimeList(this, "first_border_time_list")
        secondBorderTimeList = Utils.loadTimeList(this, "second_border_time_list")
        thirdBorderTimeList = Utils.loadTimeList(this, "third_border_time_list")
        fourthBorderTimeList = Utils.loadTimeList(this, "fourth_border_time_list")

        val etWord = findViewById<EditText>(R.id.etWord)
        val btnAddWord = findViewById<Button>(R.id.btnAddWord)
        val rvWordList = findViewById<RecyclerView>(R.id.rvWordList)
        wordAdapter = WordAdapter(wordList)
        rvWordList.layoutManager = LinearLayoutManager(this)
        rvWordList.adapter = wordAdapter

        btnAddWord.setOnClickListener {
            val input = etWord.text.toString().trim()
            if (input.isNotEmpty()) {
                val lines = input.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
                var added = 0
                for (line in lines) {
                    if (wordList.size < 1000) {
                        wordList.add(line)
                        added++
                    } else break
                }
                wordAdapter.notifyDataSetChanged()
                Utils.saveWordList(this, wordList)
                etWord.text.clear()
                Toast.makeText(this, "$added word(s)/sentence(s) added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Enter word(s) or sentence(s)!", Toast.LENGTH_SHORT).show()
            }
        }

        wordAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() { Utils.saveWordList(this@ThirdActivity, wordList) }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) { Utils.saveWordList(this@ThirdActivity, wordList) }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) { Utils.saveWordList(this@ThirdActivity, wordList) }
        })

        // Section 1
        val etMilli1 = findViewById<EditText>(R.id.etMilli1)
        val btnAddTime1 = findViewById<Button>(R.id.btnAddTime1)
        val rvTimeList1 = findViewById<RecyclerView>(R.id.rvTimeList1)
        timeAdapter1 = TimeAdapter(timeList1, 1) { pos ->
            if (pos >= 0 && pos < timeList1.size) {
                timeList1.removeAt(pos)
                timeAdapter1.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_1", timeList1)
            }
        }
        rvTimeList1.layoutManager = LinearLayoutManager(this)
        rvTimeList1.adapter = timeAdapter1
        btnAddTime1.setOnClickListener {
            val ms = etMilli1.text.toString().toIntOrNull() ?: 0
            if (timeList1.size < 100) {
                timeList1.add(TimeData(0, ms))
                timeAdapter1.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_1", timeList1)
                etMilli1.text.clear()
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        // Section 2
        val etSecond2 = findViewById<EditText>(R.id.etSecond2)
        val etMilli2 = findViewById<EditText>(R.id.etMilli2)
        val btnAddTime2 = findViewById<Button>(R.id.btnAddTime2)
        val rvTimeList2 = findViewById<RecyclerView>(R.id.rvTimeList2)
        timeAdapter2 = TimeAdapter(timeList2, 2) { pos ->
            if (pos >= 0 && pos < timeList2.size) {
                timeList2.removeAt(pos)
                timeAdapter2.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_2", timeList2)
            }
        }
        rvTimeList2.layoutManager = LinearLayoutManager(this)
        rvTimeList2.adapter = timeAdapter2
        btnAddTime2.setOnClickListener {
            val sec = etSecond2.text.toString().toIntOrNull() ?: 0
            val ms = etMilli2.text.toString().toIntOrNull() ?: 0
            if (timeList2.size < 100) {
                timeList2.add(TimeData(sec, ms))
                timeAdapter2.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_2", timeList2)
                etSecond2.text.clear()
                etMilli2.text.clear()
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        // Section 3
        val etSecond3 = findViewById<EditText>(R.id.etSecond3)
        val etMilli3 = findViewById<EditText>(R.id.etMilli3)
        val btnAddTime3 = findViewById<Button>(R.id.btnAddTime3)
        val rvTimeList3 = findViewById<RecyclerView>(R.id.rvTimeList3)
        timeAdapter3 = TimeAdapter(timeList3, 3) { pos ->
            if (pos >= 0 && pos < timeList3.size) {
                timeList3.removeAt(pos)
                timeAdapter3.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_3", timeList3)
            }
        }
        rvTimeList3.layoutManager = LinearLayoutManager(this)
        rvTimeList3.adapter = timeAdapter3
        btnAddTime3.setOnClickListener {
            val sec = etSecond3.text.toString().toIntOrNull() ?: 0
            val ms = etMilli3.text.toString().toIntOrNull() ?: 0
            if (timeList3.size < 100) {
                timeList3.add(TimeData(sec, ms))
                timeAdapter3.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_3", timeList3)
                etSecond3.text.clear()
                etMilli3.text.clear()
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        // Section 4
        timeAdapter4 = TimeAdapter(timeList4, 4) { pos ->
            if (pos >= 0 && pos < timeList4.size) {
                timeList4.removeAt(pos)
                timeAdapter4.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_4", timeList4)
            }
        }
        rvTimeList4.layoutManager = LinearLayoutManager(this)
        rvTimeList4.adapter = timeAdapter4
        btnAddTime4.setOnClickListener {
            val sec = etSecond4.text.toString().toIntOrNull() ?: 0
            val ms = etMilli4.text.toString().toIntOrNull() ?: 0
            if (timeList4.size < 100) {
                timeList4.add(TimeData(sec, ms))
                timeAdapter4.notifyDataSetChanged()
                Utils.saveTimeList(this, "time_list_4", timeList4)
                etSecond4.text.clear()
                etMilli4.text.clear()
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }

        // First Border
        setupBorderTimeSection(
            etSecondId = R.id.etFirstBorderSec,
            etMilliId = R.id.etFirstBorderMs,
            btnAddId = R.id.btnAddFirstBorderTime,
            rvId = R.id.rvFirstBorderTime,
            timeList = firstBorderTimeList,
            adapterSetter = { firstBorderTimeAdapter = it },
            key = "first_border_time_list",
            section = 6
        )

        // Second Border
        setupBorderTimeSection(
            etSecondId = R.id.etSecondBorderSec,
            etMilliId = R.id.etSecondBorderMs,
            btnAddId = R.id.btnAddSecondBorderTime,
            rvId = R.id.rvSecondBorderTime,
            timeList = secondBorderTimeList,
            adapterSetter = { secondBorderTimeAdapter = it },
            key = "second_border_time_list",
            section = 4
        )

        // Third Border
        setupBorderTimeSection(
            etSecondId = R.id.etThirdBorderSec,
            etMilliId = R.id.etThirdBorderMs,
            btnAddId = R.id.btnAddThirdBorderTime,
            rvId = R.id.rvThirdBorderTime,
            timeList = thirdBorderTimeList,
            adapterSetter = { thirdBorderTimeAdapter = it },
            key = "third_border_time_list",
            section = 5
        )

        // Fourth Border
        setupBorderTimeSection(
            etSecondId = R.id.etFourthBorderSec,
            etMilliId = R.id.etFourthBorderMs,
            btnAddId = R.id.btnAddFourthBorderTime,
            rvId = R.id.rvFourthBorderTime,
            timeList = fourthBorderTimeList,
            adapterSetter = { fourthBorderTimeAdapter = it },
            key = "fourth_border_time_list",
            section = 7
        )
    }

    private fun setupBorderTimeSection(
        etSecondId: Int,
        etMilliId: Int,
        btnAddId: Int,
        rvId: Int,
        timeList: MutableList<TimeData>,
        adapterSetter: (TimeAdapter) -> Unit,
        key: String,
        section: Int
    ) {
        val etSecond = findViewById<EditText>(etSecondId)
        val etMilli = findViewById<EditText>(etMilliId)
        val btnAdd = findViewById<Button>(btnAddId)
        val rv = findViewById<RecyclerView>(rvId)

        lateinit var localAdapter: TimeAdapter
        localAdapter = TimeAdapter(timeList, section) { pos ->
            if (pos >= 0 && pos < timeList.size) {
                timeList.removeAt(pos)
                localAdapter.notifyDataSetChanged()
                Utils.saveTimeList(this, key, timeList)
            }
        }
        adapterSetter(localAdapter)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = localAdapter

        btnAdd.setOnClickListener {
            val sec = etSecond.text.toString().toIntOrNull() ?: 0
            val ms = etMilli.text.toString().toIntOrNull() ?: 0
            if (timeList.size < 100) {
                timeList.add(TimeData(sec, ms))
                localAdapter.notifyDataSetChanged()
                Utils.saveTimeList(this, key, timeList)
                etSecond.text.clear()
                etMilli.text.clear()
            } else {
                Toast.makeText(this, "Limit reached!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}