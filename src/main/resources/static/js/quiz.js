let questions = [];
let IAQs = []; //Incorrectly Answered Questions
let totalQuestions = 0;
let answeredQuestions = 0;
let currentQuestion = null;
let isAnswered = false;
let quizTitle = '';
let currentQuizId = null;
let isRetakeMode = false; 
document.addEventListener('DOMContentLoaded', initializeQuiz);

function initializeQuiz() {
    
    isRetakeMode = /^\/quiz\/\d+$/.test(window.location.pathname);

    const questionsJson = localStorage.getItem('questions');
    if (!questionsJson) {
        alert('No quiz questions found. Please return to the home page and create a quiz.');
        window.location.href = '/';
        return;
    }
    
    questions = JSON.parse(questionsJson);
    totalQuestions = questions.length;
    quizTitle = localStorage.getItem('quizTitle') || 'Generated Quiz';
    currentQuizId = localStorage.getItem('currentQuizId') || null;
    
    document.getElementById('total-questions').textContent = totalQuestions;
    
    showNextQuestion();
}

function showNextQuestion() {
    if (questions.length === 0) {
        if (IAQs.length === 0) {
            showQuizResults();
            return;
        }
        
        currentQuestion = IAQs.shift();
    } else {
        currentQuestion = questions.shift();
    }
    
    isAnswered = false;
    displayQuestion(currentQuestion);
}

function displayQuestion(question) {
    const questionElement = document.getElementById('question-text');
    questionElement.textContent = question.questionText;
    
    const explanationArea = document.getElementById('explanationArea');
    explanationArea.style.display = 'none';
    explanationArea.textContent = question.explanation || '';
    
    const answerArea = document.getElementById('answer-area');
    answerArea.innerHTML = '';
    
    if(question.questionType === 'MULTIPLE_CHOICE'){
        const optionsList = document.createElement('ul');
        optionsList.className = 'options';
        
        question.options.forEach((option, index) => {
            const li = document.createElement('li');
            li.textContent = option;
            li.className = 'option';
            li.setAttribute('data-index', index);
            li.onclick = function() { selectOption(this); };
            optionsList.appendChild(li);
        });
        answerArea.appendChild(optionsList);
    } else if(question.questionType === 'WRITTEN_ANSWER'){
        const container = document.createElement('div');
        container.className = 'written-answer-container';

        const textarea = document.createElement('textarea');
        textarea.id = 'writtenAnswerInput';
        textarea.className = 'written-answer-input';
        textarea.placeholder = 'Your answer';
        textarea.value = '';
        
        container.appendChild(textarea);

        const submitButton = document.createElement('button');
        submitButton.id = 'submitWrittenAnswerButton';
        submitButton.textContent = 'Submit';
        submitButton.onclick = submitWrittenAnswer;
        container.appendChild(submitButton);

        answerArea.appendChild(container);
    }
    
    answeredQuestions++;
    document.getElementById('current-question').textContent = answeredQuestions;
    
    document.getElementById('nextButton').disabled = !isAnswered;
}

function selectOption(element) {
    if (isAnswered) return;

    document.querySelectorAll('.option').forEach(option => {
        option.classList.remove('selected', 'correct', 'incorrect');
    });
    element.classList.add('selected');

    const selectedIndex = parseInt(element.getAttribute('data-index'));
    
    isAnswered = true;

    const correctAnswerIndex = currentQuestion.correctAnswerIndex;
    const options = document.querySelectorAll('.option');

    if (selectedIndex === correctAnswerIndex){
        element.classList.add('correct');
    } else{
        element.classList.add('incorrect');
        options[correctAnswerIndex].classList.add('correct');
        
        IAQs.push(currentQuestion);
    }
    
    updateNextButton();

    if(document.getElementById('explanationArea').textContent.trim() !== '') {
        document.getElementById('explanationArea').style.display = 'block';
    }
}

function submitWrittenAnswer(){
    if (isAnswered) return;

    const textarea = document.getElementById('writtenAnswerInput');
    const userAnswer = textarea.value.trim();
    
    const correctAnswer = currentQuestion.correctWrittenAnswer;
    
    textarea.classList.add('submitted');

    if(userAnswer.localeCompare(correctAnswer, undefined, {sensitivity: 'accent'}) === 0){
        textarea.classList.add('correct');
        textarea.classList.remove('incorrect');
    } else {
        textarea.classList.add('incorrect');
        textarea.classList.remove('correct');
        
        IAQs.push(currentQuestion);
    }
    
    textarea.disabled = true;
    isAnswered = true;
    updateNextButton();

    if(document.getElementById('explanationArea').textContent.trim() !== '') {
        document.getElementById('explanationArea').style.display = 'block';
    }
}

function updateNextButton() {
    const nextButton = document.getElementById('nextButton');
    if (questions.length === 0 && IAQs.length === 0) {
        nextButton.textContent = 'Finish Quiz';
        nextButton.disabled = false;
    } else {
        nextButton.textContent = 'Next';
        nextButton.disabled = false;
    }
}

function nextQuestion() {
    if (isAnswered) {
        showNextQuestion();
    }
}

function showQuizResults() {
    document.getElementById('resultModal').style.display = 'block';

    const saveButton = document.getElementById('saveQuizButton');
    const goHomeButton = document.getElementById('goHomeButton');

    if (isRetakeMode) {
        if (saveButton) {
            saveButton.style.display = 'none';
        }
        if (goHomeButton) {
            goHomeButton.textContent = 'Back to My Quizzes';
            goHomeButton.onclick = goToMyQuizzes;
        }
    } else {
        if (saveButton) {
            saveButton.style.display = 'inline-block'; 
        }
        if (goHomeButton) {
            goHomeButton.textContent = 'Go Home';
            goHomeButton.onclick = goHome;
        }
    }
}

function saveQuiz() {
    const title = prompt('Enter a title for this quiz:', quizTitle);
    if (!title) return;
    
    const description = prompt('Enter a description (optional):', '') || '';
    

    const originalQuestions = JSON.parse(localStorage.getItem('questions'));
    
    const quizData = {
        title: title,
        description: description,
        questions: originalQuestions
    };
    
    fetch('/api/saved-quiz/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(quizData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to save quiz');
        }
        return response.json();
    })
    .then(result => {
        currentQuizId = result.id;
        localStorage.setItem('currentQuizId', currentQuizId);
    })
    .catch(error => {
        alert('Error saving quiz: ' + error.message);
    });
}

function retakeQuiz() {
    const originalQuestions = JSON.parse(localStorage.getItem('questions'));
    questions = [...originalQuestions];
    IAQs = [];
    answeredQuestions = 0;
    currentQuestion = null;
    isAnswered = false;
    
    document.getElementById('resultModal').style.display = 'none';
    
    const nextButton = document.getElementById('nextButton');
    nextButton.textContent = 'Next';
    nextButton.disabled = true;
    
    showNextQuestion();
}

function goHome() {
    window.location.href = '/';
}

function goToMyQuizzes() { 
    window.location.href = '/my-quizzes';
}

function closeResultModal() {
    document.getElementById('resultModal').style.display = 'none';
    if (isRetakeMode) {
        goToMyQuizzes();
    } else {
        goHome();
    }
}

function quit() {
    const confirmMessage = 'Are you sure you want to quit the quiz? Your progress will be lost.';
    if (confirm(confirmMessage)) {
        if (isRetakeMode) {
            window.location.href = '/my-quizzes';
        } else {
            window.location.href = '/';
        }
    }
}