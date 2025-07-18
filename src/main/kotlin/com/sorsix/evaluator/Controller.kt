package com.sorsix.evaluator

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller(
    private val expressionService: ExpressionService
) {

    @PostMapping("/api/evaluate")
    fun evaluateExpression(@RequestBody request: Request): ResponseEntity<Response> {
        return try {
            val result = expressionService.evaluateExpression(request.expression)
            ResponseEntity.ok(Response("Valid", result, null))
        } catch (e: InvalidExpressionException) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response("Invalid", null, e.message))
        }
    }
}