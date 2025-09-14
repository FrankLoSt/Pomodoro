package com.example.pomodoro

fun main() {
    val test = test()
    println(test.test2)
}


class test () {
    var test1 = (1..6).random()
    val test2: Int
        get() = test1
}