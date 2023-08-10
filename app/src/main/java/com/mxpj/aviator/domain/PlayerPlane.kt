package com.mxpj.aviator.domain

data class PlayerPlane(
    val position: Pair<Int, Int>,
    val health: Int = MAX_HEALTH
) {

    companion object {
        const val MAX_HEALTH = 12
    }
}