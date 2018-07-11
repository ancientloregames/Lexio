package com.ancientlore.lexio

import android.content.Context
import android.view.View
import android.widget.Filter
import android.widget.TextView

class TopicsListAdapter(context: Context, items: MutableList<Topic>):
		BaseListAdapter<Topic, TopicsListAdapter.ViewHolder>(context, items) {

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.topics_list_item

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun getFilter(): Filter {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	class ViewHolder(itemView: View): BaseViewHolder<Topic>(itemView) {

		private val titleView: TextView = itemView.findViewById(R.id.title)

		override fun bind(data: Topic) {
			titleView.text = data.name
		}
	}
}