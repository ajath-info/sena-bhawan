package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.LocationMaster;
import com.example.sena_bhawan.repository.LocationMasterRepository;
import com.example.sena_bhawan.service.LocationMasterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationMasterServiceImpl implements LocationMasterService {

    private final LocationMasterRepository repository;

    public LocationMasterServiceImpl(LocationMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LocationMaster> getAllLocations() {
        return repository.findAll();
    }

    @Override
    public long getLocationCount() {
        return repository.count();
    }

    @Override
    public LocationMaster addLocation(LocationMaster location) {
        return repository.save(location);
    }

    @Override
    public LocationMaster updateLocation(Integer srno, LocationMaster updated) {

        LocationMaster existing = repository.findById(srno)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        existing.setLocationName(updated.getLocationName());
        existing.setState(updated.getState());

        return repository.save(existing);
    }

    public void delete(Integer srno) {
        repository.deleteById(srno);
    }
}
