package com.tinashe.audioverse.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.audioverse.injection.ViewModelFactory

inline fun <reified T : ViewModel> getViewModel(activity: FragmentActivity, factory: ViewModelFactory): T {
    return ViewModelProviders.of(activity, factory)[T::class.java]
}

inline fun <reified T : ViewModel> getViewModel(activity: Fragment, factory: ViewModelFactory): T {
    return ViewModelProviders.of(activity, factory)[T::class.java]
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun RecyclerView.vertical() {
    this.layoutManager = LinearLayoutManager(context)
}

fun RecyclerView.horizontal() {
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

fun inflateView(@LayoutRes layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
        LayoutInflater.from(parent.context).inflate(layoutResId, parent, attachToRoot)
