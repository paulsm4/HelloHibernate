drop table if exists task;

-- priority: LOW, NORMAL, HIGH
-- status: PENDING, INPROGRESS, COMPLETED, BLOCKED, REOPENED
create table task (
  id int NOT NULL auto_increment,
  summary varchar(20) NOT NULL,
  priority varchar(10) NOT NULL default 'NORMAL', 
  status varchar(10) NOT NULL default 'PENDING',
  date timestamp default current_timestamp,
  PRIMARY KEY(id)
);

drop table if exists task_update;

create table task_update (
  id int NOT NULL auto_increment,
  taskid int NOT NULL,
  text varchar(255) NULL,
  date timestamp default current_timestamp,
  PRIMARY KEY(id),
  FOREIGN KEY fk_task(taskid)
  REFERENCES task(id)
);
