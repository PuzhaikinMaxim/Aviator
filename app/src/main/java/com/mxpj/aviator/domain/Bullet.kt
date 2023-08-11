package com.mxpj.aviator.domain

sealed class Bullet(
    val id: Long,
    val position: Pair<Int, Int>,
    val bulletType: BulletType,
    val isEnemyBullet: Boolean
) {
    init {
        ID += 1
    }
    class PlayerBullet(
        position: Pair<Int, Int>,
    ): Bullet(ID, position, BulletType.PLAYER_BULLET, false)

    class EnemyBullet(
        position: Pair<Int, Int>,
    ): Bullet(ID, position, BulletType.ENEMY_BULLET, true)

    companion object {
        private var ID = 0L
    }
}