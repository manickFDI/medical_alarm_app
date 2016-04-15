#Users

insert into usuario values (1, "Juan", "Rodriguez", "jr@malarm.com", "20/10/1990", 0, 65, "12345678J", "secret", "salt", 0);
insert into usuario values (2, "Manuel", "Martinez", "manuma@malarm.com", "08/08/1985", 0, 80, "12345678M", "secret", "salt", 1);
insert into usuario values (3, "Diego", "Diaz", "diediaz@malarm.com", "18/10/1993", 0, 60, "12345678D", "secret", "salt", 1);
insert into usuario values (4, "Pablo", "Panero", "pepanero@malarm.com", "19/02/1990", 0, 70, "12345678P", "secret", "salt", 2);
insert into usuario values (5, "Juan Pedro", "Guitierrez", "jprodriguez@malarm.com", "15/10/1998", 0, 60, "12345678Z", "secret", "salt", 2);
insert into usuario values (6, "Silvia", "Costa", "silcosta@malarm.com", "11/05/2987", 1, 55, "12345678S", "secret", "salt", 3);
insert into usuario values (7, "Sara", "Flores", "saflores@malarm.com", "07/09/1994", 1, 47, "12345678F", "secret", "salt", 4);

#Disease

insert into enfermedad values (1,'Ebola', false, 10, 4, 10, 15, 14, 15, 60);
insert into enfermedad values (2,'Zika', false, 20, 9, 10, 20, 14, 25, 70);
insert into enfermedad values (3,'Dengue', false, 15, 6, 13, 15, 19, 15, 55);
insert into enfermedad values (4,'Tifus', false, 12, 12, 40, 90, 62, 80, 54);
insert into enfermedad values (5,'Malaria', true, 7, 80, 10, 45, 63, 72, 67);
insert into enfermedad values (6,'Flew', true, 0, 4, 10, 12, 13, 13, 58);

#Doctor

insert into medico values (1, "Juan", "Garcia", "12 de Octubre", "Infecciosas", "123", "1234");

#Focus

insert into foco values (1, "lorem impsum", 100, 1);
insert into foco values (2, "lorem impsum", 90, 1);
insert into foco values (3, "lorem impsum", 2, 1);
insert into foco values (4, "lorem impsum", 10, 1);

#Focus' places

insert into lugaresFoco values (1, "Barcelona", "28/02/2016");
insert into lugaresFoco values (2, "Madrid", "28/02/2016");
insert into lugaresFoco values (2, "Cartagena", "28/02/2016");
insert into lugaresFoco values (1, "Vigo", "28/02/2016");
insert into lugaresFoco values (1, "Malaga", "28/02/2016");
insert into lugaresFoco values (3, "Barcelona", "28/02/2016");

#Contagions

insert into contagio values (1, 1, 1, 10, 2, "10/02/2016", 3, "lorem impsum", "Barcelona");
insert into contagio values (2, 1, 1, 20, 2, "07/04/2016", 2, "lorem impsum", "Madrid");
insert into contagio values (3, 1, 1, 2, 2, "20/03/2016", 1, "lorem impsum", "Cartagena");
insert into contagio values (4, 2, 1, 40, 10, "11/01/2016", 3, "lorem impsum", "Madrid");
insert into contagio values (5, 2, 1, 3, 10, "22/02/2016", 3, "lorem impsum", "Barcelona");
insert into contagio values (6, 3, 1, 5, 3, "13/01/2016", 2, "lorem impsum", "Cartagena");
insert into contagio values (7, 3, 1, 1, 3, "24/04/2016", 2, "lorem impsum", "Barcelona");

#Infected Users

insert into usuarioContagiado values (1,1);
insert into usuarioContagiado values (2,1);
insert into usuarioContagiado values (3,1);
insert into usuarioContagiado values (4,1);
insert into usuarioContagiado values (1,2);
insert into usuarioContagiado values (4,2);
insert into usuarioContagiado values (6,3);
insert into usuarioContagiado values (7,3);
insert into usuarioContagiado values (2,4);
insert into usuarioContagiado values (3,4);
insert into usuarioContagiado values (4,4);
insert into usuarioContagiado values (7,5);
insert into usuarioContagiado values (1,6);
insert into usuarioContagiado values (2,7);