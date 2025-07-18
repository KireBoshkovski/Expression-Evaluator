package com.sorsix.evaluator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ExpressionServiceTest {
    private val service = ExpressionService()

    @Test
    fun `single digit`() {
        val expression = "3"
        val result = service.evaluateExpression(expression)
        assertEquals(3.00, result)
    }

    @Test
    fun `simple expression`() {
        val expression = "-1 + 2"
        val result = service.evaluateExpression(expression)
        assertEquals(1.00, result)
    }

    @Test
    fun `expression with parentheses`() {
        val expression = "2 * ( 3+ 4)"
        val result = service.evaluateExpression(expression)
        assertEquals(14.00, result)
    }

    @Test
    fun `expression with decimals`() {
        val expression = "1 + 2.5"
        val result = service.evaluateExpression(expression)
        assertEquals(3.50, result)
    }

    @Test
    fun `evaluate expression with negative numbers`() {
        val expression = "-3 + 5"
        val result = service.evaluateExpression(expression)
        assertEquals(2.0, result)
    }

    @Test
    fun `evaluate expression with multiple operators`() {
        val expression = "10 + 2 * 6"
        val result = service.evaluateExpression(expression)
        assertEquals(22.0, result)
    }

    @Test
    fun `evaluate expression with division`() {
        val expression = "20 / 5 + 3"
        val result = service.evaluateExpression(expression)
        assertEquals(7.0, result)
    }

    @Test
    fun `evaluate expression with nested parentheses`() {
        val expression = "(2 + (3 * 2)) * 2"
        val result = service.evaluateExpression(expression)
        assertEquals(16.0, result)
    }

    @Test
    fun `throws on null expression`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression(null)
        }
        assertEquals("Expression cannot be null or empty!", exception.message)
    }

    @Test
    fun `throws on empty expression`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("")
        }
        assertEquals("Expression cannot be null or empty!", exception.message)
    }

    @Test
    fun `throws on invalid characters`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("2 + 3a")
        }
        assertEquals("Expression contains invalid characters!", exception.message)
    }

    @Test
    fun `throws on unmatched parentheses`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("(2 + 3")
        }
        assertEquals("Unmatched opening parenthesis", exception.message)
    }

    @Test
    fun `throws on unmatched closing parentheses`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("2 + 3)")
        }
        assertEquals("Unmatched closing parenthesis", exception.message)
    }

    @Test
    fun `throws on expression starting with operator`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("+2+3")
        }
        assertEquals("Expression cannot start or end with an operator", exception.message)
    }

    @Test
    fun `throws on expression ending with operator`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("2+3*")
        }
        assertEquals("Expression cannot start or end with an operator", exception.message)
    }

    @Test
    fun `throws on empty parentheses`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("2 + ()")
        }
        assertEquals("Empty parentheses found", exception.message)
    }

    @Test
    fun `throws on division by zero detected during validation`() {
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("4 / 0")
        }
        assertEquals("Division by zero detected", exception.message)
    }

    @Test
    fun `throws on division by zero during evaluation`() {
        // To check if zero detected during evaluation is caught properly
        val exception = assertThrows<InvalidExpressionException> {
            service.evaluateExpression("4 / (2 - 2)")
        }
        assertEquals("Invalid expression: Division by zero", exception.message)
    }
}