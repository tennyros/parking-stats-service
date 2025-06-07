package com.gitverse.testcakes.parkings.repository;

import com.gitverse.testcakes.parkings.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {

}
