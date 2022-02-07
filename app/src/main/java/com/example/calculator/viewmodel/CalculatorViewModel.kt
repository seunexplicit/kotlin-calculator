package com.example.calculator.viewmodel


import android.content.Context
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.annotation.Nullable
import androidx.lifecycle.*
import com.example.calculator.ui.fragments.CommonButton
import com.example.calculator.MathComputation
import com.example.calculator.data.Operators

enum class TRIGMODE{
    TRIG, ARC_TRIG
}

enum class OperatorButtons{
    COS, COSH, SIN, SINH, TAN, TANH, EXP, FIB, LN, LOG, SQRT, XSQRT
}

enum class DEGRAD{
    DEG, RAD
}

class CalculatorViewModel(val context: Context):ViewModel() {

    private val _exp:MutableLiveData<String> = MutableLiveData()
    val exp:LiveData<String> = _exp!!

    private val _displayExp:MutableLiveData<String> = MutableLiveData()
    val displayExp:LiveData<Spanned> = _displayExp.map { Html.fromHtml(it?:"") }

    private val _angleMode:MutableLiveData<String> = MutableLiveData()
    val angleMode:LiveData<String> = _angleMode

    private val _trigMode:MutableLiveData<TRIGMODE> = MutableLiveData(TRIGMODE.TRIG)
    val trigMode:LiveData<TRIGMODE> = _trigMode!!

    private val _degRad:MutableLiveData<DEGRAD> = MutableLiveData(DEGRAD.DEG)
    val degRad:LiveData<DEGRAD> = _degRad!!

    private val _activeOperatorButton:MutableLiveData<OperatorButtons> = MutableLiveData()
    val activeOperatorButton:LiveData<OperatorButtons> = _activeOperatorButton

    private val _result:MutableLiveData<Number?> = MutableLiveData()
    val result:LiveData<String> = _result.map { it?.toString() ?: "" }

    private val operators = Operators(context)

    fun clearExp(){
        _exp.value = ""
        _result.value = null
    }

    fun appendExp(value:String, displayValue:String?){
        val exp = if(_exp.value?.isNotBlank() == true) _exp.value.plus(value) else value
        if(MathComputation.EXP_PATTERN.matcher(exp).matches()) {
            _exp.value = exp
            _displayExp.value = if(_displayExp.value.isNullOrBlank())
                displayValue?:value else
                    _displayExp.value.plus(displayValue?:value)
            calculate()
        }

        Log.d(CommonButton.TAG, _exp.value.toString())
    }

    fun deleteExp() {
        if(exp.value.isNullOrBlank()) return

        val expLen = exp.value!!.length
        for(operator in operators.operatorMap()){
            val operatorExpIndex = exp.value?.lastIndexOf(operator.value.ComputedOperator)
            val operatorComputedLen = operator.value.ComputedOperator.length
            if((expLen - operatorComputedLen)==operatorExpIndex){
                val operatorDisplayLen = operator.value.ComputedOperator.length
                val displayExpLen = _displayExp.value!!.length
                _exp.value?.removeRange(operatorExpIndex, operatorExpIndex+operatorComputedLen)
                _displayExp.value?.removeRange(displayExpLen-operatorDisplayLen, displayExpLen)
                return calculate()

            }
        }
        _exp.value = _exp.value?.dropLast(1)
        calculate()
    }

    fun equalTo(){
        _exp.value  = _result.value.toString()
        _result.value = null
    }

    private fun calculate(){
        if(_exp.value?.isNotBlank() == true){
            val compResult = MathComputation(_exp.value!!, degRad.value!!)
            _result.value = compResult.finalResult
        }
        else{
            _result.value = null
        }
    }

    fun changeAngleFormat(format:String){
        _angleMode.value = format
    }

    fun changeTrigFormat(){
        _trigMode.value = if(_trigMode.value==TRIGMODE.TRIG) TRIGMODE.ARC_TRIG else TRIGMODE.TRIG
    }

    fun setActiveOperatorButton(button:OperatorButtons){
        _activeOperatorButton.value = button
    }

}

class CalculatorViewModelFactory(private val context:Context):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CalculatorViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}