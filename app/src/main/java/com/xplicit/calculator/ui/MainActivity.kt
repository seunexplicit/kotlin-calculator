package com.xplicit.calculator.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.activity.viewModels
import com.xplicit.calculator.BasicArithmetic
import com.xplicit.calculator.R
import com.xplicit.calculator.data.Operators
import com.xplicit.calculator.ui.fragments.AdvanceOperatorFragment
import com.xplicit.calculator.ui.fragments.CommonButton
import com.xplicit.calculator.ui.fragments.LandscapeButton
import com.xplicit.calculator.viewmodel.CalculatorViewModel
import com.xplicit.calculator.viewmodel.CalculatorViewModelFactory
import com.xplicit.calculator.viewmodel.DEGRAD
import com.xplicit.calculator.viewmodel.INFINITY
import kotlinx.coroutines.*

const val LARGE_LAYOUT_WIDTH = 400
const val X_LARGE_LAYOUT_WIDTH = 700
const val SMALL_TEXT_LENGTH = 15
const val MEDIUM_TEXT_LENGTH = 30
const val LARGE_TEXT_LENGTH = 40

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: CalculatorViewModel by viewModels {
        CalculatorViewModelFactory(this)
    }

    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var degreeNotification: TextView
    private lateinit var specialNotification:TextView
    private lateinit var operators: Operators

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val expressionTextView = findViewById<TextView>(R.id.punch_screen)
        horizontalScrollView = findViewById(R.id.horizontalScroll)
        degreeNotification = findViewById(R.id.degNotification)
        specialNotification = findViewById(R.id.specialNotification)
        operators = Operators(applicationContext)

        viewModel.result.observeForever {
            resultTextViewSetup(findViewById(R.id.resultScreen), it)
        }

        viewModel.errorMessage.observeForever {
            if (it.toString() == INFINITY) {
                findViewById<TextView>(R.id.resultScreen).text = getString(R.string.infinity)
            } else {
                findViewById<TextView>(R.id.errorMessage).text = it.toString()
            }
        }

        viewModel.activeSpecialOperator.observeForever {
            if(it==null) specialNotification.text = ""
            else {
                operators.operatorMap().forEach { operator->
                    if(operator.value == it) specialNotification.text = operator.key
                }
            }
        }

        viewModel.displayExp.observeForever {
            expressionTextView.text = it
            scope.launch {
                scrollRight()
            }
        }

        viewModel.degRad.observeForever {
            degreeNotification.text =
                if (it == DEGRAD.DEG) getString(R.string.deg) else getString(R.string.rad)
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setLandscapeFragment()
        else
            setFragment(null)

        supportActionBar?.hide()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeFragment()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setFragment(null)
        }
        resultTextViewSetup(findViewById(R.id.resultScreen), null)
        scope.launch {
            scrollRight()
        }
    }

    private fun setLandscapeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        transaction.replace(R.id.keypad, LandscapeButton(), LandscapeButton.TAG)
        transaction.commit()
    }

    fun setFragment(view: View?) {
        val fragments = supportFragmentManager.fragments
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        for (fragment in fragments) {
            if (fragment.tag == CommonButton.TAG && fragment.isVisible) {
                transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                   R.anim.slide_out_left
                )
                transaction.replace(
                    R.id.keypad,
                    AdvanceOperatorFragment(),
                    AdvanceOperatorFragment.TAG
                )
                transaction.commit()
                return
            } else {
                transaction.replace(R.id.keypad, CommonButton(), CommonButton.TAG)
                transaction.commit()
                return
            }
        }

        transaction.replace(R.id.keypad, CommonButton(), CommonButton.TAG)
        transaction.commit()
    }

    private fun resultTextViewSetup(view: TextView, resultText: String?) {
        val text = resultText ?: view.text
        if (text.isNullOrBlank()) {
            view.text = ""
            return
        }

        val screenWidth = resources.configuration.screenWidthDp

        when {
            screenWidth <= LARGE_LAYOUT_WIDTH -> {
                val computation = BasicArithmetic(SMALL_TEXT_LENGTH)
                view.text = "${computation.convertToBigDecimal(text.toString())}"
            }
            screenWidth <= X_LARGE_LAYOUT_WIDTH -> {
                val computation = BasicArithmetic(MEDIUM_TEXT_LENGTH)
                view.text = "${computation.convertToBigDecimal(text.toString())}"
            }
            else -> {
                val computation = BasicArithmetic(LARGE_TEXT_LENGTH)
                view.text = "${computation.convertToBigDecimal(text.toString())}"
            }
        }
    }

    private suspend fun scrollRight() {
        delay(5)
        horizontalScrollView.fullScroll(View.FOCUS_RIGHT)
    }


}