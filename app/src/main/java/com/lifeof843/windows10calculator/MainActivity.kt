package com.lifeof843.windows10calculator

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.lifeof843.windows10calculator.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding;
    private var operand: BigDecimal? = null
    private var operator: Operator? = null
    private var pendingClear: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        binding.buttonPercentage.setOnClickListener { clear() }
        binding.buttonCe.setOnClickListener { clear() }
        binding.buttonC.setOnClickListener { clear() }
        binding.buttonBackspace.setOnClickListener { backspace() }
        binding.button0.setOnClickListener { insertNum(0) }
        binding.button1.setOnClickListener { insertNum(1) }
        binding.button2.setOnClickListener { insertNum(2) }
        binding.button3.setOnClickListener { insertNum(3) }
        binding.button4.setOnClickListener { insertNum(4) }
        binding.button5.setOnClickListener { insertNum(5) }
        binding.button6.setOnClickListener { insertNum(6) }
        binding.button7.setOnClickListener { insertNum(7) }
        binding.button8.setOnClickListener { insertNum(8) }
        binding.button9.setOnClickListener { insertNum(9) }
        binding.buttonDecimal.setOnClickListener { insertDecimal() }
        binding.buttonDivide.setOnClickListener { operate(Operator.DIVIDE) }
        binding.buttonMultiply.setOnClickListener { operate(Operator.MULTIPLY) }
        binding.buttonSubtract.setOnClickListener { operate(Operator.SUBTRACT) }
        binding.buttonAdd.setOnClickListener { operate(Operator.ADD) }
        binding.buttonEqual.setOnClickListener { onEqual() }
    }

    private fun operate(operator: Operator) {
        this.operator = operator
        pendingClear = true

        val operand1 = operand
        val operand2 = getCurrentNumber()
        if (operand1 == null) {
            operand = getCurrentNumber()
        } else {
            val output = operator.operation.invoke(operand1, operand2)
            binding.textLabel.text = formatLabel(output.toString())
            logState(operand1, operand2, output)
            operand = getCurrentNumber()
        }
    }

    private fun logState(operand1: BigDecimal, operand2: BigDecimal, output: BigDecimal) {
        Log.d(
            this.javaClass.name,
            String.format(
                "A=%s, B=%s, OP=%s, OUT=%s",
                operand1.toString(),
                operand2.toString(),
                operator.toString(),
                output.toString()
            )
        )
    }

    private fun onEqual() {
        operator?.let { operate(it) }
    }

    private fun getCurrentNumber() = binding.textLabel.text.toString().toBigDecimal()

    private fun clear() {
        operand = null
        operator = null
        binding.textLabel.text = "0"
    }

    @SuppressLint("SetTextI18n")
    private fun insertDecimal() {
        if (binding.textLabel.text.contains(".")) return

        binding.textLabel.text = binding.textLabel.text.toString() + "."
    }

    @SuppressLint("SetTextI18n")
    private fun insertNum(num: Int) {
        if (pendingClear) {
            binding.textLabel.text = ""
            pendingClear = false
        }

        if (num == 0 && binding.textLabel.text == "0") return

        val combinedLabel = binding.textLabel.text.toString() + num.toString()
        binding.textLabel.text = formatLabel(combinedLabel)
    }

    private fun backspace() {
        val newLabel = binding.textLabel.text.toString().substring(0, binding.textLabel.text.length - 1)
        binding.textLabel.text = formatLabel(newLabel)
    }

    private fun formatLabel(label: String): String {
        if (label.contains(".")) {
            return label
        }

        val newLabel = label.replace(",", "")
        val decimalFormat = DecimalFormat.getInstance() as DecimalFormat
        val decimalFormatSymbols = DecimalFormatSymbols.getInstance()
        decimalFormatSymbols.groupingSeparator = ','
        decimalFormat.decimalFormatSymbols = decimalFormatSymbols
        return decimalFormat.format(newLabel.toBigDecimal().longValueExact())
    }
}