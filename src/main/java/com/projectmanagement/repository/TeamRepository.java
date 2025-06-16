package com.projectmanagement.repository;

import com.projectmanagement.model.Team;
import com.projectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTeamLead(User teamLead);
    boolean existsByName(String name);
}