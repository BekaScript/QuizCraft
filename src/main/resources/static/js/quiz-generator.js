let currentQuizData = null;

// Initialize AI decide checkbox as checked by default
document.addEventListener('DOMContentLoaded', function() {
    const aiDecideCheckbox = document.getElementById('aiDecideNOQ');
    const NOQInput = document.getElementById('NOQ');
    
    // Set checkbox as checked by default
    aiDecideCheckbox.checked = true;
    
    // Set input field state accordingly
    NOQInput.disabled = true;
    NOQInput.value = '';
    NOQInput.placeholder = 'AI will decide automatically';
});

// Tab switching functionality
document.querySelectorAll('.quiz-tab').forEach(tab => {
    tab.addEventListener('click', function() {
        const targetTab = this.getAttribute('data-tab');
        
        // Update active tab
        document.querySelectorAll('.quiz-tab').forEach(t => t.classList.remove('active'));
        this.classList.add('active');
        
        // Update active content
        document.querySelectorAll('.quiz-tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.getElementById(targetTab + '-content').classList.add('active');
    });
});

// AI Decide checkbox functionality
document.getElementById('aiDecideNOQ').addEventListener('change', function() {
    const NOQInput = document.getElementById('NOQ');
    if (this.checked) {
        NOQInput.disabled = true;
        NOQInput.value = '';
        NOQInput.placeholder = 'AI will decide automatically';
    } else {
        NOQInput.disabled = false;
        NOQInput.value = '5';
        NOQInput.placeholder = 'Enter number of questions (1-20)';
    }
});

document.getElementById('topicForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    
    // Map form data to backend expected parameters
    const quizParams = {
        prompt: formData.get('topic'),
        questionsAmount: formData.get('numberOfQuestions'),
        quizType: formData.get('quizType')
    };
    
    await generateQuiz(quizParams);
});

document.getElementById('textForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const aiDecide = document.getElementById('aiDecideNOQ').checked;
    
    // Map form data to backend expected parameters
    const quizParams = {
        prompt: formData.get('text'),
        questionsAmount: aiDecide ? 'auto' : (formData.get('numberOfQuestions') || '5'),
        quizType: formData.get('quizType')
    };
    
    await generateQuiz(quizParams);
});

document.getElementById('convertForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    
    // Map form data to backend expected parameters
    const quizParams = {
        prompt: formData.get('quizText'),
        questionsAmount: 'auto',
        quizType: 'MIXED'
    };
    
    await generateQuiz(quizParams);
});

async function generateQuiz(quizParams) {
    // Show loading state
    document.getElementById('loading').style.display = 'block';
    document.getElementById('errorMessage').style.display = 'none';
    
    try {
        const response = await fetch('/api/generate-quiz', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(quizParams)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        // Store quiz data in localStorage for the existing quiz system
        localStorage.setItem('questions', JSON.stringify(data));
        localStorage.setItem('quizTitle', 'Generated Quiz');
        localStorage.removeItem('currentQuizId');
        
        // Redirect directly to the quiz-taking page
        window.location.href = '/quiz';
        
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to generate quiz. Please try again.');
        document.getElementById('loading').style.display = 'none';
    }
}

function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

// Generate New Quiz functionality
document.getElementById('generateNewBtn').addEventListener('click', function() {
    document.getElementById('topicForm').reset();
    document.getElementById('textForm').reset();
    document.getElementById('convertForm').reset();
    document.getElementById('errorMessage').style.display = 'none';
    currentQuizData = null;
    
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
});