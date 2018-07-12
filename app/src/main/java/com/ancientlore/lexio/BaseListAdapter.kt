package com.ancientlore.lexio

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable

abstract class BaseListAdapter<P, T: BaseViewHolder<P>>(context: Context, items: MutableList<P>):
		RecyclerView.Adapter<T>(), Filterable {

	interface Listener<P> {
		fun onItemSelected(item: P)
	}
	var listener: Listener<P>? = null

	protected var originalItems : MutableList<P> = items
	protected var filteredItems : MutableList<P> = items

	private val layoutInflater = LayoutInflater.from(context)

	abstract fun getViewHolderLayoutRes(viewType: Int): Int

	abstract fun getViewHolder(layout: View): T

	private fun getViewHolderLayout(parent: ViewGroup, layoutRes: Int) = layoutInflater.inflate(layoutRes, parent,false)

	override fun getItemCount() = filteredItems.count()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
		val layoutRes = getViewHolderLayoutRes(viewType)
		val layout = getViewHolderLayout(parent, layoutRes)
		return getViewHolder(layout)
	}

	override fun onBindViewHolder(holder: T, index: Int) {
		val item = filteredItems[index]
		holder.bind(item)
		holder.onClick(Runnable {
			listener?.onItemSelected(item)
		})
	}

	fun filter(constraint: String) {
		filter.filter(constraint)
	}
}