package com.mxpj.aviator.domain

class SmallHealthPack: HealthPack {

    override fun addHealth(playerPlane: PlayerPlane): PlayerPlane {
        val health = minOf(playerPlane.health + 1, PlayerPlane.MAX_HEALTH)
        return playerPlane.copy(health = health)
    }
}