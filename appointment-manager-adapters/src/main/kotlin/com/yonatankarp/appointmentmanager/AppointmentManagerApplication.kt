package com.yonatankarp.appointmentmanager

class AppointmentManagerApplication {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    println(AppointmentManagerApplication().greeting)
}
