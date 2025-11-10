package com.sk.PCnWS.model; // Correct package

import jakarta.persistence.*;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plantId;

    @Column(nullable = false)
    private String plantName;

    private String plantType;
    private String imageUrl;

    @Column(nullable = false)
    private Integer wateringFrequencyDays;

    @Column(nullable = false)
    private Integer fertilizingFrequencyDays;

    // This is the Foreign Key relationship
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- Getters and Setters ---

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getWateringFrequencyDays() {
        return wateringFrequencyDays;
    }

    public void setWateringFrequencyDays(Integer wateringFrequencyDays) {
        this.wateringFrequencyDays = wateringFrequencyDays;
    }

    public Integer getFertilizingFrequencyDays() {
        return fertilizingFrequencyDays;
    }

    public void setFertilizingFrequencyDays(Integer fertilizingFrequencyDays) {
        this.fertilizingFrequencyDays = fertilizingFrequencyDays;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}