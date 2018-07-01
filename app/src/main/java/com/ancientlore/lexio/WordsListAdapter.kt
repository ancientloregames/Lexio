package com.ancientlore.lexio

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class WordsListAdapter(private val items : MutableList<Word>) : RecyclerView.Adapter<WordsListAdapter.ViewHolder>() {
	interface Listener {
		fun onWordSelected(word: Word)
	}
	var listener: Listener? = null

	@UiThread
	fun addItem(newWord: Word) {
		items.add(newWord)
		notifyItemInserted(itemCount - 1)
	}

	@UiThread
	fun updateItem(word: Word) {
		items.indexOfFirst { it.id == word.id }.takeIf { it != -1 }?.let { index ->
			items[index] = word
			notifyItemChanged(index)
		}
	}

	override fun getItemCount(): Int {
		return items.count()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dictionary_list_item, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, index: Int) {
		val word = items[index]
		holder.bind(word)
		holder.setClickListener(Runnable {
			listener?.onWordSelected(word)
		})
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