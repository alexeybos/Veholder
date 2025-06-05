package org.skillsmart.veholder.entity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.skillsmart.veholder.entity.Enterprise;

@Getter
@Setter
@NoArgsConstructor
public class EnterpriseExportDTO {
    private Long id;
    private String name;
    private String city;
    private String directorName;
    private String timezon;

    public EnterpriseExportDTO(Enterprise enterprise) {
        this.id = enterprise.getId();
        this.name = enterprise.getName();
        this.city = enterprise.getCity();
        this.directorName = enterprise.getDirectorName();
        this.timezon = null;
        if (enterprise.getTimezone() != null) this.timezon = enterprise.getTimezone().getId();
    }
}
