package com.example.tubespbd.ui.graph


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.example.tubespbd.database.TransactionRepository
import com.example.tubespbd.database.TransactionSum
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class GraphViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    private val _allTransactions: LiveData<List<TransactionSum>> = transactionRepository.getTransactionsSumByCategoryLiveData()

    val pieChartData: LiveData<PieData> = _allTransactions.map { transactionSums ->
        val pieEntries = ArrayList<PieEntry>()
        val colors = mutableListOf<Int>()

        transactionSums.forEachIndexed() { index, transactionSum ->
            pieEntries.add(PieEntry(transactionSum.amount.toFloat(), transactionSum.category))
            colors.add(ColorTemplate.PASTEL_COLORS[index % ColorTemplate.PASTEL_COLORS.size])
        }

        val pieDataSet = PieDataSet(pieEntries, "Transaction Categories").apply {
            valueTextSize = 12f
            setColors(colors)
        }

        PieData(pieDataSet).apply {
            setDrawValues(true)
        }
    }
}


class GraphViewModelFactory(private val transactionRepository : TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GraphViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GraphViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}