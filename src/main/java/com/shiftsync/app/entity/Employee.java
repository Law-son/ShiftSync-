package com.shiftsync.app.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "skills_certifications", columnDefinition = "TEXT[]")
    private String[] skillsCertifications;

    @Column(name = "contracted_weekly_hours")
    private BigDecimal contractedWeeklyHours;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Employee() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public String[] getSkillsCertifications() { return skillsCertifications; }
    public void setSkillsCertifications(String[] skillsCertifications) { this.skillsCertifications = skillsCertifications; }
    public BigDecimal getContractedWeeklyHours() { return contractedWeeklyHours; }
    public void setContractedWeeklyHours(BigDecimal contractedWeeklyHours) { this.contractedWeeklyHours = contractedWeeklyHours; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
