package org.external.customer.mgt.models;

public class Customer {


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String customerName;
    public String phoneNumber;
    public String address;
    public String country;


    public Customer(String customerName, String phoneNumber, String address, String country) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.country = country;
    }
}
