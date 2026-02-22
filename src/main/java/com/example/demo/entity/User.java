package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

  
    // PROFILE FIELDS
    

    private String fullName;

    @Column(length = 1000)
    private String bio;

//    @Column(columnDefinition="LONGTEXT")
    private String profilePicture;

    private String location;

    private String website;

    // =============================
    // PROFILE_MG PART - BUSINESS FIELDS
    // =============================

    private String category;

    private String contactInfo;

    private String businessAddress;

    private String businessHours;
    
    private String bannerImage;

    
    // PRIVACY
 

    @Column(nullable = false)
    private boolean isPrivate = false;

    public User() {}

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

   
    // GETTERS
    

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getBio() { return bio; }
    public String getProfilePicture() { return profilePicture; }
    public String getLocation() { return location; }
    public String getWebsite() { return website; }

    // PROFILE_MG GETTERS
    public String getCategory() { return category; }
    public String getContactInfo() { return contactInfo; }
    public String getBusinessAddress() { return businessAddress; }
    public String getBusinessHours() { return businessHours; }

    public boolean isPrivate() { return isPrivate; }
    
    public String getBannerImage() {
        return bannerImage;
    }

   
    // SETTERS
    

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public void setLocation(String location) { this.location = location; }
    public void setWebsite(String website) { this.website = website; }

    //=======================
    // PROFILE_MG SETTERS
    // ======================
    public void setCategory(String category) { this.category = category; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public void setPrivate(boolean aPrivate) { this.isPrivate = aPrivate; }
    
    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }
}