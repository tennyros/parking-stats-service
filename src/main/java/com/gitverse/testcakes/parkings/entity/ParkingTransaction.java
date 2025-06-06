package com.gitverse.testcakes.parkings.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import org.hibernate.proxy.HibernateProxy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking_transactions")
public class ParkingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "car_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Car car;

    @ToString.Exclude
    @JoinColumn(name = "spot_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ParkingSpot spot;
    
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @Transient
    public Duration getDuration() {
        return Duration.between(entryTime, exitTime != null ? exitTime : LocalDateTime.now());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (!(o instanceof ParkingTransaction parkingTransaction)) return false;

        Class<?> oEffectiveClass = (o instanceof HibernateProxy hibernateProxy)
                ? hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = (o instanceof HibernateProxy hibernateProxy)
                ? hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return id != null && Objects.equals(id, parkingTransaction.id);
    }

    @Override
    public final int hashCode() {
        return (this instanceof HibernateProxy thisProxy)
                ? thisProxy.getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode()
                : getClass().hashCode();
    }
}