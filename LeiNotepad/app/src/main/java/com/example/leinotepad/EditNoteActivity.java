package com.example.leinotepad;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;
import com.greendao.gen.NoteDao;

import org.greenrobot.greendao.query.QueryBuilder;

import data.Note;

public class EditNoteActivity extends AppCompatActivity {

    private EditText mTitle, mContent;
    private Button mSave, mCancel;
    private int mType;
    private boolean isCreated;
    private Note mNote;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private NoteDao mNoteDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);
        getData();
        initData();
        bindComponents();
        initializeButtons();
        if (!isCreated) {
            mTitle.setText(mNote.getTitle());
            mContent.setText(mNote.getContent());
        }
    }

    private void initData() {
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mNoteDao = mDaoSession.getNoteDao();
    }

    private void getData() {
        Intent intent = getIntent();
        isCreated = intent.getBooleanExtra("create", true);
        if (isCreated) {
            mType = intent.getIntExtra("type", Note.OTHER);
        } else {
            Bundle bundle = intent.getBundleExtra("bundle");
            mNote = (Note) bundle.getSerializable("note");
        }
    }

    private void bindComponents() {
        mTitle = findViewById(R.id.note_title);
        mContent = findViewById(R.id.note_content);
        mSave = findViewById(R.id.save);
        mCancel = findViewById(R.id.cancel);
    }

    private void initializeButtons() {
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCreated) {
                    mNote = new Note(System.currentTimeMillis(), mTitle.getText().toString(), mContent.getText().toString(), false, mType);
                    mDaoSession.insert(mNote);
                    finish();
                } else {
                    mNote.setTitle(mTitle.getText().toString());
                    mNote.setContent(mContent.getText().toString());
                    mDaoSession.update(mNote);
                    finish();
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
