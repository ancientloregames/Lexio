package com.ancientlore.lexio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

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
}
