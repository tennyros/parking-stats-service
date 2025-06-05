package com.gitverse.testcakes.parkings.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking_transaction")
public class ParkingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "spot_id")
    private ParkingSpot spot;
    
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @Transient
    public Duration getDuration() {
        return Duration.between(entryTime, exitTime != null ? exitTime : LocalDateTime.now());
    }

}