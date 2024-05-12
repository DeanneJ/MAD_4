import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.Note
import com.example.notesapp.R
import com.example.notesapp.UpdateNoteActivity


class NotesAdapter(private var notes: List<Note>, private val context: Context) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val db: NotesDatabaseHelper = NotesDatabaseHelper(context)
    private var filteredNotes: List<Note> = notes

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = filteredNotes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = filteredNotes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.priorityTextView.text = "Priority: ${note.priority}"
        holder.deadlineTextView.text = "Deadline: ${note.deadline}"

        holder.updateButton.setOnClickListener {
            val intent = Intent(context, UpdateNoteActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteNote(note.id)
            refreshData(db.getAllNotes())
            Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun setFilteredNotes(filteredNotes: List<Note>) {
        this.filteredNotes = filteredNotes
        notifyDataSetChanged()
    }

    fun refreshData(newNotes: List<Note>) {
        notes = newNotes
        setFilteredNotes(notes) // Refresh filtered list
    }
}
