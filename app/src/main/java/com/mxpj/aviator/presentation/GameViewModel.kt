package com.mxpj.aviator.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mxpj.aviator.domain.*
import kotlinx.coroutines.*
import kotlin.random.Random

class GameViewModel: ViewModel() {

    private val _player = MutableLiveData<PlayerPlane>()
    val player: LiveData<PlayerPlane>
        get() = _player

    private val _enemies = MutableLiveData<ArrayList<EnemyPlane>>(ArrayList())
    val enemies: LiveData<ArrayList<EnemyPlane>>
        get() = _enemies

    private val _bullets = MutableLiveData<ArrayList<Bullet>>(ArrayList())
    val bullets: LiveData<ArrayList<Bullet>>
        get() = _bullets

    private val _healthPack = MutableLiveData<HealthPack>()
    val healthPack: LiveData<HealthPack>
        get() = _healthPack

    private var planeSpawnJob: Job

    private val enemyLogic = HashMap<Int, Job>()

    init {
        _player.value = PlayerPlane(position = Pair(0,0))
        val scope = CoroutineScope(Dispatchers.IO)
        val enemyPlane = EnemyPlane(position = Pair(500,-200))
        _enemies.value = _enemies.value!!.apply {
            add(enemyPlane)
        }
        enemyLogic[enemyPlane.id] = scope.launch {
            while (true) {
                delay(EnemyPlane.SHOOT_DELAY)
                addEnemyBullet(enemyPlane.id)
            }
        }
        planeSpawnJob = scope.launch {
            while (true) {
                yield()
                delay(2000L)
                val ep = EnemyPlane(position = Pair(Random.nextInt(0, 1000),-200))
                _enemies.value!!.add(ep)
                enemyLogic[ep.id] = scope.launch {
                    while (true) {
                        delay(EnemyPlane.SHOOT_DELAY)
                        addEnemyBullet(ep.id)
                    }
                }
            }
        }
    }

    private fun addEnemyBullet(enemyPlaneId: Int) {
        val enemiesCopy = _enemies.value!!.clone() as ArrayList<EnemyPlane>
        val enemyPlane = enemiesCopy.findMutable { it.id == enemyPlaneId } ?: return
        _bullets.postValue(_bullets.value!!.apply {
            add(
                Bullet.EnemyBullet(
                    enemyPlane.position.copy(first = enemyPlane.position.first + 70,second = enemyPlane.position.second + 150)
                )
            )
        })
    }

    fun changeEnemyPosition(plane: EnemyPlane, newPosition: Pair<Int, Int>) {
        _enemies.value!!.replaceAll {
            if(it.id == plane.id) {
                return@replaceAll it.copy(position = newPosition)
            }
            it
        }
        _enemies.value = _enemies.value
    }

    fun removeEnemyPlane(plane: EnemyPlane) {
        val enemyJob = enemyLogic[plane.id]
        enemyJob?.cancel()
        enemyLogic.remove(plane.id)
        _enemies.value!!.removeIf {
            plane.id == it.id
        }
    }

    fun removeBullet(bullet: Bullet) {
        _bullets.value!!.removeIf {
            bullet.id == it.id
        }
    }

    fun changePlayerPlanePosition(translationX: Int) {
        val position = _player.value!!.position
        _player.value = _player.value!!.copy(
            position = position.copy(
                first = (position.first + translationX)
            )
        )
    }

    fun setPlayerPlanePosition(positionX: Float, positionY: Float) {
        _player.value = _player.value!!.copy(
            position = Pair(positionX.toInt(), positionY.toInt())
        )
    }
}