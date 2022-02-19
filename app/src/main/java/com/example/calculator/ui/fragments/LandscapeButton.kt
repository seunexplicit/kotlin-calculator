package com.example.calculator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.calculator.R
import com.example.calculator.data.Operators
import com.example.calculator.databinding.FragmentLandscapeButtonBinding
import com.example.calculator.viewmodel.CalculatorViewModel
import com.example.calculator.viewmodel.CalculatorViewModelFactory


class LandscapeButton : Fragment() {
    // TODO: Rename and change types of parameters

    private val sharedViewModel: CalculatorViewModel by activityViewModels {
        CalculatorViewModelFactory(requireContext())
    }
    private lateinit var _binding:FragmentLandscapeButtonBinding
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
        _binding = FragmentLandscapeButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        operators = Operators(requireContext())
        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = this@LandscapeButton.viewLifecycleOwner

            sinButton.setOnClickListener {
                val operator  = operators.operatorMap()[sinButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            tanButton.setOnClickListener {
                val operator  = operators.operatorMap()[tanButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            cosButton.setOnClickListener {
                val operator  = operators.operatorMap()[cosButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            sinhButton.setOnClickListener {
                val operator  = operators.operatorMap()[sinhButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            tanhButton.setOnClickListener {
                val operator  = operators.operatorMap()[tanhButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            coshButton.setOnClickListener {
                val operator  = operators.operatorMap()[coshButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            expButton.setOnClickListener {
                val operator  = operators.operatorMap()[expButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            fibButton.setOnClickListener {
                val operator  = operators.operatorMap()[fibButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            lnButton.setOnClickListener {
                val operator  = operators.operatorMap()[lnButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            logButton.setOnClickListener {
                val operator  = operators.operatorMap()[logButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            sqrtButton.setOnClickListener {
                val operator  = operators.operatorMap()[sqrtButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            variableSqrtButton.setOnClickListener {
                val operator  = operators.operatorMap()[variableSqrtButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
            powButton.setOnClickListener {
                val operator  = operators.operatorMap()[powButton.text]
                sharedViewModel.appendExp(operator!!.ComputedOperator, "${operator.DisplayOperator}")
            }
        }
    }

    companion object {
        const val TAG="LANDSCAPE BUTTON FRAGMENT"
    }
}