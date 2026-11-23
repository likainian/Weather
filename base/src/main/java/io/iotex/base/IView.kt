package io.iotex.base

interface IView {

    fun showLoading(cancelOutside:Boolean = true)

    fun isShowLoading(): Boolean

    fun hideLoading()
}