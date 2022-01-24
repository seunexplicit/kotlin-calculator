package com.example.calculator

import android.util.Log
import java.lang.ArithmeticException
import java.util.regex.Pattern
import java.lang.Exception as Exception1

val  inputRegex:Pattern = Pattern.compile("^((\\(|cos\\(|sin\\(|tan\\(|ln\\(|log\\()*(-?\\d+(?<!(\\d{0,64}\\.\\d{0,64}))(\\.?\\d*))*((?<=\\(.{0,64})(?<![(+-\\/%x])\\)?)?((?<=(\\)|\\.?\\d{1,64}))(?<!\\.{2})[+-\\/x%]?))*$")

class MathComputation(Exp:String) {

    private lateinit var _finalResult:Number;
    val finalResult:Number
        get(){
            val intResult = this._finalResult.toInt()
            val doubleResult = this._finalResult?.toDouble()
            return if((doubleResult.minus(intResult?:0))==0.0) {
                this._finalResult?.toInt()
            } else this._finalResult.toDouble()
        }

    init{
        this._finalResult = simpleArithemeticOps(Exp)
    }


    private fun simpleArithemeticOps(exp:String):Double{
       try {
           val simpleRegex: Pattern = Pattern.compile("^-?\\d+\\.?\\d*$")
           var result: Double? = null
           val operation: Char
           val values: List<String>
           when {
               "%" in exp ->{
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
               if (result === null && it.isNotEmpty()) result = if (simpleRegex.matcher(it).matches()) it.toDouble() else simpleArithemeticOps(it)
               else if (result === null && it.isEmpty()) result = 0.0
               else if (result !== null) {
                   when (operation) {
                       '/' -> {
                           if(it.isNotEmpty()) result = if (simpleRegex.matcher(it).matches()) result!! / it.toDouble() else result!! / simpleArithemeticOps(it)
                       }
                       'x' -> {
                           if(it.isNotEmpty()) result = if (simpleRegex.matcher(it).matches()) result!! * it.toDouble() else result!! * simpleArithemeticOps(it)
                       }
                       '+' -> {
                           if(it.isNotEmpty()) result = if (simpleRegex.matcher(it).matches()) result!! + it.toDouble() else result!! + simpleArithemeticOps(it)
                       }
                       '%'->{
                           if(it.isNotEmpty()) result = if (simpleRegex.matcher(it).matches()) result!! % it.toDouble() else result!! % simpleArithemeticOps(it)
                       }
                       '-' -> {
                           if(it.isNotEmpty()) result = if (simpleRegex.matcher(it).matches()) result!! - it.toDouble() else result!! - simpleArithemeticOps(it)
                       }
                   }
               }
           }

           return result!!
       }
       catch(e:ArithmeticException){
           Log.d("Error Arithemtic", e.message?:"")
            return 0.0
       }
        catch(e: Exception1){
            Log.d("Error Exception", e.message?:"")
            return 0.0
        }
    }

    companion object{
        val  EXP_PATTERN:Pattern = Pattern.compile("^((\\(|cos\\(|sin\\(|tan\\(|ln\\(|log\\()*(-?\\d+(?<!(\\d{0,64}\\.\\d{0,64}))(\\.?\\d*))*((?<=\\(.{0,64})(?<![(+-\\/%x])\\)?)?((?<=(\\)|\\.?\\d{1,64}))(?<!\\.{2})[+-\\/x%]?))*$")
    }
}