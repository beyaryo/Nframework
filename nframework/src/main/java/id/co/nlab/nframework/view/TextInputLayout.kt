package id.co.nlab.nframework.view

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import id.co.nlab.nframework.R

class TextInputLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): TextInputLayout(context, attrs, defStyleAttr) {

    private var errorGravity: Int = 0
    private var errorColor: Int = 0

    init {
        context.theme.obtainStyledAttributes( attrs, R.styleable.TextInputLayout, 0, 0)
                .apply {
                    errorGravity = getInteger(R.styleable.TextInputLayout_errorGravity, 8388613)
                    errorColor = getResourceId(R.styleable.TextInputLayout_errorColor, android.R.color.holo_red_dark)
                }.recycle()
    }

    override fun setError(error: CharSequence?) {
        super.setError(error)

        if (!isErrorEnabled) return
        try{
            val errorField = TextInputLayout::class.java.getDeclaredField("mErrorView")
            errorField.isAccessible = true

            (errorField.get(this) as TextView).let {
                it.gravity = Gravity.CENTER
                it.setTextColor(ContextCompat.getColor(context, errorColor))
                it.textAlignment = errorGravity

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.gravity = Gravity.END
                it.layoutParams = params
            }
        }catch (e: Exception){ e.printStackTrace() }
    }
}