package com.example.leinotepad.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leinotepad.Adapter.CollectRecyclerAdapter;
import com.example.leinotepad.EditNoteActivity;
import com.example.leinotepad.R;
import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;
import com.greendao.gen.NoteDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import data.Note;


public class NoteFragment extends Fragment {

    private int type;
    // 当前标签是什么类型

    private View view;
    private RecyclerView mRecyclerView;
    private List<Note> noteList = new ArrayList<>();
    private CollectRecyclerAdapter mCollectRecyclerAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Queue<Note> removeQueue = new LinkedList<>();
    private Queue<Integer> removeIndexQueue = new LinkedList<>();
    // 存储删除的项，有利于撤销，分别存储实体和序号
    private Context mContext;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private FloatingActionButton floatingActionButton;

    public void setType(int type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_layout, container, false);
        mContext = getActivity();
        // TODO:change BackImage here
//        view.setBackground();
        initRecyclerView();
        initData();
        initializeFloatingActionButton();
        return view;
    }

    private void initializeFloatingActionButton() {
        floatingActionButton = view.findViewById(R.id.add_new_note);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, EditNoteActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("create", true);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mCollectRecyclerAdapter = new CollectRecyclerAdapter(getActivity(), noteList);
        mRecyclerView.setAdapter(mCollectRecyclerAdapter);
        // 设置显示效果，VERTICAL 即为垂直，reverseLayout 即为倒序
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        // 设置分割线
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mCollectRecyclerAdapter.setOnItemClickListener(new CollectRecyclerAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Note note) {
                // 点击事件
                Intent intent = new Intent();
                intent.setClass(mContext, EditNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                intent.putExtra("bundle", bundle);
                intent.putExtra("create", false);
                startActivity(intent);
            }
        });
        // Item 的增加与移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // ItemTouchHelper 工具用于处理 RecyclerView 中 item 的选中、滑动和拖拽
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                //得到当前拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = viewHolder1.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(noteList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(noteList, i, i - 1);
                    }
                }
                mCollectRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                mCollectRecyclerAdapter.notifyItemRemoved(position);
                Note note = noteList.get(position);
                removeQueue.offer(note);
                removeIndexQueue.offer(position);
                noteList.remove(position);
                mDaoSession.delete(note);
            }

            // 处理左滑删除动画
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // 滑动时改变Item的透明度，以实现滑动过程中实现渐变效果
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            // 撤销
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (!removeQueue.isEmpty()) {
                    // 如果删除队列非空，则有删除的 note
                    Snackbar.make(view, R.string.delete_info, Snackbar.LENGTH_LONG).setAction(R.string.repeal, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 点击撤销按钮，取出队列中的项
                            Note note = removeQueue.poll();
                            int index = removeIndexQueue.poll();
                            noteList.add(index, note);
                            mCollectRecyclerAdapter.notifyItemInserted(index);
                        }
                    }).addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                        }
                    }).show();// setCallback is deprecated, using add instead

                }
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void initData() {
        mHelper = new DaoMaster.DevOpenHelper(getActivity(), "notes-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        QueryBuilder<Note> builder = mDaoSession.queryBuilder(Note.class);
        noteList.clear();
        noteList.addAll(builder.where(NoteDao.Properties.Type.eq(type)).list());
        mCollectRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }
}
