package com.ancientlore.lexio

import android.app.Activity
import android.content.Intent
import android.view.View
import com.ancientlore.lexio.databinding.ActivityWordBinding

class WordActivity : BaseActivity<ActivityWordBinding, WordViewModel>() {

	companion object {
		const val EXTRA_WORD = "word"
	}

	override fun getLayoutId() = R.layout.activity_word

	override fun getBindingVariable() = BR.wordModel

	override fun createViewModel() = intent.getParcelableExtra<Word>(EXTRA_WORD)?.let { WordViewModel(it) } ?: WordViewModel()

	fun onSubmitWord(view: View) {
		val activityResult = Intent()
		activityResult.putExtra(EXTRA_WORD, viewModel.getWord())
		setResult(Activity.RESULT_OK, activityResult)
		finish()
	}
}