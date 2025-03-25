package application;

import java.util.List;

/**
 * <p>Title: HW3 Test Automation (without JUnit)</p>
 * 
 * <p>Description: This file contains a set of Java test automations for Questions and Answers classes based on the Team Project 2 submission.</p>
 * 
 * @author Arnav Lohiya
 */
public class HW3TestAutomation {
    static int numPassed = 0;
    static int numFailed = 0;
    
    /**
     * Main method to execute all automated test cases.
     * Displays results of each test and summarizes the pass/fail count.
     */
    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nHW3 Automated Testing\n");
        
        testAddAndGetQuestion();
//        testUpdateQuestion();
        testUpdateNonExistentQuestion();
//        testDeleteQuestion();
//        testGetAllQuestions();
        testAddAndRetrieveAnswer();
//        testDeleteAnswer();
        testMultipleAnswersForSameQuestion();
        testRetrieveEmptyAnswers();
        
        System.out.println("____________________________________________________________________________");
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }
    
    /**
     * Tests adding and retrieving a question.
     */
    public static void testAddAndGetQuestion() {
        Questions questions = new Questions();
        questions.addQuestion("Demo Question?", "Demo Answer.", 1);
        Question q = questions.getQuestion(1);
        
        evaluateTest("testAddAndGetQuestion", q != null && q.getTitle().equals("Demo Question?") && q.getBody().equals("Demo Answer."));
    }
    
    
    
    /**
     * Tests updating a non-existent question and expects an exception.
     */
    public static void testUpdateNonExistentQuestion() {
        Questions questions = new Questions();
        boolean exceptionThrown = false;
        
        try {
            questions.updateQuestion(99, "New Title");
        } catch (Exception e) {
            exceptionThrown = e.getMessage().equals("Question not found.");
        }
        
        evaluateTest("testUpdateNonExistentQuestion", exceptionThrown);
    }
    
    /**
     * Tests adding and retrieving an answer.
     */
    public static void testAddAndRetrieveAnswer() {
        Answers answers = new Answers();
        answers.addAnswer(20, "Answer for question 20", "Demo User");
        List<Answer> answerList = answers.getAnswersForQuestion(20);
        
        evaluateTest("testAddAndRetrieveAnswer", answerList.size() == 1 && answerList.get(0).getText().equals("Answer for question 20"));
    }
    
    
    
    /**
     * Tests adding multiple answers to the same question.
     */
    public static void testMultipleAnswersForSameQuestion() {
        Answers answers = new Answers();
        answers.addAnswer(40, "First answer for question 40", "Bob");
        answers.addAnswer(40, "Second answer for question 40", "John");
        
        evaluateTest("testMultipleAnswersForSameQuestion", answers.getAnswersForQuestion(40).size() == 2);
    }
    
    /**
     * Tests retrieving answers for a question with no answers.
     */
    public static void testRetrieveEmptyAnswers() {
        Answers answers = new Answers();
        
        evaluateTest("testRetrieveEmptyAnswers", answers.getAnswersForQuestion(50).isEmpty());
    }
    
    /**
     * Helper method to evaluate test results.
     * @param testName The name of the test case.
     * @param passed Whether the test passed.
     */
    public static void evaluateTest(String testName, boolean passed) {
        if (passed) {
            numPassed++;
            System.out.println("***Success*** " + testName + " passed.");
        } else {
            numFailed++;
            System.out.println("***Failure*** " + testName + " failed.");
        }
    }
}
