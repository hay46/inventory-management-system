package com.injibara.iams.backend.service;

import com.injibara.iams.backend.domain.Location;
import com.injibara.iams.backend.repository.LocationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LocationService {
    private final LocationRepository repo;
    public LocationService(LocationRepository repo) { this.repo = repo; }
    public List<Location> all() { return repo.findAll(); }
    public Location create(Location l) {
        if (repo.existsByName(l.getName())) throw new IllegalArgumentException("Location name already exists");
        return repo.save(l);
    }
    public Location update(Long id, Location l) {
        Location existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Location not found"));
        existing.setName(l.getName());
        existing.setDescription(l.getDescription());
        return repo.save(existing);
    }
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Location not found");
        repo.deleteById(id);
    }
}
