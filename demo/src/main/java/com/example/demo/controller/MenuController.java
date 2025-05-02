package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Answer;
import com.example.demo.model.Choice;
import com.example.demo.model.Question;
import com.example.demo.model.QuestionView;
import com.example.demo.model.QuizRecord;
import com.example.demo.model.Result;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.service.QuizRecordService;
import com.example.demo.service.QuizService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;

@Controller
public class MenuController {

    private final AnswerRepository answerRepository;
    private final QuizService quizService;
    private QuizRecordService quizRecordService;

    // MenuController内にこのメソッドを追加する
    public void saveUserIdToSession(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            session.setAttribute("userId", userDetails.getUsername()); // userIdをセッションにセット
        }
    }

    @Autowired
    public MenuController(QuizService quizService, AnswerRepository answerRepository,
            QuizRecordService quizRecordService) {
        this.quizService = quizService;
        this.answerRepository = answerRepository;
        this.quizRecordService = quizRecordService;
    }

    @GetMapping("/menu")
    public String showMenu(HttpServletResponse response, HttpSession session) {

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        session.setAttribute("username", username);
        answerRepository.deleteAll();
        return "menu";
    }

    @GetMapping("/evaluate")
    public String evaluateAnswers(Model model, HttpSession session) {
        System.out.println("=== /evaluate 処理開始 ===");

        List<Answer> answers = answerRepository.findAll(); // 回答一覧取得
        System.out.println("回答数: " + answers.size());

        int correctCount = (int) answers.stream().filter(Answer::isCorrect).count(); // 正解数カウント
        System.out.println("正解数: " + correctCount);

        model.addAttribute("result", new Result(correctCount, answers.size())); // 結果をResultで渡す

        Boolean isMockExam = (Boolean) session.getAttribute("isMockExam");
        Map<Long, Question> questionMap = (Map<Long, Question>) session.getAttribute("questionMap");

        System.out.println("模擬試験かどうか: " + isMockExam);

        // 模擬試験 or 通常問題の表示設定
        if (Boolean.TRUE.equals(isMockExam)) {
            model.addAttribute("chapterNumber", "模擬試験");
            model.addAttribute("chapterTitle", "");


            double percentage = (answers.size() == 0) ? 0.0 : ((double) correctCount / answers.size()) * 100;
            model.addAttribute("isPass", percentage >= 65.0);

            model.addAttribute("isMockExam", Boolean.TRUE.equals(isMockExam));

            List<Question> mockExamQuestions = (List<Question>) session.getAttribute("mockExamQuestions");
            List<Integer> chapters = new ArrayList<>(); // 章番号を格納するリストに変更
            for (Question question : mockExamQuestions) {
                chapters.add(question.getChapter()); // 各問題の章番号をリストに追加
            }
            model.addAttribute("chapters", chapters); // 各問題の章情報をモデルに追加

        } else if (!answers.isEmpty()) {
            Long firstQuestionId = answers.get(0).getQuestionId();
            Question firstQuestion = questionMap != null ? questionMap.get(firstQuestionId) : null;

            if (firstQuestion != null) {
                model.addAttribute("chapterNumber", firstQuestion.getChapter());
                model.addAttribute("chapterTitle", firstQuestion.getChapterTitle());
            }
        }

        // 表示用に加工した問題リストを作成
        List<QuestionView> questionsForView = answers.stream()
                .map(answer -> {
                    Question question = questionMap != null ? questionMap.get(answer.getQuestionId()) : null;
                    if (question == null)
                        return null;

                    QuestionView view = new QuestionView();
                    view.setQuestion(question.getQuestion());
                    view.setCorrect(answer.isCorrect());
                    view.setChoices(question.getChoices());
                    view.setSelectedChoiceId(answer.getSelectedChoiceId());
                    return view;
                })
                .filter(Objects::nonNull)
                .toList();

        model.addAttribute("questions", questionsForView);

        // 通常問題のみ記録を保存
        Long userId = (Long) session.getAttribute("userId");
        System.out.println("ユーザーID: " + userId);

        if (userId != null && !Boolean.TRUE.equals(isMockExam)) {
            System.out.println("保存処理に入るよ〜！");

            Long firstQuestionId = answers.get(0).getQuestionId();
            Question firstQuestion = questionMap != null ? questionMap.get(firstQuestionId) : null;

            if (firstQuestion != null) {
                System.out.println(
                        "記録保存情報: chapter=" + firstQuestion.getChapter() + ", title=" + firstQuestion.getChapterTitle());

                quizRecordService.saveQuizRecord(
                        correctCount,
                        answers.size(),
                        firstQuestion.getChapter(),
                        firstQuestion.getChapterTitle(),
                        userId);

                System.out.println("保存処理呼び出し完了！");
            } else {
                System.out.println("firstQuestionがnullだったよ😥");
            }
        } else {
            System.out.println("保存スキップされたよ〜（userIdなし or 模擬試験）");
        }

        return "result"; // 結果ページに遷移
    }

    @GetMapping("/reset")
    public String resetAnswers(HttpSession session) {
        answerRepository.deleteAll(); // 回答履歴を全部削除！
        session.removeAttribute("isMockExam"); // ★リセット時に削除
        session.removeAttribute("mode"); // ★ 追加！mode もリセット！
        return "redirect:/menu"; // メニューに戻る
    }

    // クイズページ表示（指定された章と問題番号）
    @GetMapping("/chapter/{chapterNumber}/question/{questionNumber}")
    public String showQuestionByNumber(
            @PathVariable int chapterNumber,
            @PathVariable int questionNumber,

            Model model, HttpSession session) {

        Integer currentChapter = (Integer) session.getAttribute("currentChapter");

        if (currentChapter == null || currentChapter != chapterNumber) {
            // 不正アクセスと判断してエラー表示
            model.addAttribute("chapterNumber", chapterNumber);
            model.addAttribute("chapterTitle", "アクセスできません");
            model.addAttribute("displayNumber", questionNumber);
            model.addAttribute("question", null);
            model.addAttribute("correctChoiceText", "不正なアクセスが検出されました");
            model.addAttribute("hasNext", false);
            return "redirect:/error";
        }

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

            List<Integer> answeredQuestions = new ArrayList<>();
            answeredQuestions.add(1); // ← 1問目は表示してるので入れとく
            session.setAttribute("answeredQuestions", answeredQuestions);

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

        if (!answeredQuestions.contains(questionNumber)) {
            answeredQuestions.add(questionNumber);
            session.setAttribute("answeredQuestions", answeredQuestions);
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

    @GetMapping("/record")
    public String showRecordPage(Model model) {
        // 履歴データをDBから取得
        List<QuizRecord> records = quizRecordService.getAllRecords(); // 履歴取得

        // Modelにデータを渡す
        model.addAttribute("records", records);

        return "record"; // record.html に遷移
    }

}
