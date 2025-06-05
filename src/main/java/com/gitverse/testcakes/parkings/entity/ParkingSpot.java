package com.gitverse.testcakes.parkings.entity;

import com.gitverse.testcakes.parkings.entity.enums.CarType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking_spots")
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CarType spotType;

    @ManyToOne
    private Car car;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private double duration;

    private boolean occupied;

    @ToString.Exclude
    @OneToMany(mappedBy = "spot")
    private List<ParkingTransaction> transactions;

}
