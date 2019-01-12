package com.example.hellohibernate.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;  // Use Lombok to auto-generate setting/getter methods

@Entity
@Table(name = "task")
@Data
public class Task {

	public static enum Priority {
		LOW("LOW"),
		NORMAL("NORMAL"),
		HIGH("HIGH");

		private String text;
		
		Priority(final String text) {
			this.text = text;
		}
		
		@Override
		public String toString() { return text; }
	}
	
	public static enum Status {
		PENDING("PENDING"),
		INPROGRESS("INPROGRESS"),
		COMPLETED("COMPLETED"),
		BLOCKED("BLOCKED"),
		REOPENED("REOPENED");

		private String text;
		
		Status(final String text) {
			this.text = text;
		}
		
		@Override
		public String toString() { return text; }
	}
	
	public Task () {}
	
	public Task (String summary, Priority priority, Status status) {
		this.summary = summary;
		this.priority = priority.toString();
		this.status = status.toString();
	}
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "summary")
	private String summary;

	@Column(name = "priority")
	private String priority;

	@Column(name = "status")
	private String status;

	@Column(name = "date")
	private Date date;
	
	// Note: we always want to fetch all updates whenever we fetch a task
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name="taskid")
	private List<TaskUpdate> updates = new ArrayList<>();

}
