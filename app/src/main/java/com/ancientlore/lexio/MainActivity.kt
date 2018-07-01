package com.ancientlore.lexio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ancientlore.lexio.WordActivity.Companion.EXTRA_WORD
import com.ancientlore.lexio.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), WordsListAdapter.Listener {

	companion object {
		const val INTENT_ADD_WORD = 101
		const val INTENT_UPDATE_WORD = 102
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private lateinit var listAdapter: WordsListAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val listView: RecyclerView = findViewById(R.id.listView)

		listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

		val db = WordsDatabase.getInstance(this)

		dbExec.submit {
			listAdapter = WordsListAdapter(db.wordDao().getAll().toMutableList())
			listAdapter.listener = this
			listView.adapter = listAdapter
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (resultCode != Activity.RESULT_OK) return

		when(requestCode) {
			INTENT_ADD_WORD ->
				data?.let { it.getParcelableExtra<Word>(WordActivity.EXTRA_WORD).let { addWord(it) } }
			INTENT_UPDATE_WORD ->
				data?.let { it.getParcelableExtra<Word>(WordActivity.EXTRA_WORD)?.let { updateWord(it) } }
		}
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainViewModel()

	private fun addWord(word: Word) {
		dbExec.submit { addWordToDb(word) }
		runOnUiThread { listAdapter.addItem(word) }
	}

	private fun updateWord(word: Word) {
		dbExec.submit { updateWordInDb(word) }
		runOnUiThread { listAdapter.updateItem(word) }
	}

	@WorkerThread
	private fun addWordToDb(word: Word) {
		WordsDatabase.getInstance(this).wordDao().insert(word)
	}

	@WorkerThread
	private fun updateWordInDb(word: Word) {
		WordsDatabase.getInstance(this).wordDao().update(word)
	}

	fun onAddWord(view: View) {
		val intent = Intent(this, WordActivity::class.java)
		startActivityForResult(intent, INTENT_ADD_WORD)
	}

	override fun onWordSelected(word: Word) {
		val intent = Intent(this, WordActivity::class.java)
		intent.putExtra(EXTRA_WORD, word)
		startActivityForResult(intent, INTENT_UPDATE_WORD)
	}
}
