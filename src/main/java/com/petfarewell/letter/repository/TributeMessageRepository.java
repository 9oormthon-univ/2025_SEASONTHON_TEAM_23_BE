package com.petfarewell.letter.repository;

import com.petfarewell.letter.entity.TributeMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TributeMessageRepository extends JpaRepository<TributeMessage, Long> {
}
