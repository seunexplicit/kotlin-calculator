package com.example.calculator

import android.util.Log
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

class BasicArithmetic() {

    var context = MathContext(40)

    constructor(precision:Int):this(){
        context = MathContext(precision)
    }

    fun plus(firstValue:String, secondValue:String):BigDecimal{
        return BigDecimal(firstValue, context).plus(BigDecimal(secondValue, context))
    }

    fun plus(firstValue:Int, secondValue:Int):BigDecimal{
        return BigDecimal(firstValue, context).plus(BigDecimal(secondValue, context))
    }

    fun plus(firstValue:Double, secondValue:Double):BigDecimal{
        return BigDecimal(firstValue, context).plus(BigDecimal(secondValue, context))
    }

    fun plus(firstValue:BigDecimal, secondValue:String):BigDecimal{
        return firstValue.plus(BigDecimal(secondValue, context))
    }

    fun divide(firstValue:String, secondValue:String):BigDecimal{
        return BigDecimal(firstValue, context).divide(BigDecimal(secondValue, context))
    }

    fun divide(firstValue:Int, secondValue:Int):BigDecimal{
        return BigDecimal(firstValue, context).divide(BigDecimal(secondValue, context))
    }

    fun divide(firstValue:Double, secondValue:Double):BigDecimal{
        return BigDecimal(firstValue, context).divide(BigDecimal(secondValue, context))
    }

    fun divide(firstValue:BigDecimal, secondValue:String):BigDecimal{
        return firstValue.divide(BigDecimal(secondValue, context))
    }

    fun multiply(firstValue:String, secondValue:String):BigDecimal{
        return BigDecimal(firstValue, context).multiply(BigDecimal(secondValue, context))
    }

    fun multiply(firstValue:Int, secondValue:Int):BigDecimal{
        return BigDecimal(firstValue, context).multiply(BigDecimal(secondValue, context))
    }

    fun multiply(firstValue:Double, secondValue:Double):BigDecimal{
        return BigDecimal(firstValue, context).multiply(BigDecimal(secondValue, context))
    }

    fun multiply(firstValue:BigDecimal, secondValue:String):BigDecimal{
        return firstValue.multiply(BigDecimal(secondValue, context))
    }

    fun minus(firstValue:String, secondValue:String):BigDecimal{
        return BigDecimal(firstValue, context).minus(BigDecimal(secondValue, context))
    }

    fun minus(firstValue:Int, secondValue:Int):BigDecimal{
        return BigDecimal(firstValue, context).minus(BigDecimal(secondValue, context))
    }

    fun minus(firstValue:Double, secondValue:Double):BigDecimal{
        return BigDecimal(firstValue, context).minus(BigDecimal(secondValue, context))
    }

    fun minus(firstValue:BigDecimal, secondValue:String):BigDecimal{
        return firstValue.minus(BigDecimal(secondValue, context))
    }

    fun modulus(firstValue:String, secondValue:String):BigDecimal{
        return BigDecimal(firstValue, context).remainder(BigDecimal(secondValue, context))
    }

    fun modulus(firstValue:Int, secondValue:Int):BigDecimal{
        return BigDecimal(firstValue, context).remainder(BigDecimal(secondValue, context))
    }

    fun modulus(firstValue:Double, secondValue:Double):BigDecimal{
        return BigDecimal(firstValue, context).remainder(BigDecimal(secondValue, context))
    }

    fun modulus(firstValue:BigDecimal, secondValue:String):BigDecimal{
        return firstValue.remainder(BigDecimal(secondValue, context))
    }

    fun convertToBigDecimal(value:Int):BigDecimal{
        return BigDecimal(value, context)
    }

    fun convertToBigDecimal(value:Double):BigDecimal{
        var strValue = value.toString()
        while(strValue.contains(".")&& strValue.last() == '0'){
            strValue = strValue.removeSuffix("0")
        }
        return BigDecimal(strValue, context)
    }

    fun convertToBigDecimal(value:String):BigDecimal{
        var strValue = value
        while(strValue.contains(".")&& strValue.last() == '0'){
            strValue = strValue.removeSuffix("0")
        }
        return BigDecimal(strValue, context)
    }

    fun getNonDecimal(value:BigDecimal):BigInteger?{
        return if(value.minus(BigDecimal(value.toBigInteger()))== BigDecimal.ZERO)
            value.toBigInteger()
        else null
    }

}