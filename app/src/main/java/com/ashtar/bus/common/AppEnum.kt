package com.ashtar.bus.common

enum class City(val zhName: String) {
    Taipei("台北"),
    NewTaipei("新北")
}

enum class StopStatus(
    val code: Int,
    val display: String
) {
    Offline(
        code = -1,
        display = "網路離線"
    ),
    Normal(
        code = 0,
        display = "正常"
    ),
    NotDepart(
        code = 1,
        display = "尚未發車"
    ),
    TrafficControl(
        code = 2,
        display = "交管不停"
    ),
    LastPassed(
        code = 3,
        display = "末班已過"
    ),
    NoServiceToday(
        code = 4,
        display = "今日停駛"
    );
    companion object {
        fun fromCode(code: Int): StopStatus {
            return entries.find { it.code == code } ?: Normal
        }
    }
}