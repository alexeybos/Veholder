package org.skillsmart.veholder.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
//@Table(name = "managers")
//@AttributeOverride(name = "username", column = @Column(name = "username", nullable = false, unique = true))
public class Manager extends User {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "enterprises_managers",
            joinColumns = @JoinColumn(name = "managers_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "enterprises_id", referencedColumnName = "id"))
    private Set<Enterprise> enterprises = new HashSet<>();

    public Manager() {
    }

    public Manager(String username, String password, String fullName) {
        super(username, password);
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Enterprise> getEnterprises() {
        return enterprises;
    }

    public void setEnterprises(Set<Enterprise> enterprises) {
        this.enterprises = enterprises;
    }
}
