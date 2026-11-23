package io.iotex.base.bindbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import io.iotex.base.BaseFragment
import io.iotex.base.BaseViewModel
import java.lang.reflect.ParameterizedType


abstract class BaseBindFragment<T : BaseViewModel, K : ViewDataBinding>(private val layoutId: Int) :
    BaseFragment(layoutId) {

    protected val mBinding: K by lazy {
        DataBindingUtil.inflate<K>(layoutInflater, layoutId, null, false)
            .apply {
                lifecycleOwner = this@BaseBindFragment
            }
    }

    protected val mViewModel by lazy {
        ViewModelProvider(this)[(getInstance<Class<T>>(this, 0))]
            .apply {
                lifecycle.addObserver(this)
            }
    }

    inline fun <reified T> getInstance(any: Any, i: Int): T {
        val type = (any.javaClass
            .genericSuperclass as ParameterizedType)
            .actualTypeArguments[i]
        return if (type is T) {
            type
        } else {
            throw IllegalArgumentException("can not find class")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initView()
        initEvent()
        initData()
        return mBinding.root
    }

    override fun initView() {

    }

    override fun initEvent() {

    }

    override fun initData() {

    }
}