package com.exe.vocafy_BE.enum

enum class LearningState(val code: Int) {
    UNKNOWN(0),
    INTRODUCED(1),
    LEARNING(2),
    FAMILIAR(3),
    UNDERSTOOD(4),
    RECOGNIZED(5),
    RECALLED(6),
    MASTERED(7),
    ;

    companion object {
        fun fromCode(code: Int): LearningState =
            values().firstOrNull { it.code == code } ?: UNKNOWN
    }
}
