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
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private NotificationPreference notificationPreference;

    // ================= BASIC PROFILE =================

    private String fullName;

    @Column(length = 2000)
    private String bio;

    private String profilePicture;

    private String bannerImage;

    private String location;

    private String website;

    @Column(nullable = false)
    private boolean isPrivate = false;

    // ================= CREATOR FIELDS =================

    private String creatorName;

    private String industry;

    private String instagramLink;

    private String youtubeLink;

    private String twitterLink;

    private String portfolioLink;

    private String skills;

    // ================= BUSINESS FIELDS =================

    private String businessName;

    private String category;

    private String contactInfo;

    private String businessAddress;

    private String businessHours;

    @Column(length = 2000)
    private String businessDescription;

    @Column(length = 2000)
    private String servicesOffered;

    private String mapLocationLink;

    // ================= CONSTRUCTORS =================

    public User() {}

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ================= GETTERS =================

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    public String getFullName() { return fullName; }
    public String getBio() { return bio; }
    public String getProfilePicture() { return profilePicture; }
    public String getBannerImage() { return bannerImage; }
    public String getLocation() { return location; }
    public String getWebsite() { return website; }
    public boolean isPrivate() { return isPrivate; }

    // Creator
    public String getCreatorName() { return creatorName; }
    public String getIndustry() { return industry; }
    public String getInstagramLink() { return instagramLink; }
    public String getYoutubeLink() { return youtubeLink; }
    public String getTwitterLink() { return twitterLink; }
    public String getPortfolioLink() { return portfolioLink; }
    public String getSkills() { return skills; }

    // Business
    public String getBusinessName() { return businessName; }
    public String getCategory() { return category; }
    public String getContactInfo() { return contactInfo; }
    public String getBusinessAddress() { return businessAddress; }
    public String getBusinessHours() { return businessHours; }
    public String getBusinessDescription() { return businessDescription; }
    public String getServicesOffered() { return servicesOffered; }
    public String getMapLocationLink() { return mapLocationLink; }

    // ================= SETTERS =================

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public void setBannerImage(String bannerImage) { this.bannerImage = bannerImage; }
    public void setLocation(String location) { this.location = location; }
    public void setWebsite(String website) { this.website = website; }
    public void setPrivate(boolean aPrivate) { this.isPrivate = aPrivate; }

    // Creator setters
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public void setIndustry(String industry) { this.industry = industry; }
    public void setInstagramLink(String instagramLink) { this.instagramLink = instagramLink; }
    public void setYoutubeLink(String youtubeLink) { this.youtubeLink = youtubeLink; }
    public void setTwitterLink(String twitterLink) { this.twitterLink = twitterLink; }
    public void setPortfolioLink(String portfolioLink) { this.portfolioLink = portfolioLink; }
    public void setSkills(String skills) { this.skills = skills; }

    // Business setters
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setCategory(String category) { this.category = category; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }
    public void setBusinessDescription(String businessDescription) { this.businessDescription = businessDescription; }
    public void setServicesOffered(String servicesOffered) { this.servicesOffered = servicesOffered; }
    public void setMapLocationLink(String mapLocationLink) { this.mapLocationLink = mapLocationLink; }
}