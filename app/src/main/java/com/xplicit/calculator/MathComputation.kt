package com.xplicit.calculator


import com.xplicit.calculator.viewmodel.DEGRAD
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import java.math.BigDecimal
import java.util.regex.Pattern
import kotlin.math.*
import kotlin.Exception as Exception1

class MathComputation(Exp: String, val degrad: DEGRAD) {

    private var _finalResult: BigDecimal? = null
    val computation = BasicArithmetic()

    val finalResult: String
        get() {
            if (_finalResult == null) {
                return ""
            }
            return "${computation.getNonDecimal(_finalResult!!) ?: _finalResult}"
        }

    var errorMessage: String? = null

    init {
        this._finalResult = calculate(Exp)
    }


    private fun simpleArithmeticOps(expression: String): String {
        try {
            if (expression.isNullOrBlank()) return ""
            val exp = expression.replace("--", "+")
            val simpleRegex = NUMBER_PATTERN
            var result: BigDecimal? = null
            val operation: Char
            val values: List<String>
            when {
                "%" in exp -> {
                    values = exp.split('%')
                    operation = '%'
                }
                exp.contains("(?<!E)\\+".toRegex()) -> {
                    values = exp.split("(?<!E)\\+".toRegex())
                    operation = '+'
                }
                exp.contains("(?<!(E|x|/))-".toRegex()) -> {
                    values = exp.split("(?<!E)-".toRegex())
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
                ) computation.convertToBigDecimal(it) else {
                    computation.convertToBigDecimal(simpleArithmeticOps(it))
                }
                else if (result === null && it.isEmpty()) result = BigDecimal.ZERO
                else if (result !== null) {
                    when (operation) {
                        '/' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) computation.divide(result!!, it) else computation.divide(
                                result!!,
                                simpleArithmeticOps(it)
                            )
                        }
                        'x' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) computation.multiply(result!!, it) else computation.multiply(
                                result!!,
                                simpleArithmeticOps(it)
                            )
                        }
                        '+' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) computation.plus(result!!, it) else computation.plus(
                                result!!,
                                simpleArithmeticOps(it)
                            )
                        }
                        '%' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) computation.modulus(result!!, it) else computation.modulus(
                                result!!,
                                simpleArithmeticOps(it)
                            )
                        }
                        '-' -> {
                            if (it.isNotEmpty()) result = if (simpleRegex.matcher(it)
                                    .matches()
                            ) computation.minus(result!!, it) else computation.minus(
                                result!!,
                                simpleArithmeticOps(it)
                            )
                        }
                    }
                }
            }

            return "$result"
        } catch (e: ArithmeticException) {
            return ""
        } catch (e: Exception1) {
            return ""
        }
    }

    private fun calculate(exp: String): BigDecimal? {
        try {
            var complexOperations = exp

            //replace every constant π with Math.PI value
            while (complexOperations.contains("π")) {
                val index = complexOperations.indexOf("π")
                complexOperations =
                    appendResultToExp(Math.PI.toString(), complexOperations, index, index + 1)
            }
            while (complexOperations.contains("exp")) {
                val index = complexOperations.indexOf("exp")
                complexOperations =
                    appendResultToExp(Math.E.toString(), complexOperations, index, index + 3)
            }

            var actualExp: String
            while (complexOperations.contains("(") || complexOperations.contains(")")) {
                var index = 0
                var openIndex = -1
                for (char in complexOperations) {
                    if (char == '(') openIndex = index
                    if (char == ')') {
                        if (openIndex == -1) {
                            complexOperations =
                                appendResultToExp("", complexOperations, index, index + 1)
                            break
                        }
                        actualExp = if (openIndex == index) "" else complexOperations.substring(
                            openIndex + 1,
                            index
                        )
                        actualExp = allocComputation(actualExp)
                        if (errorMessage?.isNotBlank() == true) return null
                        complexOperations =
                            appendResultToExp(actualExp, complexOperations, openIndex, index + 1)
                        break
                    } else if ((index + 1) == complexOperations.length) {
                        actualExp = if (openIndex == index) "" else complexOperations.substring(
                            openIndex + 1,
                            index + 1
                        )
                        actualExp = allocComputation(actualExp)
                        if (errorMessage?.isNotBlank() == true) return null
                        complexOperations =
                            appendResultToExp(actualExp, complexOperations, openIndex, index + 1)
                        break
                    }

                    index++
                }
            }
            complexOperations = allocComputation(complexOperations)
            return if (RESULT_PATTERN.matcher(complexOperations).matches()) {
                computation.convertToBigDecimal(complexOperations)
            } else
                null
        } catch (err: ArithmeticException) {
            errorMessage = "A computational error occur"
            return null
        } catch (err: Exception) {
            errorMessage = "An error occur"
            return null
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
        return simpleArithmeticOps(result)
    }

    private fun trigArithmetic(expression: String): String {
        var trigExp = expression
        var match = TRIG_NUMBER_PATTERN.matcher(trigExp)
        while (match.find()) {

            val _exp = match.group(0)
            val exp = _exp!!
            val startIndex = match.start(0)
            val endIndex = match.end(0)
            val numberValue = getTrigValue(exp)
            if (numberValue.isNullOrBlank()) trigExp =
                appendResultToExp("", trigExp, startIndex, endIndex)
            else {
                val doubleNumberValue = numberValue.toDouble()
                var result: String
                when {
                    "atanh" in exp -> {
                        if (doubleNumberValue > 1 || doubleNumberValue < -1) {
                            errorMessage = "hyperbolic tangent value should be >=-1 and <=1"
                            return ""
                        } else if (doubleNumberValue.toInt().absoluteValue == 1) {
                            errorMessage = INFINITY
                            return ""
                        }
                        result = "${radToDeg(atanh(doubleNumberValue))}"
                    }
                    "acosh" in exp -> {
                        if (doubleNumberValue < 1) {
                            errorMessage = "hyperbolic cosine value should be >1"
                            return ""
                        }
                        result = "${radToDeg(acosh(doubleNumberValue))}"
                    }
                    "asinh" in exp -> {
                        result = "${radToDeg(asinh(doubleNumberValue))}"
                    }
                    "cosh" in exp -> {
                        result = "${cosh(degToRad(doubleNumberValue))}"
                    }
                    "sinh" in exp -> {
                        result = "${sinh(degToRad(doubleNumberValue))}"
                    }
                    "tanh" in exp -> {
                        result = "${tanh(degToRad(doubleNumberValue))}"
                    }
                    "atan" in exp -> {
                        result = "${radToDeg(atan(doubleNumberValue))}"
                    }
                    "acos" in exp -> {
                        if (doubleNumberValue.absoluteValue > 1) {
                            errorMessage = "value should be between 1 and -1"
                            return ""
                        }
                        result = "${radToDeg(acos(doubleNumberValue))}"
                    }
                    "asin" in exp -> {
                        if (doubleNumberValue.absoluteValue > 1) {
                            errorMessage = "value should be between 1 and -1"
                            return ""
                        }
                        result = "${radToDeg(asin(doubleNumberValue))}"
                    }
                    "cos" in exp -> {
                        result = "${cos(degToRad(doubleNumberValue))}"
                        val angleInDegree = toDegrees(degToRad(doubleNumberValue))
                        val angleRatio = angleInDegree / 90
                        if (angleRatio % 2 == 1.0) {
                            result = "0.0"
                        }
                    }
                    "sin" in exp -> {
                        result = "${sin(degToRad(doubleNumberValue))}"
                        val angleInDegree = toDegrees(degToRad(doubleNumberValue))
                        if (angleInDegree % 180 == 0.0) {
                            result = "0.0"
                        }
                    }
                    "tan" in exp -> {
                        result = "${tan(degToRad(doubleNumberValue))}"
                        val cosAngleInDegreeRatio = toDegrees(degToRad(doubleNumberValue)) / 90
                        val sinAngleInDegree = toDegrees(degToRad(doubleNumberValue))
                        if (cosAngleInDegreeRatio % 2 == 1.0) {
                            errorMessage = INFINITY
                            return ""
                        }
                        if (sinAngleInDegree % 180 == 0.0) {
                            result = "0.0"
                        }

                    }
                    "fib" in exp -> {
                        val res = mutableListOf<BigDecimal>(BigDecimal.ZERO, BigDecimal.ZERO)
                        fibonacci(round(doubleNumberValue).toInt(), res)
                        result = "${res[0]}"
                        if (errorMessage?.isNotBlank() == true) return ""
                    }
                    "ln" in exp -> {
                        if (doubleNumberValue < 0) {
                            errorMessage = "natural logarithm value should be >=0"
                            return ""
                        } else if (doubleNumberValue.equals(0.0)) {
                            errorMessage = INFINITY
                            return ""
                        }
                        result = "${ln(doubleNumberValue)}"
                    }
                    "xpon" in exp -> {
                        result = "${exp(doubleNumberValue)}"
                    }
                    "log" in exp ->{
                        result = "${log10(doubleNumberValue)}"
                    }
                    else -> {
                        if (doubleNumberValue < 0) {
                            errorMessage = "square root value should be >=0"
                            return ""
                        }
                        result = "${sqrt(doubleNumberValue)}"
                    }
                }

                val trigComp = BasicArithmetic(15)
                trigExp =
                    appendResultToExp(
                        trigComp.convertToBigDecimal(result).toString(),
                        trigExp,
                        startIndex,
                        endIndex
                    )
            }

            match = TRIG_NUMBER_PATTERN.matcher(trigExp)
        }

        return trigExp
    }

    private fun factorialCalculation(exp: String): String {
        val match = FACTORIAL_PATTERN.matcher(exp)
        var answer = exp
        while (match.find()) {
            val factorialExp = match.group(0)
            val startIndex = match.start(0)
            val endIndex = match.end(0)
            val numberValue = getTrigValue(factorialExp)?.toDouble()
            answer = appendResultToExp(
                factorial(numberValue!!.roundToInt()).toString(),
                answer,
                startIndex,
                endIndex
            )
            if (errorMessage?.isNotBlank() == true) return ""
        }

        return answer
    }

    private fun xRootPowCalculation(exp: String): String {
        var expAnswer = exp
        var match = XRT_POW_PATTERN.matcher(expAnswer)
        while (match.find()) {
            val xrtExp = match.group(0)
            val startIndex = match.start(0)
            val endIndex = match.end(0)
            val xrtValues = xrtExp?.split("(xrt|pow)".toRegex())
            if (xrtValues?.get(1)?.isNotBlank() == true) xrtValues[1].also {
                val answer = if (xrtExp.contains("xrt")) xrtValues[1].toDouble()
                    .pow(1 / xrtValues[0].toDouble())
                else xrtValues[0].toDouble().pow(xrtValues[1].toDouble())
                expAnswer = appendResultToExp(answer.toString(), expAnswer, startIndex, endIndex)
            } else expAnswer = appendResultToExp("", expAnswer, startIndex, endIndex)
            match = XRT_POW_PATTERN.matcher(expAnswer)
        }

        return expAnswer
    }

    private fun fibonacci(value: Int, res: MutableList<BigDecimal>) {
        if (value < 0) {
            errorMessage = "fibonacci value must be a positive number"
            return
        } else if (value > 500) {
            errorMessage = "fibonacci value too large"
            return
        }

        val mod =
            "1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000007"

        if (value == 0) {
            res[0] = BigDecimal.ZERO
            res[1] = BigDecimal.ONE
            return
        }

        fibonacci(value / 2, res)
        val a = res[0]
        val b = res[1]

        var c = computation.minus(computation.multiply(computation.convertToBigDecimal(2), b), a)
        if (c < BigDecimal.ZERO)
            c = computation.plus(c, computation.convertToBigDecimal(mod))

        c = computation.modulus(computation.multiply(a, c), mod)

        val d = computation.modulus(
            computation.plus(
                computation.multiply(a, a),
                computation.multiply(b, b)
            ), mod
        )

        if (value % 2 == 0) {
            res[0] = c
            res[1] = d
        } else {
            res[0] = d
            res[1] = computation.plus(c, d)
        }
    }

    private fun factorial(value: Int): BigDecimal {
        if (value.absoluteValue > 9000) {
            errorMessage = "factorial value too large"
            return BigDecimal.ZERO
        }
        if (value == 0) return BigDecimal.ZERO
        else if (value.absoluteValue == 1) {
            if (value < 0) return computation.convertToBigDecimal(-1)
            return BigDecimal.ONE
        }
        return computation.multiply(
            computation.convertToBigDecimal(value),
            factorial(value.absoluteValue - 1)
        )
    }

    private fun appendResultToExp(
        result: String,
        exp: String,
        startIndex: Int,
        endIndex: Int
    ): String {
        return if (startIndex == 0 && exp.length == endIndex) result
        else {
            var startExp = exp.substring(0, startIndex)
            val endExp =
                if (startIndex == exp.length - 1) "" else exp.substring(endIndex, exp.length)
            startExp = if (END_WITH_PATTERN.matcher(startExp).find()) startExp.plus("x${result}")
            else startExp.plus(result)
            startExp = if (START_WITH_PATTERN.matcher(endExp).find()) startExp.plus("x${endExp}")
            else startExp.plus(endExp)

            startExp
        }
    }

    private fun getTrigValue(exp: String): String? {
        val match = NUMBER_PATTERN.matcher(exp)
        if (!match.find()) return null
        return match.group(0)
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
            Pattern.compile("^(((asinh|atanh|acosh|acos|asin|atan|cosh|sinh|tanh|xpon|sin|cos|tan|(?<=(\\d|\\)))(xrt|pow)|ln|log|sqrt|fib)?\\()*((?<!-)-)?((\\d+(?<!\\.\\d{0,64})(\\.?\\d*)(E(\\+|-)?\\d+)?|π|exp))*((?<=\\(.{0,64})(?<![(+-/%x])\\)?)?((?<=(\\)|\\d|π|exp|!))(?<!\\.{2})((?<!!)!|[+-/x%]))?)*$")
        val TRIG_PATTERN: Pattern =
            Pattern.compile("(acosh|asinh|atanh|acos|asin|atan|cosh|sinh|tanh|cos|sin|tan|ln|log|xpon|sqrt|fib)")
        val TRIG_NUMBER_PATTERN: Pattern =
            Pattern.compile("(asinh|atanh|acosh|acos|asin|atan|cosh|sinh|tanh|cos|tan|sin|ln|log|xpon|sqrt|fib)(\\(?-?\\d*|-?\\d+)\\.?\\d*")
        val NUMBER_PATTERN: Pattern = Pattern.compile("-?\\d+\\.?\\d*(E(-|\\+)?\\d+)?")
        val RESULT_PATTERN: Pattern = Pattern.compile("-?\\d+\\.?\\d*(E(-|\\+)?\\d+)?")
        val FACTORIAL_PATTERN: Pattern = Pattern.compile("-?\\d*\\.?\\d*!")

        val END_WITH_PATTERN: Pattern = Pattern.compile("(-?\\d*\\.?\\d+(E(-|\\+)?\\d+)?|\\)|!)$")

        val XRT_POW_PATTERN: Pattern = Pattern.compile("-?\\d*\\.?\\d*(xrt|pow)-?\\d*\\.?\\d*")

        const val INFINITY = "Infinity"

        val START_WITH_PATTERN: Pattern =
            Pattern.compile("^(acosh|asinh|atanh|acos|asin|atan|-?\\d*\\.?\\d+(E(-|\\+)?\\d+)?(xrt|pow)?|cosh|sinh|tanh|cos|sin|tan|ln|log|exp|sqrt|fib|\\()")
    }
}