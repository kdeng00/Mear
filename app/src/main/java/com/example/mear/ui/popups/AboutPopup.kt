package com.example.mear.ui.popups

import android.animation.Animator
import android.content.Context
import android.view.View
import com.example.mear.R

import com.xu.xpopupwindow.XPopupWindow

class AboutPopup(ctx: Context): XPopupWindow(ctx) {
    override fun exitAnim(view: View): Animator? {
        return null
    }

    override fun getLayoutId(): Int {
        return R.layout.popup_layout
    }

    override fun getLayoutParentNodeId(): Int {
        return R.id.parent
    }

    override fun initData() {
    }

    override fun initViews() {
    }

    override fun startAnim(view: View): Animator? {
        return null
    }

    override fun animStyle(): Int {
        return -1
    }

}

