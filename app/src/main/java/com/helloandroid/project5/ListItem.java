package com.helloandroid.project5;

public class ListItem {
    private  int icon;
    private String money;
    private String title;
    private String date;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ListItem(int icon, String money, String title, String date) {
        this.icon = icon;
        this.money = money;
        this.title = title;
        this.date = date;
    }
}
