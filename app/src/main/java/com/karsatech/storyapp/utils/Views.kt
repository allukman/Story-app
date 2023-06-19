package com.karsatech.storyapp.utils

import android.text.TextWatcher
import android.view.View
import android.widget.EditText

object Views {
    fun View.onCLick(action: () -> Unit) {
        this.setOnClickListener { action() }
    }

    fun EditText.onTextChanged(action: () -> TextWatcher) {
        this.addTextChangedListener(action())
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.INVISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

}