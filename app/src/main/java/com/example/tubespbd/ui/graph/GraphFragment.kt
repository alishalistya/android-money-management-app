package com.example.tubespbd.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tubespbd.R
import com.example.tubespbd.databinding.FragmentGraphBinding
import com.github.mikephil.charting.charts.PieChart

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val graphViewModel =
            ViewModelProvider(this).get(GraphViewModel::class.java)

        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGraph
        graphViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val graphViewModel = ViewModelProvider(this).get(GraphViewModel::class.java)

        graphViewModel.pieChartData.observe(viewLifecycleOwner) { pieData ->
            val pieChart: PieChart = binding.pieChart.apply {
                data = pieData
                description.isEnabled = false
                isDrawHoleEnabled = false
                invalidate() // Refresh the chart
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}