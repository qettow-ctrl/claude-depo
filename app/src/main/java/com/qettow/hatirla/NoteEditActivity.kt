package com.qettow.hatirla

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

/** Not yazma/düzenleme penceresi. "+" düğmesi, widget, kısayol ve hızlı ayar kutucuğu buraya açılır. */
class NoteEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_edit)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        val id = intent.getIntExtra(EXTRA_ID, -1).takeIf { it >= 0 }
        val input = findViewById<EditText>(R.id.input)
        val delete = findViewById<Button>(R.id.delete)

        val existing = id?.let { NoteStore.get(this, it) }
        if (existing != null) {
            setTitle(R.string.edit_note)
            input.setText(existing.text)
            input.setSelection(existing.text.length)
            delete.visibility = Button.VISIBLE
        } else {
            setTitle(R.string.new_note)
        }
        input.requestFocus()

        findViewById<Button>(R.id.save).setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) NoteStore.save(this, existing?.id, text)
            finish()
        }
        delete.setOnClickListener {
            existing?.let { NoteStore.delete(this, it.id) }
            finish()
        }
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}
