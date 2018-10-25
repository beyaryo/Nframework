package id.co.nlab.sample.permission

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import id.co.nlab.nframework.permission.PermissionListener
import id.co.nlab.nframework.permission.PermissionManager
import id.co.nlab.sample.R
import kotlinx.android.synthetic.main.activity_permission.*


class PermissionActivity: AppCompatActivity(), PermissionListener {

    // PermissionManager instance
    private val manager by lazy { PermissionManager(this, this) }

    // Instance of single permission
    private val singlePermission = Manifest.permission.CAMERA
    // Instance of multiple permission
    private val multiplePermission = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        // Check if camera permission is granted
        if (PermissionManager.isGranted(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "Camera enabled", Toast.LENGTH_SHORT).show()
        }

        btn_single.setOnClickListener { manager.check(singlePermission, "") }

        btn_multiple.setOnClickListener { manager.check(multiplePermission, "") }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_permission, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_to_fragment -> startActivity(Intent(this, PermissionFragmentActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Will be called after user do action on permission dialog
     * REQUIRED inside this function must call
     * (PermissionManager instance).result()
     * */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        manager.result(requestCode, permissions, grantResults)
    }

    /**
     * Override from PermissionListener interface
     * No explanation needed for this method
     * Every one hope this method will be called
     * so the permission is granted XD
     */
    override fun onPermissionGranted(permissions: Array<out String>?, tag: String) {
        // Do something here when permission is granted
        val msg = StringBuilder("Granted (" + permissions?.size + ")")

        permissions?.let {
            for (perm in it) msg.append("\n").append(perm)
        }

        txt_granted?.text = msg
    }

    /**
     * Override from PermissionListener interface
     * This method will be called
     * when user click deny on some permission requested
     */
    override fun onPermissionDenied(permissions: Array<out String>?, tag: String) {
        val msg = StringBuilder("Denied (" + permissions?.size + ")")

        permissions?.let {
            for (perm in it) msg.append("\n").append(perm)
        }

        txt_denied?.text = msg
    }

    /**
     * Override from PermissionListener interface
     * This method will be called
     * when user click deny and check "Don't ask again" checkbox on some permission requested
     * or user disabled the permission from setting
     */
    override fun onPermissionDisabled(permissions: Array<out String>?, tag: String) {
        val msg = StringBuilder("Disabled (" + permissions?.size + ")")

        permissions?.let {
            for (perm in it) msg.append("\n").append(perm)
        }

        txt_disabled?.text = msg

        // Show alert dialog when some permissions are disabled
        manager.alert("Some permission is required", "Not now", "To setting")
    }
}