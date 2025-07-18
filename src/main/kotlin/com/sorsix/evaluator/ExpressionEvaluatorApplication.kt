package com.sorsix.evaluator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExpressionEvaluatorApplication

fun main(args: Array<String>) {
    runApplication<ExpressionEvaluatorApplication>(*args)
}
