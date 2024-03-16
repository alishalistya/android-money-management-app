package com.example.tubespbd.ui.graph

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class GraphViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _pieChartData = MutableLiveData<PieData>()
    val pieChartData: LiveData<PieData> = _pieChartData

    init {
        dummyGraph()
    }

    private fun dummyGraph(){
        val pieEntries = ArrayList<PieEntry>()
        val label = "type"

        // Initializing data
        val typeAmountMap = mapOf("Toys" to 200, "Snacks" to 230, "Clothes" to 100, "Stationary" to 500, "Phone" to 50)

        // Initializing colors for the entries
        val colors = listOf(
            Color.parseColor("#304567"),
            Color.parseColor("#309967"),
            Color.parseColor("#476567"),
            Color.parseColor("#890567"),
            Color.parseColor("#a35567"),
            Color.parseColor("#ff5f67"),
            Color.parseColor("#3ca567")
        )

        // Input data and fit data into pie chart entry
        typeAmountMap.forEach { (type, amount) ->
            pieEntries.add(PieEntry(amount.toFloat(), type))
        }

        // Collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label).apply {
            valueTextSize = 12f
            setColors(colors)
        }

        val pieData = PieData(pieDataSet).apply {
            setDrawValues(true)
        }

        // Post the pieData to LiveData
        _pieChartData.postValue(pieData)
    }
}