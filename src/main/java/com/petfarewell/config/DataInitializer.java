package com.petfarewell.config;

import com.petfarewell.letter.entity.TributeMessage;
import com.petfarewell.letter.repository.TributeMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TributeMessageRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            repository.save(new TributeMessage("LOVE", "사랑", 1));
            repository.save(new TributeMessage("THANKS", "감사", 2));
            repository.save(new TributeMessage("RESPECT", "존경", 3));
        }
    }
}
