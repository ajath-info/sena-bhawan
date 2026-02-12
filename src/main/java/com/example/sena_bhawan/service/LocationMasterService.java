package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.LocationMaster;
import java.util.List;

public interface LocationMasterService {

    List<LocationMaster> getAllLocations();

    long getLocationCount();

    LocationMaster addLocation(LocationMaster location);

    LocationMaster updateLocation(Integer srno, LocationMaster location);

    void delete(Integer srno);
}
