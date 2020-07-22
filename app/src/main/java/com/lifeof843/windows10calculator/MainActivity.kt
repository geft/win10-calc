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
import kotlin.math.sqrt

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding;
    private var operand: BigDecimal? = null
    private var operator: Operator? = null
    private var pendingClear: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(binding.root)

        binding.buttonPercentage.setOnClickListener { doPercentage() }
        binding.buttonCe.setOnClickListener { clearCurrent() }
        binding.buttonC.setOnClickListener { clearAll() }
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
        binding.buttonReciprocal.setOnClickListener { doReciprocal() }
        binding.buttonSquare.setOnClickListener { doSquare() }
        binding.buttonRoot.setOnClickListener { doRoot() }
        binding.buttonSign.setOnClickListener { changeSign() }
    }

    private fun operate(operator: Operator) {
        this.operator = operator
        pendingClear = true

        val operand1 = operand
        val operand2 = getCurrentNumber()
        if (operand1 == null) {
            operand = getCurrentNumber()
        } else {
            if (operand2 == BigDecimal.ZERO && operator == Operator.DIVIDE) {
                binding.textLabel.text = "Cannot divide by zero."
            } else {
                try {
                    val output = operator.operation.invoke(operand1, operand2)
                    binding.textLabel.text = formatLabel(output.toString())
                    logState(operand1, operand2, output)
                    operand = getCurrentNumber()
                } catch (e: ArithmeticException) {
                    Log.e(this.javaClass.name, e.message, e)
                    binding.textLabel.text = "Invalid operation"
                }
            }
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
        operand = null
    }

    private fun getCurrentNumber() = binding.textLabel.text.toString().toBigDecimal()

    private fun clearAll() {
        operand = null
        operator = null
        binding.textLabel.text = "0"
    }

    private fun clearCurrent() {
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
            return label.toBigDecimal().stripTrailingZeros().toPlainString()
        }

        val newLabel = label.replace(",", "")
        val decimalFormat = DecimalFormat.getInstance() as DecimalFormat
        val decimalFormatSymbols = DecimalFormatSymbols.getInstance()
        decimalFormatSymbols.groupingSeparator = ','
        decimalFormat.decimalFormatSymbols = decimalFormatSymbols
        return decimalFormat.format(newLabel.toBigDecimal().longValueExact())
    }

    private fun doReciprocal() {
        operand = BigDecimal.ONE
        operate(Operator.DIVIDE)
    }

    private fun doRoot() {
        val current = getCurrentNumber()
        if (current.signum() == -1) {
            binding.textLabel.text = "Invalid input"
        } else {
            val output = sqrt(getCurrentNumber().toDouble())
            binding.textLabel.text = formatLabel(output.toString())
        }
    }

    private fun doSquare() {
        val current = getCurrentNumber()
        val output = current * current
        binding.textLabel.text = formatLabel(output.toString())
    }

    private fun doPercentage() {
        val operand = this.operand
        val output = if (operand == null) BigDecimal.ZERO
        else operand * 0.01.toBigDecimal() * getCurrentNumber()
        binding.textLabel.text = formatLabel(output.toString())
    }

    private fun changeSign() {
        val output = getCurrentNumber().negate()
        binding.textLabel.text = formatLabel(output.toString())
    }
}