package com.satish.weatherapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.satish.weatherapp.R
import com.satish.weatherapp.network.CallBackListener
import com.satish.weatherapp.network.NetworkConstants
import com.satish.weatherapp.utils.CommonUtility.navigationToAppSettingsPage

object DialogUtility {

    fun getOkDialog(context: Activity, message: String?) {
        if (!context.isFinishing) {
            val lBuilder =
                AlertDialog.Builder(
                    context,
                    android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth
                )
            lBuilder.setTitle(context.getString(R.string.dialog_message))
            if (!TextUtils.isEmpty(message)) {
                lBuilder.setMessage(message)
            }

            lBuilder.setCancelable(false)
            lBuilder.setPositiveButton(context.getString(R.string.dialog_ok), null)
            val dialog = lBuilder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.black_two))
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showPermissionEnablePopup(context: Activity) {
        if (!context.isFinishing) {
            val lBuilder =
                AlertDialog.Builder(
                    context,
                    android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth
                )
            lBuilder.setTitle(context.getString(R.string.dialog_message))
            lBuilder.setMessage(context.getString(R.string.runtime_permissions_txt))
            lBuilder.setCancelable(false)
            lBuilder.setPositiveButton(context.getString(R.string.dialog_ok)) { dialog, _ ->
                context.navigationToAppSettingsPage()
                dialog.dismiss()
            }
            val dialog = lBuilder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.black_two))
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.runtime_permissions_txt),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun showLocationPopupAlert(context: Activity) {
        AlertDialog.Builder(context)
            .setTitle(R.string.background_location_permission_title)
            .setMessage(R.string.background_location_permission_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                // this request will take user to Application's Setting page
                showPermissionEnablePopup(context)
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun showConfirmationAlert(
        context: Activity,
        message: String?,
        tag: String?,
        callBackListener: CallBackListener
    ) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_message)
            .setMessage(message)
            .setPositiveButton(R.string.yes) { _, _ ->
                callBackListener.callBackReceived(tag)
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun showCloseAlert(context: Activity, message: String?, callBackListener: CallBackListener) {
        if (!context.isFinishing) {
            val lBuilder =
                AlertDialog.Builder(
                    context,
                    android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth
                )
            lBuilder.setTitle(context.getString(R.string.dialog_message))
            if (!TextUtils.isEmpty(message)) {
                lBuilder.setMessage(message)
            }
            lBuilder.setCancelable(false)
            lBuilder.setPositiveButton(context.getString(R.string.dialog_ok)) { dialog, _ ->
                dialog.dismiss()
                callBackListener.callBackReceived(NetworkConstants.KEY_CONFIRM_OK)
            }
            val dialog = lBuilder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(context, R.color.black_two))
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}