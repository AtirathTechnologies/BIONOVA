package com.cbg.service;

import com.cbg.dto.LoginRequest;
import com.cbg.dto.LoginResponse;
import com.cbg.entity.Employee;
import com.cbg.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;

    public AuthService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Employee employee =
                employeeRepository.findByEmail(request.getEmail())
                        .orElse(null);

        if (employee == null) {
            return new LoginResponse(
                    false,
                    "User Not Found",
                    null
            );
        }

        if (!employee.getPassword()
                .equals(request.getPassword())) {

            return new LoginResponse(
                    false,
                    "Invalid Password",
                    null
            );
        }

        return new LoginResponse(
                true,
                "Login Success",
                employee.getRole()
        );
    }
}