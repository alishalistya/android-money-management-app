package com.example.tubespbd.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tubespbd.App
import com.example.tubespbd.databinding.FragmentGraphBinding
import com.github.mikephil.charting.charts.PieChart

class GraphFragment : Fragment() {
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!
    private val graphViewModel: GraphViewModel by viewModels {
        GraphViewModelFactory((requireActivity().application as App).transactionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graphViewModel.pieChartData.observe(viewLifecycleOwner) { pieData ->
            val pieChart: PieChart = binding.pieChart.apply {
                data = pieData
                description.isEnabled = false
                isDrawHoleEnabled = false
                invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}