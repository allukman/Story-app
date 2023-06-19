package com.karsatech.storyapp.view.customview

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.karsatech.storyapp.R
import com.karsatech.storyapp.utils.AppUtils.InitTextWatcher
import com.karsatech.storyapp.utils.AppUtils.isEmailValid
import com.karsatech.storyapp.utils.Views.onTextChanged

class CustomEditTextEmail : AppCompatEditText {

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
        txtString = context.getString(R.string.email)

        hint = txtString
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        onTextChanged {
            InitTextWatcher {
                when {
                    it.isEmpty() -> error = context.getString(R.string.error_empty_value, txtString)
                    !isEmailValid(it) -> error = context.getString(R.string.error_email_format)
                }
            }
        }
    }
}