package org.example.agroguardian_backend.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandService {

    @Autowired
    private CommandRepository repo;

    public void create(Command command) {
        repo.save(command);
    }

    public List<Command> getTopThreeCommands() {
        return repo.getTop3ByOrderByTimestampDesc();
    }
}
