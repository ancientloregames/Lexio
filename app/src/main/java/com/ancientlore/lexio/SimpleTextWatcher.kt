package com.ancientlore.lexio

import android.text.TextWatcher

abstract class SimpleTextWatcher: TextWatcher {

	final override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

	final override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}