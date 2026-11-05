package com.injibara.iams.backend.controller;

import com.injibara.iams.backend.domain.Location;
import com.injibara.iams.backend.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService service;
    public LocationController(LocationService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Location>> all() { return ResponseEntity.ok(service.all()); }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Location> create(@RequestBody Location l) { return ResponseEntity.ok(service.create(l)); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Location> update(@PathVariable Long id, @RequestBody Location l) { return ResponseEntity.ok(service.update(id, l)); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
