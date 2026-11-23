package io.iotex.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import io.iotex.base.language.MultiLanguage
import io.iotex.base.widget.LoadingDialog
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity(private val layoutId: Int) : AppCompatActivity(), IView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        beforeInflate()
        if (layoutId != 0) {
            setContentView(layoutId)
        }
        initView()
        initEvent()
        initData()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(MultiLanguage.setLocal(newBase as Context))
    }

    protected open fun beforeInflate() {

    }

    protected open fun useEventBus() = false

    protected abstract fun initView()
    protected abstract fun initEvent()
    protected abstract fun initData()

    private var mLoadingDialog:LoadingDialog? = null

    override fun showLoading(cancelOutside: Boolean) {
        runOnUiThread {
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
        runOnUiThread {
            kotlin.runCatching {
                if (isShowLoading()) {
                    mLoadingDialog?.dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }
}
