package com.ancientlore.lexio

import android.arch.lifecycle.ViewModel
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity<T : ViewDataBinding, V : ViewModel> : AppCompatActivity() {
	private lateinit var viewDataBinding : T
	protected lateinit var viewModel : V

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())

		setSupportActionBar(findViewById(R.id.toolbar))
		supportActionBar?.title = getString(getTitleId())

		viewDataBinding.setLifecycleOwner(this)
		viewModel = createViewModel()
		viewDataBinding.setVariable(getBindingVariable(), viewModel)
		viewDataBinding.executePendingBindings()
	}

	@LayoutRes
	abstract fun getLayoutId() : Int

	abstract fun getBindingVariable() : Int

	abstract fun createViewModel() : V

	@StringRes
	abstract fun getTitleId() : Int
}