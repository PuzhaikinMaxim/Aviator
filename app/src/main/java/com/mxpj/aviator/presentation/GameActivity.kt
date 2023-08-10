package com.mxpj.aviator.presentation

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mxpj.aviator.R
import com.mxpj.aviator.databinding.ActivityMainBinding
import com.mxpj.aviator.domain.Bullet
import com.mxpj.aviator.domain.BulletType
import kotlin.math.max

class GameActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var viewModel: GameViewModel

    private val planeHolders = ArrayList<PlaneHolder>()

    private val bulletHolders = ArrayList<BulletHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ViewModelFactory())[GameViewModel::class.java]
        setEnemyShipsObserver()
        setBulletsObserver()
    }

    private fun setEnemyShipsObserver() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        viewModel.enemies.observe(this){
            for(plane in it){
                if(planeHolders.find { planeHolder -> planeHolder.plane.id == plane.id } != null) continue
                val ivPlane = ImageView(this)
                ivPlane.setImageResource(R.drawable.plane_enemy)
                ivPlane.layoutParams = ViewGroup.LayoutParams(160,160)
                planeHolders.add(PlaneHolder(plane, ivPlane))
                @Suppress("DEPRECATION")
                val animator = ValueAnimator.ofFloat(-200f, displayMetrics.heightPixels.toFloat()).setDuration(10000L).apply {
                    interpolator = LinearInterpolator()
                }
                val updateListener = AnimatorUpdateListener { va ->
                    val animatedValue = va.animatedValue as Float
                    viewModel.changeEnemyPosition(plane, plane.position.copy(second = animatedValue.toInt()))
                    ivPlane.y = animatedValue
                }
                animator.addUpdateListener(updateListener)
                animator.start()
                ivPlane.x = plane.position.first.toFloat()
                ivPlane.y = plane.position.second.toFloat()
                binding.clTest.addView(ivPlane)
                planeHolders.add(PlaneHolder(plane, ivPlane))
            }
        }
    }

    private fun setBulletsObserver() {
        viewModel.bullets.observe(this){
            val bulletCopy = it.clone() as ArrayList<Bullet>
            for(bullet in bulletCopy){
                val bulletHolder = bulletHolders.find {
                        bulletHolder -> bulletHolder.bullet.bulletId == bullet.bulletId
                }
                if(bulletHolder != null) continue
                val ivBullet = ImageView(this)
                val imageResource = when(bullet.bulletType){
                    BulletType.ENEMY_BULLET -> {
                        R.drawable.bullet_enemy
                    }
                    BulletType.PLAYER_BULLET -> {
                        R.drawable.bullet
                    }
                }
                ivBullet.layoutParams = ViewGroup.LayoutParams(21,66)
                ivBullet.setImageResource(imageResource)
                ivBullet.x = bullet.position.first.toFloat()
                binding.clTest.addView(ivBullet)
                bulletHolders.add(BulletHolder(bullet, ivBullet))
                @Suppress("DEPRECATION")
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                var duration = ((displayMetrics.heightPixels.toFloat() - bullet.position.second)/(displayMetrics.heightPixels.toFloat())) * 6000L
                duration = max(duration,0f)
                val animator = when(bullet.bulletType) {
                    BulletType.ENEMY_BULLET -> {
                        ValueAnimator.ofFloat(bullet.position.second.toFloat(), displayMetrics.heightPixels.toFloat()).setDuration(duration.toLong()).apply {
                            interpolator = LinearInterpolator()
                        }
                    }
                    BulletType.PLAYER_BULLET -> {
                        ValueAnimator.ofFloat(bullet.position.second.toFloat(), -200f).setDuration(duration.toLong())
                    }
                }
                val updateListener = AnimatorUpdateListener { va ->
                    val animatedValue = va.animatedValue as Float
                    ivBullet.y = animatedValue
                }
                animator.addUpdateListener(updateListener)
                animator.start()
            }
        }
    }
}