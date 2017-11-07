
package com.automobile.service.model.Order;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderProductDetailModel implements Serializable, Parcelable
{

    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_price")
    @Expose
    private String productPrice;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("quantity")
    @Expose
    private String quantity;



    public final static Creator<OrderProductDetailModel> CREATOR = new Creator<OrderProductDetailModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public OrderProductDetailModel createFromParcel(Parcel in) {
            OrderProductDetailModel instance = new OrderProductDetailModel();
            instance.productName = ((String) in.readValue((String.class.getClassLoader())));
            instance.productPrice = ((String) in.readValue((String.class.getClassLoader())));
            instance.productImage = ((String) in.readValue((String.class.getClassLoader())));
            instance.quantity = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public OrderProductDetailModel[] newArray(int size) {
            return (new OrderProductDetailModel[size]);
        }

    }
    ;
    private final static long serialVersionUID = 6638005484882382314L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OrderProductDetailModel() {
    }

    /**
     * 
     * @param productImage
     * @param productPrice
     * @param productName
     */
    public OrderProductDetailModel(String productName, String productPrice, String productImage,String quantity) {
        super();
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(productName);
        dest.writeValue(productPrice);
        dest.writeValue(productImage);
        dest.writeValue(quantity);
    }

    public int describeContents() {
        return  0;
    }

}
