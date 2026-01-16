package org.employdemy.library.lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.employdemy.library.lms.model.Role;

@Data
public class UserUpdateDTO {
//to be able to update without sending the password with the details
    @NotBlank
    private String name;

    @Email
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@(employdemy\\.com|gmail\\.com)$",
            message = "Email must end with @employdemy.com or @gmail.com"

    )
    private String email;


    private String password;

    private Role role;

    private String empId;

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
}
