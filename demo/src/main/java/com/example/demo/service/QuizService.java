package com.example.demo.service;

import com.example.demo.model.Question;
import com.example.demo.model.Result;
import com.example.demo.model.Choice;
import com.example.demo.repository.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuizService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // 仮の正解判定
    public Result evaluateAnswers(List<Integer> selectedAnswers) {
        int correctCount = 0;

        for (int answer : selectedAnswers) {
            if (answer == 1) { // 仮の正解処理
                correctCount++;
            }
        }

        Result result = new Result();
        result.setCorrectCount(correctCount);
        result.setMessage("You have " + correctCount + " correct answers.");

        return result;
    }

    // 新たに追加したgetQuestionByChapterメソッド
    public Question getQuestionByChapter(int chapterNumber) {
        Question question = new Question();
        question.setChapter(chapterNumber);
        question.setQuestion("第" + chapterNumber + "章の問題の例");

        List<String> choices = List.of("選択肢1", "選択肢2", "選択肢3", "選択肢4");

        List<Choice> choiceList = new ArrayList<>();
        for (int i = 0; i < choices.size(); i++) {
            Choice choice = new Choice();
            choice.setChoiceText(choices.get(i));
            choice.setCorrect(i == 0);
            choice.setQuestion(question);
            choiceList.add(choice);
        }

        question.setChoices(choiceList);

        return question;
    }

    // getQuestionsByChapterメソッド
    public List<Question> getQuestionsByChapter(int chapterNumber) {
        List<Question> questions = questionRepository.findByChapter(chapterNumber);
    
        // 選択肢をランダムに並び替え
        for (Question question : questions) {
            List<Choice> choices = question.getChoices();
            Collections.shuffle(choices); // ← 選択肢をシャッフル
        }
    
        // 問題もランダムに並び替え
        Collections.shuffle(questions); // ← 問題自体もシャッフル
        return questions;
    }

    // 模擬試験用：1〜6章の問題からランダムに40問を取得
    @Transactional
    public List<Question> getRandomQuestionsForMockExam() {
        List<Question> allQuestions = new ArrayList<>();

        for (int chapter = 1; chapter <= 6; chapter++) {
            allQuestions.addAll(getQuestionsByChapter(chapter));
        }

        for (Question q : allQuestions) {
            q.getChoices().size(); // LAZYロード対策
        }

        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(40, allQuestions.size()));
    }

    // 質問IDで1問取得する
    public Question getQuestionById(Long id) {
        Optional<Question> optional = questionRepository.findById(id);
        return optional.orElse(null);
    }
}
