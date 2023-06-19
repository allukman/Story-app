package com.karsatech.storyapp.view.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.karsatech.storyapp.R
import com.karsatech.storyapp.utils.AppUtils
import com.karsatech.storyapp.utils.Views.onTextChanged

class CustomEditTextName : AppCompatEditText {

    private var txtString: String = ""

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        txtString = context.getString(R.string.name)

        hint = txtString
        inputType = InputType.TYPE_CLASS_TEXT
        onTextChanged {
            AppUtils.InitTextWatcher {
                when {
                    it.isEmpty() -> error = context.getString(R.string.error_empty_value, txtString)
                    it.length < 6 -> error = context.getString(R.string.error_length, txtString, 6)
                }
            }
        }
    }
}