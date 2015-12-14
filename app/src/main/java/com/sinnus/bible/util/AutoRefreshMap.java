package com.sinnus.bible.util;

import com.sinnus.bible.activity.MainActivity;
import com.sinnus.bible.bean.Book;
import com.sinnus.bible.bean.Chapter;
import com.sinnus.bible.bean.Section;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by sinnus on 2015/10/23.
 */
public class AutoRefreshMap{

    private Integer key;
    MainActivity activity;
    private HashMap<Integer, Book> content = new HashMap(8);

    public AutoRefreshMap(Integer key, MainActivity activity) {
        this.key = key;
        this.activity = activity;
        load(this.key);
    }

    public void load(Integer id) { //载入对应书卷
        if (this.key != id) {
            this.key = id;
        }
        if (!this.content.containsKey(this.key)) {
            Book book = new Book(this.key, this.activity);
            this.content.put(this.key, book);
        }
        activity.current_book = this.content.get(this.key);
        autoLoad();
    }
    public void autoLoad(){ //载入相邻的书卷

        if (this.key - 1 > 0 && !this.content.containsKey(this.key - 1)) {
            Book book = new Book(this.key-1, this.activity);
            this.content.put(book.getId(), book);
        }
        if (this.key + 1 < 67 && !this.content.containsKey(this.key + 1)) {
            Book book = new Book(this.key+1, this.activity);
            this.content.put(book.getId(), book);
        }


    }
    public void loadBesideBooks(int bookId) { //在后台线程中载入
        key = bookId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                autoLoad();
            }
        }).start();

    }
    public Chapter obtainChapter(int bookId, int chapterId) {
        if (this.content.containsKey(bookId)) {
            return this.content.get(bookId).getChapter(chapterId-1);
        }
        else return new Chapter(bookId, chapterId); //空chapter
    }
    public Section obtainRandomSection() {
        if (this.content.get(this.key) != null) {
            Book book = this.content.get(this.key);
            int chapterNum = book.getChapterNum();
            Random random = new Random();
            int randomChapterId = random.nextInt(chapterNum);
            Chapter randomChapter = book.getChapter(randomChapterId);
            int randomSectionId = random.nextInt(randomChapter.getSectionNum());
            Section randomSection = randomChapter.getSection(randomSectionId);
            return randomSection;
        }
        return new Section(-1, "", -1, -1);//空section
    }
}
