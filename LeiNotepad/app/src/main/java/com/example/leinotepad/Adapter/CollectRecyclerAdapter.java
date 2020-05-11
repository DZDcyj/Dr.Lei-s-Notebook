package com.example.leinotepad.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.leinotepad.R;
import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;
import com.greendao.gen.NoteDao;

import java.util.List;

import data.Note;

public class CollectRecyclerAdapter extends RecyclerView.Adapter<CollectRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<Note> noteList;
    private LayoutInflater mLayoutInflater;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public CollectRecyclerAdapter(Context context, List<Note> noteList) {
        this.mContext = context;
        this.noteList = noteList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        initDatabase();
    }

    private void initDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(mContext, "notes-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.note_item, viewGroup, false));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Note note = noteList.get(i);
        viewHolder.mTitle.setText(note.getTitle());
        viewHolder.mCheckBox.setChecked(note.getFinished());
        viewHolder.mContent.setText(note.getContent());

        viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = noteList.get(i);
                note.setFinished(!note.getFinished());
                mDaoSession.update(note);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mContent;
        private CheckBox mCheckBox;

        public ViewHolder(final View view) {
            super(view);
            mTitle = view.findViewById(R.id.display_note_title);
            mContent = view.findViewById(R.id.display_note_content);
            mCheckBox = view.findViewById(R.id.display_note_checkbox);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(v, noteList.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, Note note);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
