package com.ancientlore.lexio

import android.content.Context
import android.support.annotation.UiThread
import android.view.View
import android.widget.Filter
import android.widget.TextView

class WordsListAdapter(context: Context, items: MutableList<Word>):
		BaseListAdapter<Word, WordsListAdapter.ViewHolder>(context, items) {

	companion object {
		const val SEARCH_WORD = 0
		const val SEARCH_TRANSLATION = 1
	}

	private val wordFilter: WordFilter by lazy { WordFilter() }
	private val translationFilter: TranslationFilter by lazy { TranslationFilter() }

	var searchDirection: Int = SEARCH_WORD

	var currentTopic = ""

	@UiThread
	fun setItem(newItems: MutableList<Word>, topic: String) {
		filter.filter("")
		originalItems = newItems
		filteredItems = newItems
		currentTopic = topic
		notifyDataSetChanged()
	}

	@UiThread
	fun addItem(newWord: Word) {
		filter.filter("")
		originalItems.add(newWord)
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

	override fun getViewHolder(layout: View) = ViewHolder(layout)

	override fun getViewHolderLayoutRes(viewType: Int) = R.layout.dictionary_list_item

	override fun getFilter() =
		when (searchDirection) {
			SEARCH_WORD -> wordFilter
			else -> translationFilter
		}

	abstract inner class ListFilter: Filter() {
		override fun performFiltering(constraint: CharSequence): FilterResults {
			val resultList =
					if (constraint.isNotEmpty()) {
						val candidate = constraint.toString().toLowerCase()

						originalItems.filter { satisfy(it, candidate) }
					}
					else originalItems

			val result = FilterResults()
			result.count = resultList.size
			result.values = resultList

			return result
		}

		abstract fun satisfy(word: Word, candidate: String): Boolean

		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			filteredItems = results.values as MutableList<Word>
			notifyDataSetChanged()
		}
	}

	inner class WordFilter: ListFilter() {
		override fun satisfy(word: Word, candidate: String) = word.name.toLowerCase().startsWith(candidate)
	}

	inner class TranslationFilter: ListFilter() {
		override fun satisfy(word: Word, candidate: String) = word.translation.toLowerCase().startsWith(candidate)
	}

	class ViewHolder(itemView: View) : BaseViewHolder<Word>(itemView) {

		private val titleView: TextView = itemView.findViewById(R.id.title)
		private val subtitleView: TextView = itemView.findViewById(R.id.subtitle)

		override fun bind(data: Word) {
			titleView.text = data.name
			subtitleView.text = data.translation
		}
	}
}