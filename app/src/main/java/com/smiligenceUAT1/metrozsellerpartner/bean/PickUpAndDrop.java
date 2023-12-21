package com.smiligenceUAT1.metrozsellerpartner.bean;

import java.util.List;

public class PickUpAndDrop
{
    String pickupAddress;
    String dropAddress;
    List<String> deliverObject;
    int totalAmountFair;
    double startPickupLatitude;
    double endPickupLongtitude;
    double startDeliveryLatitude;
    double endDeliveryLongtitude;
    int totalDistance;
    int totalAmountToPaid;
    int tipAmount;

    public int getTotalAmountToPaid() {
        return totalAmountToPaid;
    }

    public void setTotalAmountToPaid(int totalAmountToPaid) {
        this.totalAmountToPaid = totalAmountToPaid;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public int getTotalAmountFair() {
        return totalAmountFair;
    }

    public void setTotalAmountFair(int totalAmountFair) {
        this.totalAmountFair = totalAmountFair;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getEndPickupLongtitude() {
        return endPickupLongtitude;
    }

    public void setEndPickupLongtitude(double endPickupLongtitude) {
        this.endPickupLongtitude = endPickupLongtitude;
    }

    public double getEndDeliveryLongtitude() {
        return endDeliveryLongtitude;
    }

    public void setEndDeliveryLongtitude(double endDeliveryLongtitude) {
        this.endDeliveryLongtitude = endDeliveryLongtitude;
    }

    public double getStartPickupLatitude() {
        return startPickupLatitude;
    }

    public void setStartPickupLatitude(double startPickupLatitude) {
        this.startPickupLatitude = startPickupLatitude;
    }


    public double getStartDeliveryLatitude() {
        return startDeliveryLatitude;
    }

    public void setStartDeliveryLatitude(double startDeliveryLatitude) {
        this.startDeliveryLatitude = startDeliveryLatitude;
    }



    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public List<String> getDeliverObject() {
        return deliverObject;
    }

    public void setDeliverObject(List<String> deliverObject) {
        this.deliverObject = deliverObject;
    }

}
