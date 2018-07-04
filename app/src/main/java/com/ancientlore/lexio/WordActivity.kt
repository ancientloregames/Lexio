package com.ancientlore.lexio

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import com.ancientlore.lexio.databinding.ActivityWordBinding
import kotlinx.android.synthetic.main.activity_word.*

class WordActivity : BaseActivity<ActivityWordBinding, WordViewModel>() {

	companion object {
		const val EXTRA_WORD = "word"
	}

	override fun onResume() {
		super.onResume()

		val editable = !intent.hasExtra(EXTRA_WORD)

		switchKeyboard(editable)
	}

	override fun getLayoutId() = R.layout.activity_word

	override fun getBindingVariable() = BR.wordModel

	override fun createViewModel() = intent.getParcelableExtra<Word>(EXTRA_WORD)?.let { WordViewModel(it) } ?: WordViewModel()

	override fun getTitleId() = R.string.new_word

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_word, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.miEdit -> switchEditMode()
		}
		return true
	}

	fun onSubmitWord(view: View) {
		val activityResult = Intent()
		activityResult.putExtra(EXTRA_WORD, viewModel.getWord())
		setResult(Activity.RESULT_OK, activityResult)
		finish()
	}

	private fun switchEditMode() {
		val editable = viewModel.switchEditMode()
		switchKeyboard(editable)
	}

	private fun switchKeyboard(show: Boolean) {
		if (show) showKeyboard(word)
		else hideKeyboard()
	}

	private fun showKeyboard(targetView: View) {
		targetView.requestFocus()
		val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		imm.showSoftInput(targetView, SHOW_IMPLICIT)
	}

	private fun hideKeyboard() {
		currentFocus?.let {
			val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
			imm.hideSoftInputFromWindow(it.windowToken, 0)
		}
	}
}