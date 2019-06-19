package com.example.testltech

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class LoginActivity : AppCompatActivity() {

    @Volatile
    var responseText:String?=null

    private val PHONE_MASK_REQUSET_LINK = "http://dev-exam.l-tech.ru/api/v1/phone_masks"
    private val AUTH_REQUSET_LINK = "http://dev-exam.l-tech.ru/auth.php"

    private lateinit var etPhone:EditText
    private lateinit var etPassword:EditText
    private lateinit var btnSigin:Button
    private lateinit var volleyQueue:RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        volleyQueue = Volley.newRequestQueue(this)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        btnSigin = findViewById(R.id.btnSignin)

        btnSigin.setOnClickListener { v -> signIn(v) }
        checkConnection()
        getPhoneMask()


    }

    override fun onResume() {
        super.onResume()


    }

    private fun isNetworkConnected(): Boolean {

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    private fun checkConnection(){
        if (isNetworkConnected()) Toast.makeText(this,"Network connection OK",Toast.LENGTH_LONG).show()
        else Toast.makeText(this,"Network connection FAIL",Toast.LENGTH_LONG).show()
    }

    fun getPhoneMask(){
        if (isNetworkConnected()) {
        var textView = textView

          val async= object:Thread(Runnable {


                var response:String?

                val url =
                    URL(PHONE_MASK_REQUSET_LINK)
                do {



                    try {
                        val con = url.openConnection() as HttpURLConnection
                        con.requestMethod="GET"
                        val inString = BufferedReader(InputStreamReader(con.inputStream))
                        var line = ""
                        val response = StringBuffer()
                        do {
                            line=inString.readLine()
                            response.append(line)
                        }
                        while (line!=null)

                    } catch (ex: Exception) {



                    }
                } while (response == null)
                responseText=response
            }){}
            async.start()
            async.join(3000)
            textView.setText(responseText.toString())

            /*
           val stringRequest = StringRequest(
                Request.Method.GET, "http://dev-exam.l-tech.ru/api/v1/phone_masks",
                Response.Listener{ response ->

                    textView.setText(response.toString())
                },
                Response.ErrorListener { Toast.makeText(this,"Request FAIL",Toast.LENGTH_LONG).show() })

            volleyQueue.add(stringRequest)*/
        }


    }

    fun signIn(v: View){
        checkConnection()
        getPhoneMask()
        etPhone.setText(responseText)

    }
}
