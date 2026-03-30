package com.shiftsync.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-roles")
@Tag(name = "Role Testing", description = "Temporary endpoints to verify Role-Based Access Control logic")
@SecurityRequirement(name = "BearerAuth")
public class TestRoleController {

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Accessible only by EMPLOYEE role", description = "Returns 200 OK if caller has EMPLOYEE role, otherwise 403 Forbidden.")
    public ResponseEntity<String> testEmployeeAccess() {
        return ResponseEntity.ok("Success: You accessed an EMPLOYEE protected endpoint.");
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Accessible only by MANAGER role", description = "Returns 200 OK if caller has MANAGER role, otherwise 403 Forbidden.")
    public ResponseEntity<String> testManagerAccess() {
        return ResponseEntity.ok("Success: You accessed a MANAGER protected endpoint.");
    }

    @GetMapping("/hr-admin")
    @PreAuthorize("hasRole('HR_ADMIN')")
    @Operation(summary = "Accessible only by HR_ADMIN role", description = "Returns 200 OK if caller has HR_ADMIN role, otherwise 403 Forbidden.")
    public ResponseEntity<String> testHrAdminAccess() {
        return ResponseEntity.ok("Success: You accessed an HR_ADMIN protected endpoint.");
    }

    @GetMapping("/manager-or-hr")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN')")
    @Operation(summary = "Accessible by MANAGER or HR_ADMIN", description = "Returns 200 OK if caller has MANAGER or HR_ADMIN role, otherwise 403 Forbidden.")
    public ResponseEntity<String> testMultipleRolesAccess() {
        return ResponseEntity.ok("Success: You accessed an endpoint protected by MANAGER or HR_ADMIN.");
    }
}
