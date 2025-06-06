package com.gitverse.testcakes.parkings.repository;

import com.gitverse.testcakes.parkings.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {

    @Query("SELECT c FROM Car c WHERE c.licensePlate = :licensePlate")
    Optional<Car> findByLicensePlateWithTransactions(@Param("licensePlate") String licensePlate);

}
