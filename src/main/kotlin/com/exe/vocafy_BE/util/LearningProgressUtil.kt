package com.exe.vocafy_BE.util

import com.exe.vocafy_BE.enum.LearningState

object LearningProgressUtil {
    fun normalizeStateIndex(state: LearningState): Int =
        when (state) {
            LearningState.UNKNOWN -> 0
            LearningState.INTRODUCED -> 1
            LearningState.LEARNING -> 2
            LearningState.FAMILIAR,
            LearningState.RECOGNIZED,
            LearningState.RECALLED,
            LearningState.UNDERSTOOD,
            -> 3
            LearningState.MASTERED -> 4
        }

    fun normalizeStateName(state: LearningState): String =
        when (state) {
            LearningState.UNKNOWN -> "UNKNOWN"
            LearningState.INTRODUCED -> "INTRODUCED"
            LearningState.LEARNING -> "LEARNING"
            LearningState.FAMILIAR,
            LearningState.RECOGNIZED,
            LearningState.RECALLED,
            LearningState.UNDERSTOOD,
            -> "UNDERSTOOD"
            LearningState.MASTERED -> "MASTERED"
        }

    fun denormalizeState(index: Int): LearningState =
        when (index) {
            1 -> LearningState.INTRODUCED
            2 -> LearningState.LEARNING
            3 -> LearningState.UNDERSTOOD
            4 -> LearningState.MASTERED
            else -> LearningState.INTRODUCED
        }

    fun progressPercent(state: LearningState): Int = normalizeStateIndex(state) * 25
}
