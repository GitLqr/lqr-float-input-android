package com.gitlqr.float_input.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gitlqr.float_input.FloatInputDialog
import com.gitlqr.float_input.IFloatInput

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val floatInput: IFloatInput by lazy { FloatInputDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listOf<View>(findViewById(R.id.btnShow)).forEach { it.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnShow -> {
                floatInput.show()
            }
        }
    }
}