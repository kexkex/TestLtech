package com.example.testltech

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.ext.asString
import io.github.rybalkinsd.kohttp.ext.httpGet

import java.io.*



class LoginActivity : AppCompatActivity() {

    private val PHONE_MASK_REQUSET_LINK = "http://dev-exam.l-tech.ru/api/v1/phone_masks"
    private val AUTH_REQUSET_HOST = "dev-exam.l-tech.ru"
    private val AUTH_REQUSET_PATH = "/api/v1/auth"

    private lateinit var etPhone:EditText
    private lateinit var etPassword:EditText
    private lateinit var btnSigin:Button



    private var logged:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        btnSigin = findViewById(R.id.btnSignin)

        btnSigin.setOnClickListener { v -> signIn(v) }
        checkConnection()

        if (!readPhoneAndPassword()) {
            DoGet().execute()

        } else logged = true
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

    fun getPhoneMaskKoHttp(url:String):String{

        val response = url.httpGet()
        return response!!.asString()!!
    }

    fun signIn(v: View){
        if (etPhone.text.isEmpty()||etPassword.text.isEmpty())
            showAlertDialogEmptyPass()

        else DoPost().execute(etPhone.text.toString(),etPassword.text.toString())

    }

    fun showAlertDialogEmptyPass(){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Error")
            setMessage("Enter phone and password")
            setCancelable(false)
            setNegativeButton("CLOSE", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.cancel()
                }
            })
        }
        val alert = builder.create()
        alert.show()
    }

    fun getAuth(_host: String, _path:String, phone: String?, password: String?):String?{


        val response = httpPost {
            host = _host
            path = _path
            body {
                form("phone=$phone&password=$password")
            }
        }
        return response.asString()
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
            savePhoneAndPassword(etPhone.text.toString(),etPassword.text.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
       } else showAlertDialogWrongPass()

    }

    fun showAlertDialogWrongPass(){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Error")
            setMessage("Invalid phone or password")
            setCancelable(false)
            setNegativeButton("CLOSE", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.cancel()
                }
            })
        }
        val alert = builder.create()
        alert.show()
    }

    fun savePhoneAndPassword(phone: String?, password: String?){
        if (!logged) {
            try {
                var bufferedWriter = BufferedWriter(OutputStreamWriter(openFileOutput("log.ini", Context.MODE_PRIVATE)))
                bufferedWriter.write("$phone\n$password")
                bufferedWriter.close()
            } catch (e: Exception) {
            }
        }
    }

    fun readPhoneAndPassword():Boolean{
        try {
            var bufferedReader = BufferedReader(InputStreamReader(openFileInput("log.ini")))
            var str:String?=null
            str = bufferedReader.readLine()
            if (str!=null){
                etPhone.setText(str)
                etPassword.setText(bufferedReader.readLine())
                return true
            } else return false
        } catch (e:Exception){ return false }
    }


    inner class DoGet : AsyncTask<Unit, Unit, String?>() {
        override fun doInBackground(vararg params: Unit?): String? {
            var responseText:String? = getPhoneMaskKoHttp(PHONE_MASK_REQUSET_LINK)

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
                responseText = getAuth(AUTH_REQUSET_HOST, AUTH_REQUSET_PATH,params[0],params[1])
            }
            return responseText
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!=null) {
                updateAuth(parseResponseAuthAnswer(result))
            }
        }

    }
    data class PhoneMask(val phoneMask:String)
    data class AuthAnswer(val success:Boolean)
}
