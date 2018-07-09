package com.ancientlore.lexio

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView), Bindable<T>, Clickable {

	override fun onClick(action: Runnable) {
		itemView.setOnClickListener { action.run() }
	}
}