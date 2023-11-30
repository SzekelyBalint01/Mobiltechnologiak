package hu.pte.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView notesListView;
    private Button addNoteButton;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        notesListView = findViewById(R.id.notesListView);
        addNoteButton = findViewById(R.id.addNoteButton);

        // Betöltjük a jegyzeteket az adatbázisból
        loadNotes();

        // Jegyzet hozzáadása gomb eseménykezelője
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Új jegyzet létrehozása
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("NOTE_ID", -1); // Az új jegyzet esetén -1 értékkel jön létre
                startActivity(intent);
            }
        });

        // Jegyzetek listaelemre kattintás eseménykezelője
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Jegyzet szerkesztése
                String selectedContent = notesList.get(position);
                int noteId = getNoteIdByContent(selectedContent);
                if (noteId != -1) {
                    Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                    intent.putExtra("NOTE_ID", noteId);
                    startActivity(intent);
                } else {
                    // Azonosító nem található, itt kezelheted a hibát
                    Toast.makeText(MainActivity.this, "Failed to find note", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Jegyzetek listaelem hosszú lenyomás eseménykezelője
        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                // Hosszú lenyomás esemény, itt jelenítheted meg a törlés gombot
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete Note");
                builder.setMessage("Are you sure you want to delete this note?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Törlés végrehajtása
                        deleteNoteAtPosition(position);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

                // true visszaadása, hogy ne fusson tovább a kattintáskezelő
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Frissítjük a jegyzetek listáját a MainActivity újranyitásakor
        loadNotes();
    }

    private int getNoteIdByContent(String content) {
        // Jegyzetek betöltése az adatbázisból
        Cursor cursor = databaseHelper.getAllNotes();

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(databaseHelper.getNoteIdColumnName());
            int contentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT);

            do {
                int id = cursor.getInt(idIndex);
                String noteContent = cursor.getString(contentIndex);

                if (content.equals(noteContent)) {
                    cursor.close();
                    return id;
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return -1; // Azonosító nem található
    }


    private void loadNotes() {
        // Jegyzetek betöltése az adatbázisból
        Cursor cursor = databaseHelper.getAllNotes();
        notesList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT);

            do {
                String content = cursor.getString(contentIndex);
                notesList.add(content);
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Jegyzetek megjelenítése a ListView-ben
        CustomListAdapter adapter = new CustomListAdapter(this, android.R.layout.simple_list_item_1, notesList);
        notesListView.setAdapter(adapter);
    }

    private void deleteNoteAtPosition(int position) {
        // Törlés az adatbázisból
        // position + 1 az azonosító, mivel a pozíció 0-tól indul, de az azonosítók 1-től
        boolean success = databaseHelper.deleteNote(position + 1);

        // Frissítjük a listát, ha a törlés sikeres volt
        if (success) {
            loadNotes();
        } else {
            // Ha nem sikerült a törlés, itt megfelelő visszajelzést adhatsz a felhasználónak
            Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show();
        }
    }
}
