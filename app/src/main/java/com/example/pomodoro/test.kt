package com.example.pomodoro

fun main() {
     val test = test()
    println(test.test2)
    test.test1 = 290
    println(test.test2)
    test.test2= 39
    println(test.test2)
    println("${test.test2}")
    println("${test.test2 + 1}")
}


class test () {
    var test1 = 1030
    var test2: Int = 0
        set(value) {
            field = value
        }
        get() = test1
}