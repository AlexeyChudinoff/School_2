Составить первый JOIN-запрос, чтобы получить информацию обо всех студентах
(достаточно получить только имя и возраст студента) школы Хогвартс вместе с
 названиями факультетов.

SELECT s.name, s.age, f.name AS faculty_name
FROM students s
JOIN faculty f ON s.faculty_id = f.id;



Составить второй JOIN-запрос, чтобы получить только тех студентов, у которых
 есть аватарки.

 SELECT s.name, s.age, f.name AS faculty_name
 FROM students s
 JOIN faculty f ON s.faculty_id = f.id
 JOIN avatar a ON a.student_id = s.id;
