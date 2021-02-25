package com.anniemovil;

public class SpinnerData {
    public String value;
    public String desc;

    public SpinnerData(String value,String desc) {
        this.desc = desc;
        this.value = value;
    }

    @Override
    public String toString() {
        return desc;
    }
}
