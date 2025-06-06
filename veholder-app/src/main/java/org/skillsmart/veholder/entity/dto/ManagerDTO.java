package org.skillsmart.veholder.entity.dto;

public class ManagerDTO {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String enterpriseInfo;

    public ManagerDTO(Long id, String username, String password, String fullName, String enterpriseInfo) {
        this.id = id;
        this.username = username;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
