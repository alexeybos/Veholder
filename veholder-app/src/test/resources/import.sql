-- Аналог CREATE EXTENSION postgis для H2
CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";
CALL H2GIS_SPATIAL();
insert into users (dtype, id, password, username, full_name) values('Manager', 10, '$2a$12$AK3OTQ2K1bAbAsWtUo3G3eSIuI5jxJGsmhkPz6YhQgYxseT1sKCna', 'man1', 'Test Manager');
INSERT INTO enterprises (id, name, city, director_name) VALUES (5, 'Test1', 'Москва', 'Иван Иванов');
INSERT INTO enterprises (id, name, city, director_name) VALUES (7, 'Из Импорта', 'Samara', 'Петр Петров');
insert into enterprises_managers(enterprises_id, managers_id) values (5, 10);
insert into enterprises_managers(enterprises_id, managers_id) values (7, 10);

insert into brand(id, load_capacity, name, number_of_seats, tank, type) values(1, 1000, 'car1', 5, 100, 'грузовая');
insert into brand(id, load_capacity, name, number_of_seats, tank, type) values(2, 1000, 'car2', 5, 100, 'грузовая');

insert into vehicle (id, color, in_order, mileage, price, registration_number, year_of_production, brand_id, enterprise_id) VALUES (10, 'green', true, 100, 200, 'P100AA100', 2022, 1, 7);