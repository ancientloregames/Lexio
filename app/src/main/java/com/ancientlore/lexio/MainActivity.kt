package com.ancientlore.lexio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ancientlore.lexio.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
	companion object {
		const val INTENT_NEW_WORD = 101
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
			listView.adapter = listAdapter
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (resultCode != Activity.RESULT_OK) return

		when(requestCode) {
			INTENT_NEW_WORD -> {
				data?.let {
					val word = it.getParcelableExtra<Word>(WordActivity.EXTRA_WORD)
					word?.let { addWord(it) }
				}
			}
		}
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainViewModel()

	private fun addWord(word: Word) {
		dbExec.submit { addWordToDb(word) }
		runOnUiThread { listAdapter.addItem(word) }
	}

	@WorkerThread
	private fun addWordToDb(word: Word) {
		WordsDatabase.getInstance(this).wordDao().insert(word)
	}

	fun onAddWord(view: View) {
		val intent = Intent(this, WordActivity::class.java)
		startActivityForResult(intent, INTENT_NEW_WORD)
	}
}
