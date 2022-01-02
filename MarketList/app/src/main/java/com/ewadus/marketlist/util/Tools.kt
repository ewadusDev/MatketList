package com.ewadus.marketlist.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object Tools {

    fun showToast(context: Context,message:String) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }

    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show()
    }


}