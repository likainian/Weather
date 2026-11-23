package io.iotex.base.bindbase

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseBindVH<T : ViewBinding>(viewBinding: T) : RecyclerView.ViewHolder(viewBinding.root) {
    var bind = viewBinding
}