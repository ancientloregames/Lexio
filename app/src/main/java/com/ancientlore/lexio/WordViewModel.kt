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

	val name : ObservableField<String> = ObservableField("")

	val translation : ObservableField<String> = ObservableField("")

	val transcription : ObservableField<String> = ObservableField("")

	val editable : ObservableBoolean = ObservableBoolean(true)

	constructor() {
		id = 0
	}

	constructor(word: Word) {
		id = word.id
		name.set(word.name)
		translation.set(word.translation)
		transcription.set(word.transcription)
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

	fun isEditable() = editable.get()

	internal fun getWord() = Word(id, name.get().toString(), translation.get().toString(), transcription.get().toString())
}