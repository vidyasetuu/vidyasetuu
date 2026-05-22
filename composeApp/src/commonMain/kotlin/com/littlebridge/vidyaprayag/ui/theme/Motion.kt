package com.littlebridge.vidyaprayag.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.TransformOrigin

/**
 * Motion design tokens for VidyaSetu / Vidya Prayag.
 *
 * The motion language is intentionally modeled after the iOS UIKit / SwiftUI
 * default spring + Material 3 "expressive" emphasized easing used in
 * Now in Android. Every transition in the app should source its
 * AnimationSpec from this file so behaviour stays uniform and tunable.
 */
object Motion {

    // ------------------------------------------------------------------
    // Easings — Material 3 "Expressive" set + an iOS-feel cubic-bezier.
    // ------------------------------------------------------------------

    /** Standard easing for everyday content transitions (M3 standard). */
    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Emphasized easing — used when an element enters/exits with intent. */
    val Emphasized: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    /** Emphasized decelerate — for incoming, expanding elements. */
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    /** Emphasized accelerate — for elements leaving the screen quickly. */
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    /** iOS-style cubic ease for modal / sheet / press feel. */
    val IOSEase: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

    // ------------------------------------------------------------------
    // Durations
    // ------------------------------------------------------------------

    object Duration {
        const val Quick: Int = 120
        const val Short: Int = 200
        const val Medium: Int = 320
        const val Long: Int = 480
        const val ExtraLong: Int = 600
    }

    // ------------------------------------------------------------------
    // Reusable AnimationSpecs — favour springs for organic feel.
    // ------------------------------------------------------------------

    /** Crisp spring used for press, scale, and small transforms. */
    fun <T> springQuick(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMediumLow
    )

    /** Soft spring used for content reveals and content size animations. */
    fun <T> springSoft(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.85f,
        stiffness = Spring.StiffnessLow
    )

    /** Bouncy spring used for hero / playful surfaces. */
    fun <T> springBouncy(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.55f,
        stiffness = Spring.StiffnessMediumLow
    )

    /** Tween built on emphasized easing for choreographed entries. */
    fun <T> tweenEmphasized(durationMs: Int = Duration.Medium): AnimationSpec<T> =
        tween(durationMillis = durationMs, easing = Emphasized)

    /** Tween built on iOS easing — used for sheets and modals. */
    fun <T> tweenIos(durationMs: Int = Duration.Medium): AnimationSpec<T> =
        tween(durationMillis = durationMs, easing = IOSEase)

    /** Default origin used for scale-in transitions on cards / sheets. */
    val DefaultScaleOrigin: TransformOrigin = TransformOrigin(0.5f, 0.55f)
}
