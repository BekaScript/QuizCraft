let currentQuestionIndex = 0;
let totalQuestions = 0;
let userAnswers = [];
const mode = document.querySelector(".container").getAttribute('data-mode');
let isAnswered = false;


function initializeQuiz() {
    currentQuestionIndex = parseInt(document.querySelector('.progress').getAttribute('data-current'));
    totalQuestions = parseInt(document.querySelector('.progress').getAttribute('data-total'));
    writtenAnswerListener();
}

document.addEventListener('DOMContentLoaded', initializeQuiz);

function writtenAnswerListener(){
    if(mode !== 'learn'){
        const textarea = document.getElementById('writtenAnswerInput');
        if (textarea){
            textarea.value = userAnswers[currentQuestionIndex] || '';
            textarea.addEventListener('input', function(){
                userAnswers[currentQuestionIndex] = this.value.trim();
            })
        }
    }
}

function selectOption(element) {
    if (mode === 'learn' && isAnswered) {
        return;
    }

    document.querySelectorAll('.option').forEach(option => {
        option.classList.remove('selected', 'correct', 'incorrect');
    });
    element.classList.add('selected');

    const selectedIndex = parseInt(element.getAttribute('data-index'));
    userAnswers[currentQuestionIndex] = selectedIndex;

    if (mode === 'learn'){
        isAnswered = true;

        const correctAnswerIndex = parseInt(document.getElementById('question-text').getAttribute('data-correct-answer-index'));
        const options = document.querySelectorAll('.option');

        if (selectedIndex === correctAnswerIndex){
            element.classList.add('correct');
        } else{
            element.classList.add('incorrect');
            options[correctAnswerIndex].classList.add('correct');
            fetch('quiz/markIncorrect', {method: 'POST'});
        }
        document.getElementById('nextButton').disabled = false;
    }

    const explanationArea = document.getElementById('explanationArea');
    if(explanationArea && explanationArea.textContent.trim() !== '') explanationArea.style.display = 'block';
}

function submitWrittenAnswer(){
    if (isAnswered) return;

    const textarea = document.getElementById('writtenAnswerInput');
    const userAnswer = textarea.value.trim();
    userAnswers[currentQuestionIndex] = userAnswer;

    const correctAnswer = document.getElementById('question-text').getAttribute('data-correct-written-answer');
    if(userAnswer.localeCompare(correctAnswer, undefined, {sensitivity: 'accent'}) === 0){
        textarea.style.borderColor = '#28a745' //green
    } else {
        textarea.style.borderColor = '#dc3646'; //red
        fetch('quiz/markIncorrect', {method: 'POST'});
    }
    textarea.disabled = true;
    isAnswered = true;
    document.getElementById('nextButton').disabled = false;

    const explanationArea = document.getElementById('explanationArea');
    if(explanationArea&&explanationArea.textContent.trim() !==''){
        explanationArea.style.display = 'block';
    }
}


function updateQuestion(question){
    isAnswered = false;
    
    const questionElement = document.querySelector('#question-text');
    questionElement.textContent = question.questionText;
    
    if(mode === 'learn') {
        if (question.questionType === 'MULTIPLE_CHOICE'){
            questionElement.setAttribute('data-correct-answer-index', question.correctAnswerIndex);
        } else if (question.questionType === 'WRITTEN_ANSWER'){
            questionElement.setAttribute('data-correct-written-answer', question.correctWrittenAnswer);
        }
    }

    const answerArea = document.getElementById('answer-area');
    answerArea.innerHTML = '';

    if(question.questionType === 'MULTIPLE_CHOICE'){
        const optionsList = document.createElement('ul');
        optionsList.className = 'options';
        optionsList.innerHTML = '';
    
        question.options.forEach((option, index) => {
            const li = document.createElement('li');
            li.textContent = option;
            li.className = 'option';
            li.setAttribute('data-index', index);
            li.onclick = function() { selectOption(this); };
            optionsList.appendChild(li);
        });
        answerArea.appendChild(optionsList)
    } else if(question.questionType === 'WRITTEN_ANSWER'){
        const container = document.createElement('div');
        container.className = 'written-answer-container';

        const textarea = document.createElement('textarea');
        textarea.id = 'writtenAnswerInput';
        textarea.className = 'written-answer-input';
        textarea.placeholder = 'Your answer';
        textarea.disabled = false;
        
        container.appendChild(textarea);

        if(mode == 'learn'){
            const submitButton = document.createElement('button');
            submitButton.id = 'submitWrittenAnswerButton';
            submitButton.textContent = 'Submit'
            submitButton.onclick = submitWrittenAnswer;
            container.appendChild(submitButton);
        }

        answerArea.appendChild(container);

        writtenAnswerListener();
    }


    document.querySelector('.progress').innerHTML = 
        `${currentQuestionIndex + 1}/${totalQuestions}`;

    document.getElementById('previousButton').disabled = currentQuestionIndex === 0;
    document.getElementById('nextButton').disabled = 
        currentQuestionIndex === totalQuestions - 1;
    
    if(mode === 'learn') {
        document.getElementById('nextButton').disabled = true;    
    }

    const explanationArea = document.getElementById('explanationArea');
    if (explanationArea) explanationArea.style.display = 'none';
}

function showResultModal(){
    fetch('/quiz/results', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userAnswers)
    })
    .then(response => response.json())
    .then(result => {
        const resultContent = document.getElementById("resultContent");
        resultContent.innerHTML = `<h3>Score: ${result.correctAnswersCount}/${totalQuestions}<h3>`;

        const modal = document.getElementById('resultModal');
        modal.classList.add('show');   
    });
}

function handleFinishOrQuit(){
    if(mode=='learn'){
        window.location.href = '/';
    } else{
        showResultModal();
    }
}

function closeResultModal(){
    document.getElementById('resultModal').classList.remove('show');
}


function fetchNextQuestion(){
    currentQuestionIndex++;
    fetch('/quiz/next')
        .then(response => response.json())
        .then(updateQuestion);
}

function fetchPreviousQuestion(){
    currentQuestionIndex--;
    fetch('/quiz/previous')
        .then(response=>response.json())
        .then(updateQuestion)
}