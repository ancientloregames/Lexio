package com.ancientlore.lexio

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ancientlore.lexio.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
	companion object {
		const val INTENT_NEW_WORD = 101
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor()

	private lateinit var listAdapter: WordsListAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val listView: RecyclerView = findViewById(R.id.listView)

		listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

		val db = WordsDatabase.getInstance(this)

		dbExec.submit {
			listAdapter = WordsListAdapter(db.wordDao().getAll().toMutableList())
			listView.adapter = listAdapter
		}
	}

	override fun getLayoutId() = R.layout.activity_main

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = MainViewModel()
}
