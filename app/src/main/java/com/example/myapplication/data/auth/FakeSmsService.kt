package com.example.myapplication.data.auth

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FakeSmsService(
    private val context: Context
) {

    fun sendCode(phone: String, code: String) {
        CoroutineScope(Dispatchers.Main).launch {
            showToast("SMS-код для $phone: $code")
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}




