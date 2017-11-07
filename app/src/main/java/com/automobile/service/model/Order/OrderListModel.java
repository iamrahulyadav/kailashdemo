
package com.automobile.service.model.Order;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderListModel implements Serializable, Parcelable
{

    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("payment_id")
    @Expose
    private String paymentId;
    @SerializedName("order_total_amount")
    @Expose
    private String orderTotalAmount;
    @SerializedName("order_created_date")
    @Expose
    private String orderCreatedDate;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("product_details")
    @Expose
    private List<OrderProductDetailModel> productDetails = null;
    public final static Creator<OrderListModel> CREATOR = new Creator<OrderListModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public OrderListModel createFromParcel(Parcel in) {
            OrderListModel instance = new OrderListModel();
            instance.orderId = ((String) in.readValue((String.class.getClassLoader())));
            instance.productId = ((String) in.readValue((String.class.getClassLoader())));
            instance.paymentId = ((String) in.readValue((String.class.getClassLoader())));
            instance.orderTotalAmount = ((String) in.readValue((String.class.getClassLoader())));
            instance.orderCreatedDate = ((String) in.readValue((String.class.getClassLoader())));
            instance.orderStatus = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.productDetails, (OrderProductDetailModel.class.getClassLoader()));
            return instance;
        }

        public OrderListModel[] newArray(int size) {
            return (new OrderListModel[size]);
        }

    }
    ;
    private final static long serialVersionUID = 5404614581515315423L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OrderListModel() {
    }

    /**
     * 
     * @param paymentId
     * @param productDetails
     * @param orderCreatedDate
     * @param orderStatus
     * @param orderId
     * @param orderTotalAmount
     * @param productId
     */
    public OrderListModel(String orderId, String productId, String paymentId, String orderTotalAmount, String orderCreatedDate, String orderStatus, List<OrderProductDetailModel> productDetails) {
        super();
        this.orderId = orderId;
        this.productId = productId;
        this.paymentId = paymentId;
        this.orderTotalAmount = orderTotalAmount;
        this.orderCreatedDate = orderCreatedDate;
        this.orderStatus = orderStatus;
        this.productDetails = productDetails;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(String orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public String getOrderCreatedDate() {
        return orderCreatedDate;
    }

    public void setOrderCreatedDate(String orderCreatedDate) {
        this.orderCreatedDate = orderCreatedDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderProductDetailModel> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<OrderProductDetailModel> productDetails) {
        this.productDetails = productDetails;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(orderId);
        dest.writeValue(productId);
        dest.writeValue(paymentId);
        dest.writeValue(orderTotalAmount);
        dest.writeValue(orderCreatedDate);
        dest.writeValue(orderStatus);
        dest.writeList(productDetails);
    }

    public int describeContents() {
        return  0;
    }

}
