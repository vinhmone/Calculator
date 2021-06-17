package com.chaubacho.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val STATE_PENDING_OPERATION = "PendingOperation"
    private val STATE_OPERAND = "Operand1"
    private var numberButtons = listOf<Button>()
    private var operationButtons = listOf<Button>()

    private var currentOperand: Double? = null
    private var pendingOperation: String? = getString(R.string.operation_equal)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        numberButtons = listOf(button0, button1, button2, button3, button4,
            button5, button6, button7, button8, button9, buttonDot)
        operationButtons = listOf(buttonEqual, buttonPlus, buttonMinus,
            buttonMultiply, buttonDivide)
        numberButtons.forEach { it.setOnClickListener(this) }
        operationButtons.forEach { it.setOnClickListener(this) }
        buttonNeg.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            in numberButtons -> writeNumbers((v as Button).text.toString())
            in operationButtons -> writeOperation((v as Button).text.toString())
            buttonNeg -> writeNegation()
        }
    }

    private fun writeNumbers(s: String) {
        editTextNewNumber.append(s)
    }

    private fun writeOperation(op: String) {
        val value = editTextNewNumber.text.toString()
        if (value.isNotEmpty()) {
            value.toDoubleOrNull()?.let {
                performOperation(it, op)
            }
        }
        pendingOperation = op
        textOperation.text = pendingOperation
    }

    private fun writeNegation() {
        val value = editTextNewNumber.text.toString()
        if (value.isEmpty()) editTextNewNumber.setText(getString(R.string.operation_minus))
        else {
            value.toDoubleOrNull()?.let {
                editTextNewNumber.setText("${it * -1}")
            } ?: editTextNewNumber.setText("")
        }
    }

    private fun performOperation(value: Double, op1: String) {
        if (currentOperand == null) {
            currentOperand = value
        } else {
            if (pendingOperation.equals(getString(R.string.operation_equal))
                && textOperation.text != getString(R.string.operation_equal))
                pendingOperation = op1
            currentOperand = when (pendingOperation) {
                getString(R.string.operation_equal) -> value
                getString(R.string.operation_plus) -> currentOperand?.plus(value)
                getString(R.string.operation_minus) -> currentOperand?.minus(value)
                getString(R.string.operation_times) -> currentOperand?.times(value)
                getString(R.string.operation_divide) -> currentOperand?.div(value)
                else -> return
            }
        }
        editTextResult.setText("$currentOperand")
        editTextNewNumber.setText("")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_PENDING_OPERATION, pendingOperation)
        currentOperand?.let {
            outState.putDouble(STATE_OPERAND, currentOperand!!)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION)
        currentOperand = savedInstanceState.getDouble(STATE_OPERAND);
        textOperation.text = pendingOperation
    }
}
