package com.gitverse.testcakes.parkings.entity;

import com.gitverse.testcakes.parkings.entity.enums.CarType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "parking_spots")
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spot_seq")
    @SequenceGenerator(name = "spot_seq", sequenceName = "spot_seq", allocationSize = 20)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CarType spotType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Car car;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    private double duration;

    private boolean occupied;

    @ToString.Exclude
    @OneToMany(mappedBy = "spot", fetch = FetchType.LAZY)
    private List<ParkingTransaction> transactions;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (!(o instanceof ParkingSpot parkingSpot)) return false;

        Class<?> oEffectiveClass = (o instanceof HibernateProxy hibernateProxy)
                ? hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = (o instanceof HibernateProxy hibernateProxy)
                ? hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        return id != null && Objects.equals(id, parkingSpot.id);
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
