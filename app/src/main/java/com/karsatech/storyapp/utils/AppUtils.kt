package com.karsatech.storyapp.utils

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.RequiresApi
import com.karsatech.storyapp.R
import java.time.LocalTime
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateGreeting(context: Context): String {

        val morning = context.getString(R.string.good_morning)
        val afternoon = context.getString(R.string.good_afternoon)
        val evening = context.getString(R.string.good_evening)
        val night = context.getString(R.string.good_night)

        val currentTime = LocalTime.now()
        val greeting = when (currentTime.hour) {
            in 6..11 -> morning
            in 12..17 -> afternoon
            in 18..20 -> evening
            else -> night
        }

        return "$greeting"
    }
}