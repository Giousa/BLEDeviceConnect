package com.zmm.bledeviceconnect.model;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/9/25
 * Time:上午10:18
 */

public class BleModel {
    private String name;
    private String address;

    public BleModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BleModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
