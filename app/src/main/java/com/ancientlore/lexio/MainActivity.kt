package com.ancientlore.lexio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ancientlore.lexio.WordActivity.Companion.EXTRA_WORD
import com.ancientlore.lexio.WordsListAdapter.Companion.SEARCH_TRANSLATION
import com.ancientlore.lexio.WordsListAdapter.Companion.SEARCH_WORD
import com.ancientlore.lexio.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), SearchView.OnQueryTextListener {
	companion object {
		const val INTENT_ADD_WORD = 101
		const val INTENT_UPDATE_WORD = 102
		const val INTENT_SELECT_TOPIC = 103
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private lateinit var listAdapter: WordsListAdapter

	private lateinit var searchView: SearchView

	private val wordAdapterListener = object: BaseListAdapter.Listener<Word> {
		override fun onItemSelected(item: Word) {
			val intent = Intent(this@MainActivity, WordActivity::class.java)
			intent.putExtra(EXTRA_WORD, item)
			startActivityForResult(intent, INTENT_UPDATE_WORD)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val listView: RecyclerView = findViewById(R.id.listView)

		listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

		val db = WordsDatabase.getInstance(this)

		dbExec.submit {
			listAdapter = WordsListAdapter(this, db.wordDao().getAll().toMutableList())
			listAdapter.listener = wordAdapterListener
			listView.adapter = listAdapter
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)

		val searchItem = menu.findItem(R.id.miSearch)
		searchView = searchItem.actionView as SearchView
		searchView.setOnQueryTextListener(this)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when (item?.itemId) {
			R.id.miOrigSearch -> listAdapter.searchDirection = SEARCH_WORD
			R.id.miTransSearch -> listAdapter.searchDirection = SEARCH_TRANSLATION
			R.id.miSelectTopic -> selectTopic()
		}
		return true
	}

	override fun onQueryTextSubmit(query: String): Boolean {
		listAdapter.filter(query)
		return false
	}

	override fun onQueryTextChange(newText: String): Boolean {
		listAdapter.filter(newText)
		return false
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (resultCode != Activity.RESULT_OK) return

		when(requestCode) {
			INTENT_ADD_WORD ->
				data?.let { it.getParcelableExtra<Word>(WordActivity.EXTRA_WORD).let {
					addWord(it)
					it.topics.forEach { topic -> addTopicToDb(topic) }
				} }
			INTENT_UPDATE_WORD ->
				data?.let { it.getParcelableExtra<Word>(WordActivity.EXTRA_WORD)?.let {
					updateWord(it)
					it.topics.forEach { topic -> addTopicToDb(topic) }
				} }
			INTENT_SELECT_TOPIC ->
				data?.let { it.getParcelableExtra<Topic>(TopicActivity.EXTRA_SELECTED_TOPIC)
						?.let { getTopicWordsFromDb(it) } }
		}
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainViewModel()

	override fun getTitleId() = R.string.my_words

	private fun addWord(word: Word) {
		addWordToDb(word)
		runOnUiThread { listAdapter.addItem(word) }
	}

	private fun updateWord(word: Word) {
		updateWordInDb(word)
		runOnUiThread { listAdapter.updateItem(word) }
	}

	private fun addWordToDb(word: Word) {
		dbExec.submit { WordsDatabase.getInstance(this).wordDao().insert(word) }
	}

	private fun updateWordInDb(word: Word) {
		dbExec.submit {  WordsDatabase.getInstance(this).wordDao().update(word) }
	}

	private fun addTopicToDb(topic: Topic) {
		dbExec.submit { WordsDatabase.getInstance(this).topicDao().insert(topic) }
	}

	private fun getTopicWordsFromDb(topic: Topic) {
		dbExec.submit {
			val topicWords = WordsDatabase.getInstance(this).wordDao().findAllByTopic(topic.name)
			runOnUiThread { listAdapter.setItem(topicWords.toMutableList()) }
		}
	}

	fun onAddWord(view: View) {
		val intent = Intent(this, WordActivity::class.java)
		startActivityForResult(intent, INTENT_ADD_WORD)
	}

	fun selectTopic() {
		val intent = Intent(this, TopicActivity::class.java)
		startActivityForResult(intent, INTENT_SELECT_TOPIC)
	}
}
