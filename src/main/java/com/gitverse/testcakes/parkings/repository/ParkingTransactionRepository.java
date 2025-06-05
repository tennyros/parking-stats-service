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

/**
 * Repository interface for managing parking transactions.
 * Provides methods for querying and managing parking transaction data.
 *
 * <p>This repository supports:
 * <ul>
 *     <li>Finding transactions within a time range</li>
 *     <li>Finding active transactions for a specific car</li>
 *     <li>Checking if a car has an active parking session</li>
 * </ul>
 *
 * @author vadim_23
 * @see ParkingTransaction
 */
@Repository
public interface ParkingTransactionRepository extends JpaRepository<ParkingTransaction, Long> {

    /**
     * Finds all parking transactions that occurred between the specified start and end times.
     *
     * @param start the start time of the range (inclusive)
     * @param end the end time of the range (inclusive)
     * @return a list of parking transactions within the specified time range
     */
    List<ParkingTransaction> findAllByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Finds the active parking transaction for a car with the specified license plate.
     * An active transaction is one where the exit time is null.
     *
     * @param licensePlate the license plate of the car
     * @return an Optional containing the active parking transaction if found
     */
    @Query("SELECT pt FROM ParkingTransaction pt WHERE pt.car.licensePlate = :licensePlate AND pt.exitTime IS NULL")
    Optional<ParkingTransaction> findActiveTransaction(@Param("licensePlate") String licensePlate);

    /**
     * Checks if a car has an active parking session.
     *
     * @param car the car to check
     * @return true if the car has an active parking session, false otherwise
     */
    boolean existsByCarAndExitTimeIsNull(Car car);

}
