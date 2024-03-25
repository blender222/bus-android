package com.ashtar.bus.common

fun Int?.secondsToMinutes(): Int? {
    return if (this == null) null else (this / 60)
}