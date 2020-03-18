package com.example.jobscandidate.Model;

public class Candidate {
    private String email;
    private String name;
    private String password;
    private String phone;
    private String resume;
    private String image;

    public Candidate() {
    }

    public Candidate(String email, String name, String password, String phone, String resume, String image) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.resume = resume;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
