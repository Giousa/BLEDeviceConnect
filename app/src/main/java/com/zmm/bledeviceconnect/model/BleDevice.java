package com.zmm.bledeviceconnect.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/9/25
 * Time:上午10:08
 */
@Entity
public class BleDevice {

    @Id
    private Long id;

    private String name;
    private String address;
    @Generated(hash = 294382432)
    public BleDevice(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
    @Generated(hash = 1527739491)
    public BleDevice() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
