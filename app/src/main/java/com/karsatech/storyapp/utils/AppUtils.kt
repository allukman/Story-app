package com.karsatech.storyapp.utils

import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern

object AppUtils {
    class InitTextWatcher(
        private val onTextChange:(String) -> Unit
    ): TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onTextChange(p0.toString())
        }
        override fun afterTextChanged(p0: Editable?) {}
    }

    fun isEmailValid(email: String): Boolean {
        val p = Pattern.compile("[A-Z0-9a-z.-_]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,3}")
        return p.matcher(email).matches()
    }
}