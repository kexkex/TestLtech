package com.example.testltech

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class LoginActivity : AppCompatActivity() {

    private val PHONE_MASK_REQUSET_LINK = "http://dev-exam.l-tech.ru/api/v1/phone_masks"
    private val AUTH_REQUSET_LINK = "http://dev-exam.l-tech.ru/auth.php"
    private val PHONE_MASK_STRING = "phoneMask"
    private val AUTH_STRING = "success"

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
        DoPostGet().execute(PHONE_MASK_REQUSET_LINK)


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

    fun getPhoneMask(_url:String):String?{
        var responseText:String? = null
        var count = 0
        if (isNetworkConnected()) {
            val url = URL(_url)
            do {
                count++
                url.openConnection()
                responseText = url.readText()
            } while (responseText==null&&count<=20)


        }
        return responseText


    }

    fun signIn(v: View){
        checkConnection()
        getPhoneMask(PHONE_MASK_REQUSET_LINK)


    }

    fun getAuth(url: String):String{
        return ""
    }

    fun parseResponse(response:String):String{
        //val responsetext = "{\"phoneMask\":\"+44 ХХХХ-ХХХХХХ\"}"

        val gson = Gson()
        val type = object :TypeToken<PhoneMask>(){}.type
        val parsedResponse = gson.fromJson(response, type) as PhoneMask
        val parsedResponseString = parsedResponse.phoneMask

        return parsedResponseString
    }

    fun updatePhoneMask(s:String){
        etPhone.hint = s
        when{
            s.contains("+7") -> {etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher("RU"))
                etPhone.setText("+7")}
            s.contains("+44") -> {etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher("GB"))
                etPhone.setText("+44")}
            s.contains("+375") -> {etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher("BY"))
                etPhone.setText("+375")}
        }



    }

    fun updateAuth(s:String){

    }

    inner class DoPostGet : AsyncTask<String, Unit, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            var responseText:String? = null
            if (params.size!=0){
            when (params[0]){
                PHONE_MASK_REQUSET_LINK -> responseText = getPhoneMask(PHONE_MASK_REQUSET_LINK)
                AUTH_REQUSET_LINK -> responseText = getAuth(AUTH_REQUSET_LINK)
            }}
            return responseText
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!=null) {

                updatePhoneMask(parseResponse(result))
            }
        }

    }
    data class PhoneMask(val phoneMask:String)
}
