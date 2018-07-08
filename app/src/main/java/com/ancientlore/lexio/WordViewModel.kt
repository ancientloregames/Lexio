package com.ancientlore.lexio

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.text.Editable
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class WordViewModel : ViewModel {

	val id: Long

	val name : ObservableField<String> = ObservableField("")

	val translation : ObservableField<String> = ObservableField("")

	val transcription : ObservableField<String> = ObservableField("")

	val topicField: ObservableField<String> = ObservableField("")

	val topicsListField: ObservableField<String> = ObservableField("")

	val editable : ObservableBoolean = ObservableBoolean(true)

	private val topicsList = ArrayList<String>()

	constructor() {
		id = 0
	}

	constructor(word: Word) {
		id = word.id
		name.set(word.name)
		translation.set(word.translation)
		transcription.set(word.transcription)
		topicsList.addAll(word.topics)
		topicsListField.set(topicsList.joinToString())
		editable.set(false)
	}

	val typeWordWatcher = object : SimpleTextWatcher() {

		private var execService = Executors.newSingleThreadScheduledExecutor { r -> Thread(r, "translate-worker") }
		private var execTask: ScheduledFuture<*>? = null

		override fun afterTextChanged(s: Editable) {
			execTask?.cancel(true)
			if (s.length > 2) {
				execTask = execService.schedule( {
					Utils.getTranslation(s.toString(), setTranslation, printError)
				}, 200, TimeUnit.MILLISECONDS)
			}
		}

		private val setTranslation = object : Runnable1<String> {
			override fun run(translation: String) {
				setTranslation(translation)
			}
		}

		private val printError = object : Runnable1<Throwable> {
			override fun run(throwable: Throwable) {
				throwable.printStackTrace()
			}
		}
	}

	fun setTranslation(translation: String) {
		this.translation.set(translation)
	}

	fun switchEditMode(): Boolean {
		val wasEditable = editable.get()
		editable.set(!wasEditable)
		return !wasEditable
	}

	fun addTopic(text: String) {
		val newTopic = text.toLowerCase()
		if (isTopicNew(newTopic)) {
			topicField.set("")
			topicsList.add(newTopic)
			topicsListField.set(topicsList.joinToString())
		}
	}

	private fun isTopicNew(candidate: String) = candidate.isNotEmpty() && topicsList.none { it == candidate }

	fun isEditable() = editable.get()

	internal fun getWord() = Word(id,
			name.get().toString(),
			translation.get().toString(),
			transcription.get().toString(),
			topicsList)
}