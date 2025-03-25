// Answer class
package application;
import java.util.Date;

public class Answer {
    private static int idCounter = 1;
    private int id;
    private int questionId;
    private String text;
    private String author;
    private Date createdAt;
    //Arnav Lohiya TP2, 2025-02-25
    //adding new isntance variables
    private boolean isMarkedResolved;
    private int questionOwnerId;

    public Answer(int questionId, String text, String author) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty.");
        }
        this.id = idCounter++;
        this.questionId = questionId;
        this.text = text;
        this.author = author;
        this.createdAt = new Date();
    }
    
    //Arnav Lohiya TP2, 2025-02-25
    //Creating OverLoaded Constructor
    public Answer(int answerId, int ownerUserId, int questionId, String body, String authorName, boolean isMarkedResolved) {
    	if(body == null || body.trim().isEmpty()) {
    		body = "NA";
    	}
    	this.text = body;
    	this.author = authorName;
    	this.isMarkedResolved = isMarkedResolved;
    	this.id = answerId;
    	this.questionOwnerId = ownerUserId;
    	this.questionId = questionId;
    }
    
    public int getId() { return id; }
    public int getQuestionId() { return questionId; }
    public String getText() { return text; }
    public String getAuthor() { return author; }
    public Date getCreatedAt() { return createdAt; }
    public boolean getIsMarkedResolved() {return isMarkedResolved;}
    public int getQuestionOwnerId() {return questionOwnerId;}    
}
