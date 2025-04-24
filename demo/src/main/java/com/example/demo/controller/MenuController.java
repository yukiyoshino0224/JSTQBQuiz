package com.example.demo.controller;

import com.example.demo.model.Result;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.model.Answer;
import com.example.demo.model.Choice;
import com.example.demo.model.Question;
import com.example.demo.model.QuestionView;
import com.example.demo.service.QuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/menu")
    public String showMenu(HttpSession session) {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        session.setAttribute("username", username);
        answerRepository.deleteAll();
        return "menu";
    }

    @GetMapping("/evaluate")
    public String evaluateAnswers(Model model, HttpSession session) {
        List<Answer> answers = answerRepository.findAll(); // ←全件とって評価！
        int correctCount = (int) answers.stream().filter(Answer::isCorrect).count(); // 正解の数カウント

        Result result = new Result(correctCount, answers.size()); // ←Result に詰める（作ってる？）
        model.addAttribute("result", result);

        Boolean isMockExam = (Boolean) session.getAttribute("isMockExam");

        if (Boolean.TRUE.equals(isMockExam)) {
            model.addAttribute("chapterNumber", "模擬試験"); // ★模擬試験用
            model.addAttribute("chapterTitle", "");
            model.addAttribute("isMockExam", Boolean.TRUE.equals(isMockExam));
        } else if (!answers.isEmpty()) {
            Long firstQuestionId = answers.get(0).getQuestionId();
      
            Map<Long, Question> questionMap = (Map<Long, Question>) session.getAttribute("questionMap");
            Question question = questionMap != null ? questionMap.get(firstQuestionId) : null;

            if (question != null) {
                model.addAttribute("chapterNumber", question.getChapter());
                model.addAttribute("chapterTitle", question.getChapterTitle());
            }
        }
        Map<Long, Question> questionMap = (Map<Long, Question>) session.getAttribute("questionMap");

        List<QuestionView> questionsForView = answers.stream().map(answer -> {
            Question question = questionMap != null ? questionMap.get(answer.getQuestionId()) : null;
            if (question != null) {
                QuestionView view = new QuestionView();
                view.setQuestion(question.getQuestion());
                view.setCorrect(answer.isCorrect());
                view.setChoices(question.getChoices()); // 正解情報を含む選択肢のリストを設定
                view.setSelectedChoiceId(answer.getSelectedChoiceId()); // ユーザーが選択したIDを設定
                return view;
            } else {
                return null;
            }
        }).filter(Objects::nonNull).toList();

        //
        if (Boolean.TRUE.equals(isMockExam)) {
            model.addAttribute("chapterNumber", "模擬試験"); // ★模擬試験用
            model.addAttribute("chapterTitle", "");
            model.addAttribute("isMockExam", Boolean.TRUE.equals(isMockExam));

            // ★ 合否判定追加（正答率65%以上で合格）
            double percentage = (answers.size() == 0) ? 0.0 : ((double) correctCount / answers.size()) * 100;
            boolean isPass = percentage >= 65.0;
            model.addAttribute("isPass", isPass);
        }
        //

        model.addAttribute("questions", questionsForView);

        return "result";
    }

    @GetMapping("/reset")
    public String resetAnswers(HttpSession session) {
        answerRepository.deleteAll(); // 回答履歴を全部削除！
        session.removeAttribute("isMockExam"); // ★リセット時に削除
        return "redirect:/menu"; // メニューに戻る
    }

    // クイズページ表示（指定された章と問題番号）
    @GetMapping("/chapter/{chapterNumber}/question/{questionNumber}")
    public String showQuestionByNumber(
            @PathVariable int chapterNumber,
            @PathVariable int questionNumber,

            Model model , HttpSession session) { 
                
            Integer currentChapter = (Integer) session.getAttribute("currentChapter");

            if (currentChapter == null || currentChapter != chapterNumber) {
                // 不正アクセスと判断してエラー表示
                model.addAttribute("chapterNumber", chapterNumber);
                model.addAttribute("chapterTitle", "アクセスできません");
                model.addAttribute("displayNumber", questionNumber);
                model.addAttribute("question", null);
                model.addAttribute("correctChoiceText", "不正なアクセスが検出されました");
                model.addAttribute("hasNext", false);
                return "redirect:/error";}

        List<Question> questions = (List<Question>) session.getAttribute("chapterQuestions");

        if (!questions.isEmpty() && questionNumber >= 1 && questionNumber <= questions.size()) {
            Question question = questions.get(questionNumber - 1);

            // ✅ 順番通りに進んでいるかチェック
            List<Integer> answeredQuestions = (List<Integer>) session.getAttribute("answeredQuestions");
            if (answeredQuestions == null) {
                answeredQuestions = new ArrayList<>();
            }


        if (questionNumber > 1 && !answeredQuestions.contains(questionNumber - 1)) {
            // 順番が守られていない場合、500エラーをスロー
            return "redirect:/error"; // 500エラーページにリダイレクト
        }

            // 正解の選択肢を取得
            Choice correctChoice = question.getChoices().stream()
                    .filter(Choice::isCorrect)
                    .findFirst()
                    .orElse(null);

            // ★★【ここから追加：回答済みかどうかチェック】★★
            Answer existingAnswer = answerRepository.findByQuestionId(question.getId());
            boolean isAlreadyAnswered = existingAnswer != null;

            model.addAttribute("isAlreadyAnswered", isAlreadyAnswered);
            model.addAttribute("selectedChoiceId", isAlreadyAnswered ? existingAnswer.getSelectedChoiceId() : null);

            model.addAttribute("correctChoiceText", correctChoice != null ? correctChoice.getChoiceText() : "正解なし");
            model.addAttribute("chapterNumber", chapterNumber);
            model.addAttribute("chapterTitle", question.getChapterTitle());
            model.addAttribute("displayNumber", questionNumber);
            model.addAttribute("question", question);
            model.addAttribute("hasNext", questionNumber < questions.size());

            // 解答済み問題リストに現在の問題番号を追加
            if (!answeredQuestions.contains(questionNumber)) {
                answeredQuestions.add(questionNumber);
                session.setAttribute("answeredQuestions", answeredQuestions);
            }
        } else {
            // 範囲外だった場合、404エラーを返す
            return "redirect:/404"; // 404ページへリダイレクト
        }

        return "quiz";
    }

    // 最初の問題（デフォルト表示）
    @GetMapping("/chapter/{chapterNumber}")
    public String showChapter(@PathVariable int chapterNumber, Model model, HttpSession session) {
        session.setAttribute("currentChapter", chapterNumber);

        // ランダムに並べた問題を取得してセッションに保存
        List<Question> questions = quizService.getQuestionsByChapter(chapterNumber);

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question q : questions) {
            questionMap.put(q.getId(), q);
        }

        session.setAttribute("questionMap", questionMap);
        session.setAttribute("chapterQuestions", questions); // ←ここがポイント

        session.setAttribute("answeredQuestions", new ArrayList<Integer>());
        return "redirect:/chapter/" + chapterNumber + "/question/1";

    }

    @PostMapping("/submit")
    @ResponseBody
    public String handleAnswer(
            @RequestParam("answer") Long selectedChoiceId,
            @RequestParam("questionId") Long questionId) {
        System.out.println("受け取った answerId: " + selectedChoiceId + ", questionId: " + questionId);

        Question currentQuestion = quizService.getQuestionById(questionId);

        if (currentQuestion == null) {
            return "指定された問題が見つかりません";
        }

        // ★ すでに回答されてるかチェック！
        Answer existingAnswer = answerRepository.findByQuestionId(questionId);
        if (existingAnswer != null) {
            System.out.println("⚠️ すでに回答済みの問題です！登録しません！");
            return "already answered"; // JS側でこの文字をキャッチして何もしないようにできる
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

        // return "OK";
        //
        // 結果を返すためのデータを返す
        if (isCorrect) {
            return "correct"; // 正解
        } else {
            return "incorrect"; // 不正解
        }
        //
    }

    // 模擬試験の初期画面（ランダムな40問）
    @GetMapping("/quiz/random")
    @Transactional
    public String startMockExam(Model model, HttpSession session) {
        List<Question> mockExamQuestions = quizService.getRandomQuestionsForMockExam();

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question q : mockExamQuestions) {
            questionMap.put(q.getId(), q);
        }

        // 最初の問題を表示
        if (!mockExamQuestions.isEmpty()) {
            Question question = mockExamQuestions.get(0);

            session.setAttribute("mockExamQuestions", mockExamQuestions);
            session.setAttribute("questionMap", questionMap); 
            session.setAttribute("currentQuestionIndex", 0);
            session.setAttribute("isMockExam", true);

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
            return "redirect:/404"; // 404ページへリダイレクト
        }

        // 現在の問題を取得
        Question question = mockExamQuestions.get(questionNumber - 1);

        // 解答済みの問題番号リストを取得
        List<Integer> answeredQuestions = (List<Integer>) session.getAttribute("answeredQuestions");
        if (answeredQuestions == null) {
            answeredQuestions = new ArrayList<>();
        }

        // ユーザーが解いていない番号の問題にアクセスした場合、500エラーページへリダイレクト
        if (questionNumber > 1 && !answeredQuestions.contains(questionNumber - 1)) {
            return "redirect:/error"; // 500エラーページにリダイレクト
        }

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
}
