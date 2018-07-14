package com.ancientlore.lexio

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.text.Editable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class WordViewModel : ViewModel {

	val id: Long

	val name: ObservableField<String> = ObservableField("")

	val translation: ObservableField<String> = ObservableField("")

	val transcription: ObservableField<String> = ObservableField("")

	val topicField: ObservableField<String> = ObservableField("")

	val topicsListField: ObservableField<String> = ObservableField("")

	val editable: ObservableBoolean = ObservableBoolean(true)

	private var withAutoTranslation = true

	private val topicsList = ArrayList<String>()

	constructor(withAutoTranslation: Boolean) {
		id = 0
		this.withAutoTranslation = withAutoTranslation
	}

	constructor(word: Word, withAutoTranslation: Boolean) {
		id = word.id
		name.set(word.name)
		translation.set(word.translation)
		transcription.set(word.transcription)
		word.topics.forEach { topicsList.add(it.name) }
		topicsListField.set(topicsList.joinToString())
		editable.set(false)
		this.withAutoTranslation = withAutoTranslation
	}

	val typeWordWatcher = object : SimpleTextWatcher() {

		private var execService = Executors.newSingleThreadScheduledExecutor { r -> Thread(r, "translate-worker") }
		private var execTask: ScheduledFuture<*>? = null

		override fun afterTextChanged(s: Editable) {
			if (withAutoTranslation) {
				execTask?.cancel(true)
				if (s.length > 2) {
					execTask = execService.schedule({
						Utils.getTranslation(s.toString(), setTranslation, printError)
					}, 200, TimeUnit.MILLISECONDS)
				}
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

	private fun getTopicsList(): ArrayList<Topic> {
		val list = ArrayList<Topic>()
		topicsList.forEach { list.add(Topic(it)) }
		return list
	}

	internal fun getWord() = Word(id,
			name.get().toString(),
			translation.get().toString(),
			transcription.get().toString(),
			getTopicsList())
}