#Select database

use malarm;

#Users

insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Juan", "Rodriguez", "jr@malarm.com", "20/10/1990", 0, 65, "12345678J", "secret", "salt", 0);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Manuel", "Martinez", "manuma02@ucm.es", "07/05/1992", 0, 80, "11868634J", "secret", "salt", 1);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Diego", "Diaz", "diediaz@malarm.com", "18/10/1993", 0, 60, "12345678D", "secret", "salt", 1);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Pablo", "Panero", "pepanero@malarm.com", "19/02/1990", 0, 70, "12345678P", "secret", "salt", 2);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Juan Pedro", "Guitierrez", "jprodriguez@malarm.com", "15/10/1998", 0, 60, "12345678Z", "secret", "salt", 2);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Silvia", "Costa", "silcosta@malarm.com", "11/05/2987", 1, 55, "12345678S", "secret", "salt", 3);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Sara", "Flores", "saflores@malarm.com", "07/09/1994", 1, 47, "12345678F", "secret", "salt", 4);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Paz", "Sánchez García", "pazsanga@gmail.com", "15/02/1962", 1, 47, "50305550L", "secret", "salt", 0);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Manuel", "Martínez Zurimendi", "manuelzuri.martinez@ono.es", "02/10/1960", 0, 47, "50301055D", "secret", "salt", 0);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Victoria", "García Contreras", "prueba@malarm.com", "24/01/1933", 1, 47, "02162236Y", "secret", "salt", 0);
insert into usuario (nombre, apellidos, email, fechaNacimiento, sexo, peso, DNI, secret, salt, estado) values ("Juan Domingo", "Sánchez Lorenzo", "prueba@malarm.com", "12/11/1934", 0, 47, "00625351G", "secret", "salt", 0);

#Disease

insert into enfermedad (nombre, erradicada, numMuertes, numNinyos, numJovenes, numAdultos, numAncianos, numMujeres, numHombres, peso) values ('Ebola', false, 10, 4, 10, 15, 2, 16, 15, 60);
insert into enfermedad (nombre, erradicada, numMuertes, numNinyos, numJovenes, numAdultos, numAncianos, numMujeres, numHombres, peso) values ('Zika', false, 20, 9, 10, 20, 7, 14, 23, 70);
insert into enfermedad (nombre, erradicada, numMuertes, numNinyos, numJovenes, numAdultos, numAncianos, numMujeres, numHombres, peso)  values ('Dengue', false, 15, 6, 13, 15, 8, 26, 16, 55);
insert into enfermedad (nombre, erradicada, numMuertes, numNinyos, numJovenes, numAdultos, numAncianos, numMujeres, numHombres, peso)  values ('Tifus', false, 12, 12, 40, 90, 27, 82, 87, 54);
insert into enfermedad (nombre, erradicada, numMuertes, numNinyos, numJovenes, numAdultos, numAncianos, numMujeres, numHombres, peso)  values ('Malaria', true, 7, 80, 10, 45, 11, 64, 79, 67);
insert into enfermedad (nombre, erradicada, numMuertes, numNinyos, numJovenes, numAdultos, numAncianos, numMujeres, numHombres, peso)  values ('Flew', true, 0, 4, 10, 12, 1, 10, 17, 58);

#Doctor

insert into medico (nombre, apellidos, hospital, departamento, secret, salt) values ("Juan", "Garcia", "12 de Octubre", "Infecciosas", "123", "1234");

#Focus

insert into foco (descripcion, numPersonas, idMedico) values ("lorem impsum", 100, 1);
insert into foco (descripcion, numPersonas, idMedico) values ("lorem impsum", 90, 1);
insert into foco (descripcion, numPersonas, idMedico) values ("lorem impsum", 2, 1);
insert into foco (descripcion, numPersonas, idMedico) values ("lorem impsum", 10, 1);

#Focus' places

insert into lugaresFoco (idFoco, lugar, fecha) values (1, "Barcelona", "28/02/2016");
insert into lugaresFoco (idFoco, lugar, fecha) values (2, "Madrid", "28/02/2016");
insert into lugaresFoco (idFoco, lugar, fecha) values (2, "Cartagena", "28/02/2016");
insert into lugaresFoco (idFoco, lugar, fecha) values (1, "Vigo", "28/02/2016");
insert into lugaresFoco (idFoco, lugar, fecha) values (1, "Malaga", "28/02/2016");
insert into lugaresFoco (idFoco, lugar, fecha) values (3, "Barcelona", "28/02/2016");

#Contagions

insert into contagio (idEnfermedad, idMedico, tiempo, distancia, fecha, nivel, descripcion, zona) values (1, 1, 10, 2, "10/02/2016", 3, "lorem impsum", "Barcelona");
insert into contagio (idEnfermedad, idMedico, tiempo, distancia, fecha, nivel, descripcion, zona) values (1, 1, 20, 2, "07/04/2016", 2, "lorem impsum", "Madrid");
insert into contagio (idEnfermedad, idMedico, tiempo, distancia, fecha, nivel, descripcion, zona) values (1, 1, 2, 2, "20/03/2016", 1, "lorem impsum", "Cartagena");
insert into contagio (idEnfermedad, idMedico, tiempo, distancia, fecha, nivel, descripcion, zona) values (2, 1, 40, 10, "11/01/2016", 2, "lorem impsum", "Madrid");
insert into contagio (idEnfermedad, idMedico, tiempo, distancia, fecha, nivel, descripcion, zona) values (2, 1, 3, 10, "22/02/2016", 3, "lorem impsum", "Barcelona");
insert into contagio (idEnfermedad, idMedico, tiempo, distancia, fecha, nivel, descripcion, zona) values (3, 1, 5, 3, "13/01/2016", 0, "lorem impsum", "Cartagena");
insert into contagio (idEnfermedad, idMedico, tiempo, distancia, fecha, nivel, descripcion, zona) values (3, 1, 1, 3, "24/04/2016", 0, "lorem impsum", "Barcelona");

#Infected Users

insert into usuarioContagiado (idUsuario, idContagio) values (2,1);
insert into usuarioContagiado (idUsuario, idContagio) values (9,1);
insert into usuarioContagiado (idUsuario, idContagio) values (3,1);
insert into usuarioContagiado (idUsuario, idContagio) values (4,1);
insert into usuarioContagiado (idUsuario, idContagio) values (10,2);
insert into usuarioContagiado (idUsuario, idContagio) values (4,2);
insert into usuarioContagiado (idUsuario, idContagio) values (6,3);
insert into usuarioContagiado (idUsuario, idContagio) values (7,3);
insert into usuarioContagiado (idUsuario, idContagio) values (2,4);
insert into usuarioContagiado (idUsuario, idContagio) values (3,4);
insert into usuarioContagiado (idUsuario, idContagio) values (4,4);
insert into usuarioContagiado (idUsuario, idContagio) values (7,5);
insert into usuarioContagiado (idUsuario, idContagio) values (11,6);
insert into usuarioContagiado (idUsuario, idContagio) values (2,7);

