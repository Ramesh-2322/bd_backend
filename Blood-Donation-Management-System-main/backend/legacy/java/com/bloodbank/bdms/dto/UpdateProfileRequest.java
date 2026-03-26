package com.bloodbank.bdms.dto;

import org.springframework.web.multipart.MultipartFile;

public class UpdateProfileRequest {
    private String email;
    private String phone;
    private String state;
    private String city;
    private String address;
    private MultipartFile image;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}
