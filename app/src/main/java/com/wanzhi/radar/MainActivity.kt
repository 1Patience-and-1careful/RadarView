package com.wanzhi.radar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.guanyc.somecustomview.view.path.RadarData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createTestData(et_count.text.toString())
        et_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    createTestData(p0.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        et_maxValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    rv_radar.updateMaxValue(p0.toString().toFloat())
                }
            }
        })
    }

    private fun createTestData(count: String) {
        var arrayListOf = arrayListOf<RadarData>()
        for (i in 0 until count.toInt()) {
            var radarData = RadarData("健康值$i", 1000 * i * 0.1f)
            arrayListOf.add(radarData)
        }
        rv_radar.data = arrayListOf
    }
}
