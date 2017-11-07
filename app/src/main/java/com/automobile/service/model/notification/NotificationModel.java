package com.automobile.service.model.notification;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationModel implements Parcelable {

    @SerializedName("noti_id")
    @Expose
    private String notiId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("noti_text")
    @Expose
    private String notiText;
    @SerializedName("noti_title")
    @Expose
    private String notiTitle;
    @SerializedName("noti_created")
    @Expose
    private String notiCreated;
    public final static Parcelable.Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public NotificationModel createFromParcel(Parcel in) {
            NotificationModel instance = new NotificationModel();
            instance.notiId = ((String) in.readValue((String.class.getClassLoader())));
            instance.userId = ((String) in.readValue((String.class.getClassLoader())));
            instance.notiText = ((String) in.readValue((String.class.getClassLoader())));
            instance.notiTitle = ((String) in.readValue((String.class.getClassLoader())));
            instance.notiCreated = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public NotificationModel[] newArray(int size) {
            return (new NotificationModel[size]);
        }

    };

    public String getNotiId() {
        return notiId;
    }

    public void setNotiId(String notiId) {
        this.notiId = notiId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotiText() {
        return notiText;
    }

    public void setNotiText(String notiText) {
        this.notiText = notiText;
    }

    public String getNotiTitle() {
        return notiTitle;
    }

    public void setNotiTitle(String notiTitle) {
        this.notiTitle = notiTitle;
    }

    public String getNotiCreated() {
        return notiCreated;
    }

    public void setNotiCreated(String notiCreated) {
        this.notiCreated = notiCreated;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(notiId);
        dest.writeValue(userId);
        dest.writeValue(notiText);
        dest.writeValue(notiTitle);
        dest.writeValue(notiCreated);
    }

    public int describeContents() {
        return 0;
    }

}