import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notesapp.Note

class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_PRIORITY = "priority"
        private const val COLUMN_DEADLINE = "deadline"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT, $COLUMN_PRIORITY INTEGER DEFAULT 0, $COLUMN_DEADLINE TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertNote(note: Note) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(COLUMN_TITLE, note.title)
                put(COLUMN_CONTENT, note.content)
                put(COLUMN_PRIORITY, note.priority)
                put(COLUMN_DEADLINE, note.deadline)
            }
            db.insert(TABLE_NAME, null, values)
        }
    }

    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        readableDatabase.use { db ->
            val query = "SELECT * FROM $TABLE_NAME"
            db.rawQuery(query, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                    val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                    val priority = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
                    val deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
                    notesList.add(Note(id, title, content, priority, deadline))
                }
            }
        }
        return notesList
    }
    fun searchNotesByTitle(query: String): List<Note> {
        val notesList = mutableListOf<Note>()
        readableDatabase.use { db ->
            val searchQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TITLE LIKE ?"
            val cursor = db.rawQuery(searchQuery, arrayOf("%$query%"))
            cursor.use {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                    val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                    val priority = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
                    val deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
                    notesList.add(Note(id, title, content, priority, deadline))
                }
            }
        }
        return notesList
    }


    fun updateNote(note: Note) {
        writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(COLUMN_TITLE, note.title)
                put(COLUMN_CONTENT, note.content)
                put(COLUMN_PRIORITY, note.priority)
                put(COLUMN_DEADLINE, note.deadline)
            }
            val whereClause = "$COLUMN_ID = ?"
            val whereArgs = arrayOf(note.id.toString())
            db.update(TABLE_NAME, values, whereClause, whereArgs)
        }
    }

    fun getNoteByID(noteId: Int): Note? {
        var note: Note? = null
        readableDatabase.use { db ->
            val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(noteId.toString()))
            cursor.use {
                if (it.moveToFirst()) {
                    val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                    val title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                    val content = it.getString(it.getColumnIndexOrThrow(COLUMN_CONTENT))
                    val priority = it.getInt(it.getColumnIndexOrThrow(COLUMN_PRIORITY))
                    val deadline = it.getString(it.getColumnIndexOrThrow(COLUMN_DEADLINE))
                    note = Note(id, title, content, priority, deadline)
                }
            }
        }
        return note
    }

    fun deleteNote(noteId: Int) {
        writableDatabase.use { db ->
            val whereClause = "$COLUMN_ID = ?"
            val whereArgs = arrayOf(noteId.toString())
            db.delete(TABLE_NAME, whereClause, whereArgs)
        }
    }
}
