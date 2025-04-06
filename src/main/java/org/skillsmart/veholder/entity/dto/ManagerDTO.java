package org.skillsmart.veholder.entity.dto;

public class ManagerDTO {
    private Long id;
    private String username;
    private String fullName;
    private String enterpriseInfo;

    public ManagerDTO(Long id, String username, String fullName, String enterpriseInfo) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.enterpriseInfo = enterpriseInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEnterpriseInfo() {
        return enterpriseInfo;
    }

    public void setEnterpriseInfo(String enterpriseInfo) {
        this.enterpriseInfo = enterpriseInfo;
    }
}
