package hu.pte.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {

    private EditText noteEditText;
    private Button saveNoteButton;
    private int noteId;
    private DatabaseHelper databaseHelper;
    private Button deleteNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        databaseHelper = new DatabaseHelper(this);
        noteEditText = findViewById(R.id.noteEditText);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        deleteNoteButton = findViewById(R.id.deleteNoteButton);


        // Jegyzet azonosítójának lekérése az Intentből
        Intent intent = getIntent();
        noteId = intent.getIntExtra("NOTE_ID", -1);

        // Ha a noteId -1, akkor új jegyzetet hozunk létre, egyébként szerkesztjük a meglévőt
        if (noteId != -1) {
            // Jegyzet szerkesztése esetén betöltjük a jegyzet adatait
            String noteContent = databaseHelper.getNoteById(noteId);
            noteEditText.setText(noteContent);
        }

        // Jegyzet mentése gomb eseménykezelője
        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        deleteNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteNote(noteId);
                finish();
            }
        });
    }

    private void saveNote() {
        String content = noteEditText.getText().toString();

        // Jegyzet mentése az adatbázisba
        long id;
        if (noteId == -1) {
            // Új jegyzet esetén
            id = databaseHelper.insertNote(content);
        } else {
            // Meglévő jegyzet szerkesztése esetén
            id = databaseHelper.updateNote(noteId, content);
        }

        // Ellenőrzés, hogy a jegyzet mentése sikeres volt-e
        if (id != -1) {
            // Visszalépés a főképernyőre
            finish();
        } else {
            // Hibakezelés, például Toast üzenet megjelenítése
            showToast("Failed to save note. Please try again.");
        }
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
