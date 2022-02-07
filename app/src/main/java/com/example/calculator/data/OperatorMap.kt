package com.example.calculator.data

import android.content.Context
import com.example.calculator.R

data class OperatorMap(
    val ComputedOperator:String,
    val DisplayOperator:String
)

class Operators(private val context:Context){

    fun operatorMap():Map<String, OperatorMap>{
        return mapOf(
            context.getString(R.string.cos) to OperatorMap(context.getString(R.string.d_cos), context.getString(R.string.d_cos)),
            context.getString(R.string.acos) to OperatorMap(context.getString(R.string.e_acos), context.getString(R.string.d_acos)),
            context.getString(R.string.cosh) to OperatorMap(context.getString(R.string.d_cosh), context.getString(R.string.d_cosh)),
            context.getString(R.string.acosh) to OperatorMap(context.getString(R.string.e_acosh), context.getString(R.string.d_acosh)),
            context.getString(R.string.tan) to OperatorMap(context.getString(R.string.d_tan), context.getString(R.string.d_tan)),
            context.getString(R.string.atan) to OperatorMap(context.getString(R.string.e_atan), context.getString(R.string.d_atan)),
            context.getString(R.string.tanh) to OperatorMap(context.getString(R.string.d_tanh), context.getString(R.string.d_tanh)),
            context.getString(R.string.atanh) to OperatorMap(context.getString(R.string.e_atanh), context.getString(R.string.d_atanh)),
            context.getString(R.string.sinh) to OperatorMap(context.getString(R.string.d_sinh), context.getString(R.string.d_sinh)),
            context.getString(R.string.asinh) to OperatorMap(context.getString(R.string.e_asinh), context.getString(R.string.d_asinh)),
            context.getString(R.string.sin) to OperatorMap(context.getString(R.string.d_sin), context.getString(R.string.d_sin)),
            context.getString(R.string.asin) to OperatorMap(context.getString(R.string.e_asin), context.getString(R.string.d_asin)),
            context.getString(R.string.ln) to OperatorMap(context.getString(R.string.d_ln), context.getString(R.string.d_ln)),
            context.getString(R.string.exp) to OperatorMap(context.getString(R.string.e_exp), context.getString(R.string.d_exp)),
            context.getString(R.string.fib) to OperatorMap(context.getString(R.string.d_fib), context.getString(R.string.d_fib)),
            context.getString(R.string.sqrt) to OperatorMap(context.getString(R.string.e_sqrt), context.getString(R.string.d_sqrt)),
            context.getString(R.string.xrt) to OperatorMap(context.getString(R.string.e_xrt), context.getString(R.string.d_xrt)),
            context.getString(R.string.log) to OperatorMap(context.getString(R.string.d_log), context.getString(R.string.d_log)),
        )
    }
}
