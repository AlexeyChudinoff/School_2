// Возраст студента не может быть меньше 16 лет.
ALTER TABLE student
ADD CONSTRAINT chk_age CHECK (age > 16);


// Имена студентов должны быть уникальными и не равны нулю.(и не длиннее 50 символов)
ALTER TABLE student
ADD CONSTRAINT unique_notnull_name VARCHAR (50) UNIQUE NOT NULL; //UNIQUE NOT NULL == PRIMARY KEY

//Пара “значение названия” - “цвет факультета” должна быть уникальной.
ALTER TABLE faculty
ADD CONSTRAINT unique_name_color UNIQUE (name, color);


// При создании студента без возраста ему автоматически должно присваиваться 20 лет.
ALTER TABLE student
ADD CONSTRAINT default_student_age DEFAULT 20 FOR age;


