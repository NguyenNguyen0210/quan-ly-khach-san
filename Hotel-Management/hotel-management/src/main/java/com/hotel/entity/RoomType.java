package com.hotel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "room_type")
public class RoomType extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price_per_night", nullable = false)
    private Double pricePerNight;

    @Column(name = "description")
    private String description;

    public RoomType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}