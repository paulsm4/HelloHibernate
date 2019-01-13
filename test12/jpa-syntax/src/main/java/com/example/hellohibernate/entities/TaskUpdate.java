package com.example.hellohibernate.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;  // Use Lombok to auto-generate setting/getter methods

@Entity
@Table(name = "task_update")
@Data
public class TaskUpdate {

   public TaskUpdate() {}
   
   public TaskUpdate (int taskId, String updateText) {
	   this.taskId = taskId;
	   this.text = updateText;
   }
   
   @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private int id;

   @Column(name = "taskid")
   private int taskId;

   @Column(name = "text")
   private String text;

   @Column(name = "date")
   private Date date;
   
}
