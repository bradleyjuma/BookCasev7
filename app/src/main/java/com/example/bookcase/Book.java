package com.example.bookcase;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Book implements Parcelable {
    private int id;
    public int duration;
    private String title;
    private String author;
    private String coverURL;
    private String published;


    public Book(JSONObject jsonBook) throws JSONException {
        this.id = jsonBook.getInt("book_id");
        this.title = jsonBook.getString("title");
        this.author = jsonBook.getString("author");
        this.published = jsonBook.getString("published");
        this.coverURL = jsonBook.getString("cover_url");


    }

    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        coverURL = in.readString();
        published = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    //setters and getters
    public void setId(int id) {

        this.id = id;
    }

    public void setPublished(String published) {

        this.published = published;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public int getId() {
        return id;
    }

    public String getPublished() {
        return published;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public int getDuration(){
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(coverURL);
        dest.writeString(published);
    }
}
