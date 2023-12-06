package com.example.mongodbrealmcourse.view.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.mongodbrealmcourse.viewmodel.utils.PreferenceHelper
import com.example.mongodbrealmcourse.R
import com.example.mongodbrealmcourse.databinding.ItemViewLoginBinding
import com.example.mongodbrealmcourse.viewmodel.utils.DrawableHelper


class LoginItemView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {
    private val binding = ItemViewLoginBinding.inflate(LayoutInflater.from(context), this, true)
    private var icon: Drawable? = null
    private var iconRed: Drawable? = null
    private val preferenceHelper: PreferenceHelper by lazy { PreferenceHelper(context) }

    init {
        val textHint: String?
        val isAsterisk: Boolean
        val inputType: Int?
        val focusable: Boolean

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LoginItemView, 0, 0)
        try {
            icon = a.getDrawable(R.styleable.LoginItemView_item_icon)
            iconRed = a.getDrawable(R.styleable.LoginItemView_item_icon_red)
            textHint = a.getString(R.styleable.LoginItemView_item_hint)
            isAsterisk = a.getBoolean(R.styleable.LoginItemView_item_asterisk, false)
            inputType = a.getInt(
                R.styleable.LoginItemView_android_inputType, InputType.TYPE_TEXT_VARIATION_NORMAL
            )
            focusable = a
                .getBoolean(R.styleable.LoginItemView_android_focusable, true)
        } finally {
            a.recycle()
        }

        binding.apply {
//            viewContent.background =
//                DrawableHelper.rectangle(
//                    context,
//                    if (preferenceHelper.isNightMode) R.color.colorText_Night_2 else R.color.colorText_Day_2,
//                    1f, 8f
//                )

            icon?.apply { ivIcon.setImageDrawable(this) }
            etContent.hint = textHint
            tvAsterisk.visibility = if (isAsterisk) View.VISIBLE else View.GONE
            inputType?.apply { etContent.inputType = this }
            etContent.isFocusable = focusable
        }
    }

    fun showWarning(warning: String) {
        binding.apply {
            viewContent.background = DrawableHelper.rectangle(context, R.color.colorAccent, 1f, 13f)
            iconRed?.apply { ivIcon.setImageDrawable(this) }
            tvWarning.visibility = View.VISIBLE
            tvWarning.text = warning
        }
    }

    fun hideWarning() {
        binding.apply {
            val drawable = ContextCompat.getDrawable(context, R.drawable.bg_round_purpil_4)
            viewContent.background = drawable
            icon?.apply { ivIcon.setImageDrawable(this) }
            tvWarning.visibility = View.GONE
        }
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        binding.etContent.addTextChangedListener(textWatcher)
    }

    fun getContent(): String {
        return binding.etContent.text.toString()
    }

    fun setContent(content: String) {
        binding.etContent.setText(content)
    }

    fun isFocus(): Boolean {
        return binding.etContent.isFocused
    }

    fun getViewContent(): View {
        return binding.viewContent
    }

    fun getViewEditContent(): View {
        return binding.etContent
    }

}