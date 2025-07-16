package org.skillsmart.veholder.entity.dto;

import org.springframework.web.multipart.MultipartFile;

public class TrackUploadDto {
    private Long vehicleId;
    private MultipartFile file;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
