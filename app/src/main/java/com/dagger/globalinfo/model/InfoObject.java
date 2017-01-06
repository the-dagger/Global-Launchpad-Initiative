package com.dagger.globalinfo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Harshit on 25/12/16.
 */

public class InfoObject implements Parcelable{
    public static final Creator<InfoObject> CREATOR = new Creator<InfoObject>() {
        @Override
        public InfoObject createFromParcel(Parcel in) {
            return new InfoObject(in);
        }

        @Override
        public InfoObject[] newArray(int size) {
            return new InfoObject[size];
        }
    };
    private String title;
    private String url;
    private String description;
    private String author;
    private String email;
    private String category;
    private String timestamp;
    private String photo;
    private String contentKey;
    /**
     * To sort in reverse!
     */
    private long timeInMillis;

    public InfoObject() {
    }

    public InfoObject(String title, String url, String description, String author, String category, String timestamp, String email, String photo, long timeInMillis) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.author = author;
        this.category = category;
        this.timestamp = timestamp;
        this.email = email;
        this.photo = photo;
        setTimeInMillis(timeInMillis);
    }

    protected InfoObject(Parcel in) {
        title = in.readString();
        url = in.readString();
        description = in.readString();
        author = in.readString();
        email = in.readString();
        category = in.readString();
        timestamp = in.readString();
        photo = in.readString();
        contentKey = in.readString();
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
        parcel.writeString(description);
        parcel.writeString(author);
        parcel.writeString(email);
        parcel.writeString(category);
        parcel.writeString(timestamp);
        parcel.writeString(photo);
        parcel.writeString(contentKey);
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = -1 * timeInMillis;
    }
}
