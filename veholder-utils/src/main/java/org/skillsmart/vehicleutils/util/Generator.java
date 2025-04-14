package org.skillsmart.vehicleutils.util;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Vehicle;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Generator {

    //private Vehicle
    public List<Brand> getBrands() {
        Sort sortBrands = Sort.by("id").ascending();
        return null;
    }
    
    public void generateVehiclesAndDrivers(Long enterpriseId, int vehicleCnt, int driverCnt, List<Brand> brands) {
        //minValue + (int) (Math.random() * (maxValue - minValue + 1))
        for (int i = 0; i < vehicleCnt; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setBrand(brands.get((int) (Math.random() * (brands.size()))));
        }
    }
}
