package com.example.sena_bhawan.repository.formation;

import com.example.sena_bhawan.entity.formation.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command,Long> {
    List<Command> findAll();
}
