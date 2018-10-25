package id.co.nlab.sample.permission

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import id.co.nlab.sample.R


class PermissionFragmentActivity: AppCompatActivity() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_fragment)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, PermissionFragment())
                .commit()
    }
}