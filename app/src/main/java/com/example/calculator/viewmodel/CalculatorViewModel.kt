package com.example.calculator.viewmodel


import android.content.Context
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import androidx.lifecycle.*
import com.example.calculator.MathComputation
import com.example.calculator.R
import com.example.calculator.data.Operators
import kotlinx.coroutines.*

enum class TRIGMODE {
    TRIG, ARC_TRIG
}

enum class DEGRAD {
    DEG, RAD
}

const val INFINITY = MathComputation.INFINITY

class CalculatorViewModel(val context: Context) : ViewModel() {

    private val _exp: MutableLiveData<String> = MutableLiveData()
    val exp: LiveData<String> = _exp

    private val _displayExp: MutableLiveData<String> = MutableLiveData()
    val displayExp: LiveData<Spanned> = _displayExp.map { HtmlCompat.fromHtml(it ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY) }

    private val _trigMode: MutableLiveData<TRIGMODE> = MutableLiveData(TRIGMODE.TRIG)
    val trigMode: LiveData<TRIGMODE> = _trigMode

    private val _errorMessage:MutableLiveData<String> = MutableLiveData()
    val errorMessage:LiveData<String> = _errorMessage

    private val _degRad: MutableLiveData<DEGRAD> = MutableLiveData(DEGRAD.DEG)
    val degRad: LiveData<DEGRAD> = _degRad

    private val _result: MutableLiveData<String?> = MutableLiveData()
    val result: LiveData<String> = _result.map { it ?: "" }

    private val operators = Operators(context)

    fun clearExp() {
        _exp.value = ""
        _displayExp.value = ""
        _result.value = null
    }

    fun appendExp(value: String, displayValue: String?) {
        val exp = if (_exp.value?.isNotBlank() == true) _exp.value.plus(value) else value
        if (MathComputation.EXP_PATTERN.matcher(exp).matches()) {
            _exp.value = exp
            val displayVal  = if(displayValue.isNullOrBlank()) value else displayValue
            _displayExp.value = if (_displayExp.value.isNullOrBlank())
                displayVal else
                _displayExp.value.plus(displayVal)

            viewModelScope.launch {
                calculate()
            }

        }


    }

    fun deleteExp() {
        if (exp.value.isNullOrBlank()) return

        val expLen = exp.value!!.length
        for (operator in operators.operatorMap()) {
            val operatorExpIndex = exp.value?.lastIndexOf(operator.value.ComputedOperator)
            val operatorComputedLen = operator.value.ComputedOperator.length
            if ((expLen - operatorComputedLen) == operatorExpIndex && operatorExpIndex!=-1) {
                val operatorDisplayLen = operator.value.ComputedOperator.length
                val displayExpLen = _displayExp.value!!.length
                _exp.value = _exp.value?.removeRange(operatorExpIndex, operatorExpIndex + operatorComputedLen)
                _displayExp.value = _displayExp.value?.removeRange(displayExpLen - operatorDisplayLen, displayExpLen)
                viewModelScope.launch {
                    calculate()
                }
                return
            }
        }
        _exp.value = _exp.value?.dropLast(1)
        _displayExp.value = _displayExp.value?.dropLast(1)
        viewModelScope.launch {
            calculate()
        }
    }

    fun equalTo() {
        _exp.value = ""
        _displayExp.value = ""
        appendExp(_result.value.toString(), null)
        _result.value = null
    }

    private suspend fun  calculate() {
        if (_exp.value?.isNotBlank() == true) {
            withContext(Dispatchers.Main){
                val compResult = MathComputation(_exp.value!!, degRad.value!!)
                _result.value = compResult.finalResult
                if(!compResult.errorMessage.isNullOrBlank()){
                    _errorMessage.value = compResult.errorMessage
                    compResult.resetErrorMessage()
                    delay(ERROR_TIMEOUT)
                    _errorMessage.value = ""
                }

            }

        } else {
            _result.value = null
        }
    }

    fun changeAngleFormat() {
        _degRad.value = if (degRad.value == DEGRAD.DEG) DEGRAD.RAD else DEGRAD.DEG
        viewModelScope.launch {
            calculate()
        }
    }

    fun changeTrigFormat() {
        _trigMode.value = if (_trigMode.value == TRIGMODE.TRIG) TRIGMODE.ARC_TRIG else TRIGMODE.TRIG
        viewModelScope.launch {
            calculate()
        }
    }

    companion object{
        const val  ERROR_TIMEOUT = 9_000L
    }

}

class CalculatorViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}