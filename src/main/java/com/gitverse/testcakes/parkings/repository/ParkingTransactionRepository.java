package com.gitverse.testcakes.parkings.repository;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingTransactionRepository extends JpaRepository<ParkingTransaction, Long> {

    List<ParkingTransaction> findAllByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT pt FROM ParkingTransaction pt WHERE pt.car.licensePlate = :licensePlate AND pt.exitTime IS NULL")
    Optional<ParkingTransaction> findActiveTransaction(@Param("licensePlate") String licensePlate);

    boolean existsByCarAndExitTimeIsNull(Car car);

}
