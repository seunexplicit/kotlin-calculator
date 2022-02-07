package com.example.calculator

import android.util.Log
import com.example.calculator.viewmodel.DEGRAD
import com.example.calculator.viewmodel.TRIGMODE
import java.lang.ArithmeticException
import java.lang.Math.toDegrees
import  java.lang.Math.toRadians
import java.util.regex.Pattern
import kotlin.math.*
import java.lang.Exception as Exception1

class MathComputation(Exp: String, val degrad: DEGRAD) {

    private lateinit var _finalResult: Number
    val finalResult: Number
        get() {
            val intResult = this._finalResult?.toInt()
            val doubleResult = this._finalResult?.toDouble()
            return if((doubleResult.minus(intResult))==0.0) {
                this._finalResult.toInt()
            } else this._finalResult.toDouble()
        }

    lateinit var errorMessage: String

    init {
        this._finalResult = calculate(Exp)
    }


    private fun simpleArithmeticOps(exp: String): Double {
        try {
            val simpleRegex: Pattern = Pattern.compile("^-?\\d+\\.?\\d*$")
            var result: Double? = null
            val operation: Char
            val values: List<String>
            when {
                "%" in exp -> {
                    values = exp.split('%')
                    operation = '%'
                }
                "+" in exp -> {
                    values = exp.split('+')
                    operation = '+'
                }
                "-" in exp -> {
                    values = exp.split("-")
                    operation = '-'
                }
                "x" in exp -> {
                    values = exp.split("x")
                    operation = 'x'
                }
                else -> {
                    values = exp.split('/')
                    operation = '/'
                }
            }
            values.forEach {
                if (result === null && it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                        .matches()
                ) it.toDouble() else simpleArithmeticOps(it)
                else if (result === null && it.isEmpty()) result = 0.0
                else if (result !== null) {
                    when (operation) {
                        '/' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) result!! / it.toDouble() else result!! / simpleArithmeticOps(it)
                        }
                        'x' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) result!! * it.toDouble() else result!! * simpleArithmeticOps(it)
                        }
                        '+' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) result!! + it.toDouble() else result!! + simpleArithmeticOps(it)
                        }
                        '%' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) result!! % it.toDouble() else result!! % simpleArithmeticOps(it)
                        }
                        '-' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) result!! - it.toDouble() else result!! - simpleArithmeticOps(it)
                        }
                    }
                }
            }

            return result!!
        } catch (e: ArithmeticException) {
            Log.d("Error Arithemtic", e.message ?: "")
            return 0.0
        } catch (e: Exception1) {
            Log.d("Error Exception", e.message ?: "")
            return 0.0
        }
    }

    private fun calculate(exp: String): Double {
        try {
            var complexOperations = exp

            //replace every constant π with Math.PI value
            while(complexOperations.contains("π")){
                val index = complexOperations.indexOf("π")
                complexOperations = appendResultToExp(complexOperations, Math.PI.toString(), index, index+1)
            }

            var actualExp: String
            while (complexOperations.contains("(") || complexOperations.contains(")")) {
                var index = 0
                var openIndex = -1
                for (char in complexOperations) {
                    if (char == '(') openIndex = index
                    else if (char == ')') {
                        if (openIndex == -1) {
                            complexOperations = complexOperations.removeRange(index, index + 1)
                            break
                        }
                        actualExp = complexOperations.substring(openIndex + 1, index)
                        actualExp = allocComputation(actualExp)
                        if (errorMessage.isNotBlank()) return 0.0
                        complexOperations =
                            appendResultToExp(actualExp, complexOperations, openIndex, index + 1)
                        break
                    }
                    index++
                }
            }
            complexOperations = allocComputation(complexOperations)
            return if (NUMBER_PATTERN.matcher(complexOperations).matches())
                complexOperations.toDouble()
            else
                0.0
        } catch (err: ArithmeticException) {
            errorMessage = "A computational error occur"
            return 0.0
        } catch (err: Exception) {
            errorMessage = "An error occur"
            return 0.0
        }
    }

    private fun allocComputation(exp: String): String {
        var result = exp
        if (XRT_POW_PATTERN.matcher(result).find()) {
            result = xRootPowCalculation(result)
        }
        if (FACTORIAL_PATTERN.matcher(result).find()) {
            result = factorialCalculation(result)
        }
        if (TRIG_PATTERN.matcher(result).find()) {
            result = trigArithmetic(result)
        }
        return "${simpleArithmeticOps(result)}"
    }

    private fun trigArithmetic(expression: String): String {
        var trigExp = expression
        val match = TRIG_NUMBER_PATTERN.matcher(trigExp)
        while (match.find()) {
            val _exp = match.group(1)
            val exp = _exp!!
            val startIndex = match.start(1)
            val endIndex = match.end(1)
            val numberValue = getTrigValue(exp)
            if (numberValue.isNullOrBlank()) trigExp =
                appendResultToExp("", trigExp, startIndex, endIndex)
            else {
                val doubleNumberValue = numberValue.toDouble()
                var result: Double
                when {
                    "atanh" in exp -> {
                        if (doubleNumberValue > 1 || doubleNumberValue < -1) {
                            errorMessage = "hyperbolic tangent value should be >=-1 and <=1"
                            return ""
                        } else if (doubleNumberValue.toInt().absoluteValue == 1) {
                            errorMessage = "Infinity"
                            return ""
                        }
                        result = radToDeg(atanh(doubleNumberValue))
                    }
                    "acosh" in exp -> {
                        if (doubleNumberValue < 1) {
                            errorMessage = "hyperbolic cosine value should be >1"
                        }
                        result = radToDeg(acosh(doubleNumberValue))
                    }
                    "asinh" in exp -> {
                        result = radToDeg(asinh(doubleNumberValue))
                    }
                    "cosh" in exp -> {
                        result = cosh(degToRad(doubleNumberValue))
                    }
                    "sinh" in exp -> {
                        result = sinh(degToRad(doubleNumberValue))
                    }
                    "tanh" in exp -> {
                        result = tanh(degToRad(doubleNumberValue))
                    }
                    "atan" in exp -> {
                        result = radToDeg(atan(doubleNumberValue))
                    }
                    "acos" in exp -> {
                        result = radToDeg(acos(doubleNumberValue))
                    }
                    "asin" in exp -> {
                        result = radToDeg(asin(doubleNumberValue))
                    }
                    "cos" in exp -> {
                        result = cos(degToRad(doubleNumberValue))
                    }
                    "sin" in exp -> {
                        result = sin(degToRad(doubleNumberValue))
                    }
                    "tan" in exp -> {
                        result = tan(degToRad(doubleNumberValue))
                    }
                    "fib" in exp->{
                        result = fibonacci(round(doubleNumberValue).toInt()).toDouble()
                        if(errorMessage.isNotBlank()) return ""
                    }
                    "ln" in exp -> {
                        if (doubleNumberValue < 0) {
                            errorMessage = "natural logarithm value should be >=0"
                            return ""
                        } else if (doubleNumberValue.equals(0.0)) {
                            errorMessage = "Infinity"
                            return ""
                        }
                        result = ln(doubleNumberValue)
                    }
                    "e" in exp -> {
                        result = exp(doubleNumberValue)
                    }
                    else -> {
                        if (doubleNumberValue < 0) {
                            errorMessage = "square root value should be >=0"
                            return ""
                        }
                        result = sqrt(doubleNumberValue)
                    }
                }
                trigExp =
                    appendResultToExp(result.toString(), trigExp, startIndex, endIndex)
            }
        }

        return trigExp
    }

    private fun factorialCalculation(exp: String): String {
        val match = FACTORIAL_PATTERN.matcher(exp)
        var answer = exp
        while (match.find()) {
            val factorialExp = match.group(1)
            val startIndex = match.start(1)
            val endIndex = match.end(1)
            val numberValue = NUMBER_PATTERN.matcher(factorialExp).group(1).toDouble()
            answer = appendResultToExp(
                factorial(numberValue.roundToInt()).toString(),
                answer,
                startIndex,
                endIndex
            )
            if (errorMessage.isNotBlank()) return ""
        }

        return answer
    }

    private fun xRootPowCalculation(exp: String): String {
        var expAnswer = exp
        val match = XRT_POW_PATTERN.matcher(exp)
        while (match.find()) {
            val xrtExp = match.group(1)
            val startIndex = match.start(1)
            val endIndex = match.end(1)
            val xrtValues = xrtExp?.split("(xrt|pow)".toRegex())
            if (xrtValues?.get(1)?.isNotBlank() == true) xrtValues[1].also {
                val answer = if (xrtExp.contains("xrt")) xrtValues[1].toDouble()
                    .pow(1 / xrtValues[0].toDouble())
                else xrtValues[1].toDouble().pow(xrtValues[0].toDouble())
                expAnswer = appendResultToExp(answer.toString(), expAnswer, startIndex, endIndex)
            } else appendResultToExp("", expAnswer, startIndex, endIndex)

        }

        return expAnswer
    }

    private fun fibonacci(value: Int): Int {
        if (value < 0) {
            errorMessage = "Fibonacci value must be a positive number"
            return 0
        } else if (value > 250) {
            errorMessage = "Fibonacci value too large"
            return 0
        }
        if (value == 0) return 0
        if (value == 1) return 1
        if (value == 2) return 1

        return fibonacci(value - 1) + fibonacci(value - 2)
    }

    private fun factorial(value: Int): Int {
        if (value.absoluteValue > 9000) {
            errorMessage = "Factorial value too large"
            return 0
        }
        if (value == 0) return 0
        else if (value.absoluteValue == 1) {
            if (value < 0) return -1
            return 1
        }
        return value * factorial(value.absoluteValue - 1)
    }

    private fun appendResultToExp(
        result: String,
        exp: String,
        startIndex: Int,
        endIndex: Int
    ): String {
        return if (startIndex == 0 && exp.length == endIndex) result
        else {
            val startExp = exp.substring(0..startIndex)
            val endExp = exp.substring(endIndex..exp.length)
            if (END_WITH_PATTERN.matcher(startExp).find()) startExp.plus("x${result}")
            else startExp.plus(result)
            if (START_WITH_PATTERN.matcher(endExp).find()) startExp.plus("x${endExp}")
            else startExp.plus(endExp)

            startExp
        }
    }

    private fun getTrigValue(exp: String): String? {
        val match = NUMBER_PATTERN.matcher(exp);
        if (match.group(1).isNullOrBlank()) return null
        return match.group(1)
    }

    private fun degToRad(value: Double): Double {
        return if (degrad == DEGRAD.DEG) toRadians(value)
        else value
    }

    private fun radToDeg(value: Double): Double {
        return if (degrad == DEGRAD.DEG) toDegrees(value)
        else value
    }

    fun resetErrorMessage() {
        errorMessage = ""
    }


    companion object {
        val EXP_PATTERN: Pattern =
            Pattern.compile("^(((cos|sin|tan|acos|asin|atan|cosh|sinh|tanh|acosh|(?<=(\\d|\\)))(xrt|pow)|asinh|atanh|ln|log|exp|sqrt|fib)?\\()*((-?\\d+(?<!\\.\\d{0,64})(\\.?\\d*)|π))*((?<=\\(.{0,64})(?<![(+-/%x])\\)?)?((?<=(\\)|\\d|π))(?<!\\.{2})[+-/!x%])?)*$")
        val TRIG_PATTERN: Pattern =
            Pattern.compile("(cos|sin|tan|acos|asin|atan|cosh|sinh|tanh|acosh|asinh|atanh|ln|log|exp|sqrt|fib)")
        val TRIG_NUMBER_PATTERN: Pattern =
            Pattern.compile("(cos|sin|tan|acos|asin|atan|cosh|sinh|tanh|acosh|asinh|atanh|ln|log|exp|sqrt|fib)(\\(?-?\\d*|-?\\d+)\\.?\\d*")
        val NUMBER_PATTERN: Pattern = Pattern.compile("-?\\d*\\.?\\d+")
        val FACTORIAL_PATTERN: Pattern = Pattern.compile("-?\\d*\\.?\\d*!")

        val END_WITH_PATTERN: Pattern = Pattern.compile("(-?\\d*\\.?\\d+|\\)|!)$")

        val XRT_POW_PATTERN: Pattern = Pattern.compile("-?\\d*\\.?\\d*(xrt|pow)-?\\d*\\.?\\d*")

        val START_WITH_PATTERN: Pattern =
            Pattern.compile("^(cos|sin|tan|acos|asin|atan|-?\\d*\\.?\\d+(xrt|pow)?|cosh|sinh|tanh|acosh|asinh|atanh|ln|log|exp|sqrt|fib|\\()")
    }
}