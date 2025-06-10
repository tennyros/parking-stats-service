package com.github.tennyros.parkings.repository;

import com.github.tennyros.parkings.entity.Car;
import com.github.tennyros.parkings.entity.ParkingTransaction;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
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
     * Finds the active parking transaction for a car with the specified license plate.
     * An active transaction is one where the exit time is null.
     *
     * @param licensePlate the license plate of the car
     * @return an Optional containing the active parking transaction if found
     */
    @Query("""
        SELECT pt
        FROM ParkingTransaction pt
        WHERE pt.car.licensePlate = :licensePlate AND pt.exitTime IS NULL
    """)
    Optional<ParkingTransaction> findActiveTransaction(@Param("licensePlate") String licensePlate);

    /**
     * Checks if a car has an active parking session.
     *
     * @param car the car to check
     * @return true if the car has an active parking session, false otherwise
     */
    boolean existsByCarAndExitTimeIsNull(Car car);

    @Query("""
        SELECT pt
        FROM ParkingTransaction pt
        JOIN FETCH pt.car
        WHERE pt.entryTime BETWEEN :start AND :end
        ORDER BY pt.entryTime, pt.id
    """)
    @QueryHints({
            @QueryHint(name = "org.hibernate.fetchSize", value = "100"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true")
    })
    List<ParkingTransaction> findAllByEntryTimeBetweenWithPagination(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );

}
