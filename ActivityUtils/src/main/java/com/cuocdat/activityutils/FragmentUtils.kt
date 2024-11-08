package com.cuocdat.activityutils

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

//Ẩn BottomBar
fun Fragment.hideBottomBar() = requireActivity().apply {
    requireActivity().apply {
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

//Hiển thị BottomBar
fun Fragment.showBottomBar() = requireActivity().apply {
    requireActivity().apply {
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.navigationBars())
    }
}

//Đặt màu StatusBar
fun Fragment.setStatusBarColor(@ColorRes color: Int) = requireActivity().apply {
    window.statusBarColor = ContextCompat.getColor(this, color)
}

// Chiều cao StatusBar (px)
val Fragment.getStatusBarHeight: Int
    get() {
        requireActivity().apply {
            val rect = Rect()
            window.decorView.getWindowVisibleDisplayFrame(rect)
            return rect.top
        }
    }

//Ẩn StatusBar
fun Fragment.hideStatusBar() = requireActivity().apply {
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

//Hiển thị StatusBar
fun Fragment.showStatusBar() = requireActivity().apply {
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.statusBars())
}

// bật Status bar và set độ trong suốt
@SuppressLint("ObsoleteSdkInt")
fun Fragment.setStatusBarHomeTransparent() = requireActivity().apply {
    if (Build.VERSION.SDK_INT >= 21) {
        with(window) {
            navigationBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            setWindowFlag(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false
            )
            statusBarColor = Color.TRANSPARENT
        }
    }
}

private fun Activity.setWindowFlag(bits: Int, on: Boolean) {
    window.attributes.also {
        it.flags = if (on) {
            it.flags or bits
        } else {
            it.flags and bits.inv()
        }
    }
}

//Set màu status bar
fun Fragment.setStatusBarDrawable(resId: Int) = requireActivity().apply {
    ContextCompat.getDrawable(this, resId)?.let {
        with(window) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = resources.getColor(R.color.transparent)
            navigationBarColor = resources.getColor(R.color.transparent)
            setBackgroundDrawable(it)
        }
    }
}

//Đặt màu NavigationBar
fun Fragment.setNavigationBarColor(@ColorRes color: Int) = requireActivity().apply {
    window.navigationBarColor = ContextCompat.getColor(this, color)
}

//Đặt màu dải phân cách NavigationBar
@RequiresApi(api = Build.VERSION_CODES.P)
fun Fragment.setNavigationBarDividerColor(@ColorRes color: Int) = requireActivity().apply {
    window.navigationBarDividerColor = ContextCompat.getColor(this, color)
}

//Bật chế độ full màn hình
fun Fragment.enterFullScreenMode() = requireActivity().apply {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

//Thoát chế độ full màn hình
fun Fragment.exitFullScreenMode() = requireActivity().apply {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

//Tắt chụp màn hình
fun Fragment.addSecureFlag() = requireActivity().apply {
    window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

//Mở chụp màn hình
fun Fragment.clearSecureFlag() = requireActivity().apply {
    window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

//Hiển thị bàn phím
fun Fragment.showKeyboard(toFocus: View) = requireActivity().apply {
    toFocus.requestFocus()
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(toFocus, InputMethodManager.SHOW_IMPLICIT)
}

//Ẩn bàn phím
fun Fragment.hideKeyboard() = requireActivity().apply {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(
            Context
                .INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}


//Thay đổi độ sáng
var Fragment.brightness: Float?
    get() {
        requireActivity().apply {
            return this.window?.attributes?.screenBrightness
        }
    }
    set(value) {
        requireActivity().apply {
            val window = this.window
            val layoutParams = window.attributes
            layoutParams?.screenBrightness = value //0 is turned off, 1 is full brightness
            window?.attributes = layoutParams
        }
    }

//Bật tính năng luôn cho màn hình sáng
fun Fragment.keepScreenOn() = requireActivity().apply {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

//Tắt tính năng Luôn cho màn hình sáng
fun Fragment.keepScreenOFF() = requireActivity().apply {
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}


//Khoá xoay màn hình
fun Fragment.lockOrientation() = requireActivity().apply {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
}

//Mở khoá xoay màn hình
fun Fragment.unlockScreenOrientation() = requireActivity().apply {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
}

//Khoá hướng màn hình hiện tại
fun Fragment.lockCurrentScreenOrientation() = requireActivity().apply {
    requestedOrientation = when (resources.configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }
}

//Check PIP
val Fragment.supportsPictureInPicture: Boolean
    get() {
        requireActivity().apply {
            return SDK_INT >= N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        }
    }

//Kích thước Activity (px)
val Fragment.displaySizePixels: Point
    get() {
        requireActivity().apply {
            var display = this.windowManager.defaultDisplay
            if (SDK_INT >= Build.VERSION_CODES.R) {
                display = this.display
            }
            return DisplayMetrics()
                .apply {
                    display.getRealMetrics(this)
                }.let {
                    Point(it.widthPixels, it.heightPixels)
                }
        }
    }

// Restart Activity
inline fun Fragment.restart(intentBuilder: Intent.() -> Unit = {}) = requireActivity().apply {
    val i = Intent(this, this::class.java)
    val oldExtras = intent.extras
    if (oldExtras != null)
        i.putExtras(oldExtras)
    i.intentBuilder()
    startActivity(i)
    finish()
}