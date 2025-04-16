package com.example.demo.controller;

import com.example.demo.model.Result;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.model.Answer;
import com.example.demo.model.Choice;
import com.example.demo.model.Question;
import com.example.demo.model.QuestionView;
import com.example.demo.service.QuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class MenuController {

    private final AnswerRepository answerRepository;
    private final QuizService quizService;

    @Autowired
    public MenuController(QuizService quizService, AnswerRepository answerRepository) {
        this.quizService = quizService;
        this.answerRepository = answerRepository;
    }

    /*
     * @GetMapping("/menu")
     * public String showMenu() {
     * answerRepository.deleteAll();
     * return "menu";
     * }
     */

    @GetMapping("/evaluate")
    public String evaluateAnswers(Model model) {
        List<Answer> answers = answerRepository.findAll(); // ←全件とって評価！
        int correctCount = (int) answers.stream().filter(Answer::isCorrect).count(); // 正解の数カウント

        Result result = new Result(correctCount, answers.size()); // ←Result に詰める（作ってる？）
        model.addAttribute("result", result);

        if (!answers.isEmpty()) {
            Long firstQuestionId = answers.get(0).getQuestionId();
            Question question = quizService.getQuestionById(firstQuestionId); // service 経由で取得

            if (question != null) {
                model.addAttribute("chapterNumber", question.getChapter());
                model.addAttribute("chapterTitle", question.getChapterTitle());
            }
        }

        List<QuestionView> questionsForView = answers.stream().map(answer -> {
            Question question = quizService.getQuestionById(answer.getQuestionId());
            if (question != null) {
                QuestionView view = new QuestionView();
                view.setQuestion(question.getQuestion());
                view.setCorrect(answer.isCorrect());
                view.setChoices(question.getChoices());
                return view;
            }
            return null;
        }).filter(Objects::nonNull).toList();

        model.addAttribute("questions", questionsForView);

        return "result";
    }

    @GetMapping("/reset")
    public String resetAnswers() {
        answerRepository.deleteAll(); // 回答履歴を全部削除！
        return "redirect:/menu"; // メニューに戻る
    }

    // クイズページ表示（指定された章と問題番号）
    @GetMapping("/chapter/{chapterNumber}/question/{questionNumber}")
    public String showQuestionByNumber(
            @PathVariable int chapterNumber,
            @PathVariable int questionNumber,
            Model model) {
        List<Question> questions = quizService.getQuestionsByChapter(chapterNumber);

        // 問題番号が範囲内かチェック
        if (!questions.isEmpty() && questionNumber >= 1 && questionNumber <= questions.size()) {
            Question question = questions.get(questionNumber - 1);

            // 正解の選択肢を取得
            Choice correctChoice = question.getChoices().stream()
                    .filter(Choice::isCorrect)
                    .findFirst()
                    .orElse(null);

            model.addAttribute("correctChoiceText", correctChoice != null ? correctChoice.getChoiceText() : "正解なし");
            model.addAttribute("chapterNumber", chapterNumber);
            model.addAttribute("chapterTitle", question.getChapterTitle());
            model.addAttribute("displayNumber", questionNumber);
            model.addAttribute("question", question);
            model.addAttribute("hasNext", questionNumber < questions.size());
        } else {
            // 範囲外だった場合
            model.addAttribute("chapterNumber", chapterNumber);
            model.addAttribute("chapterTitle", "該当なし");
            model.addAttribute("displayNumber", questionNumber);
            model.addAttribute("question", null);
            model.addAttribute("correctChoiceText", "問題が存在しません");
            model.addAttribute("hasNext", false);
        }

        return "quiz";
    }

    // 最初の問題（デフォルト表示）
    @GetMapping("/chapter/{chapterNumber}")
    public String showChapter(@PathVariable int chapterNumber, Model model) {
        return "redirect:/chapter/" + chapterNumber + "/question/1";
    }

    @PostMapping("/submit")
    @ResponseBody
    public String handleAnswer(
            @RequestParam("answer") Long selectedChoiceId,
            @RequestParam("questionId") Long questionId) {
        System.out.println("受け取った answerId: " + selectedChoiceId + ", questionId: " + questionId);

        Question currentQuestion = quizService.getQuestionById(questionId); // ← service 側で用意してね

        if (currentQuestion == null) {
            return "指定された問題が見つかりません";
        }

        boolean isCorrect = currentQuestion.getChoices().stream()
                .filter(Choice::isCorrect)
                .anyMatch(choice -> choice.getId().equals(selectedChoiceId));

        Answer answer = new Answer();
        answer.setQuestionId(currentQuestion.getId());
        answer.setSelectedChoiceId(selectedChoiceId);
        answer.setCorrect(isCorrect);

        try {
            answerRepository.save(answer);
            System.out.println("回答を保存しました！");
        } catch (Exception e) {
            System.out.println("保存時エラー: " + e.getMessage());
        }

        return "OK";
    }


    // 模擬試験の初期画面（ランダムな40問）
    @GetMapping("/quiz/random")
    @Transactional
    public String startMockExam(Model model, HttpSession session) {
        List<Question> mockExamQuestions = quizService.getRandomQuestionsForMockExam();

        // 最初の問題を表示
        if (!mockExamQuestions.isEmpty()) {
            Question question = mockExamQuestions.get(0);

            session.setAttribute("mockExamQuestions", mockExamQuestions);
            session.setAttribute("currentQuestionIndex", 0);

            // 正解の選択肢
            Choice correctChoice = question.getChoices().stream()
                    .filter(Choice::isCorrect)
                    .findFirst()
                    .orElse(null);

            // 問題の情報を設定
            model.addAttribute("correctChoiceText", correctChoice != null ? correctChoice.getChoiceText() : "正解なし"); // ここでは仮に"正解なし"
            model.addAttribute("question", question);
            model.addAttribute("hasNext", mockExamQuestions.size() > 1); // 次の問題があるかの判定
            model.addAttribute("displayNumber", 1);
        }

        return "quiz";
    }

    // 模擬試験の次の問題
    @GetMapping("/quiz/random/question/{questionNumber}")
    public String showMockExamQuestion(
            @PathVariable int questionNumber,
            Model model,
            HttpSession session) {
        List<Question> mockExamQuestions = (List<Question>) session.getAttribute("mockExamQuestions");

        // もし範囲外の番号ならエラーページ
        if (mockExamQuestions == null || questionNumber < 1 || questionNumber > mockExamQuestions.size()) {
            System.out.println("ERROR: mockExamQuestions is null in model");
            model.addAttribute("message", "問題が存在しません");
            return "error";
        }

        // 現在の問題を取得
        Question question = mockExamQuestions.get(questionNumber - 1);

        // 正解の選択肢
        Choice correctChoice = question.getChoices().stream()
                .filter(Choice::isCorrect)
                .findFirst()
                .orElse(null);

        model.addAttribute("correctChoiceText", correctChoice != null ? correctChoice.getChoiceText() : "正解なし");
        model.addAttribute("question", question);
        model.addAttribute("hasNext", questionNumber < mockExamQuestions.size());
        model.addAttribute("displayNumber", questionNumber);

        return "quiz";
    }

    @GetMapping("/menu")
    public String showMenuPage(Principal principal, Model model) {
        if (principal == null) {
            // 未ログインの場合はログインページへリダイレクト
            return "redirect:/login";
        }

        String username = principal.getName(); // ログインユーザーの名前を取得
        model.addAttribute("username", username); // 必要なら画面にも渡す

        return "menu";
    }

}
