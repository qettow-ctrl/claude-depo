package com.qettow.hatirla

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Note(val id: Int, val text: String)

/** Notları SharedPreferences içinde JSON olarak saklar. */
object NoteStore {
    private const val PREFS = "notes"
    private const val KEY_NOTES = "notes"
    private const val KEY_NEXT_ID = "next_id"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun all(context: Context): List<Note> {
        val raw = prefs(context).getString(KEY_NOTES, null) ?: return emptyList()
        val array = JSONArray(raw)
        return (0 until array.length()).map {
            val obj = array.getJSONObject(it)
            Note(obj.getInt("id"), obj.getString("text"))
        }
    }

    fun get(context: Context, id: Int): Note? = all(context).firstOrNull { it.id == id }

    /** Yeni notu en üste ekler ya da var olan notu günceller. */
    fun save(context: Context, id: Int?, text: String) {
        val notes = all(context).toMutableList()
        val index = notes.indexOfFirst { it.id == id }
        if (id != null && index >= 0) {
            notes[index] = Note(id, text)
        } else {
            val nextId = prefs(context).getInt(KEY_NEXT_ID, 1)
            notes.add(0, Note(nextId, text))
            prefs(context).edit().putInt(KEY_NEXT_ID, nextId + 1).apply()
        }
        write(context, notes)
    }

    fun delete(context: Context, id: Int) {
        write(context, all(context).filterNot { it.id == id })
    }

    private fun write(context: Context, notes: List<Note>) {
        val array = JSONArray()
        notes.forEach { array.put(JSONObject().put("id", it.id).put("text", it.text)) }
        prefs(context).edit().putString(KEY_NOTES, array.toString()).apply()
        Reminders.refresh(context)
    }
}
