package com.projectmanagement.repository;

import com.projectmanagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByNameContaining(String name);
    
    List<Project> findByStartDateAfter(LocalDate date);
    
    List<Project> findByEndDateBefore(LocalDate date);
}