package com.gitverse.testcakes.parkings.entity;

import com.gitverse.testcakes.parkings.entity.enums.CarType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private CarType type;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @ToString.Exclude
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParkingTransaction> transactions;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (!(o instanceof Car car)) return false;

        Class<?> oEffectiveClass = (o instanceof HibernateProxy hibernateProxy)
                ? hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = (o instanceof HibernateProxy hibernateProxy)
                ? hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return Objects.equals(licensePlate, car.licensePlate);
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
