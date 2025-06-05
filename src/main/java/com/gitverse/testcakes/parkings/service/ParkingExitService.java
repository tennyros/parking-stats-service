package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.entity.ParkingTransaction;

public interface ParkingExitService {

    ParkingTransaction processExit(String licensePlate);

}
