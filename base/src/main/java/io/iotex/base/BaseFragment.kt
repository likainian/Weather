package io.iotex.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment(private val layoutId: Int) : Fragment(), IView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutId, container, false)
        initView()
        initEvent()
        initData()
        return view
    }

    protected open fun useEventBus() = false

    protected abstract fun initView()
    protected abstract fun initEvent()
    protected abstract fun initData()

    override fun showLoading(cancelOutside: Boolean) {
        (activity as? BaseActivity)?.showLoading()
    }

    override fun hideLoading() {
        (activity as? BaseActivity)?.hideLoading()
    }

    override fun isShowLoading(): Boolean {
        return (activity as? BaseActivity)?.isShowLoading() ?: false
    }

    override fun onDestroyView() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroyView()
    }
}