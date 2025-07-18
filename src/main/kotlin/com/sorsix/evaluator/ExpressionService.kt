package com.sorsix.evaluator

import org.springframework.stereotype.Service
import java.util.*

@Service
class ExpressionService {
    fun evaluateExpression(expression: String?): Double {
        if (expression.isNullOrEmpty()) {
            throw InvalidExpressionException("Expression cannot be null or empty!")
        }

        val cleanExpression = expression.replace("\\s+".toRegex(), "")

        validateExpression(cleanExpression)

        return try {
            evaluate(cleanExpression)
        } catch (e: Exception) {
            throw InvalidExpressionException("Invalid expression: ${e.message}")
        }
    }

    private fun validateExpression(expression: String) {
        if (!expression.matches("[0-9+\\-*/().]+".toRegex())) {
            throw InvalidExpressionException("Expression contains invalid characters!")
        }

        var parenthesesCount = 0
        for (c in expression) {
            when (c) {
                '(' -> parenthesesCount++
                ')' -> parenthesesCount--
            }
            if (parenthesesCount < 0) {
                throw InvalidExpressionException("Unmatched closing parenthesis")
            }
        }
        if (parenthesesCount != 0) {
            throw InvalidExpressionException("Unmatched opening parenthesis")
        }

        if (expression.matches("^[+*/].*".toRegex()) || expression.matches(".*[+\\-*/]$".toRegex())) {
            throw InvalidExpressionException("Expression cannot start or end with an operator")
        }

        if (expression.contains("()")) {
            throw InvalidExpressionException("Empty parentheses found")
        }

        if (expression.contains("/0")) {
            throw InvalidExpressionException("Division by zero detected")
        }
    }

    private fun evaluate(expression: String): Double {
        val tokens = tokenize(expression)

        val numbers = Stack<Double>()
        val operators = Stack<String>()

        for (token in tokens) {
            when {
                isNumber(token) -> {
                    numbers.push(token.toDouble())
                }

                token == "(" -> {
                    operators.push(token)
                }

                token == ")" -> {
                    while (operators.isNotEmpty() && operators.peek() != "(") {
                        processOperator(numbers, operators)
                    }
                    if (operators.isNotEmpty()) {
                        operators.pop()
                    }
                }

                isOperator(token) -> {
                    while (operators.isNotEmpty() &&
                        operators.peek() != "(" &&
                        getPrecedence(operators.peek()) >= getPrecedence(token)
                    ) {
                        processOperator(numbers, operators)
                    }
                    operators.push(token)
                }
            }
        }

        while (operators.isNotEmpty()) {
            processOperator(numbers, operators)
        }

        if (numbers.size != 1) {
            throw InvalidExpressionException("Invalid expression format")
        }

        return numbers.pop()
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        val currentToken = StringBuilder()

        for (i in expression.indices) {
            val c = expression[i]

            when {
                c.isDigit() || c == '.' -> currentToken.append(c)
                c == '-' && (i == 0 || expression[i - 1] == '(') -> currentToken.append(c)
                else -> {
                    if (currentToken.isNotEmpty()) {
                        tokens.add(currentToken.toString())
                        currentToken.clear()
                    }
                    tokens.add(c.toString())
                }
            }
        }

        if (currentToken.isNotEmpty()) {
            tokens.add(currentToken.toString())
        }

        return tokens
    }

    private fun isNumber(token: String): Boolean {
        return try {
            token.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun processOperator(numbers: Stack<Double>, operators: Stack<String>) {
        if (numbers.size < 2 || operators.isEmpty()) {
            throw InvalidExpressionException("Invalid expression format")
        }

        val operator = operators.pop()
        val b = numbers.pop()
        val a = numbers.pop()

        val result = when (operator) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> {
                if (b == 0.0) {
                    throw InvalidExpressionException("Division by zero")
                }
                a / b
            }

            else -> throw InvalidExpressionException("Unknown operator: $operator")
        }

        numbers.push(result)
    }

    private fun isOperator(token: String): Boolean {
        return token in setOf("+", "-", "*", "/")
    }

    private fun getPrecedence(operator: String): Int {
        return when (operator) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }
    }
}
