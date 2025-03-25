package application;

import java.util.ArrayList;
import java.util.List;

// Answers class - Manages multiple answers
public class Answers {
    private List<Answer> answerList = new ArrayList<>();

    public void addAnswer(int questionId, String text, String author) {
        answerList.add(new Answer(questionId, text, author));
    }

    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> results = new ArrayList<>();
        for (Answer a : answerList) {
            if (a.getQuestionId() == questionId) {
                results.add(a);
            }
        }
        return results;
    }

    public void deleteAnswer(int id) {
        answerList.removeIf(a -> a.getId() == id);
    }
}
