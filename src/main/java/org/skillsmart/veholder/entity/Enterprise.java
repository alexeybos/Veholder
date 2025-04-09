package org.skillsmart.veholder.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.skillsmart.veholder.entity.dto.EnterprisesDriversDto;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "enterprises")
//@SqlResultSetMapping(
//    name = "UserPostMapping",
//    classes = @ConstructorResult(
//        targetClass = UserPostDto.class,
//        columns = {
//            @ColumnResult(name = "user_id", type = Long.class),
//            @ColumnResult(name = "username", type = String.class),
//            @ColumnResult(name = "post_id", type = Long.class),
//            @ColumnResult(name = "post_title", type = String.class)
//        }
//    )
//)
/*@SqlResultSetMapping(
        name = "EnterpriseDriversMapping",
        classes = @ConstructorResult(
                targetClass = EnterprisesDriversDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "drivers", type = String.class)
                }
        )
)*/
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private String directorName;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "enterprises_managers",
    joinColumns = @JoinColumn(name = "enterprises_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "managers_id", referencedColumnName = "id"))
    private Set<Manager> managers = new HashSet<>();
    //@ManyToMany(cascade = CascadeType.ALL)
    //   @JoinTable(name="employee_task",
    //	       joinColumns=  @JoinColumn(name="employee_id", referencedColumnName="id"),
    //           inverseJoinColumns= @JoinColumn(name="task_id", referencedColumnName="id") )
    //   private Set<EmployeeTask> tasks = new HashSet<EmployeeTask>();

    public Enterprise() {
    }

    public Enterprise(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public Set<Manager> getManagers() {
        return managers;
    }

    public void setManagers(Set<Manager> managers) {
        this.managers = managers;
    }

    @Override
    public String toString() {
        return "Enterprise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", directorName='" + directorName + '\'' +
                '}';
    }
}
