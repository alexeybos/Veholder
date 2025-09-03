package org.skillsmart.veholder.entity.processor;

import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.TripDatesDTO;
import org.skillsmart.veholder.entity.dto.TripExportDTO;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.skillsmart.veholder.service.VehicleTrackService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;
import java.util.List;

public class TripToExportDTOProcessor implements ItemProcessor<TripDatesDTO, TripExportDTO> {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private VehicleTrackService trackService;

    @Override
    public TripExportDTO process(TripDatesDTO trip) throws Exception {
        Vehicle vehicle = vehicleRepository.findById(trip.vehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Enterprise enterprise = enterpriseRepository.findById(vehicle.getEnterprise().getId())
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        TripExportDTO dto = new TripExportDTO();
        dto.setId(trip.id());
        dto.setVehicleId(trip.vehicleId());
        dto.setTripStart(trip.startInterval());
        dto.setTripEnd(trip.endInterval());
        // Пример дополнительных вычислений
        List<VehicleTrack> points = trackService.getStartAndEndPointByInterval(trip.vehicleId(),
                trip.startInterval().atZone(ZoneOffset.UTC), trip.endInterval().atZone(ZoneOffset.UTC));
        VehicleTrack first = points.getFirst();
        VehicleTrack last = points.getLast();
        dto.setStartLon(first.getPoint().getY());
        dto.setStartLat(first.getPoint().getX());
        dto.setEndLon(last.getPoint().getY());
        dto.setEndLat(last.getPoint().getX());

        return dto;
    }

}
