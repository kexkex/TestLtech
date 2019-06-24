package com.example.testltech

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class VolleyHttp{

    private val PHONE_MASK_REQUSET_LINK = "http://dev-exam.l-tech.ru/api/v1/phone_masks"
    private val AUTH_REQUSET_LINK = "http://dev-exam.l-tech.ru/api/v1/auth"
    private val LIST_LINK = "http://dev-exam.l-tech.ru/api/v1/posts"


    private lateinit var volleyQueue: RequestQueue
    private lateinit var context: Context

    constructor(_context: Context){
        this.context=_context
        volleyQueue = Volley.newRequestQueue(context)
    }

    fun getPhoneMask():String{
        var responseText=""

        val stringRequest = StringRequest(
            Request.Method.GET, PHONE_MASK_REQUSET_LINK,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                responseText = response.toString()
            },
            Response.ErrorListener { responseText = "That didn't work!" })


        volleyQueue.add(stringRequest)
        return responseText
    }
}

