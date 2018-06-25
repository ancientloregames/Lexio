package com.ancientlore.lexio

import com.ancientlore.lexio.databinding.ActivityWordBinding

class WordActivity : BaseActivity<ActivityWordBinding, WordViewModel>() {

	override fun getLayoutId() = R.layout.activity_word

	override fun getBindingVariable() = BR.wordModel

	override fun createViewModel() = WordViewModel()
}