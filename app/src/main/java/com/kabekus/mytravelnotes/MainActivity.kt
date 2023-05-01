package com.kabekus.mytravelnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.kabekus.mytravelnotes.databinding.ActivityDetailBinding
import com.kabekus.mytravelnotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var noteList : ArrayList<Note>
    private lateinit var noteAdapter : NoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        noteList = ArrayList<Note>()

        noteAdapter = NoteAdapter(noteList)
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMain.adapter = noteAdapter

        try {
            val database = this.openOrCreateDatabase("TravelNotes", MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM travelNotes",null)
            var noteTitleIx = cursor.getColumnIndex("note_title")
            var noteIdIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                val noteTitle = cursor.getString(noteTitleIx)
                val id = cursor.getInt(noteIdIx)
                val note = Note(noteTitle,id)
                noteList.add(note)
            }
            noteAdapter.notifyDataSetChanged()
            cursor.close()
        }catch (e:Exception){
            e.printStackTrace()
        }




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
            intent.putExtra("info","newNote")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}