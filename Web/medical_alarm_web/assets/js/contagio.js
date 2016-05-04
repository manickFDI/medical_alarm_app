NUM_COLUMNS = 5, // dni, nombre, enfermedad, fecha, confirmar revision
DOCTOR_ID = 1,
ENTRYPOINT_CONTAGIONS = "http://localhost:5000/malarm/api/contagions/",
ENTRYPOINT_USERS_CONTAGIONS = "http://localhost:5000/malarm/api/users/contagions/",
ENTRYPOINT_NOTIFICATION = "http://localhost:5000/malarm/api/notification/",
ENTRYPOINT_USERS_NOTIFICATIONS = "http://localhost:5000/malarm/api/user/notifications/",
ENTRYPOINT_DISEASES = "http://localhost:5000/malarm/api/diseases/"

/*
	Busca las notificaciones enviadas a un usuario.
	Delega en updateNotificationsTable la funcion de actualizar la tabla de notificaciones.
*/
function getNotifications() {
	var dni = document.getElementById('inputTxt').value;
	if(dni != "") {
		if(validateDNI(dni)) {
			var apiURL = ENTRYPOINT_USERS_NOTIFICATIONS + "?dni=" + dni;
			personalAlert("CARGANDO  ", " --  Cargando notificaciones...", "info", 150, true);
			return $.ajax({
				url: apiURL,
			}).always(function() {
				$("#notificationsTable").remove(); // Vaciar la tabla
			}).done(function (data, textStatus, jqXHR) {
				updateNotificationsTable(data, dni);
			}).fail(function (jqXHR, textStatus, errorThrown) {
				//alert("Error al buscar el usuario.");
				personalAlert("ERROR  ", " --  Error al buscar el usuario.", "danger", 2000, false);
			});

			//updateUsersTable(); // OJO!! quitar esta linea cuando funcione la peticion AJAX
		}
		else {
			//alert("DNI incorrecto.");
			personalAlert("ERROR  ", " --  DNI incorrecto.", "danger", 2000, false);
		}
	}
	else {
		//alert("Debe introducir el DNI de un usuario.");
		personalAlert("ERROR  ", " --  Debe introducir el DNI de un usuario.", "danger", 2000, false);
	}
}


/*
	Actualiza la tabla de notificaciones enviadas al usuario a partir de la respuesta AJAX.
	* Params: data - la respuesta de la peticion AJAX.
*/
function updateNotificationsTable(data, dni) {
	$("#usersTable").remove(); // Eliminamos la tabla inicial para crear la nueva tabla

	// Crea el elemento <table>, el elemento <thead> y el elemento <tbody>
	var tabla = document.createElement("table");
	var tblThead = document.createElement("thead");
	var tblBody = document.createElement("tbody");
	// Añadimos la cabecera
	var cabecera = document.createElement("tr");
	var celda1 = document.createElement("th");
	var textoCabecera1 = document.createTextNode("DNI:");
	celda1.appendChild(textoCabecera1);
	var celda2 = document.createElement("th");
	var textoCabecera2 = document.createTextNode("Nombre y apellidos:");
	celda2.appendChild(textoCabecera2);
	var celda3 = document.createElement("th");
	var textoCabecera3 = document.createTextNode("Enfermedad:");
	celda3.appendChild(textoCabecera3);
	var celda4 = document.createElement("th");
	var textoCabecera4 = document.createTextNode("Notificación enviada:");
	celda4.appendChild(textoCabecera4);
	var celda5 = document.createElement("th");
	var textoCabecera5 = document.createTextNode("Evaluación de la revisión:");
	celda5.appendChild(textoCabecera5);
	// appends
	cabecera.appendChild(celda1);
	cabecera.appendChild(celda2);
	cabecera.appendChild(celda3);
	cabecera.appendChild(celda4);
	cabecera.appendChild(celda5);
	tblThead.appendChild(cabecera);

	for(i=0; i<data.notifications.length; i++) { // para cada notificacion
		var fila = document.createElement("tr");
		var notification = data.notifications[i];
		var user = data.user;

		for (var k=0; k<NUM_COLUMNS; k++) { //columnas
			var celda = document.createElement("td");

			if(k==0) { // dni
				var textoCelda = document.createTextNode(dni);
				celda.appendChild(textoCelda);
			}
			else if(k==1) { // nombre y apellidos
				var textoCelda = document.createTextNode(user.name + " " + user.lastname);
				celda.appendChild(textoCelda);
			}
			else if(k==2) { // enfermedad
				var textoCelda = document.createTextNode(notification.contagion.disease.name);
				celda.appendChild(textoCelda);
			}
			else if(k==3) { // fecha de la notificacion
				var textoCelda = document.createTextNode(notification.date);
				celda.appendChild(textoCelda);
			}
			else if(k==4) { // evaluacion de la revisión -> dos botones: Positivo y Negativo
				var button = document.createElement("button");
				button.setAttribute("class", "btn btn-danger");
				var userId = user.user_id;
				var contagioId = notification.contagion_id;
				//console.log(userId);
				//console.log(contagioId);
				//button.setAttribute("id", userId);
				button.setAttribute("onclick", "confirmPositive(" + userId + ", " + contagioId + ", this)");
				//var spanButton = document.createElement("span");
				//spanButton.setAttribute("class", "glyphicon glyphicon-ok");
				//button.appendChild(spanButton);
				button.innerHTML = "Contagiado";
				celda.appendChild(button);

				var button2 = document.createElement("button");
				button2.setAttribute("class", "btn btn-success");
				//button2.setAttribute("id", userId);
				button2.setAttribute("onclick", "confirmNegative(" + userId + ", " + contagioId + ", this)");
				//var spanButton = document.createElement("span");
				//spanButton.setAttribute("class", "glyphicon glyphicon-ok");
				//button2.appendChild(spanButton);
				button2.innerHTML = "Falsa alarma";
				celda.appendChild(button2);
			}

			fila.appendChild(celda);
		}

		tblBody.appendChild(fila); // agrega la fila al final de la tabla (al final del elemento tblbody)
	}

	tabla.appendChild(tblThead);
	tabla.appendChild(tblBody);

	tabla.setAttribute("id", "notificationsTable");
	tabla.setAttribute("class", "table table-striped table-bordered");

	// appends <table> into <div>
	$("#divNotificationsTable").append(tabla);	
}


/*
	Confirma que el usuario ha pasado cita con el medico y que SI esta contagiado.
	Cambia el campo bool Confirmado de la tabla Notificacion y elimina la fila de la tabla.
	Ademas, añade el nuevo contagio.
	* Params: el id del usuario, el id del contagio y la fila que debemos quitar
*/
function confirmPositive(userId, contagionId, row) {
	var apiURL = ENTRYPOINT_NOTIFICATION;
	var userData = '{"notification":{ "user_id":"' + userId + '",' + '"contagion_id":"' + contagionId + '"}}'; // Creamos el txt con el json
	//userData = JSON.stringify(userData); // Verificamos que el formato es json

	return $.ajax({
		url: apiURL,
		type: "PUT",
		data: userData
	}).done(function (data, textStatus, jqXHR) {
		addNewUserInfected(userId, contagionId);
		removeUserFromTable(row);
	}).fail(function (jqXHR, textStatus, errorThrown) {
		//alert("Error al buscar usuario.");
		personalAlert("ERROR  ", " --  Error al buscar usuario.", "danger", 2000, false);
	});

	//removeUserFromTable(row); // OJO!! quitar esta linea cuando funcione la peticion AJAX
}


/*
	Añade una nueva fila en la tabla UsuariosContagiados
*/
function addNewUserInfected(userId, contagionId) {
	var apiURL = ENTRYPOINT_USERS_CONTAGIONS;
	var userData = '{"user_contagion":{ "user_id":"' + userId + '",' + '"contagion_id":"' + contagionId + '"}}'; // Creamos el txt con el json
	//userData = JSON.stringify(userData); // Verificamos que el formato es json

	return $.ajax({
		url: apiURL,
		type: "POST",
		data: userData
	}).done(function (data, textStatus, jqXHR) {
		//alert("Se ha añadido el usuario al contagio actual.");
		personalAlert("INFO  ", " --  Se ha añadido el usuario al contagio actual.", "success", 2000, false);
	}).fail(function (jqXHR, textStatus, errorThrown) {
		//alert("Error al buscar usuario.");
		personalAlert("ERROR  ", " --  Error al buscar usuario.", "danger", 2000, false);
	});
}



/*
	Confirma que el usuario ha pasado cita con el medico y que NO esta contagiado.
	Cambia el campo bool Confirmado de la tabla Notificacion y elimina la fila de la tabla.
	* Params: el id del usuario, el id del contagio y la fila que debemos quitar
*/
function confirmNegative(userId, contagionId, row) {
	//console.log(userId + ", " + contagionId);
	var apiURL = ENTRYPOINT_NOTIFICATION;
	var userData = '{"notification":{"user_id":"' + userId + '",' + '"contagion_id":"' + contagionId + '"}}'; // Creamos el txt con el json
	//console.log(userData);

	//userData = JSON.stringify(userData); // Verificamos que el formato es json NOOOOOOOO!

	return $.ajax({
		url: apiURL,
		type: "PUT",
		data: userData
	}).done(function (data, textStatus, jqXHR) {
		personalAlert("INFO  ", " --  Falsa alarma establecida correctamente.", "success", 2000, false);
		removeUserFromTable(row);
	}).fail(function (jqXHR, textStatus, errorThrown) {
		//alert("Error al confirmar falsa alarma.");
		personalAlert("ERROR  ", " --  Error al confirmar falsa alarma.", "danger", 2000, false);
	});

	//removeUserFromTable(row); // OJO!! quitar esta linea cuando funcione la peticion AJAX
}


/*
	Quita la fila del usuario que hemos "Confirmado revision"
*/
function removeUserFromTable(row) {
	var i = row.parentNode.parentNode.rowIndex;
    document.getElementById("notificationsTable").deleteRow(i);
}


/*
	Recoge los campos del formulario y realiza el post AJAX para dar de alta el Contagio
*/
function confirmSubmit() {
	var dni = document.getElementById('dni').value;
	if(validateDNI(dni)) {
		if(confirm("¿Esta seguro de que quiere dar de alta este contagio?")) {
			// Valores del formulario
			var time = document.getElementById('time').value;
			var distance = document.getElementById('distance').value;
			var comboboxDiseases = document.getElementById('disease');
			var disease = comboboxDiseases.options[comboboxDiseases.selectedIndex].value;
			var date = $("#datetimepicker").data("DateTimePicker").date(); // lo devuelve en ¿segundos? desde ... ?
			var comboboxLevel = document.getElementById('level');
			var level = comboboxLevel.options[comboboxLevel.selectedIndex].value;
			var timeWindow = document.getElementById('timeWindow').value;
			var description = document.getElementById('description').value;

			var userData = '{"user_contagion":{ "user_dni":"' + dni + '",' +
							'"exposure":"' + time + '",' +
							'"distance":"' + distance + '",' +
							'"disease":"' + disease + '",' +
							'"date":"' + date + '",' +
							'"level":"' + level + '",' +
							'"time_window":"' + timeWindow + '",' +
							'"doctor_id":"' + DOCTOR_ID + '",' +
							'"description":"' + description + '"' +
							' }}'; // Creamos el txt con el json
			//alert(userData);
			
			//userData = JSON.stringify(userData); // Verificamos que el formato es json

			var apiURL = ENTRYPOINT_CONTAGIONS;

			return $.ajax({
				url: apiURL,
				type: "POST",
				data: userData
			}).done(function (data, textStatus, jqXHR) {
				//alert("Contagio dado de alta correctamente");
				personalAlert("SUCCESS  ", " --  Contagio dado de alta correctamente.", "success", 2000, false);
			}).fail(function (jqXHR, textStatus, errorThrown) {
				//alert("Error al buscar usuario.");
				personalAlert("ERROR  ", " --  Error al dar de alta el contagio.", "danger", 2000, false);
			});
		}
	}
	else {
		//alert("DNI no válido.");
		personalAlert("ERROR  ", " --  DNI no válido.", "danger", 2000, false);
	}

}


/*
	Carga de la BD la lista de enfermedades y rellena el comboBox con ellas
*/
function loadDiaseases() {
	var apiURL = ENTRYPOINT_DISEASES + "?type=all&top=0";

	return $.ajax({
		url: apiURL
	}).done(function (data, textStatus, jqXHR) {
		var comboboxDiseases = document.getElementById('disease');

		for(var i=0; i<data.length; i++) {
			var option = document.createElement("option");
			option.text = data[i].name;
			option.value = data[i].name;
			comboboxDiseases.appendChild(option, i);
		}

	}).fail(function (jqXHR, textStatus, errorThrown) {
		personalAlert("ERROR  ", " --  Error al obtener la lista de enfermedades.", "danger", 2000, false);
	});
}