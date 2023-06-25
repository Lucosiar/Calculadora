package com.example.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.lang.Exception

class MainActivity : AppCompatActivity(){

    var screen : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screen = findViewById(R.id.screen)

    }

    fun calcular(view: View){
        val boton = view as Button
        val textoBoton = boton.text.toString()
        var concatenar = screen?.text.toString() + textoBoton

        var quitarCeros = omitirCeros(concatenar)

        if(textoBoton== "="){
            var resultado = 0.0
            try{
                resultado = eval(screen?.text.toString())
                screen?.text = resultado.toString()
            }catch (e: Exception){
                screen?.text = e.toString()
            }
        }else if(textoBoton == "CLEAR"){
            screen?.text = "0"
        }else{
            screen?.text = quitarCeros
        }

    }

    fun omitirCeros(str: String):String{
        var i = 0;
        while(i < str.length && str[i] == '0')i++

        val sb = StringBuffer(str)
        sb.replace(0, i, "")
        return sb.toString()
    }


    fun eval(str: String): Double {
        val expression = str.replace(" ", "")  // Elimina los espacios en blanco

        return evaluateExpression(expression)
    }

    fun evaluateExpression(expression: String): Double {
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<Char>()
        var number = ""
        var decimalFlag = false

        var i = 0
        while (i < expression.length) {
            val ch = expression[i]

            if (ch.isDigit() || ch == '.' || ch == ',') {
                if (ch == ',' && !decimalFlag) {
                    decimalFlag = true
                    number += '.'
                } else {
                    number += ch
                }

                if (i == expression.length - 1) {
                    numbers.add(number.toDouble())
                }
            } else {
                if (number.isNotEmpty()) {
                    numbers.add(number.toDouble())
                    number = ""
                    decimalFlag = false
                }

                if (ch == '(') {
                    val closingIndex = findClosingParenthesis(expression, i)
                    val subExpression = expression.substring(i + 1, closingIndex)
                    numbers.add(evaluateExpression(subExpression))
                    i = closingIndex
                } else {
                    while (operators.isNotEmpty() && hasPrecedence(ch, operators.last())) {
                        performOperation(numbers, operators)
                    }
                    operators.add(ch)
                }
            }

            i++
        }

        while (operators.isNotEmpty()) {
            performOperation(numbers, operators)
        }

        return numbers.first()
    }

    fun performOperation(numbers: MutableList<Double>, operators: MutableList<Char>) {
        val operator = operators.removeAt(operators.lastIndex)
        val num2 = numbers.removeAt(numbers.lastIndex)
        val num1 = numbers.removeAt(numbers.lastIndex)
        val result = when (operator) {
            '+' -> num1 + num2
            '-' -> num1 - num2
            '*' -> num1 * num2
            '/' -> num1 / num2
            '^' -> Math.pow(num1, num2)
            else -> throw IllegalArgumentException("Invalid operator: $operator")
        }
        numbers.add(result)
    }

    fun hasPrecedence(operator1: Char, operator2: Char): Boolean {
        if (operator2 == '(' || operator2 == ')') {
            return false
        }

        return (operator1 == '^') || (operator2 == '+' || operator2 == '-')
    }

    fun findClosingParenthesis(expression: String, startIndex: Int): Int {
        var openParentheses = 1
        for (i in startIndex + 1 until expression.length) {
            when (expression[i]) {
                '(' -> openParentheses++
                ')' -> openParentheses--
            }
            if (openParentheses == 0) {
                return i
            }
        }
        throw IllegalArgumentException("No closing parenthesis found")
    }
}
