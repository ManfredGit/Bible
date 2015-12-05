package com.sinnus.bible.bean;

import java.util.ArrayList;

/**
 * Created by sinnus on 2015/8/4.
 * chapter 类代表一章，其中包含bookId、id、section列表、notes列表。
 *
 */
public class Chapter {
    private final int bookId;
    private final int id;
//    public static int autoId = 0;
    private ArrayList<Section> content;
    private ArrayList<Note> notes;

    public Chapter(int id,int bookId){
        this.bookId = bookId;
        this.id = id;
        this.content = new ArrayList();
        this.notes = new ArrayList();
    }

    public void addSection(Section section){
        this.content.add(section);
    }
    public void addNote(Note note){this.notes.add(note);}
    public Note getNote(int id){return this.notes.get(id);}
    public int getId(){
        return id;
    }
    public int getBookId(){
        return bookId;
    }
    public ArrayList<Section> getContent(){
        return this.content;
    }
    public Section getSection(int id){
        return this.content.get(id);
    }
    public int getSectionNum(){
        return this.content.size();
    }
    public int getNoteNum(){
        return this.notes.size();
    }
}
