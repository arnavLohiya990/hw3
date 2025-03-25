package application;

public class Question {
    private String ownerName;
    private int ownerUserId;
    private String title;
    private String body;
    private int questionId;

   
    // Constructor with parameters to initialize the fields
    public Question(int ownerUserId, String title, String body) {
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.body = body;
    }
    
 // Constructor with parameters to initialize the fields
    public Question(String ownerName, int ownerUserId,String title,String body ,int questionId) {
        this.ownerName = ownerName;
        this.ownerUserId = ownerUserId;
        this.title = title;
        this.questionId = questionId;
        this.body = body;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
    
    // Getter and setter for ownerName
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    // Getter and setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and setter for title
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    // Getter and setter for questionId
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "Question ID: " + questionId + ", Title: " + title + ", Owner: " + ownerName;
    }
}
