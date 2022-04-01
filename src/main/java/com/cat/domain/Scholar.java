package com.cat.domain;

import com.alibaba.excel.annotation.ExcelProperty;

public class Scholar {
    @ExcelProperty(value = "学者姓名", index = 0)
    private String name;

    @ExcelProperty(value = "学者证件", index = 1)
    private String card;

    @ExcelProperty(value = "学者编号", index = 2)
    private String id;

    @ExcelProperty(value = "学者学院", index = 3)
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Scholar{" +
                "name='" + name + '\'' +
                ", card='" + card + '\'' +
                ", id='" + id + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
