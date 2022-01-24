package com.example.calculator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.calculator.R
import com.example.calculator.ui.fragments.CommonButton
import com.example.calculator.viewmodel.CalculatorViewModel


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val commonButton:Fragment = CommonButton()
    private val viewModel:CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.result.observeForever {
            findViewById<TextView>(R.id.resultScreen).text = it.toString()
        }

        viewModel.exp.observeForever {
            findViewById<TextView>(R.id.punch_screen).text = it.toString()
        }

        supportActionBar?.hide()

        replaceFragment(commonButton);

    }

    private fun replaceFragment(fragment:Fragment){
        if(fragment!==null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.keypad, fragment);
            transaction.commit()
        }
    }


}