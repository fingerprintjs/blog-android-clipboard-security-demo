package com.fingerprintjs.android.clipboard_reader

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.provider.Settings.canDrawOverlays
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.LinkedList


class ClipboardUtils(private val applicationContext: Context) {

    fun pasteTheData(clipboard: ClipboardManager): String {
        clipboard.primaryClip?.getItemAt(0)
        return clipboard.primaryClip?.getItemAt(0)?.text.toString()
    }

    fun pasteTheDataSilently(clipboard: ClipboardManager): String {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            return clipboard.primaryClip?.getItemAt(0)?.text.toString()
        }
        if (!canDrawOverlays(applicationContext)) {
            Toast.makeText(
                applicationContext,
                "SYSTEM_ALERT_WINDOW permission has not been granted, so no overdraw for the system Toast",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            kotlin.runCatching {
                Handler(applicationContext.mainLooper).post {
                    drawWhiteRectangleOnTheBottomMultipleTimes()
                }
            }
        }

        return clipboard.primaryClip?.getItemAt(0)?.text.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drawWhiteRectangleOnTheBottomMultipleTimes() {
        val windowManager: WindowManager =
            applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams()
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.format = PixelFormat.TRANSLUCENT
        params.windowAnimations = android.R.style.Animation_Toast
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            params.isFitInsetsIgnoringVisibility = true
        }
        params.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        params.y = applicationContext.resources.getDimensionPixelSize(
            R.dimen.toast_y_offset
        )
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL


        val overlays = LinkedList<View>()
        for (i in 0..NUMBER_OF_OVERLAYS - 1) {
            val view = LayoutInflater.from(applicationContext).inflate(R.layout.dummy_view, null)
            overlays.add(view)
            windowManager.addView(overlays[i], params)
        }

        Handler(applicationContext.mainLooper).postDelayed({
            for (i in 0..NUMBER_OF_OVERLAYS - 1) {
                windowManager.removeViewImmediate(overlays[i])
            }
        }, 3000)
    }
}

private const val NUMBER_OF_OVERLAYS = 3
