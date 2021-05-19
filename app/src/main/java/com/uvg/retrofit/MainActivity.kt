package com.uvg.retrofit


import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.uvg.retrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*



class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener, View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticleAdapter
    private val articleList = mutableListOf<Articles>()

    public var country : String = ""
    public var category : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchNews.setOnQueryTextListener(this)

        initRecyclerView()
        searchNew("general")

        val button1: Button = findViewById(R.id.gb)
        val button2: Button = findViewById(R.id.br)
        val button3: Button = findViewById(R.id.de)
        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)

    }

    private fun initRecyclerView(){

        adapter = ArticleAdapter(articleList)
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter

    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.gb -> {
                //Cambiar a Inglaterra
                country = "gb"
                searchNew(category)
            }
            R.id.br -> {
                //Cambiar a Brasil
                country = "br"
                searchNew(category)
            }
            R.id.de -> {
                //Cambiar a Alemania
                country = "de"
                searchNew(category)
            }
        }
    }



    private fun searchNew(category: String){

        val api = Retrofit2()

        CoroutineScope(Dispatchers.IO).launch {

            val call = api.getService()?.getNewsByCategory(country , category, "4b94054dbc6b4b3b9e50d8f62cde4f6c")
            val news: NewsResponse? = call?.body()

            runOnUiThread{

                if (call!!.isSuccessful){
                    if (news?.status.equals("ok")){
                        val articles = news?.articles ?: emptyList()
                        articleList.clear()
                        articleList.addAll(articles)
                        adapter.notifyDataSetChanged()
                    }else{
                        showMessage("Error en webservices")
                    }
                }else{
                    showMessage("Error en retrofit")
                }
                hideKeyBoard()
            }

        }

    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        showMessage(query.toString())
        if (!query.isNullOrEmpty()){
            searchNew(query.toLowerCase(Locale.ROOT))
            category = query.toLowerCase(Locale.ROOT)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }




}