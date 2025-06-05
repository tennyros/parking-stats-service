package com.gitverse.testcakes.parkings.repository;

import com.gitverse.testcakes.parkings.entity.Car;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParkingTransactionRepository extends JpaRepository<ParkingTransaction, Long> {

    List<ParkingTransaction> findAllByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<ParkingTransaction> findFirstByCarLicensePlateAndExitTimeIsNullOrderByEntryTimeDesc(String licensePlate);

    boolean existsByCarAndExitTimeIsNull(Car car);

}
