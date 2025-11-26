package org.employdemy.library.lms.dto;

import org.employdemy.library.lms.model.Role;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String empId;
    private boolean active;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
