package id.co.nlab.nframework.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import java.util.*


interface PermissionListener{
    fun onPermissionGranted(permissions: Array<out String>?, tag: String)
    fun onPermissionDenied(permissions: Array<out String>?, tag: String)
    fun onPermissionDisabled(permissions: Array<out String>?, tag: String)
}

class PermissionManager private constructor() {

    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var listener: PermissionListener? = null
    private val REQ_PERMISSION = 27615
    private var TAG = ""

    constructor(activity: Activity?, listener: PermissionListener?): this(){
        this.activity = activity
        this.listener = listener
    }

    constructor(fragment: Fragment?, listener: PermissionListener?): this(){
        this.fragment = fragment
        this.listener = listener
    }

    companion object {
        fun isGranted(context: Context, permission: String): Boolean{
            // Check device OS
            // When device OS is 22 or below,
            // don't worry, it's always enabled
            return Build.VERSION.SDK_INT <= 22 ||
                    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isGranted(context: Context, permission: String): Boolean{
        return PermissionManager.isGranted(context, permission)
    }

    /**
     * Request list permissions without tag
     */
    fun check(permissions: List<String>) {
        check(permissions, "")
    }

    /**
     * Request list permissions with tag
     */
    fun check(permissions: List<String>, tag: String) {
        check(permissions, tag)
    }

    /**
     * Request single permission without tag
     */
    fun check(permission: String) {
        val permissions = arrayOf(permission)
        check(permissions, "")
    }

    /**
     * Request single permission with tag
     */
    fun check(permission: String, tag: String) {
        val permissions = arrayOf(permission)
        check(permissions, tag)
    }

    /**
     * Request multiple permissions without tag
     */
    fun check(permissions: Array<String>) {
        check(permissions, "")
    }

    /**
     * Request multiple permission with tag
     */
    fun check(permissions: Array<out String>?, tag: String) {
        this.TAG = tag

        // Check device OS
        if (Build.VERSION.SDK_INT <= 22) {
            // When device OS is 22 or below,
            // don't worry, it's always enabled
            listener?.onPermissionGranted(permissions, TAG)
        } else if (permissions != null && permissions.isNotEmpty()) {
            // If list not empty, request all permissions
            if (activity != null)
                activity!!.requestPermissions(permissions, REQ_PERMISSION)
            else
                fragment!!.requestPermissions(permissions, REQ_PERMISSION)
        }
    }

    /**
     * Request all permissions in manifest without tag
     */
    fun checkAllFromManifest() {
        checkAllFromManifest("")
    }

    /**
     * Request all permissions in manifest with tag
     */
    fun checkAllFromManifest(tag: String) {
        var permissions = arrayOf<String>()

        try {
            permissions = (if(activity != null)
                activity!!.packageManager!!.getPackageInfo(activity!!.packageName, PackageManager.GET_PERMISSIONS)!!
            else
                fragment!!.context!!.packageManager.getPackageInfo(fragment!!.activity!!.packageName, PackageManager.GET_PERMISSIONS))
                    .requestedPermissions
        } catch (ignore: Exception) {}

        check(permissions, tag)
    }

    /**
     * Check the result after request permission
     * Must be called inside onRequestPermissionsResult()
     */
    fun result(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // Check requestCode
        if (requestCode == REQ_PERMISSION) {

            // Vessel of all granted permissions
            val granted = ArrayList<String>()
            // Vessel of all denied permissions
            val denied = ArrayList<String>()
            // Vessel of all permissions which are
            // denied and rationale checkbox is checked
            val disabled = ArrayList<String>()

            // Iterate all permissions then
            // put every single permission to each vessel
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    granted.add(permissions[i])
                else if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permissions[i]))
                    denied.add(permissions[i])
                else if (fragment!!.shouldShowRequestPermissionRationale(permissions[i]))
                    denied.add(permissions[i])
                else
                    disabled.add(permissions[i])
            }

            if(granted.isNotEmpty()) listener?.onPermissionGranted(granted.array(), TAG)
            if(denied.isNotEmpty()) listener?.onPermissionDenied(denied.array(), TAG)
            if(disabled.isNotEmpty()) listener?.onPermissionDisabled(disabled.array(), TAG)
        }
    }

    /**
     * Show alert dialog to redirect user to setting page
     * WARNING Activity must use Theme.AppCompat theme (or descendant)
     */
    fun alert(body: String, negativeButton: String, positiveButton: String) {
        alert(body, negativeButton, positiveButton, ::doNothing, ::openSetting)
    }

    fun alert(body: String, negativeButton: String, positiveButton: String, negativeAction: () -> Unit){
        alert(body, negativeButton, positiveButton, negativeAction, ::openSetting)
    }

    fun alert(body: String, negativeButton: String, positiveButton: String, negativeAction: () -> Unit, positiveAction: () -> Unit){
        val context = if(activity != null) activity!!
        else fragment!!.context!!

        AlertDialog.Builder(context)
                .setMessage(body)
                .setPositiveButton(positiveButton) { dialog, _->
                    positiveAction.invoke()
                    dialog.dismiss()
                }
                .setNegativeButton(negativeButton) { dialog, _->
                    negativeAction.invoke()
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
    }

    private fun openSetting(){
        val packageName = if (activity != null) activity?.packageName ?: ""
        else fragment?.activity?.packageName ?: ""

        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }.also {
            if (activity != null) activity?.startActivity(it)
            else fragment?.startActivity(it)
        }
    }

    private fun doNothing(){}

    private fun ArrayList<String>.array(): Array<out String>{
        return this.toArray(arrayOfNulls(this.size))
    }
}