package com.stratix.demo.model

enum class OsType(val type: Int) {
    iOS(0),
    Andorid(1),
    WinPhone(2);

    companion object {
        fun fromInt(value: Int) = OsType.values().first { it.type == value }
    }
}