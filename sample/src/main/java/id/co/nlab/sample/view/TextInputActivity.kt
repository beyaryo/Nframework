package id.co.nlab.sample.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import id.co.nlab.nframework.validation.Validation
import id.co.nlab.nframework.validation.ValidationDelegate
import id.co.nlab.sample.R
import kotlinx.android.synthetic.main.activity_textinput.*

class TextInputActivity: AppCompatActivity(), ValidationDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_textinput)

        val validation = Validation(this).apply {
            registerField().requiredRule(edit_default, til_default, "Default Error", "default")
            registerField().requiredRule(edit_center, til_center, "Error in center", "center")
            registerField().requiredRule(edit_end, til_end, "Error in end", "end")
        }

        btn_validate.setOnClickListener { validation.validation() }
    }

    override fun validationSuccess(data: HashMap<String, String>) {
        // Do something here
    }
}