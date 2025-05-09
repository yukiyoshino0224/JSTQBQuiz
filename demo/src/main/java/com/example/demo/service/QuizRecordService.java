package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.QuizRecord;
import com.example.demo.repository.QuizRecordRepository;

@Service
public class QuizRecordService {

    @Autowired
    private QuizRecordRepository quizRecordRepository;

    // ユーザーの quizRecord を保存するメソッド
    public void saveQuizRecord(int correctCount, int totalCount, int chapter, String chapterTitle, Long userId) {
        QuizRecord record = new QuizRecord();
        record.setCorrectCount(correctCount);
        record.setTotalCount(totalCount);
        record.setChapter(chapter);
        record.setChapterTitle(chapterTitle);
        record.setUserId(userId);  // userId をそのまま渡して使う
        record.setCreatedAt(LocalDateTime.now());
        quizRecordRepository.save(record);
    }

    // 履歴を全件取得するメソッド
    public List<QuizRecord> getAllRecords() {
        return quizRecordRepository.findAll();
    }

    // 特定のchapterに紐づく履歴を取得するメソッド
    public List<QuizRecord> getRecordsByChapter(int chapter) {
        return quizRecordRepository.findByChapter(chapter);
    }
    public List<QuizRecord> getRecordsForUser(Long userId) {
        return quizRecordRepository.findByUserId(userId); 
    }
}
