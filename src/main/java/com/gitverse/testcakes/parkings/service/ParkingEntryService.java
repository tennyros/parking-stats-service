package com.gitverse.testcakes.parkings.service;

import com.gitverse.testcakes.parkings.dto.car.request.CarEntryRequest;
import com.gitverse.testcakes.parkings.entity.ParkingTransaction;

public interface ParkingEntryService {

    ParkingTransaction registerEntry(CarEntryRequest request);

}
