package com.example.mvvmrunningapp.extensions

import android.view.MenuItem
import android.view.View

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}

fun MenuItem.toVisible() {
    isVisible = true
}

fun MenuItem.toInvisible() {
    isVisible = false
}