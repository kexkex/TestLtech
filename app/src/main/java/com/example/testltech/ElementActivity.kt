package com.example.testltech

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_element.*

class ElementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_element)

        val ivImage = ivElemImage
        val tvTitle = tvElemTitle
        val tvText = tvElemText

        try {
            var bundle: Bundle? = intent.extras

            if (bundle != null) {

                val title = bundle.getString("MainActTitle")
                setTitle(title)
                tvTitle.text = title
                tvText.text = bundle.getString("MainActText")
                Picasso.get().load("http://dev-exam.l-tech.ru"+bundle.getString("MainActImage")).into(ivImage)
            }


        } catch (ex: Exception) {
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
