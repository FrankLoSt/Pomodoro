package com.example.pomodoro.ui

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

sealed class ScreenShape {
    object PhonePortrait : ScreenShape()
    object PhoneLandscape : ScreenShape()
    object TabletPortrait : ScreenShape()
    object TabletLandscape : ScreenShape()

}

fun detectScreenShape(
    widthClass: WindowWidthSizeClass,
    heightClass: WindowHeightSizeClass,
    screenWidth: Int,
    screenHeight: Int
): ScreenShape {
    val isPortrait = screenHeight > screenWidth
    val isLandscape = screenWidth > screenHeight

    return when {
        widthClass == WindowWidthSizeClass.Compact && isPortrait -> ScreenShape.PhonePortrait
        widthClass == WindowWidthSizeClass.Compact && isLandscape -> ScreenShape.PhoneLandscape
        widthClass >= WindowWidthSizeClass.Medium && isPortrait -> ScreenShape.TabletPortrait
        widthClass >= WindowWidthSizeClass.Medium && isLandscape -> ScreenShape.TabletLandscape
        else -> ScreenShape.PhonePortrait // fallback
    }
}
