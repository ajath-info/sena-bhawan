package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.Role;
import java.util.List;

public interface RoleCrud {

    Role getRoleById(Long id);
    Role addRole(Role role);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
}
