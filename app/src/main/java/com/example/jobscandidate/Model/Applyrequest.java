package com.example.jobscandidate.Model;

public class Applyrequest {
    private String name;
    private String email;
    private String phone;
    private String jobId;
    private String resume;
    private String appliedfor;
    private String image;

    public Applyrequest() {
    }

    public Applyrequest(String name, String email, String phone, String jobId, String resume, String appliedfor, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.jobId = jobId;
        this.resume = resume;
        this.appliedfor = appliedfor;
        this.image = image;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getAppliedfor() {
        return appliedfor;
    }

    public void setAppliedfor(String appliedfor) {
        this.appliedfor = appliedfor;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}