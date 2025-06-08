package org.skillsmart.veholder.config;

import org.skillsmart.veholder.entity.dto.TripExportDTO;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import java.time.Instant;

public class TripDescriptionFieldSetMapper implements FieldSetMapper<TripExportDTO> {

    @Override
    public TripExportDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        TripExportDTO tripDesc = new TripExportDTO();

        tripDesc.setVehicleId(fieldSet.readLong(0));
        tripDesc.setTripStart(Instant.parse(fieldSet.readString(1)));
        tripDesc.setTripEnd(Instant.parse(fieldSet.readString(2)));
        tripDesc.setStartLon(fieldSet.readDouble(3));
        tripDesc.setStartLat(fieldSet.readDouble(4));
        tripDesc.setEndLon(fieldSet.readDouble(5));
        tripDesc.setEndLat(fieldSet.readDouble(6));

        return tripDesc;
    }
}
