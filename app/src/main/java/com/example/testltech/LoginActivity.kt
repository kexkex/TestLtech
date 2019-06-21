package com.example.testltech

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets


class LoginActivity : AppCompatActivity() {

    private val PHONE_MASK_REQUSET_LINK = "http://dev-exam.l-tech.ru/api/v1/phone_masks"
    private val AUTH_REQUSET_LINK = "http://dev-exam.l-tech.ru/api/v1/auth"
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
        DoGet().execute()


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

            } while (responseText==null&&count<=5)


        }
        return responseText


    }

    fun signIn(v: View){
        val doPost = DoPost()
        doPost.execute(etPhone.text.toString(),etPassword.text.toString())

    }

    fun getAuth(_url: String, phone: String?, password: String?):String?{
        var responseText:String? = null
        var count = 0
        if (isNetworkConnected()) {
            val message = "phone=$phone&password=$password"
            val url = URL(_url)
            val con = url.openConnection() as HttpURLConnection
                with(con){
                    requestMethod = "POST"
                    connectTimeout = 15000
                    readTimeout = 15000
                    doOutput = true
                    doInput = true
                    setRequestProperty("Content-Type","application/x-www-form-urlencoded")

                }

            val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
            try {
                val outputStream = DataOutputStream(con.outputStream)
                val writer = BufferedWriter(OutputStreamWriter(outputStream))
                writer.write(message)
                writer.flush()
                writer.close()
                outputStream.close()

                val inputStream = DataInputStream(con.inputStream)
                val reader = BufferedReader(InputStreamReader(inputStream))
                responseText = con.responseMessage

                do {
                    count++


            } while (responseText==null&&count<10)

            } catch (exception: Exception) {} finally {
                con.disconnect()
            }



        }
        return responseText
    }

    fun parseResponsePhoneMask(response:String):String{

        val gson = Gson()
        val type = object :TypeToken<PhoneMask>(){}.type
        val parsedResponse = gson.fromJson(response, type) as PhoneMask
        val parsedResponseString = parsedResponse.phoneMask

        return parsedResponseString
    }

    fun parseResponseAuthAnswer(response: String?):Boolean{

        val gson = Gson()
        val type = object :TypeToken<AuthAnswer>(){}.type
        val parsedResponse = gson.fromJson(response, type) as AuthAnswer
        val parsedResponseString = parsedResponse.success

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

    fun updateAuth(auth:Boolean){
        if (auth) {
            etPhone.setText(auth.toString())
        } else {
            etPhone.setText(auth.toString())
       }

    }

    inner class DoGet : AsyncTask<Unit, Unit, String?>() {
        override fun doInBackground(vararg params: Unit?): String? {
            var responseText:String? = getPhoneMask(PHONE_MASK_REQUSET_LINK)

            return responseText
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!=null) {
                updatePhoneMask(parseResponsePhoneMask(result))
            }
        }

    }
    inner class DoPost  : AsyncTask<String, Unit, String?>() {
        override fun doInBackground(vararg params: String?):String? {
            var responseText:String? = null
            if (params.size!=0){
                responseText = getAuth(AUTH_REQUSET_LINK,params[0],params[1])

            }
            return responseText
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!=null) {


            }
            //updateAuth(parseResponseAuthAnswer(result))
            etPhone.setText(result)
        }



    }
    data class PhoneMask(val phoneMask:String)
    data class AuthAnswer(val success:Boolean)
}
