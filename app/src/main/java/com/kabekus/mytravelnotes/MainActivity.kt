package com.kabekus.mytravelnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.kabekus.mytravelnotes.databinding.ActivityDetailBinding
import com.kabekus.mytravelnotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    //Bağlama işlemi
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    //Tıklandığında yapılacak işlemler
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addItem){
            val intent = Intent(this@MainActivity,DetailActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}