package com.example.testltech


import android.content.Intent

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.rybalkinsd.kohttp.ext.asString
import io.github.rybalkinsd.kohttp.ext.httpGet
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),MyAdapter.MyAdapterListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var list: ArrayList<Element>
    private lateinit var sortOrder:String
    private lateinit var btnServerSort:Button
    private lateinit var btnDateSort:Button

    val ORDER_BY_DATE = "Date"
    val ORDER_BY_SERVER = "Server"
    val LIST_LINK = "http://dev-exam.l-tech.ru/api/v1/posts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnServerSort = findViewById(R.id.btnServerSort)
        btnDateSort = findViewById(R.id.btnDateSort)

        btnServerSort.setOnClickListener { v -> loadList(ORDER_BY_SERVER) }
        btnDateSort.setOnClickListener { v -> loadList(ORDER_BY_DATE) }

        list = arrayListOf()

        sortOrder = ORDER_BY_SERVER
        DoGetList().execute()

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(list, this)

        recyclerView = findViewById<RecyclerView>(R.id.rvList).apply {
            layoutManager = viewManager
            addItemDecoration(object: DividerItemDecoration(this@MainActivity,LinearLayoutManager.VERTICAL){})
            adapter = viewAdapter}

        val timer = Timer()
        val timerTask = object :TimerTask(){
            override fun run() {
                loadList(sortOrder)
            }
        }
        timer.schedule(timerTask, 5000, 30000)


    }

    override fun onMessageRowClicked(position: Int) {
        val intent = Intent(this, ElementActivity::class.java)
        intent.putExtra("MainActTitle", list[position].title)
        intent.putExtra("MainActText", list[position].text)
        intent.putExtra("MainActImage", list[position].image)


        startActivity(intent)
    }

    override fun onResume() {
        loadList(sortOrder)

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_reload, menu)
    return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId){
            R.id.mnReload -> loadList(sortOrder)
        }


        viewAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    fun loadList(sort:String){
        sortOrder = sort
        DoGetList().execute()
    }

    fun doGetList(url:String):String?{

        val response = url.httpGet()
        return response!!.asString()!!
    }

    fun parseResponseList(response:String?):ArrayList<Element>{
        if (response!=null){
            val gson = Gson()

            val type = object : TypeToken<ArrayList<Element>>(){}.type
            val parsedResponse = gson.fromJson(response, type) as ArrayList<Element>

            return parsedResponse
        } else return arrayListOf()
    }

    inner class DoGetList:AsyncTask<Unit, Unit, String?>(){
        override fun doInBackground(vararg params: Unit?): String? {
            return doGetList(LIST_LINK)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!=null&&result.isNotEmpty()) {
                list.clear()
                list.addAll(parseResponseList(result))
                when (sortOrder) {
                    ORDER_BY_SERVER -> list.sortBy { it.sort }
                    ORDER_BY_DATE -> list.sortBy { it.date }
                }
                viewAdapter.notifyDataSetChanged()
            } else {
                list.clear()
                list.add(Element("0","Error","Loading","",0,"List"))
                viewAdapter.notifyDataSetChanged()
            }
        }
    }
}


