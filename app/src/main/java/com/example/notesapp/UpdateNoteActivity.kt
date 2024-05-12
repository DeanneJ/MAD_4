package com.example.notesapp

import NotesDatabaseHelper
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.notesapp.databinding.ActivityUpdateNoteBinding
import java.util.Calendar

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNoteBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NotesDatabaseHelper(this)

        noteId = intent.getIntExtra("note_id", -1)

        if (noteId == -1) {
            finish()
            return
        }

        val note = db.getNoteByID(noteId)
        note?.let {
            binding.UpdateTitleEditText.setText(it.title)
            binding.UpdateContentEditText.setText(it.content)
            val priorityArray = resources.getStringArray(R.array.priority_array)
            val priorityIndex = priorityArray.indexOf(it.priority.toString())
            if (priorityIndex != -1) {
                binding.prioritySpinner.setSelection(priorityIndex)
            }
            binding.UpdateDeadlineEditText.setText(it.deadline)
        }

        val prioritySpinner: Spinner = binding.prioritySpinner

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.priority_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            prioritySpinner.adapter = adapter
        }

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.UpdateTitleEditText.text.toString()
            val newContent = binding.UpdateContentEditText.text.toString()
            val priority = prioritySpinner.selectedItem.toString().toInt()
            val deadline = binding.UpdateDeadlineEditText.text.toString()

            // Create an updated Note object with priority and deadline values
            val updatedNote = Note(noteId, newTitle, newContent, priority, deadline)

            db.updateNote(updatedNote)
            finish()
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }

        // Set click listener for deadlineEditText to open DatePickerDialog
        binding.UpdateDeadlineEditText.setOnClickListener {
            showDatePickerDialog(binding.UpdateDeadlineEditText)
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
