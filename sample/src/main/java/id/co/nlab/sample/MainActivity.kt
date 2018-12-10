package id.co.nlab.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import id.co.nlab.nframework.base.ViewState
import id.co.nlab.sample.adapter.PaginationActivity
import id.co.nlab.sample.permission.PermissionActivity
import id.co.nlab.sample.validation.SampleValidation
import id.co.nlab.sample.view.TextInputActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_validation.setOnClickListener { to(SampleValidation::class.java) }
        btn_permission.setOnClickListener { to(PermissionActivity::class.java) }
        btn_til.setOnClickListener { to(TextInputActivity::class.java) }
        btn_pagination.setOnClickListener { to(PaginationActivity::class.java) }
    }

    private fun to(toActivity: Class<*>){
        startActivity(Intent(this, toActivity))
    }
}
