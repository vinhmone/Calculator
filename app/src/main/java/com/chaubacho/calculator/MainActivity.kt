package com.chaubacho.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.chaubacho.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var numberButtons = listOf<Button>()
    private var operationButtons = listOf<Button>()
    private var currentOperand = Double.MAX_VALUE
    private var pendingOperation = ""
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_PENDING_OPERATION, pendingOperation)
        outState.putDouble(STATE_OPERAND, currentOperand)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION).toString()
        currentOperand = savedInstanceState.getDouble(STATE_OPERAND)
        binding.textOperation.text = pendingOperation
    }

    override fun onClick(v: View?) {
        if (v is Button) {
            when (v) {
                in numberButtons -> writeNumbers(v.text.toString())
                in operationButtons -> writeOperation(v.text.toString())
                binding.buttonNeg -> writeNegation()
            }
        }
    }

    private fun initViews() {
        with(binding) {
            numberButtons = listOf(button0, button1, button2, button3, button4, button5,
                button6, button7, button8, button9, buttonDot)
            operationButtons = listOf(buttonEqual, buttonPlus, buttonMinus, buttonMultiply,
                buttonDivide)
        }
        numberButtons.forEach { it.setOnClickListener(this) }
        operationButtons.forEach { it.setOnClickListener(this) }
        binding.buttonNeg.setOnClickListener(this)
    }

    private fun initData() {
        pendingOperation = getString(R.string.operation_equal)
    }

    private fun writeNumbers(s: String) {
        binding.editTextNewNumber.append(s)
    }

    private fun writeOperation(op: String) {
        val value = binding.editTextNewNumber.text.toString()
        if (value.isNotEmpty()) {
            value.toDoubleOrNull()?.let {
                performOperation(it, op)
            }
        }
        pendingOperation = op
        binding.textOperation.text = pendingOperation
    }

    private fun writeNegation() {
        val value = binding.editTextNewNumber.text.toString()
        if (value.isEmpty()) {
            binding.editTextNewNumber.setText(getString(R.string.operation_minus))
        } else {
            value.toDoubleOrNull()?.let {
                binding.editTextNewNumber.setText("${it * -1}")
            } ?: binding.editTextNewNumber.setText("")
        }
    }

    private fun performOperation(value: Double, op1: String) {
        if (currentOperand == Double.MAX_VALUE) {
            currentOperand = value
        } else {
            if (pendingOperation == getString(R.string.operation_equal)
                && binding.textOperation.text != getString(R.string.operation_equal)
            ) pendingOperation = op1
            currentOperand = when (pendingOperation) {
                getString(R.string.operation_equal) -> value
                getString(R.string.operation_plus) -> currentOperand.plus(value)
                getString(R.string.operation_minus) -> currentOperand.minus(value)
                getString(R.string.operation_times) -> currentOperand.times(value)
                getString(R.string.operation_divide) -> currentOperand.div(value)
                else -> return
            }
        }
        binding.editTextResult.setText(currentOperand.toString())
        binding.editTextNewNumber.setText("")
    }

    companion object {
        const val STATE_PENDING_OPERATION = "PendingOperation"
        const val STATE_OPERAND = "Operand"
    }
}
