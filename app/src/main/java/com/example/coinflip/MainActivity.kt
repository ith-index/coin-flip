package com.example.coinflip

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

enum class CoinSide {
    Heads, Tails
}

fun flipACoin(): CoinSide =
    if (Random.nextInt() % 2 == 0)
        CoinSide.Heads
    else
        CoinSide.Tails

private const val KEY_CURRENT_COIN_SIDE = "KEY_CURRENT_COIN_SIDE"

class MainActivity : AppCompatActivity() {
    private lateinit var currentCoinSide: CoinSide

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_CURRENT_COIN_SIDE)) {
            val currentCoinSideSerializable = savedInstanceState.getSerializable(KEY_CURRENT_COIN_SIDE)
            if (currentCoinSideSerializable is CoinSide) {
                currentCoinSide = currentCoinSideSerializable
            }
        }

        setContentView(R.layout.activity_main)
        val textViewInitial: TextView = findViewById(R.id.textViewInitial)
        val textViewHeads: TextView = findViewById(R.id.textViewHeads)
        val textViewTails: TextView = findViewById(R.id.textViewTails)
        val buttonFlip: Button = findViewById(R.id.buttonFlip)

        var viewToFadeOut: TextView =
            if (::currentCoinSide.isInitialized) {
                textViewInitial.visibility = View.GONE
                when (currentCoinSide) {
                    CoinSide.Heads -> textViewHeads
                    CoinSide.Tails -> textViewTails
                }.apply {
                    alpha = 1f
                }
            } else {
                textViewInitial
            }

        buttonFlip.setOnClickListener {
            buttonFlip.isEnabled = false
            currentCoinSide = flipACoin()
            val viewToFadeIn =
                when (currentCoinSide) {
                    CoinSide.Heads -> textViewHeads
                    CoinSide.Tails -> textViewTails
                }
            AnimatorSet().apply {
                playSequentially(
                    ObjectAnimator.ofFloat(viewToFadeOut, "alpha", 0f).apply {
                        duration = 300
                    },
                    ObjectAnimator.ofFloat(viewToFadeIn, "alpha", 1f).apply {
                        duration = 300
                    }
                )
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        buttonFlip.isEnabled = true
                    }
                })
                start()
            }
            viewToFadeOut = viewToFadeIn
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (::currentCoinSide.isInitialized) {
                savedInstanceState.putSerializable(KEY_CURRENT_COIN_SIDE, currentCoinSide)
            }
        }
        super.onSaveInstanceState(savedInstanceState)
    }
}