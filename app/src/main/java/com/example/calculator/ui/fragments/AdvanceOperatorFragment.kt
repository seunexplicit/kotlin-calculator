package com.example.calculator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.calculator.R
import com.example.calculator.data.OperatorMap
import com.example.calculator.data.Operators
import com.example.calculator.databinding.FragmentAdvanceOperatorBinding
import com.example.calculator.viewmodel.CalculatorViewModel
import com.example.calculator.viewmodel.CalculatorViewModelFactory
import com.example.calculator.viewmodel.OperatorButtons
import com.example.calculator.viewmodel.TRIGMODE


class AdvanceOperatorFragment : Fragment() {

    private val viewModel: CalculatorViewModel by activityViewModels {
        CalculatorViewModelFactory(requireContext())
    }
    private lateinit var _binding: FragmentAdvanceOperatorBinding
    private val binding get() = _binding!!
    private lateinit var operators: Operators

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdvanceOperatorBinding.inflate(inflater, container, false)
        _init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        operators = Operators(requireContext())
        binding.apply {
            viewModel = this@AdvanceOperatorFragment.viewModel
            radButton.setOnClickListener { changeAngleFormat() }
            sinButton.setOnClickListener {
                val operator  = operators.operatorMap()[sinButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            tanButton.setOnClickListener {
                val operator  = operators.operatorMap()[tanButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            cosButton.setOnClickListener {
                val operator  = operators.operatorMap()[cosButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            sinhButton.setOnClickListener {
                val operator  = operators.operatorMap()[sinhButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            tanhButton.setOnClickListener {
                val operator  = operators.operatorMap()[tanhButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            coshButton.setOnClickListener {
                val operator  = operators.operatorMap()[coshButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            expButton.setOnClickListener {
                val operator  = operators.operatorMap()[expButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            fibButton.setOnClickListener {
                val operator  = operators.operatorMap()[fibButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            lnButton.setOnClickListener {
                val operator  = operators.operatorMap()[lnButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            logButton.setOnClickListener {
                val operator  = operators.operatorMap()[logButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            sqrtButton.setOnClickListener {
                val operator  = operators.operatorMap()[sqrtButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
            variableSqrtButton.setOnClickListener {
                val operator  = operators.operatorMap()[variableSqrtButton.text]
                viewModel.appendExp(operator!!.ComputedOperator, operator.DisplayOperator)
            }
        }
    }

    private fun changeAngleFormat() {
        if (viewModel.angleMode.value == getString(R.string.rad)) viewModel.changeAngleFormat(
            getString(R.string.deg)
        )
        else viewModel.changeAngleFormat(getString(R.string.rad))
    }

    private fun _init() {
        if (viewModel.angleMode.value.isNullOrBlank()) viewModel.changeAngleFormat(getString(R.string.deg))
    }

}