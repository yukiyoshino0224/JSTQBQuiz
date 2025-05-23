package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.QuizRecord;

@Repository
public interface QuizRecordRepository extends JpaRepository<QuizRecord, Long> {
    List<QuizRecord> findAllByOrderByCreatedAtDesc(); // 最新順に表示したい

    List<QuizRecord> findByUserId(Long userId);
    List<QuizRecord> findByChapter(int chapter);
    // QuizRecordRepository.java
    List<QuizRecord> findByUserIdAndChapterOrderByCreatedAtDesc(Long userId, int chapter);
    List<QuizRecord> findByUserIdAndIsMockExamTrueOrderByCreatedAtDesc(Long userId);


}
