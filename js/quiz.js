document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById('quiz-form');
    const submitButton = document.getElementById('submit-button');
    const nextButton = document.getElementById('next-question');
    const radioButtons = document.querySelectorAll('input[type="radio"]');
  
    form.addEventListener("submit", function (e) {
      e.preventDefault();
  
      const selected = document.querySelector('input[name="answer"]:checked');
      if (!selected) return;
  
      const answerValue = selected.value;
      const questionId = /*[[${question.id}]]*/ 0;
  
      fetch('/submit', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `answer=${encodeURIComponent(answerValue)}&questionId=${questionId}`
      })
        .then(response => response.text())
        .then(result => {
          // --- ここで正誤判定表示！
          let judgeMessage = document.getElementById('judge-message');
          if (!judgeMessage) {
            judgeMessage = document.createElement('div');
            judgeMessage.id = 'judge-message';
            judgeMessage.style.marginTop = '10px';
            judgeMessage.style.fontSize = '18px';
            judgeMessage.style.fontWeight = 'bold';
            document.getElementById('correct-answer').after(judgeMessage);
          }
  
          if (result === "correct") {
            judgeMessage.textContent = '正解！🎉';
            judgeMessage.style.color = 'green';
          } else if (result === "incorrect") {
            judgeMessage.textContent = '不正解…😢';
            judgeMessage.style.color = 'red';
          } else if (result === "already answered") {
            alert("この問題はすでに回答済みです");
            // 次へボタンの有効化とかそのまま続けてね〜
          }
  
          // ボタン無効化＆次へ表示も必要ならここで！
  
        })
        .catch(error => {
          alert("エラーが発生しました：" + error.message);
        });
  
      // ボタン無効化などもここで引き続きやってOKだよ！
    });
  });
  