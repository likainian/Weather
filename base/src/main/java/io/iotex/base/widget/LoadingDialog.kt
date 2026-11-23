package io.iotex.base.widget

import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import io.iotex.base.BaseViewModel
import io.iotex.base.R
import io.iotex.base.bindbase.BaseBindDialog
import io.iotex.base.databinding.DialogLoadingBinding

class LoadingDialog : BaseBindDialog<BaseViewModel, DialogLoadingBinding>(R.layout.dialog_loading) {

    var isShow = false
    var cancelOutside = false

    override fun initView() {
        if(cancelOutside && isShow){
            mBinding.root.setOnClickListener {
                dismiss()
            }
        }
    }

    fun show() {
        val activity = ActivityUtils.getTopActivity() as? AppCompatActivity ?: return
        if (activity.isFinishing || activity.isDestroyed) return
        if (ActivityUtils.isActivityAlive(activity) && !isShow && !activity.supportFragmentManager.isStateSaved) {
            isShow = true
            show(
                activity.supportFragmentManager,
                System.currentTimeMillis().toString()
            )
        }
    }

    override fun dismiss() {
        isShow = false
        super.dismissAllowingStateLoss()
    }
}