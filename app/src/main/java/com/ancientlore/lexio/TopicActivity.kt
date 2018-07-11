package com.ancientlore.lexio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.ancientlore.lexio.databinding.ActivityTopicBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TopicActivity: BaseActivity<ActivityTopicBinding, TopicActivityViewModel>() {

	companion object {
		const val EXTRA_SELECTED_TOPIC = "selected_topic"
	}

	private val dbExec: ExecutorService = Executors.newSingleThreadExecutor { r -> Thread(r, "db_worker") }

	private lateinit var listAdapter: TopicsListAdapter

	private val wordAdapterListener = object: BaseListAdapter.Listener<Topic> {
		override fun onItemSelected(item: Topic) {
			onResult(item)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val listView: RecyclerView = findViewById(R.id.listView)

		listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

		val db = WordsDatabase.getInstance(this)

		dbExec.submit {
			listAdapter = TopicsListAdapter(this, db.topicDao().getAll().toMutableList())
			listAdapter.listener = wordAdapterListener
			listView.adapter = listAdapter
		}
	}

	override fun getLayoutId() = R.layout.activity_topic

	override fun getBindingVariable() = BR.viewModel

	override fun createViewModel() = TopicActivityViewModel()

	override fun getTitleId() = R.string.select_topic

	private fun onResult(topic: Topic) {
		val intent = Intent()
		intent.putExtra(EXTRA_SELECTED_TOPIC, topic.name)
		setResult(Activity.RESULT_OK, intent)
		finish()
	}
}