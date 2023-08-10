package com.mxpj.aviator.domain

class BigHealthPack: HealthPack {

    override fun addHealth(playerPlane: PlayerPlane): PlayerPlane {
        return playerPlane.copy(health = PlayerPlane.MAX_HEALTH)
    }
}