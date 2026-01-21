package com.exe.vocafy_BE.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class SePayUtil (

    @param:Value("\${sepay.url}")
    private val sePayQrUrl: String,

    @param:Value("\${sepay.account}")
    private val accountNumber: String,

    @param:Value("\${sepay.brand}")
    private val brandName: String,

    @param:Value("\${sepay.template}")
    private val template: String,

){

    fun generateSePayQrUrl(amount: Double, ref1: String): String {
        return "$sePayQrUrl?acc=$accountNumber&bank=$brandName&amount=$amount&des=$ref1"
    }

}