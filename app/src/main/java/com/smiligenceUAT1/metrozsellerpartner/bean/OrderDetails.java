package com.smiligenceUAT1.metrozsellerpartner.bean;

import java.util.ArrayList;
import java.util.List;


public class OrderDetails implements Comparable< OrderDetails>
{
    String orderDay;
    String orderStatus;
    String orderDeliverCodeForSeller;
    String orderDeliverCodeForDrliveryBoy;
    int totalAmount;
    int totalItem;
    String orderTime;
    String storeName;
    String paymentId;
    String orderIdfromPaymentGateway;
    String orderId;
    int paymentamount;
    String paymentDate;
    String paymentType;
    String fullName;
    String shippingPincode;
    String storePincode;
    String shippingaddress;
    ItemDetails itemDetails;
    String assignedTo;
    String phoneNumber;
    String customerName;
    String customerId;
    List<PickUpAndDrop> pickUpAndDroplist=new ArrayList<PickUpAndDrop> ();
    String customerPhoneNumber;
    PickUpAndDrop pickUpAndDrop;
    String categoryType;
    String instructionsToDeliveryBoy;
    String orderDeliveryType;
    int tipAmount;
    String orderCreateDate;
    String notificationStatusForSeller;
    String deliveryBoyImage;
    ArrayList<String> giftWrappingItemName=new ArrayList<>();
    int giftWrapCharge;
    String formattedDate;

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
    public int getGiftWrapCharge() {
        return giftWrapCharge;
    }

    public void setGiftWrapCharge(int giftWrapCharge) {
        this.giftWrapCharge = giftWrapCharge;
    }


    public ArrayList<String> getGiftWrappingItemName() {
        return giftWrappingItemName;
    }

    public void setGiftWrappingItemName(ArrayList<String> giftWrappingItemName) {
        this.giftWrappingItemName = giftWrappingItemName;
    }



    public String getDeliveryBoyImage() {
        return deliveryBoyImage;
    }

    public void setDeliveryBoyImage(String deliveryBoyImage) {
        this.deliveryBoyImage = deliveryBoyImage;
    }

    public String getNotificationStatusForSeller() {
        return notificationStatusForSeller;
    }

    public void setNotificationStatusForSeller(String notificationStatusForSeller) {
        this.notificationStatusForSeller = notificationStatusForSeller;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderCreateDate() {
        return orderCreateDate;
    }

    public void setOrderCreateDate(String orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getOrderDeliveryType() {
        return orderDeliveryType;
    }

    public void setOrderDeliveryType(String orderDeliveryType) {
        this.orderDeliveryType = orderDeliveryType;
    }

    public String getInstructionsToDeliveryBoy() {
        return instructionsToDeliveryBoy;
    }

    public void setInstructionsToDeliveryBoy(String instructionsToDeliveryBoy) {
        this.instructionsToDeliveryBoy = instructionsToDeliveryBoy;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public PickUpAndDrop getPickUpAndDrop() {
        return pickUpAndDrop;
    }

    public void setPickUpAndDrop(PickUpAndDrop pickUpAndDrop) {
        this.pickUpAndDrop = pickUpAndDrop;
    }

    public List<PickUpAndDrop> getPickUpAndDroplist() {
        return pickUpAndDroplist;
    }

    public void setPickUpAndDroplist(List<PickUpAndDrop> pickUpAndDroplist) {
        this.pickUpAndDroplist = pickUpAndDroplist;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    String deliveryType;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getShippingPincode() {
        return shippingPincode;
    }

    public void setShippingPincode(String shippingPincode) {
        this.shippingPincode = shippingPincode;
    }

    public String getStorePincode() {
        return storePincode;
    }

    public void setStorePincode(String storePincode) {
        this.storePincode = storePincode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    ArrayList<ItemDetails> itemDetailList=new ArrayList<ItemDetails> ();

    public String getOrderDay() {
        return orderDay;
    }

    public void setOrderDay(String orderDay) {
        this.orderDay = orderDay;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDeliverCodeForSeller() {
        return orderDeliverCodeForSeller;
    }

    public void setOrderDeliverCodeForSeller(String orderDeliverCodeForSeller) {
        this.orderDeliverCodeForSeller = orderDeliverCodeForSeller;
    }

    public String getOrderDeliverCodeForDrliveryBoy() {
        return orderDeliverCodeForDrliveryBoy;
    }

    public void setOrderDeliverCodeForDrliveryBoy(String orderDeliverCodeForDrliveryBoy) {
        this.orderDeliverCodeForDrliveryBoy = orderDeliverCodeForDrliveryBoy;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderIdfromPaymentGateway() {
        return orderIdfromPaymentGateway;
    }

    public void setOrderIdfromPaymentGateway(String orderIdfromPaymentGateway) {
        this.orderIdfromPaymentGateway = orderIdfromPaymentGateway;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPaymentamount() {
        return paymentamount;
    }

    public void setPaymentamount(int paymentamount) {
        this.paymentamount = paymentamount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getShippingaddress() {
        return shippingaddress;
    }

    public void setShippingaddress(String shippingaddress) {
        this.shippingaddress = shippingaddress;
    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public ArrayList<ItemDetails> getItemDetailList() {
        return itemDetailList;
    }

    public void setItemDetailList(ArrayList<ItemDetails> itemDetailList) {
        this.itemDetailList = itemDetailList;
    }

    @Override
    public int compareTo(OrderDetails o) {
        int id1=Integer.parseInt(this.getOrderId());
        int id=Integer.parseInt(o.getOrderId());
        return id - id1;
    }
}