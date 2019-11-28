package com.developer.gkweb.knowlocation.Model;

public class User {

    public String id, name, email, password, phonenumber, status, imageUrl, search;


    public User() {
    }

    public User(String id, String name, String email, String password, String phonenumber, String status, String imageUrl, String search) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
        this.status = status;
        this.imageUrl = imageUrl;
        this.search = search;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
