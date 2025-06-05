package com.gitverse.testcakes.parkings.entity;

import com.gitverse.testcakes.parkings.entity.enums.CarType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "cars")
public class Car {

    @Id
    @Column(unique = true, nullable = false)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private CarType type;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @ToString.Exclude
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<ParkingTransaction> transactions;

}
