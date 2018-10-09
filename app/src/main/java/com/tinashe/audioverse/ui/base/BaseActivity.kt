package com.tinashe.audioverse.ui.base

import android.os.Build
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    protected fun setLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags
        }
    }

    protected fun clearLightStatusBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            view.systemUiVisibility = flags
        }
    }
}