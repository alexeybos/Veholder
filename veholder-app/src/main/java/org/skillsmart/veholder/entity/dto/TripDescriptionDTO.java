package org.skillsmart.veholder.entity.dto;

import com.vladmihalcea.hibernate.type.range.Range;

import java.time.ZonedDateTime;

public class TripDescriptionDTO {

    private Long id;
    private Long vehicleId;
    private ZonedDateTime tripStart;
    private ZonedDateTime tripEnd;
    private String startEnterpriseTZ;
    private String endEnterpriseTZ;
    private Long startPointId;
    private Long endPointId;
    private String startPointDesc;
    private String endPointDesc;

    public TripDescriptionDTO() {
    }

    public TripDescriptionDTO(Long id, Long vehicleId, ZonedDateTime tripStart, ZonedDateTime tripEnd,
                              String startEnterpriseTZ, String endEnterpriseTZ) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.tripStart = tripStart;
        this.tripEnd = tripEnd;
        this.startEnterpriseTZ = startEnterpriseTZ;
        this.endEnterpriseTZ = endEnterpriseTZ;
    }

    public TripDescriptionDTO(Long id, Long vehicleId, ZonedDateTime tripStart, ZonedDateTime tripEnd,
                              String startEnterpriseTZ, String endEnterpriseTZ, Long startPointId,
                              Long endPointId, String startPointDesc, String endPointDesc) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.tripStart = tripStart;
        this.tripEnd = tripEnd;
        this.startEnterpriseTZ = startEnterpriseTZ;
        this.endEnterpriseTZ = endEnterpriseTZ;
        this.startPointId = startPointId;
        this.endPointId = endPointId;
        this.startPointDesc = startPointDesc;
        this.endPointDesc = endPointDesc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public ZonedDateTime getTripStart() {
        return tripStart;
    }

    public void setTripStart(ZonedDateTime tripStart) {
        this.tripStart = tripStart;
    }

    public ZonedDateTime getTripEnd() {
        return tripEnd;
    }

    public void setTripEnd(ZonedDateTime tripEnd) {
        this.tripEnd = tripEnd;
    }

    public Long getStartPointId() {
        return startPointId;
    }

    public void setStartPointId(Long startPointId) {
        this.startPointId = startPointId;
    }

    public Long getEndPointId() {
        return endPointId;
    }

    public void setEndPointId(Long endPointId) {
        this.endPointId = endPointId;
    }

    public String getStartPointDesc() {
        return startPointDesc;
    }

    public void setStartPointDesc(String startPointDesc) {
        this.startPointDesc = startPointDesc;
    }

    public String getEndPointDesc() {
        return endPointDesc;
    }

    public void setEndPointDesc(String endPointDesc) {
        this.endPointDesc = endPointDesc;
    }

    public String getStartEnterpriseTZ() {
        return startEnterpriseTZ;
    }

    public void setStartEnterpriseTZ(String startEnterpriseTZ) {
        this.startEnterpriseTZ = startEnterpriseTZ;
    }

    public String getEndEnterpriseTZ() {
        return endEnterpriseTZ;
    }

    public void setEndEnterpriseTZ(String endEnterpriseTZ) {
        this.endEnterpriseTZ = endEnterpriseTZ;
    }

    @Override
    public String toString() {
        return "TripDescriptionDTO{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", tripStart=" + tripStart +
                ", tripEnd=" + tripEnd +
                ", startEnterpriseTZ='" + startEnterpriseTZ + '\'' +
                ", endEnterpriseTZ='" + endEnterpriseTZ + '\'' +
                ", startPointId=" + startPointId +
                ", endPointId=" + endPointId +
                ", startPointDesc='" + startPointDesc + '\'' +
                ", endPointDesc='" + endPointDesc + '\'' +
                '}';
    }
}
