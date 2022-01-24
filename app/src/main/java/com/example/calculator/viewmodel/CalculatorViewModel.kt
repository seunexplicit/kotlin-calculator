package com.example.calculator.viewmodel

import android.util.Log
import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.calculator.ui.fragments.CommonButton
import com.example.calculator.MathComputation

class CalculatorViewModel:ViewModel() {

    private val _exp:MutableLiveData<String> = MutableLiveData()
    val exp:LiveData<String> = _exp

    @Nullable
    private val _result:MutableLiveData<Number?> = MutableLiveData()
    val result:LiveData<String> = _result.map { it?.toString() ?: "" }

    fun clearExp(){
        _exp.value = ""
        _result.value = null
    }

    fun appendExp(value:String){
        val exp = if(_exp.value?.isNotBlank() == true) _exp.value.plus(value) else value
        Log.d("${CommonButton.TAG}0", "$exp - $value")
        if(MathComputation.EXP_PATTERN.matcher(exp).matches()) {
            Log.d("${CommonButton.TAG}12","Message")
            _exp.value = exp
            calculate()
        }

        Log.d(CommonButton.TAG, _exp.value.toString())
    }

    fun deleteExp(){
        _exp.value = _exp.value?.dropLast(1)
        calculate()
    }

    fun equalTo(){
        _exp.value  = _result.value.toString()
        _result.value = null
    }

    private fun calculate(){
        if(_exp.value?.isNotBlank() == true){
            val compResult = MathComputation(_exp.value!!)
            _result.value = compResult.finalResult
        }
        else{
            _result.value = null
        }
    }

}