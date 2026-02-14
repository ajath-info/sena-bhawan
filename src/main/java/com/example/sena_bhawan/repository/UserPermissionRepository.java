package com.example.sena_bhawan.repository;


import com.example.sena_bhawan.entity.UserMaster;
import com.example.sena_bhawan.dto.UserPermissionProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserPermissionRepository extends CrudRepository<UserMaster, Long> {

    @Query(value = """
    SELECT 
        m.id AS moduleId,
        m.name AS moduleName,
        p.id AS permissionId,
        p.label AS label,
        p.url AS url,
        bool_or(rp.allowed) AS allowed
    FROM modules m
    JOIN permissions p ON p.module_id = m.id
    LEFT JOIN user_role_info uri ON uri.user_id = :userId
    LEFT JOIN role_permissions rp 
           ON rp.role_id = uri.role_id
          AND rp.permission_id = p.id
    GROUP BY m.id, m.name, p.id, p.label, p.url
    ORDER BY m.id, p.id
""", nativeQuery = true)
    List<UserPermissionProjection> getPermissionsForUser(Long userId);


}

