package org.skillsmart.veholder.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
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

    /*@OneToOne
    @JoinColumn(name = "username")
    private User user;*/
    /*@Column(name = "username", updatable = false, unique = true)
    @Override
    public String getUsername() {
        return super.getUsername();
    }*/

    protected Manager() {
    }

    public Manager(String username, String password, Set<Role> authorities, String fullName) {
        super(username, password, authorities);
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
}
