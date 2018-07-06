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

	companion object {
		const val SEARCH_WORD = 0
		const val SEARCH_TRANSLATION = 1
	}

	interface Listener {
		fun onWordSelected(word: Word)
	}
	var listener: Listener? = null

	private val originalItems : MutableList<Word> = items
	private var filteredItems : MutableList<Word> = items

	private val wordFilter: WordFilter by lazy { WordFilter() }
	private val translationFilter: TranslationFilter by lazy { TranslationFilter() }

	var searchDirection: Int = SEARCH_WORD

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