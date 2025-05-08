package org.example.agroguardian_backend.command;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandRepository extends JpaRepository<Command, String> {
    List<Command> getTop3ByOrderByTimestampDesc();
}

