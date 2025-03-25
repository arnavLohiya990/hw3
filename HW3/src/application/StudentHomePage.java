package application;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * This page displays a simple welcome message for the user.
 */
public class StudentHomePage {
    private final DatabaseHelper databaseHelper;

    private int questionToDisplayId = 0;
    private Pane questionDetailsContainer;
    private VBox listOfQuestion;  
    private User user;
    public StudentHomePage(User user, DatabaseHelper databaseHelper) {
        this.user = user;
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        //initializing class level Layouts
        questionDetailsContainer = new Pane();
        questionDetailsContainer.setStyle(
                "-fx-background-color: #ffffff;" + // Light gray background
                "-fx-padding: 20px 20px 20px 20px;" + // Add padding inside the container
                "-fx-border-color: #ccc;" + // Light gray border
                "-fx-border-radius: 10px;" + // Rounded corners
                "-fx-border-width: 2px;"
            );
        listOfQuestion = new VBox(10);
        updateQuestionList(); // Call this method to fetch and display latest questions
        
        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display Hello user
        Label userLabel = new Label("*** Hello, " + user.getUserFullName() + " ***");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        //Button to add new questions
        Button addQuestionBtn = new Button("+ Ask New Question");
        addQuestionBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Create a new stage (popup)
                Stage popupStage = new Stage();
                popupStage.setTitle("Ask a New Question");

                // Create layout for the popup
                VBox popupLayout = new VBox(10);
                popupLayout.setPadding(new Insets(15));
                popupLayout.setAlignment(Pos.CENTER);

                // Title input field
                Label titleLabel = new Label("Title (10-40 chars):");
                TextField titleField = new TextField();
                titleField.setPromptText("Enter your question title");
                titleField.setMaxWidth(300);

                // Title error label (initially hidden)
                Label titleErrorLabel = new Label();
                titleErrorLabel.setStyle("-fx-text-fill: red;");
                titleErrorLabel.setVisible(false);

                // Body input field
                Label bodyLabel = new Label("Body (20-255 chars):");
                TextArea bodyField = new TextArea();
                bodyField.setPromptText("Enter details of your question");
                bodyField.setWrapText(true);
                bodyField.setMaxWidth(300);
                bodyField.setPrefRowCount(5);

                // Body error label (initially hidden)
                Label bodyErrorLabel = new Label();
                bodyErrorLabel.setStyle("-fx-text-fill: red;");
                bodyErrorLabel.setVisible(false);

                // Submit button
                Button submitButton = new Button("Submit");

                // Validation logic for submit button
                submitButton.setOnAction(e -> {
                    boolean valid = true;

                    String titleText = titleField.getText().trim();
                    String bodyText = bodyField.getText().trim();

                    // Title validation
                    if (titleText.length() < 10 || titleText.length() > 40) {
                        titleErrorLabel.setText("Title must be between 10 and 40 characters.");
                        titleErrorLabel.setVisible(true);
                        valid = false;
                    } else {
                        titleErrorLabel.setVisible(false);
                    }

                    // Body validation
                    if (bodyText.length() < 20 || bodyText.length() > 255) {
                        bodyErrorLabel.setText("Body must be between 20 and 255 characters.");
                        bodyErrorLabel.setVisible(true);
                        valid = false;
                    } else {
                        bodyErrorLabel.setVisible(false);
                    }

                    // If all validations pass, close the popup (or process the submission)
                    if (valid) {
                        //insert this new post
                        boolean res = databaseHelper.postNewQuestion( user.getUserId(), titleField.getText().trim(), bodyField.getText().trim());
                        if(res) {
                             updateQuestionList();
                             popupStage.close();
                        } else {
                            System.out.print("DEBUG: Error 122, not able to post new question");
                        }
                    }
                });

                // Layout all elements
                popupLayout.getChildren().addAll(
                    titleLabel, titleField, titleErrorLabel, 
                    bodyLabel, bodyField, bodyErrorLabel, 
                    submitButton
                );

                // Create the popup scene
                Scene popupScene = new Scene(popupLayout, 350, 300);
                popupStage.setScene(popupScene);

                // Show the popup window
                popupStage.show();
            }
        });

        //Hbox to store the buttons (Log out, View Inbox, Ask New Question)
        HBox hbox = new HBox();
        Button backBtn = new Button("Log out");
        backBtn.setOnAction(event -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });
        // NEW: Button to view inbox (private feedback messages)
        Button inboxBtn = new Button("View My Private Messages");
        inboxBtn.setOnAction(event -> {
            Stage inboxStage = new Stage();
            inboxStage.setTitle("View Private Messages");
            VBox inboxLayout = new VBox(10);
            inboxLayout.setPadding(new Insets(20));
            Label inboxLabel = new Label("Private Messages:");
            ListView<String> inboxListView = new ListView<>();
            ArrayList<String> messages = databaseHelper.getPM(user.getUserId());
            if(messages.isEmpty()){
                inboxListView.getItems().add("No feedback messages.");
            } else {
                inboxListView.getItems().addAll(messages);
            }
            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(e -> inboxStage.close());
            inboxLayout.getChildren().addAll(inboxLabel, inboxListView, closeBtn);
            Scene inboxScene = new Scene(inboxLayout, 800, 400);
            inboxStage.setScene(inboxScene);
            inboxStage.show();
        });
        hbox.getChildren().addAll(backBtn, inboxBtn, addQuestionBtn);
        hbox.setStyle("-fx-alignment: top-right;");
        
        //Hbox to store the questionsList and the QuestionDetailsPane
        HBox rowPane = new HBox(20);
        listOfQuestion = new VBox(10);
        ArrayList<Question> questionsList = databaseHelper.fetchQuestionList();
        if(questionsList.isEmpty()) {
            listOfQuestion.getChildren().clear();
            Label errorLabel = new Label("No questions to display currently.");
            listOfQuestion.getChildren().add(errorLabel);
        } else {
            for (Question q : questionsList) {
                listOfQuestion.getChildren().add(getQuestionListItemLayout(q));
            }
        }
        
        Label placeholderLabel = new Label("Select a question to view details");
        placeholderLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;"); // Styled text
        questionDetailsContainer.getChildren().add(placeholderLabel);
        questionDetailsContainer.setPrefSize(600, 600);  // Width = 600, Height = 600
        rowPane.getChildren().addAll(listOfQuestion, questionDetailsContainer);
        layout.getChildren().addAll(userLabel, hbox, rowPane);
        Scene userScene = new Scene(layout, 1000, 800);

        // Set the scene to primary stage
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Student Page");
        
    }
    
    public VBox getQuestionListItemLayout(Question question) {
        VBox column = new VBox();
        column.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 10px;");
        column.setPadding(new Insets(20)); // 20px padding on all sides

        column.setOnMouseClicked(event -> {
            System.out.println("VBox clicked!");
            questionToDisplayId = question.getQuestionId();
            updateQuestionDetailPane();
        });
        Label titleLabel = new Label("❓" + question.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        Label ownerNameLabel = new Label(question.getOwnerName());
        column.getChildren().addAll(titleLabel, ownerNameLabel);
        return column;
    }

     //Arnav Lohiya TP2- 2025-02-26
    //returns the layout for a specific answer item.
    //if this answer is to a question posted by the current user, then allow them to mark it as resolving/not resolving.
    public VBox getAnswerListItemLayout(Answer answer, boolean isUsersQuestion ) {
    	VBox column = new VBox();
    	VBox.setMargin(column, new Insets(10));
        column.setStyle("-fx-border-color: blue; -fx-border-width: 1px; -fx-border-radius: 2px;");
        column.setPadding(new Insets(10)); // 20px padding on all sides
        Label titleLabel = new Label("Posted By: "+ answer.getAuthor());
        Label bodyLabel = new Label(answer.getText());
        String status = answer.getIsMarkedResolved()?"✅ Resolves the problem\n": "\n" ;
        Label answerStatusLabel = new Label(status);
        Label errorLabel = new Label("");
        
        column.getChildren().addAll(titleLabel, bodyLabel, answerStatusLabel);
        if(isUsersQuestion) {
        	System.out.print("isUsersQuestion is true\n");
        	//show a button that toggles the status of the answer from and to resolving and not resolving.
            String buttonMessage = answer.getIsMarkedResolved()?"Doens't Resolve The Problem?": "Resolves The problem?" ;

        	Button toggleStatusBtn = new Button(buttonMessage);
        	toggleStatusBtn.setOnAction(a->{
        		//toggle that value in the database and update the answers list.
        		boolean res = databaseHelper.updateAnswerStatus(answer.getId() ,!answer.getIsMarkedResolved());
        		if(res) {
        			errorLabel.setText("");
        			updateQuestionDetailPane();
        		}else {
        			errorLabel.setText("There was an error updating the answer status. Please try again.");
        		}
        	});
        	column.getChildren().add(toggleStatusBtn);
        }else {
        	System.out.print("isUsersQuestion is false\n");
        }
        
        return column;
    }
    
    public VBox getQuestionDetailsLayout(Question question) {     
        VBox column = new VBox();
        VBox.setMargin(column, new Insets(10)); // Applies margin on all sides

        Label ownerNameLabel = new Label("Posted by: " + question.getOwnerName());
        ownerNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
        Label titleLabel = new Label(question.getTitle());
        titleLabel.setStyle(" -fx-font-size: 24;");
        Label bodyLabel = new Label("Title: " + question.getBody());
        bodyLabel.setMaxWidth(550); // Set the maximum width for the label
        bodyLabel.setWrapText(true); // Enable word wrapping
        bodyLabel.setStyle(" -fx-font-size: 18;");
        column.getChildren().addAll(ownerNameLabel, titleLabel, bodyLabel);
        
        VBox buttonColumn = new VBox();
        // Existing buttons for owner (delete/edit)
        if(question.getOwnerUserId() == user.getUserId()) {
            Button deleteBtn = new Button("Delete post");
            deleteBtn.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Delete");
                alert.setHeaderText("Are you sure you want to delete this post?");
                alert.setContentText("This action cannot be undone.");

                // Get user's choice
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean success = databaseHelper.deleteQuestion(question.getQuestionId());
                    if (success) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Deleted");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Post deleted successfully!");
                        successAlert.showAndWait();
                        questionToDisplayId = 0;
                        updateQuestionDetailPane();
                        updateQuestionList();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Failed to delete post. Please try again.");
                        errorAlert.showAndWait();
                    }
                }
            });

            Button editBtn = new Button("Edit Post");
            editBtn.setOnAction(event -> {
                Stage popupStage = new Stage();
                popupStage.setTitle("Edit Post");

                VBox popupLayout = new VBox(10);
                popupLayout.setPadding(new Insets(20));

                Label titleEditLabel = new Label("Edit Title:");
                TextField titleField = new TextField(question.getTitle());

                Label bodyEditLabel = new Label("Edit Body:");
                TextArea bodyField = new TextArea(question.getBody());
                bodyField.setPrefRowCount(5);

                Label errorLabel = new Label();
                errorLabel.setStyle("-fx-text-fill: red;");

                Button updateBtn = new Button("Update");
                updateBtn.setOnAction(updateEvent -> {
                    String newTitle = titleField.getText().trim();
                    String newBody = bodyField.getText().trim();

                    // Perform validation like for creating a new post
                    if (newTitle.isEmpty() || newBody.isEmpty()) {
                        errorLabel.setText("Title and body cannot be empty.");
                        return;
                    }
                    if (newTitle.length() < 10 || newBody.length() < 20) {
                        errorLabel.setText("Title must 10-40 characters, and body from 20-255 characters.");
                        return;
                    }

                    // Update the question in the database (implement update logic)
                    boolean success = databaseHelper.updateQuestion(question.getQuestionId(), newTitle, newBody);
                    if (success) {
                        updateQuestionDetailPane();
                        updateQuestionList();
                        popupStage.close();
                        // Optionally, refresh the UI to reflect changes
                    } else {
                        errorLabel.setText("Failed to update. Try again.");
                    }
                });

                popupLayout.getChildren().addAll(titleEditLabel, titleField, bodyEditLabel, bodyField, errorLabel, updateBtn);

                Scene popupScene = new Scene(popupLayout, 400, 300);
                popupStage.setScene(popupScene);
                popupStage.showAndWait();
            });

            buttonColumn.getChildren().addAll(deleteBtn, editBtn);
        } else {
            //Arnav Lohiya TP2, 2025-02-25
        	//Creating the new button to post replies(potential answers) to questions
        	Button answerButton = new Button("Reply");
        	answerButton.setOnAction(a-> {
        		System.out.print("Answer button clicked");
        		Stage replyStage = new Stage();
        		replyStage.setTitle("Reply to this question!");
        		// Start of Credit to R.McQuesten for the textArea code, I picked it up from his implementation. 
        		TextArea replyTextField = new TextArea();
        		replyTextField.setPromptText("Type your feedback here...");
        		replyTextField.setWrapText(true);
        		Label errorLabel = new Label();
                errorLabel.setStyle("-fx-text-fill: red;");
                //end of credit
                
                Button postReplyButton = new Button("Post Reply");
                postReplyButton.setOnAction(b->{
                	String answerField = replyTextField.getText().trim();
                	if(answerField.length() < 10) {
                		errorLabel.setText("Answer Must be atleast 10 characters. Please try again.");
                	}else {
                		//this is a valid answer
                		//Post the potential answer to the answers table.
                		boolean res = databaseHelper.postPotentialAnswer(user.getUserId(), questionToDisplayId, answerField);
                		if(res) {
                			updateQuestionDetailPane();
                			//show success alert pop up
                			//Start of Credit to R.McQuestens implementation of alert pop up
                			Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("*** SUCCESS *** Your reply has been posted.");
                            alert.showAndWait();
                            replyStage.close();
                            //end of credit
                            
                		}else {
                			errorLabel.setText("Reply post has been unsuccessful. Please check your input and try again.");
                		}
                	}
                	
                });
                VBox replyPopUpLayout = new VBox();
                replyPopUpLayout.getChildren().addAll(replyTextField, errorLabel, postReplyButton);
            	Scene replyScene = new Scene(replyPopUpLayout, 400, 300);
                replyStage.setScene(replyScene);
                replyStage.show();
        	});
        	
        	buttonColumn.getChildren().add(answerButton);
            
            // R.McQuesten-TP2, 2025-02-21, Handle private message feedback
            // When viewing a question from another student, 
        	// Allow sending a private feedback message
            Button pmBtn = new Button("Send Private Message");
            pmBtn.setOnAction(a -> {
                // Set PM stage and title
                Stage pmStage = new Stage();
                pmStage.setTitle("Send Private Feedback");
                // Set layout and padding
                VBox feedbackLayout = new VBox(10);
                feedbackLayout.setPadding(new Insets(20));
                // Instantiate and set PM label
                Label pmLbl = new Label("Enter your private message:");
                // Instantiate textarea obj pmArea 
                TextArea pmArea = new TextArea();
                // Set promot text for PM
                pmArea.setPromptText("Type your feedback here...");
                // Set wrap to true so itll allow for lrg-ish PMs
                pmArea.setWrapText(true);
                // Instantiate and set errLbl style
                Label errorLabel = new Label();
                errorLabel.setStyle("-fx-text-fill: red;");
                // Instantiate and set btn text
                Button sendButton = new Button("Send");
                // Action handler for send btn
                sendButton.setOnAction(b -> {
                    String pMsg = pmArea.getText().trim();
                    if(pMsg.isEmpty()){
                        errorLabel.setText("*** ERROR *** Private message cannot be empty.");
                    } else {
                        // Pass the question id so the PM is associated with the question
                        boolean sent = databaseHelper.sendPM(user.getUserId(), question.getOwnerUserId(), question.getQuestionId(), pMsg);
                        if(sent){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Feedback Sent");
                            alert.setHeaderText(null);
                            alert.setContentText("*** SUCCESS *** Private message has been sent.");
                            alert.showAndWait();
                            pmStage.close();
                        } else {
                            errorLabel.setText("*** ERROR *** Failed to send private message. Please try again later.");
                        }
                    }
                });
                
                feedbackLayout.getChildren().addAll(pmLbl, pmArea, errorLabel, sendButton);
                Scene feedbackScene = new Scene(feedbackLayout, 400, 300);
                pmStage.setScene(feedbackScene);
                pmStage.show();
            });
            buttonColumn.getChildren().add(pmBtn);
        }
        column.getChildren().addAll(buttonColumn);
        return column;
    }
    
    private void updateQuestionDetailPane() {
        questionDetailsContainer.getChildren().clear(); // Clear the existing list
        if(questionToDisplayId <= 0){
            return;
        }
        Question question = databaseHelper.fetchQuestionDetails(questionToDisplayId); // Fetch updated questions
//        questionDetailsContainer.getChildren().add(getQuestionDetailsLayout(question));
        
        ArrayList<Answer> listOfAnswers = databaseHelper.fetchAnswers(questionToDisplayId);
        VBox column = new VBox();
        column.getChildren().add(getQuestionDetailsLayout(question));
        Label answerSubHeading = new Label("Here are the list of replies to this question:\n");
        column.getChildren().add(answerSubHeading);
        
        for(Answer answer: listOfAnswers) {
//        	System.out.println(answer.getQuestionOwnerId() == user.getUserId());
//        	System.out.println(answer.getQuestionOwnerId() );
//        	System.out.println(user.getUserName()+" "+ answer.getAuthor() );

        	column.getChildren().add(getAnswerListItemLayout(answer, question.getOwnerUserId() == user.getUserId()));
        }
        questionDetailsContainer.getChildren().add(column);
        
    }
    
    private void updateQuestionList() {
        listOfQuestion.getChildren().clear();     // Clear the existing list
        ArrayList<Question> questionsList = databaseHelper.fetchQuestionList(); // Fetch updated questions
        if (questionsList.isEmpty()) {
            Label errorLabel = new Label("No questions to display currently.");
            listOfQuestion.getChildren().add(errorLabel);
        } else {
            for (Question q : questionsList) {
                listOfQuestion.getChildren().add(getQuestionListItemLayout(q));
            }
        }
    }
    
}
