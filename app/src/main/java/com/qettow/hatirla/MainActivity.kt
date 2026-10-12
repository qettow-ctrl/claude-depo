package com.qettow.hatirla

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val adapter = NotesAdapter()
    private lateinit var empty: View

    private val askPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { Reminders.refresh(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        empty = findViewById(R.id.empty)

        findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
        findViewById<FloatingActionButton>(R.id.add).setOnClickListener {
            startActivity(Intent(this, NoteEditActivity::class.java))
        }

        if (Build.VERSION.SDK_INT >= 33) askPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onResume() {
        super.onResume()
        val notes = NoteStore.all(this)
        adapter.submit(notes)
        empty.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        Reminders.refresh(this)
    }

    private inner class NotesAdapter : RecyclerView.Adapter<NotesAdapter.Holder>() {
        private var notes: List<Note> = emptyList()

        fun submit(newNotes: List<Note>) {
            notes = newNotes
            notifyDataSetChanged()
        }

        override fun getItemCount() = notes.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false))

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val note = notes[position]
            holder.text.text = note.text
            holder.itemView.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, NoteEditActivity::class.java)
                        .putExtra(NoteEditActivity.EXTRA_ID, note.id)
                )
            }
            holder.delete.setOnClickListener {
                NoteStore.delete(this@MainActivity, note.id)
                onResume()
            }
        }

        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val text: TextView = view.findViewById(R.id.text)
            val delete: ImageButton = view.findViewById(R.id.delete)
        }
    }
}
