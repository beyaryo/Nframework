package id.co.nlab.sample.permission

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import id.co.nlab.nframework.permission.PermissionListener
import id.co.nlab.nframework.permission.PermissionManager
import id.co.nlab.sample.R
import kotlinx.android.synthetic.main.fragment_permission.*


class PermissionFragment: Fragment(), PermissionListener {

    private val manager by lazy { PermissionManager(this, this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_permission.setOnClickListener {
            manager.check(Manifest.permission.WRITE_CALENDAR)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        manager.result(requestCode, permissions, grantResults)
    }

    override fun onPermissionGranted(permissions: Array<out String>?, tag: String) {
        Toast.makeText(context, "Permission granted " + permissions!!.size, Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDenied(permissions: Array<out String>?, tag: String) {
        Toast.makeText(context, "Permission denied " + permissions!!.size, Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDisabled(permissions: Array<out String>?, tag: String) {
        manager.alert("Permission required", "Not Now", "To Setting",
                ::whenNegativeClicked,
                {
                    Toast.makeText(context, "Positive Button Clicked", Toast.LENGTH_SHORT).show()
                })
    }

    private fun whenNegativeClicked(){
        Toast.makeText(context, "Negative Button Clicked", Toast.LENGTH_SHORT).show()
    }
}