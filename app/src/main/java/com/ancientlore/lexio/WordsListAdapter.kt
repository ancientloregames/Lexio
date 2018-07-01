package com.ancientlore.lexio

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

class WordsListAdapter(items: MutableList<Word>) : RecyclerView.Adapter<WordsListAdapter.ViewHolder>(), Filterable {
	interface Listener {
		fun onWordSelected(word: Word)
	}
	var listener: Listener? = null

	private val originalItems : MutableList<Word> = items
	private var filteredItems : MutableList<Word> = items

	private var wordFilter: WordFilter? = null

	@UiThread
	fun addItem(newWord: Word) {
		originalItems.add(newWord)
		filteredItems.add(newWord)
		notifyItemInserted(itemCount - 1)
	}

	@UiThread
	fun updateItem(word: Word) {
		filter.filter("")
		originalItems.indexOfFirst { it.id == word.id }.takeIf { it != -1 }?.let { index ->
			originalItems[index] = word
			notifyItemChanged(index)
		}
	}

	override fun getItemCount(): Int {
		return filteredItems.count()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dictionary_list_item, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, index: Int) {
		val word = filteredItems[index]
		holder.bind(word)
		holder.setClickListener(Runnable {
			listener?.onWordSelected(word)
		})
	}

	fun filter(constraint: String) {
		filter.filter(constraint)
	}

	override fun getFilter() = wordFilter ?: WordFilter()

	inner class WordFilter: Filter() {
		override fun performFiltering(constraint: CharSequence): FilterResults {
			val result = FilterResults()

			if (constraint.isNotEmpty())
			{
				val text = constraint.toString().toLowerCase()

				val list = originalItems

				val itemsCount = list.count()
				val resultList = ArrayList<Word>(itemsCount)

				for (i in 0 until itemsCount) {
					val word = list[i]
					if (word.name.toLowerCase().startsWith(text)) resultList.add(word)
				}
				result.count = resultList.size
				result.values = resultList
			}
			else
			{
				result.count = originalItems.size
				result.values = originalItems
			}

			return result
		}

		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			filteredItems = results.values as ArrayList<Word>
			notifyDataSetChanged()
		}
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		private val titleView: TextView = itemView.findViewById(R.id.title)
		private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)

		fun bind(data: Word) {
			titleView.text = data.name
			subtitleView.text = data.translation
		}

		fun setClickListener(action: Runnable) {
			itemView.setOnClickListener { action.run() }
		}
	}
}