package main.api.request.profile;

import org.springframework.web.multipart.MultipartFile;



public class ProfileRequest {
    private String name;
    private int removePhoto;
    private String email;
    private String password;
    private String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getRemovePhoto() {
        return removePhoto;
    }
    public void setRemovePhoto(int removePhoto) {
        this.removePhoto = removePhoto;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
