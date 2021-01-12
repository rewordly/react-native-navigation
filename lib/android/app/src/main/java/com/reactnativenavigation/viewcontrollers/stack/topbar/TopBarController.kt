package com.reactnativenavigation.viewcontrollers.stack.topbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.core.view.doOnPreDraw
import androidx.viewpager.widget.ViewPager
import com.reactnativenavigation.options.AnimationOptions
import com.reactnativenavigation.options.Options
import com.reactnativenavigation.utils.CollectionUtils.forEachIndexed
import com.reactnativenavigation.utils.ViewUtils
import com.reactnativenavigation.utils.resetViewProperties
import com.reactnativenavigation.viewcontrollers.stack.topbar.button.ButtonController
import com.reactnativenavigation.viewcontrollers.stack.topbar.title.TitleBarReactViewController
import com.reactnativenavigation.views.stack.StackLayout
import com.reactnativenavigation.views.stack.topbar.TopBar
import com.reactnativenavigation.views.stack.topbar.titlebar.LeftButtonsBar
import com.reactnativenavigation.views.stack.topbar.titlebar.TitleBar


open class TopBarController(private val animator: TopBarAnimator = TopBarAnimator()) {
    lateinit var view: TopBar
    private lateinit var titleBar: TitleBar
    private lateinit var leftButtonsBar: LeftButtonsBar


    val height: Int
        get() = view.height
    val rightButtonsCount: Int
        get() = view.rightButtonsCount
    val leftButtonsCount: Int
        get() = leftButtonsBar.buttonsCount

    fun getRightButton(index: Int): MenuItem = titleBar.getButton(index)

    fun createView(context: Context, parent: StackLayout): TopBar {
        if (!::view.isInitialized) {
            view = createTopBar(context, parent)
            titleBar = view.titleBar
            leftButtonsBar = view.leftButtonsBar
            animator.bindView(view)
        }
        return view
    }

    protected open fun createTopBar(context: Context, stackLayout: StackLayout): TopBar {
        return TopBar(context)
    }

    fun initTopTabs(viewPager: ViewPager?) = view.initTopTabs(viewPager)

    fun clearTopTabs() = view.clearTopTabs()

    fun getPushAnimation(appearingOptions: Options, additionalDy: Float = 0f): Animator? {
        if (appearingOptions.topBar.animate.isFalse) return null
        return animator.getPushAnimation(
                appearingOptions.animations.push.topBar,
                appearingOptions.topBar.visible,
                additionalDy
        )
    }

    fun getPopAnimation(appearingOptions: Options, disappearingOptions: Options): Animator? {
        if (appearingOptions.topBar.animate.isFalse) return null
        return animator.getPopAnimation(
                disappearingOptions.animations.pop.topBar,
                appearingOptions.topBar.visible
        )
    }

    fun getSetStackRootAnimation(appearingOptions: Options, additionalDy: Float = 0f): Animator? {
        if (appearingOptions.topBar.animate.isFalse) return null
        return animator.getSetStackRootAnimation(
                appearingOptions.animations.setStackRoot.topBar,
                appearingOptions.topBar.visible,
                additionalDy
        )
    }

    fun show() {
        if (ViewUtils.isVisible(view) || animator.isAnimatingShow()) return
        view.resetViewProperties()
        view.visibility = View.VISIBLE
    }

    fun showAnimate(options: AnimationOptions, additionalDy: Float) {
        if (ViewUtils.isVisible(view) || animator.isAnimatingShow()) return
        animator.show(options, additionalDy)
    }

    fun hide() {
        if (!animator.isAnimatingHide()) view.visibility = View.GONE
    }

    fun hideAnimate(options: AnimationOptions, additionalDy: Float) {
        if (!ViewUtils.isVisible(view) || animator.isAnimatingHide()) return
        animator.hide(options, additionalDy)
    }

    fun setTitleComponent(component: TitleBarReactViewController) {
        view.setTitleComponent(component.view)
    }

    fun applyRightButtons(toAdd: List<ButtonController>) {
        view.clearRightButtons()
        toAdd.forEachIndexed { i, b -> b.addToMenu(titleBar, (toAdd.size - i) * 10) }
    }

    fun mergeRightButtons(toAdd: List<ButtonController>, toRemove: List<ButtonController>) {
        toRemove.forEach { view.removeRightButton(it) }
        toAdd.forEachIndexed { i, b -> b.addToMenu(titleBar, (toAdd.size - i) * 10) }
    }

    open fun applyLeftButtons(toAdd: List<ButtonController>) {
        leftButtonsBar.minimumWidth = leftButtonsBar.width
        view.clearBackButton()
        view.clearLeftButtons()
        forEachIndexed(toAdd) { b: ButtonController, i: Int -> b.addToMenu(leftButtonsBar, (toAdd.size - i) * 10) }
        leftButtonsBar.doOnPreDraw { leftButtonsBar.minimumWidth = 0 }
    }

    open fun mergeLeftButtons(toAdd: List<ButtonController>, toRemove: List<ButtonController>) {
        leftButtonsBar.minimumWidth = leftButtonsBar.width
        view.clearBackButton();
        toRemove.forEach {view.removeLeftButton(it) }
        forEachIndexed(toAdd) { b: ButtonController, i: Int -> b.addToMenu(leftButtonsBar, (toAdd.size - i) * 10) }
        leftButtonsBar.doOnPreDraw { leftButtonsBar.minimumWidth = 0 }
    }
}