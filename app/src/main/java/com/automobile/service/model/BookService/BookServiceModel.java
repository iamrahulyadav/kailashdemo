
package com.automobile.service.model.BookService;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookServiceModel implements Parcelable
{

    @SerializedName("book_id")
    @Expose
    private String bookId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("service_type")
    @Expose
    private String serviceType;
    @SerializedName("services_car_name")
    @Expose
    private String servicesCarName;
    @SerializedName("service_address")
    @Expose
    private String serviceAddress;
    @SerializedName("service_customer_name")
    @Expose
    private String serviceCustomerName;
    @SerializedName("service_customer_contact")
    @Expose
    private String serviceCustomerContact;
    @SerializedName("service_desc")
    @Expose
    private String serviceDesc;
    @SerializedName("services_booked_time")
    @Expose
    private String servicesBookedTime;
    @SerializedName("services_booked_date")
    @Expose
    private String servicesBookedDate;
    @SerializedName("service_status")
    @Expose
    private String serviceStatus;
    @SerializedName("service_created")
    @Expose
    private String serviceCreated;
    public final static Creator<BookServiceModel> CREATOR = new Creator<BookServiceModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public BookServiceModel createFromParcel(Parcel in) {
            BookServiceModel instance = new BookServiceModel();
            instance.bookId = ((String) in.readValue((String.class.getClassLoader())));
            instance.userId = ((String) in.readValue((String.class.getClassLoader())));
            instance.serviceType = ((String) in.readValue((String.class.getClassLoader())));
            instance.servicesCarName = ((String) in.readValue((String.class.getClassLoader())));
            instance.serviceAddress = ((String) in.readValue((String.class.getClassLoader())));
            instance.serviceCustomerName = ((String) in.readValue((String.class.getClassLoader())));
            instance.serviceCustomerContact = ((String) in.readValue((String.class.getClassLoader())));
            instance.serviceDesc = ((String) in.readValue((String.class.getClassLoader())));
            instance.servicesBookedTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.servicesBookedDate = ((String) in.readValue((String.class.getClassLoader())));
            instance.serviceStatus = ((String) in.readValue((String.class.getClassLoader())));
            instance.serviceCreated = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public BookServiceModel[] newArray(int size) {
            return (new BookServiceModel[size]);
        }

    }
    ;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServicesCarName() {
        return servicesCarName;
    }

    public void setServicesCarName(String servicesCarName) {
        this.servicesCarName = servicesCarName;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getServiceCustomerName() {
        return serviceCustomerName;
    }

    public void setServiceCustomerName(String serviceCustomerName) {
        this.serviceCustomerName = serviceCustomerName;
    }

    public String getServiceCustomerContact() {
        return serviceCustomerContact;
    }

    public void setServiceCustomerContact(String serviceCustomerContact) {
        this.serviceCustomerContact = serviceCustomerContact;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public String getServicesBookedTime() {
        return servicesBookedTime;
    }

    public void setServicesBookedTime(String servicesBookedTime) {
        this.servicesBookedTime = servicesBookedTime;
    }

    public String getServicesBookedDate() {
        return servicesBookedDate;
    }

    public void setServicesBookedDate(String servicesBookedDate) {
        this.servicesBookedDate = servicesBookedDate;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceCreated() {
        return serviceCreated;
    }

    public void setServiceCreated(String serviceCreated) {
        this.serviceCreated = serviceCreated;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(bookId);
        dest.writeValue(userId);
        dest.writeValue(serviceType);
        dest.writeValue(servicesCarName);
        dest.writeValue(serviceAddress);
        dest.writeValue(serviceCustomerName);
        dest.writeValue(serviceCustomerContact);
        dest.writeValue(serviceDesc);
        dest.writeValue(servicesBookedTime);
        dest.writeValue(servicesBookedDate);
        dest.writeValue(serviceStatus);
        dest.writeValue(serviceCreated);
    }

    public int describeContents() {
        return  0;
    }

}
