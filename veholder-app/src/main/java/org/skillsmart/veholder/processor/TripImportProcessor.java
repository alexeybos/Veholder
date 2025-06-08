package org.skillsmart.veholder.processor;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.skillsmart.veholder.entity.Trip;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.TripExportDTO;
import org.skillsmart.veholder.entity.dto.TripImportResult;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import com.vladmihalcea.hibernate.type.range.Range;

import java.time.ZoneOffset;

@Component
public class TripImportProcessor implements ItemProcessor<TripExportDTO, TripImportResult> {

    private final VehicleRepository vehicleRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public TripImportProcessor(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public TripImportResult process(TripExportDTO tripDesc) throws Exception {
        // 1. Создаем объект Trip
        Trip trip = new Trip();
        trip.setTimeInterval(Range.closed(tripDesc.getTripStart().atZone(ZoneOffset.UTC),
                tripDesc.getTripEnd().atZone(ZoneOffset.UTC)));

        // Устанавливаем vehicle
        Vehicle vehicle = vehicleRepository.findById(tripDesc.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + tripDesc.getVehicleId()));
        trip.setVehicle(vehicle);

        // 2. Создаем объект VehicleTrack
        VehicleTrack trackStart = new VehicleTrack();
        trackStart.setRecordedAt(tripDesc.getTripStart().atZone(ZoneOffset.UTC));
        trackStart.setVehicleId(tripDesc.getVehicleId());
        Point startPoint = geometryFactory.createPoint(new Coordinate(tripDesc.getStartLon(), tripDesc.getStartLat()));
        startPoint.setSRID(4326);
        trackStart.setPoint(startPoint);

        VehicleTrack trackEnd = new VehicleTrack();
        trackEnd.setRecordedAt(tripDesc.getTripEnd().atZone(ZoneOffset.UTC));
        trackEnd.setVehicleId(tripDesc.getVehicleId());
        Point endPoint = geometryFactory.createPoint(new Coordinate(tripDesc.getEndLon(), tripDesc.getEndLat()));
        endPoint.setSRID(4326);
        trackEnd.setPoint(endPoint);

        return new TripImportResult(trip, trackStart, trackEnd);
    }
}
