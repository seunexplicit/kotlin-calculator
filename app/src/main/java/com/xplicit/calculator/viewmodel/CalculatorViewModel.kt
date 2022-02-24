package com.xplicit.calculator.viewmodel


import android.content.Context
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.lifecycle.*
import com.xplicit.calculator.BasicArithmetic
import com.xplicit.calculator.MathComputation
import com.xplicit.calculator.R
import com.xplicit.calculator.data.OperatorMap
import com.xplicit.calculator.data.Operators
import com.xplicit.calculator.ui.SMALL_TEXT_LENGTH
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
    val displayExp: LiveData<Spanned> =
        _displayExp.map { HtmlCompat.fromHtml(it ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY) }

    private val _trigMode: MutableLiveData<TRIGMODE> = MutableLiveData(TRIGMODE.TRIG)
    val trigMode: LiveData<TRIGMODE> = _trigMode

    private val _activeSpecialOperator: MutableLiveData<OperatorMap?> = MutableLiveData()
    val activeSpecialOperator: LiveData<OperatorMap?> = _activeSpecialOperator

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _degRad: MutableLiveData<DEGRAD> = MutableLiveData(DEGRAD.DEG)
    val degRad: LiveData<DEGRAD> = _degRad

    private val _result: MutableLiveData<String?> = MutableLiveData()
    val result: LiveData<String> = _result.map { it ?: "" }

    private val scope = CoroutineScope(Job())

    private val operators = Operators(context)

    fun clearExp() {
        _exp.value = ""
        _displayExp.value = ""
        _result.value = null
        removeSpecialOperator()
    }

    fun appendExp(value: String, displayValue: String?) {
        val exp = if (_exp.value?.isNotBlank() == true) _exp.value.plus(value) else value
        val pattern =
            if (_activeSpecialOperator.value == null) MathComputation.EXP_PATTERN.matcher(exp)
            else {
                if(value==context.getString(R.string.close_bracket)){
                    setSpecialOperator(_activeSpecialOperator.value!!)
                }
                MathComputation.NUMBER_PATTERN.matcher(value)
            }
        if (pattern.matches()) {
            _exp.value = exp
            val displayVal = if (displayValue.isNullOrBlank()) value else displayValue
            _displayExp.value = if (_displayExp.value.isNullOrBlank())
                displayVal else
                _displayExp.value.plus(displayVal)
            scope.launch {
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
            if ((expLen - operatorComputedLen) == operatorExpIndex && operatorExpIndex != -1) {
                val operatorDisplayLen = operator.value.ComputedOperator.length
                val displayExpLen = _displayExp.value!!.length
                _exp.value = _exp.value?.removeRange(
                    operatorExpIndex,
                    operatorExpIndex + operatorComputedLen
                )
                _displayExp.value = _displayExp.value?.removeRange(
                    displayExpLen - operatorDisplayLen,
                    displayExpLen
                )
                scope.launch {
                    calculate()
                }
                return
            }
        }
        if (_exp.value?.last() == '(' && _activeSpecialOperator.value != null) {
            removeSpecialOperator()
        }
        _exp.value = _exp.value?.dropLast(1)
        _displayExp.value = _displayExp.value?.dropLast(1)
        scope.launch {
            calculate()
        }
    }

    fun equalTo() {
        _exp.value = ""
        _displayExp.value = ""
        val computation = BasicArithmetic(SMALL_TEXT_LENGTH)
        if(!_result.value.isNullOrBlank())
            appendExp("${computation.convertToBigDecimal(_result.value!!)}", null)
        _result.value = null
    }

    suspend fun calculate() {
        if (_exp.value?.isNotBlank() == true) {
            val compResult = MathComputation(_exp.value!!, degRad.value!!)
            withContext(Dispatchers.Main) {
                _result.value = compResult.finalResult
                if (!compResult.errorMessage.isNullOrBlank()) {
                    _errorMessage.value = compResult.errorMessage
                    compResult.resetErrorMessage()
                    delay(ERROR_TIMEOUT)
                    _errorMessage.value = ""
                }

            }

        } else {
            withContext(Dispatchers.Main) {
                _result.value = null
            }

        }
    }

    fun changeAngleFormat() {
        _degRad.value = if (degRad.value == DEGRAD.DEG) DEGRAD.RAD else DEGRAD.DEG
        scope.launch {
            calculate()
        }
    }

    fun changeTrigFormat() {
        _trigMode.value = if (_trigMode.value == TRIGMODE.TRIG) TRIGMODE.ARC_TRIG else TRIGMODE.TRIG
        scope.launch {
            calculate()
        }
    }

    fun setSpecialOperator(operatorMap: OperatorMap) {
        if (_activeSpecialOperator.value == null) {
            appendExp(context.getString(R.string.open_bracket), null)
            _activeSpecialOperator.value = operatorMap
        } else {
            val activeSpecial = _activeSpecialOperator.value!!
            removeSpecialOperator()
            appendExp(
                activeSpecial.ComputedOperator,
                "${activeSpecial.DisplayOperator}"
            )

        }
    }

    private fun removeSpecialOperator() {
        _activeSpecialOperator.value = null
    }

    companion object {
        const val ERROR_TIMEOUT = 9_000L
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