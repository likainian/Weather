package io.iotex.base.bindbase

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.BarUtils
import io.iotex.base.BaseActivity
import io.iotex.base.BaseViewModel
import java.lang.reflect.ParameterizedType


abstract class BaseBindActivity<T : BaseViewModel, K : ViewDataBinding>(private val layoutId: Int) :
    BaseActivity(layoutId) {

    protected val mBinding: K by lazy {
        DataBindingUtil.inflate<K>(layoutInflater, layoutId, null, false)
            .apply {
                lifecycleOwner = this@BaseBindActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mBinding.root.parent == null) {
            mBinding.lifecycleOwner = this
            setContentView(mBinding.root)
        }
    }

    override fun initView() {}
    override fun initData() {}
    override fun initEvent() {}
}
