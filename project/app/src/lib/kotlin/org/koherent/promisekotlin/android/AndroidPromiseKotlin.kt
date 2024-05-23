package org.koherent.promisekotlin.android

import android.animation.Animator
import android.app.AlertDialog
import android.content.Context
import org.koherent.promisekotlin.Promise

public fun promisedAnimate(animations: () -> Animator): Promise<Boolean> {
    return Promise { resolve ->
        val animator = animations()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                resolve(Promise(true))
            }

            override fun onAnimationCancel(animation: Animator) {
                resolve(Promise(false))
            }
        })
        animator.start()
    }
}

public data class Item<T>(val title: String, val value: T)

public data class Value<T>(val value: T)

public fun <T> promisedShowAlertDialog(context: Context, title: String? = null, message: String? = null, positiveButton: Item<T>? = null, negativeButton: Item<T>? = null, neutralButton: Item<T>? = null): Promise<T> {
    return promisedShowAlertDialog(context, title, message, positiveButton, negativeButton, neutralButton, null)
}

public fun <T : Any> promisedShowAlertDialog(context: Context, title: String? = null, message: String? = null, positiveButton: Item<T>? = null, negativeButton: Item<T>? = null, neutralButton: Item<T>? = null, cancelValue: T?): Promise<T> {
    return promisedShowAlertDialog(context, title, message, positiveButton, negativeButton, neutralButton, cancelValue?.let { Value(it) })
}

public fun <T> promisedShowAlertDialog(context: Context, title: String? = null, message: String? = null, positiveButton: Item<T>? = null, negativeButton: Item<T>? = null, neutralButton: Item<T>? = null, cancelValue: Value<T>?): Promise<T> {
    return Promise { resolve ->
        val builder = AlertDialog.Builder(context)

        if (title != null) {
            builder.setTitle(title)
        }

        if (message != null) {
            builder.setMessage(message)
        }

        if (positiveButton != null) {
            builder.setPositiveButton(positiveButton.title) { dialog, which ->
                resolve(Promise(positiveButton.value))
            }
        }

        if (negativeButton != null) {
            builder.setNegativeButton(negativeButton.title) { dialog, which ->
                resolve(Promise(negativeButton.value))
            }
        }

        if (neutralButton != null) {
            builder.setNeutralButton(neutralButton.title) { dialog, which ->
                resolve(Promise(neutralButton.value))
            }
        }

        if (cancelValue != null) {
            builder.setOnCancelListener { resolve(Promise(cancelValue.value)) }
        } else {
            builder.setCancelable(false)
        }

        builder.show()
    }
}

public fun <T> promisedShowAlertDialog(context: Context, title: String? = null, items: List<Item<T>>): Promise<T> {
    return promisedShowAlertDialog(context, title, items, null)
}

public fun <T : Any> promisedShowAlertDialog(context: Context, title: String? = null, items: List<Item<T>>, cancelValue: T?): Promise<T> {
    return promisedShowAlertDialog(context, title, items, cancelValue?.let { Value(it) })
}

public fun <T> promisedShowAlertDialog(context: Context, title: String? = null, items: List<Item<T>>, cancelValue: Value<T>?): Promise<T> {
    return Promise { resolve ->
        val builder = AlertDialog.Builder(context)

        if (title != null) {
            builder.setTitle(title)
        }

        builder.setItems(items.map { it.title }.toTypedArray()) { dialog, which ->
            resolve(Promise(items[which].value))
        }

        if (cancelValue != null) {
            builder.setOnCancelListener { resolve(Promise(cancelValue.value)) }
        } else {
            builder.setCancelable(false)
        }

        builder.show()
    }
}
