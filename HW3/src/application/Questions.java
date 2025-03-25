package application;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// Questions class - Manages multiple questions
public class Questions {
    private List<Question> questionList = new ArrayList<>();

    public void addQuestion(String title, String body, int userId) {
    	questionList.add(new Question(userId, title, body));
    }

    public Question getQuestion(int id) {
        return questionList.stream().filter(q -> q.getOwnerUserId() == id).findFirst().orElse(null);
    }

    public void updateQuestion(int id, String newText) {
        Question q = getQuestion(id);
        if (q != null) {
            q.setTitle(newText);
        } else {
            throw new NoSuchElementException("Question not found.");
        }
    }

    public void deleteQuestion(int id) {
        questionList.removeIf(q -> q.getOwnerUserId() == id);
    }

    public List<Question> getAllQuestions() {
        return questionList;
    }
}
