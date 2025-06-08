package org.skillsmart.veholder.component;

import org.skillsmart.veholder.entity.Trip;
import org.skillsmart.veholder.entity.VehicleTrack;
import org.skillsmart.veholder.entity.dto.TripImportResult;
import org.skillsmart.veholder.repository.TripRepository;
import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class TripImportWriter implements ItemWriter<TripImportResult> {

    private final TripRepository tripRepository;
    private final VehicleTrackRepository trackRepository;

    public TripImportWriter(TripRepository tripRepository,
                            VehicleTrackRepository trackRepository) {
        this.tripRepository = tripRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    @Transactional
    public void write(Chunk<? extends TripImportResult> items) throws Exception {
        for (TripImportResult result : items) {
            // 1. Сохраняем Trip
            tripRepository.save(result.getTrip());

            // 2. Устанавливаем связь и сохраняем VehicleTrack
            /*VehicleTrack track = result.getTrack();
            track.setTrip(savedTrip);*/
            trackRepository.save(result.getTrackStart());
            trackRepository.save(result.getTrackEnd());
        }
    }
}
