package data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class Note implements Serializable {

    // 实现 Serializable 接口需要
    private static final long serialVersionUID = -7469021523456975052L;

    // 笔记类型，即放入哪一个标签下
    public static final int WORK = 1;
    public static final int LIFE = 2;
    public static final int OTHER = 3;
    public static final int LEARNING = 4;


    @Id(autoincrement = true)
    private long id;
    private String title;
    private String content;
    private boolean finished;
    private int type;


    public Note() {
        // 使用当前时间（毫秒数）避免 id 重复
        this.id = System.currentTimeMillis();
        this.type = OTHER;
        this.title = "title";
        this.content = "content";
        this.finished = false;
    }

    @Generated(hash = 2045141157)
    public Note(long id, String title, String content, boolean finished, int type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.finished = finished;
        this.type = type;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
