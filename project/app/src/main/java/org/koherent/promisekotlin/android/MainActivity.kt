package org.koherent.promisekotlin.android

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button

public class MainActivity : Activity() {
    private var square: View? = null
    private var actionButton: Button? = null

    private enum class Action {
        Animation, AlertDialog, Cancel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        square = findViewById(R.id.square)
        actionButton = findViewById(R.id.action_button) as? Button

        actionButton?.setOnClickListener {
            promisedShowAlertDialog(this, "Actions", listOf(Item("Animation", Action.Animation), Item("AlertDialog", Action.AlertDialog)), Value(Action.Cancel)).map { action ->
                when (action) {
                    Action.Animation -> doAnimate()
                    Action.AlertDialog -> doShowAlertDialog()
                    Action.Cancel -> Unit
                }
            }
        }
    }

    private fun doAnimate() {
        val square = this.square!!
        val actionButton = this.actionButton!!

        actionButton.isEnabled = false

        promisedAnimate {
            ObjectAnimator.ofFloat(square, "translationX", square.translationX + square.width).setDuration(500L)
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "translationY", square.translationY + square.height).setDuration(500L)
            }
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "translationX", square.translationX - square.width * 2).setDuration(600L)
            }
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "translationY", square.translationY - square.height * 2).setDuration(600L)
            }
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "translationX", square.translationX + square.width * 2).setDuration(600L)
            }
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "translationY", square.translationY + square.height).setDuration(500L)
            }
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "translationX", square.translationX - square.width).setDuration(500L)
            }
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "alpha", 0.0f).setDuration(500L)
            }
        }.flatMap { finished ->
            promisedAnimate {
                ObjectAnimator.ofFloat(square, "alpha", 1.0f).let {
                    it.startDelay = 500L
                    it
                }.setDuration(500L)
            }
        }.map { finished ->
            actionButton.isEnabled = true
        }

    }

    private fun doShowAlertDialog() {
        promisedShowAlertDialog(this, "Confirmation", "Do you want to animate the square?", Item
        ("Yes", true), Item("No", false), cancelValue = Value(false)).map { answer ->
            if (answer) {
                doAnimate()
            }
        }

    }
}
