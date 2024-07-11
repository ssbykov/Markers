package ru.netology.markers.utils

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogManager {
    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, card: View) {
        if (dialog == null || !dialog!!.isShowing) {
            dialog = MaterialAlertDialogBuilder(context)
                .setView(card)
                .create()
            dialog?.show()
        }
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }
}