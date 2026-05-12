package com.hwinterton.gyminventory.ui;

import com.hwinterton.gyminventory.domain.AuditLog;
import com.hwinterton.gyminventory.service.AuditService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Controls the action log viewer screen
 * 
 * <p>This controller loads recent audit log records from SQLite and displays them
 * in a JavaFX TableView</p>
 * 
 * <p>Uses {@link Task} so database loading runs in the background instead of 
 * blocking the JavaFX TableView</p>
 */
public class ActionLogController {
	private static final int LOG_LIMIT = 200; // max rows to load
	
	@FXML private TableView<AuditLog> actionLogTable; 			// audit log table
	@FXML private TableColumn<AuditLog, Long> colId; 			// auditAuditLogumn
	@FXML private TableColumn<AuditLog, String> colCreatedAt; 	// timestamp column
	@FXML private TableColumn<AuditLog, String> colUsername; 	// username column
	@FXML private TableColumn<AuditLog, String> colAction; 		// action column
	@FXML private TableColumn<AuditLog, String> colDetails; 	// details column
	
	@FXML private Label messageLabel;	// status and error message
	
	private final AuditService auditService = new AuditService(); // audit log business logic 
	private final ObservableList<AuditLog> logRows = FXCollections.observableArrayList(); // table tacking list
	
	/**
	 * Initializes action log screen after FXML loads
	 * 
	 * <p>Connects TableView columns to AuditLog values and then starts the first
	 * background load of audit log records.</p>
	 */
	@FXML
	private void initialize() {
		hideMessage();
		
		actionLogTable.setItems(logRows);
		
		// table set up - connect table columns to AuditLog getter values
		colId.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
		colCreatedAt.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCreatedAt()));
		colUsername.setCellValueFactory(cell -> new SimpleStringProperty(displayUsername(cell.getValue())));
		colAction.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAction()));
		colDetails.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDetails()));
		
		actionLogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		loadActionLogs();
	}
	
	/**
	 * Loads audit log records on a background thread
	 * 
	 * <p>Contains JavaFX concurrency example for the action log viewer. The database query 
	 * runs inside a task so the UI stays responsive </p>
	 */
	private void loadActionLogs() {
		hideMessage();
		showMessage("Loading action logs...", "message-success");
		
		Task<List<AuditLog>> task = new Task<>() {
			
			@Override
			protected List<AuditLog> call() throws Exception {			
				
				// background work - query SQLite away from UI thread
				return auditService.findRecent(LOG_LIMIT);
			}
		};
		
		task.setOnSucceeded(event -> {
			
			// success handler - update TableView after background work finishes
			logRows.setAll(task.getValue());
			showMessage("Loaded " + logRows.size() + " action log records.", "message-success");
		});
		
		task.setOnFailed(event -> {
			
			// failure handler - show error if the background database load fails
			Throwable error = task.getException();
			String message = error == null ? "Failed to load action logs." : error.getMessage();
			showMessage(message, "message-error");
		});
		
		// start background thread
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Build user display text for audit log row
	 * 
	 * <p>Log viewer prefers username, falls back to user id and shows System when
	 * the action was not tied to a user</p>
	 * 
	 * @param log the audit log row being displayed
	 * @return readable user text from the table
	 */
	private String displayUsername(AuditLog log) {
		if (log.getUsername() != null && !log.getUsername().isBlank()) {
			return log.getUsername();
		}
		
		if (log.getActorUserId() != null) {
			return "User ID " + log.getActorUserId();
		}
		return "System";
	}
	
	// FXML event - fresh button
	@FXML
	private void onRefresh() {
		loadActionLogs();
	}
	
	// FXML event - back button
	@FXML
	private void onBack() {
		Router.showMain();
	}
	
	// UI helper - show status or error message
	private void showMessage(String text, String styleClass) {
		messageLabel.getStyleClass().removeAll("message-success", "message-error");
		messageLabel.getStyleClass().add(styleClass);
		messageLabel.setText(text);
		messageLabel.setVisible(true);
		messageLabel.setManaged(true);
	}
	
	// UI helper - clear message label
	private void hideMessage() {
		messageLabel.getStyleClass().removeAll("message-success", "message-error");
		messageLabel.setText("");
		messageLabel.setVisible(false);
		messageLabel.setManaged(false);
	}
}



































