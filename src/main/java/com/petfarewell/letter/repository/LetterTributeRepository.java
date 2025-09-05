package com.petfarewell.letter.repository;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.LetterTribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LetterTributeRepository extends JpaRepository<LetterTribute, Long> {
    boolean existsByUserAndLetter(User user, Letter letter);
    List<LetterTribute> findAllByLetterId(Long letterId);
    long countByUserId(Long userId);
    @Query("SELECT SUM(l.tributeCount) FROM Letter l WHERE l.user.id = :userId")
    Long getTotalTributeCountByUserId(@Param("userId") Long userId);
}
