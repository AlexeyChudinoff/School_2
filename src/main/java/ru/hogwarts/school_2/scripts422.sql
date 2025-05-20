//В этом задании по описанию необходимо спроектировать таблицы, связи между ними и корректно определить типы данных. Здесь не важно, какой тип вы выберете, например, для данных, представленных в виде строки (varchar или text). Важно, что вы выберете один из строковых типов, а не числовых, например.
//  Описание структуры: у каждого человека есть машина. Причем несколько человек могут пользоваться одной машиной. У каждого человека есть имя, возраст и признак того, что у него есть права (или их нет). У каждой машины есть марка, модель и стоимость. Также не забудьте добавить таблицам первичные ключи и связать их.

CREATE TABLE car (
  car_id INT PRIMARY KEY AUTO_INCREMENT,
  brand VARCHAR(50) NOT NULL,
  model VARCHAR(50) NOT NULL,
  price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE person (
  person_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  age INT NOT NULL,
  has_license BOOLEAN NOT NULL,
  car_id INT,
  CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES car(car_id)
);
