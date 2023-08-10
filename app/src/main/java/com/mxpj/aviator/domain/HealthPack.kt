package com.mxpj.aviator.domain

interface HealthPack {

    fun addHealth(playerPlane: PlayerPlane): PlayerPlane
}