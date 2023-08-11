package com.mxpj.aviator.domain

data class EnemyPlane(
    val id: Int = ID,
    val position: Pair<Int, Int>,
    val isDestroyed: Boolean = false
) {
    init {
        ID += 1
    }

    companion object {
        private var ID = 0
        const val SHOOT_DELAY = 2800L
    }
}