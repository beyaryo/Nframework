package id.co.nlab.sample.validation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import id.co.nlab.nframework.validation.Validation
import id.co.nlab.nframework.validation.ValidationDelegate
import id.co.nlab.sample.R
import kotlinx.android.synthetic.main.activity_validation.*

class SampleValidation :AppCompatActivity(), ValidationDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validation)

        val validator = Validation(this)

        validator.apply {
            registerField().requiredRule(username,"Invalid username","username")
            registerField().emailRule(email,"invalid email","email")
            registerField().confirmationRules(confrim_email,email,"Email not macth","confrim")
            registerField().regexRule(password,paswword_input,"^(?=.*[0-9])",
                    "Invalid Password Combination","password")
            validator.registerField().lengthRule(password,paswword_input,4,5,"Invalid Lenght","password")
        }

        register.setOnClickListener{
            validator.validation()
        }
    }

    override fun validationSuccess(data: HashMap<String, String>) {
        // Do something when all data validated
    }
}