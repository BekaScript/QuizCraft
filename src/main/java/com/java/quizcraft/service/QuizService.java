package com.java.quizcraft.service;

import com.java.quizcraft.model.Question;
import org.springframework.stereotype.Service;
import com.java.quizcraft.model.QuestionType;

import java.util.List;
import java.util.ArrayList;

@Service
public class QuizService {

    private List<Question> quiz = new ArrayList<>();
    private int currentQuestionIndex;

    private List<Integer> incorrectQuestionIndices = new ArrayList<>();
    private boolean learnMode;

    public void processQuiz(List<Question> questions, boolean isLearnMode) {
        quiz = questions;
        currentQuestionIndex = 0;
        learnMode = isLearnMode;
        incorrectQuestionIndices.clear();
    }

    public Integer calculateCorrectAnswersCount(List<Object> userAnswers){
        int correctAnswersCount = 0;
        List<Question> questions = getAllQuestions();

        for(int i=0; i<questions.size() && i<userAnswers.size(); i++){
            Question q = questions.get(i);
            Object userAnswer = userAnswers.get(i);

            if(q.getQuestionType() == QuestionType.MULTIPLE_CHOICE){
                Integer userAnswerIndex = null;
                if (userAnswer instanceof Integer){
                    userAnswerIndex = (Integer) userAnswer;
                } else if(userAnswer instanceof Number){
                    userAnswerIndex = ((Number) userAnswer).intValue();
                }
                if(userAnswerIndex != null && userAnswerIndex.equals(q.getCorrectAnswerIndex())){
                    correctAnswersCount++;
                }
            } else if(q.getQuestionType() == QuestionType.WRITTEN_ANSWER){
                String userText = userAnswer == null? "" : userAnswer.toString().trim();
                String correctText = q.getCorrectWrittenAnswer() == null? "": q.getCorrectWrittenAnswer().trim();
                if(userText.equalsIgnoreCase(correctText)) correctAnswersCount ++;
            }
        }
        return correctAnswersCount;
    }
    
    public Question getCurrentQuestion() {
        return quiz.get(currentQuestionIndex);
    }

    public Question getNextQuestion(){
        if (currentQuestionIndex >= quiz.size()-1 && 
            !(learnMode && !incorrectQuestionIndices.isEmpty())) {
            return getCurrentQuestion(); 
        }
        
        if (learnMode && !incorrectQuestionIndices.isEmpty() && currentQuestionIndex > quiz.size() - 2) {
            currentQuestionIndex = incorrectQuestionIndices.remove(0);
        }
        else if (currentQuestionIndex < quiz.size()-1) {
            currentQuestionIndex++;
        }
        
        return getCurrentQuestion();
    }

    public void markCurrentQuestionIncorrect(){
        if (learnMode && !incorrectQuestionIndices.contains(currentQuestionIndex)){
            incorrectQuestionIndices.add(currentQuestionIndex);
        }
    }

    public boolean isLastQuestion(){
        return learnMode?
            currentQuestionIndex > quiz.size() - 2 && incorrectQuestionIndices.isEmpty()
            :
            currentQuestionIndex >= quiz.size() - 1;
    }

    public Question getPreviousQuestion(){
        if (currentQuestionIndex>0) currentQuestionIndex--;
        return getCurrentQuestion();
    }

    public int getQuestionsAmount(){
        return quiz.size();
    }

    public int getCurrentQuestionIndex(){
        return currentQuestionIndex;
    }

    public List<Question> getAllQuestions(){
        return quiz;
    }
}
