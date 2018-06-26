package com.ancientlore.lexio

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField

class WordViewModel : ViewModel {

	val name : ObservableField<String>

	val translation : ObservableField<String>

	val transcription : ObservableField<String>

	internal fun getWord() =
		 Word(name = name.get().toString(),
				translation = translation.get().toString(),
				transcription = transcription.get().toString())

	constructor() : super() {
		this.name = ObservableField("")
		this.translation = ObservableField("")
		this.transcription = ObservableField("")
	}
}