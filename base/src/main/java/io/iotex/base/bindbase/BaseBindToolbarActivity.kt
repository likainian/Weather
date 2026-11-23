package io.iotex.base.bindbase

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import io.iotex.base.BaseViewModel
import io.iotex.base.R
import io.iotex.base.databinding.ActivityBaseToolbarBinding


abstract class BaseBindToolbarActivity<T : BaseViewModel, K : ViewDataBinding>(layoutId: Int) :
    BaseBindActivity<T, K>(layoutId) {

    private val mBindingRoot by lazy {
        ActivityBaseToolbarBinding.inflate(layoutInflater)
    }

    private var back: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mBindingRoot.lifecycleOwner = this
        mBinding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mBindingRoot.llRoot.addView(mBinding.root)
        super.onCreate(savedInstanceState)
        setContentView(mBindingRoot.root)
        toolBar()
    }

    private fun toolBar() {
        mBindingRoot.ivToolbarBack.setOnClickListener {
            if (back != null) back?.invoke() else finish()
        }
    }

    fun setToolbarTitle(title: String) {
        mBindingRoot.tvToolbarTitle.text = title
    }

    fun setToolbarSubmitText(text: String) {
        mBindingRoot.tvToolbarSubmit.text = text
    }

    fun setToolbarSubmit(isVisible: Boolean) {
        mBindingRoot.flToolbarSubmit.isVisible = isVisible
    }

    fun setToolbarSubmitImage(@DrawableRes resId: Int) {
        mBindingRoot.ivToolbarSubmit.setImageResource(resId)
    }

    fun setToolbarSubmitThemeColor() {
        val color = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_title))
        mBindingRoot.ivToolbarSubmit.imageTintList = color
    }

    fun setToolbarSubmitClick(action: () -> Unit) {
        mBindingRoot.flToolbarSubmit.setOnClickListener {
            action.invoke()
        }
    }

    fun setToolbarBackClick(back: () -> Unit) {
        this.back = back
        mBindingRoot.ivToolbarBack.setOnClickListener {
            back.invoke()
        }
    }

    fun hideToolbarBack() {
        mBindingRoot.ivToolbarBack.isVisible = false
    }
}