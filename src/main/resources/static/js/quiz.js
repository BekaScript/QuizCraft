let questions = [];
let IAQs = []; //Incorrectly Answered Questions
let totalQuestions = 0;
let answeredQuestions = 0;
let currentQuestion = null;
let isAnswered = false;

document.addEventListener('DOMContentLoaded', initializeQuiz);

function initializeQuiz() {
    const questionsJson = localStorage.getItem('quizQuestions');
    if (!questionsJson) {
        alert('No quiz questions found. Please return to the home page and create a quiz.');
        window.location.href = '/';
        return;
    }
    
    questions = JSON.parse(questionsJson);
    totalQuestions = questions.length;
    document.getElementById('total-questions').textContent = totalQuestions;
    
    showNextQuestion();
}

function showNextQuestion() {
    // If we're out of questions in the main queue, check incorrect questions
    if (questions.length === 0) {
        if (IAQs.length === 0) {
            return;
        }
        
        currentQuestion = IAQs.shift();
    } else {
        // Get next question from main queue
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
        
        // Save incorrect question for review
        IAQs.push(currentQuestion);
    }
    document.getElementById('nextButton').disabled = false || (questions.length === 0 && IAQs.length ===0);

    if(document.getElementById('explanationArea').textContent.trim() !== '') {
        document.getElementById('explanationArea').style.display = 'block';
    }
}

function submitWrittenAnswer(){
    if (isAnswered) return;

    const textarea = document.getElementById('writtenAnswerInput');
    const userAnswer = textarea.value.trim();
    
    const correctAnswer = currentQuestion.correctWrittenAnswer;
    
    if(userAnswer.localeCompare(correctAnswer, undefined, {sensitivity: 'accent'}) === 0){
        textarea.style.borderColor = '#28a745'; //green
    } else {
        textarea.style.borderColor = '#dc3646'; //red
        
        // Save incorrect question for review
        IAQs.push(currentQuestion);
    }
    
    textarea.disabled = true;
    isAnswered = true;
    document.getElementById('nextButton').disabled = false || (questions.length === 0 && IAQs.length ===0);

    if(document.getElementById('explanationArea').textContent.trim() !== '') {
        document.getElementById('explanationArea').style.display = 'block';
    }
}

function nextQuestion() {
    if (isAnswered) {
        showNextQuestion();
    }
}

function quit() {
    window.location.href = '/';
}