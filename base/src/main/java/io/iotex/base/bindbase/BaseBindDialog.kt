package io.iotex.base.bindbase

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import io.iotex.base.BaseViewModel
import io.iotex.base.IView
import io.iotex.base.widget.LoadingDialog
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

abstract class BaseBindDialog<T : BaseViewModel, K : ViewDataBinding>(private val layoutId: Int) :
    DialogFragment(), IView {

    protected val mBinding: K by lazy {
        DataBindingUtil.inflate<K>(layoutInflater, layoutId, null, false)
            .apply {
                lifecycleOwner = this@BaseBindDialog
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

    protected open fun useEventBus() = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initView()
        initEvent()
        initData()
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStart() {
        super.onStart()
        val attributes = dialog?.window?.attributes
        attributes?.width = ScreenUtils.getScreenWidth()- ConvertUtils.dp2px(50f)
        dialog?.window?.attributes = attributes
    }

    protected open fun initView(){

    }
    protected open fun initData(){

    }

    protected open fun initEvent(){

    }


    private var mLoadingDialog:LoadingDialog? = null

    override fun showLoading(cancelOutside:Boolean) {
        MainScope().launch {
            if (!isShowLoading()) {
                mLoadingDialog = LoadingDialog()
                mLoadingDialog?.cancelOutside = cancelOutside
                mLoadingDialog?.show()
            }
        }
    }

    override fun isShowLoading(): Boolean{
        return mLoadingDialog?.isShow == true
    }

    override fun hideLoading() {
        MainScope().launch {
            if (isShowLoading()) {
                mLoadingDialog?.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }
}