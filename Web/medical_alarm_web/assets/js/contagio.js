ENTRYPOINT_CONTAGIONS = "malarm/api/contagions/",
ENTRYPOINT_NOTIFICATIONS = "malarm/api/notifications/"

/*
	Busca los posibles usuarios contagiados por la enfermedad buscada.
	Actualiza la tabla de usuarios contagiados
*/
function getInfectedUsers() {
	var apiURL = ENTRYPOINT_CONTAGIONS + "getUsersInfected" + "/";
	var disease = document.getElementById('inputTxt').value; // Cogemos el nombre de la enfermedad del input
	if(disease != "") {
		/*var userData = '{ "disease":"' + disease + '"}'; // Creamos el txt con el json
		userData = JSON.stringify(userData); // Verificamos que el formato es json

		return $.ajax({
			url: apiURL,
			type: "POST",
			data: userData
		}).always(function() {
			$("#usersTable").remove(); // Vaciar la tabla de posibles usuarios contagiados
		}).done(function (data, textStatus, jqXHR) {
			updateUsersTable(data); // data contiene
		}).fail(function (jqXHR, textStatus, errorThrown) {
			alert("Error al buscar la enfermedad.");
		});*/

		updateUsersTable(); // OJO!! quitar esta linea cuando funcione la peticion AJAX
	}
	else {
		alert("Debe introducir el nombre de la enfermedad.");
	}
}


/*
	Actualiza la tabla de usuarios contagiados a partir de la respuesta AJAX.
	A cada boton de la fila le asigna como id la id del usuario para que facilite "Confirmar revision"
	* Params: data - la respuesta de la peticion AJAX. Contiene el id del contagio y la lista de usuarios contagiados
*/
function updateUsersTable(data) {
	$("#usersTable").remove(); // Eliminamos la tabla inicial para crear la nueva tabla

	// Crea el elemento <table>, el elemento <thead> y el elemento <tbody>
	var tabla = document.createElement("table");
	var tblThead = document.createElement("thead");
	var tblBody = document.createElement("tbody");
	// Añadimos la cabecera
	var cabecera = document.createElement("tr");
	var celda1 = document.createElement("th");
	var textoCabecera1 = document.createTextNode("Nombre y apellidos:");
	celda1.appendChild(textoCabecera1);
	var celda2 = document.createElement("th");
	var textoCabecera2 = document.createTextNode("Estado actual:");
	celda2.appendChild(textoCabecera2);
	var celda3 = document.createElement("th");
	var textoCabecera3 = document.createTextNode("Confirmar revisión:");
	celda3.appendChild(textoCabecera3);
	// appends
	cabecera.appendChild(celda1);
	cabecera.appendChild(celda2);
	cabecera.appendChild(celda3);
	tblThead.appendChild(cabecera);

	// Crea las celdas
	for (var i=0; i<2; i++) { //filas
		var fila = document.createElement("tr");

		for (var j=0; j<3; j++) { //columnas
			var celda = document.createElement("td");

			if(j==2) { // creamos el boton y el span de "Confirmar revision"
				var button = document.createElement("button");
				button.setAttribute("class", "btn btn-success");
				var userId = i; // OJO!! userId vendrá en la respuesta de la peticion AJAX
				var contagioId = i;
				button.setAttribute("id", userId);	// usamos como id del boton el id del usuario para que cuando pulsemos
													// en "Confirmar revisión" podamos actualizar su estado
				button.setAttribute("onclick", "confirmRevision(" + userId + ", " + contagioId + ", this)");
				var spanButton = document.createElement("span");
				spanButton.setAttribute("class", "glyphicon glyphicon-ok");
				button.appendChild(spanButton);
				celda.appendChild(button);
			}
			else {
				var textoCelda = document.createTextNode("celda en la fila " + i + ", columna " + j);
				celda.appendChild(textoCelda);
			}
			fila.appendChild(celda);
		}

		// agrega la fila al final de la tabla (al final del elemento tblbody)
		tblBody.appendChild(fila);
	}

	tabla.appendChild(tblThead);
	tabla.appendChild(tblBody);

	tabla.setAttribute("id", "usersTable");
	tabla.setAttribute("class", "table table-striped table-bordered");

	// appends <table> into <div>
	$("#divUsersTable").append(tabla);	
}


/*
	Confirma que el usuario ha pasado cita con el medico.
	Cambia el campo bool Confirmado de la tabla Notificacion y elimina la fila de la tabla
	* Params: el id del usuario, el id del contagio y la fila que debemos quitar
*/
function confirmRevision(userId, contagionId, row) {
	/*var apiURL = ENTRYPOINT_NOTIFICATIONS + "confirmRevision" + "/";
	var userData = '{ "userId":"' + userId + '",' + '"contagionId":"' + contagionId + '"}'; // Creamos el txt con el json
	userData = JSON.stringify(userData); // Verificamos que el formato es json

	return $.ajax({
		url: apiURL,
		type: "POST",
		data: userData
	}).done(function (data, textStatus, jqXHR) {
		removeUserFromTable(row);
	}).fail(function (jqXHR, textStatus, errorThrown) {
		alert("Error al buscar usuario.");
	});*/
	removeUserFromTable(row); // OJO!! quitar esta linea cuando funcione la peticion AJAX
}


/*
	Quita la fila del usuario que hemos "Confirmado revision"
*/
function removeUserFromTable(row) {
	var i = row.parentNode.parentNode.rowIndex;
    document.getElementById("usersTable").deleteRow(i);
}


/*
	Recoge los campos del formulario y realiza el post AJAX para dar de alta el Contagio
*/
function confirmSubmit() {
	if(confirm("¿Esta seguro de que quiere dar de alta este contagio?")) {
		var apiURL = ENTRYPOINT_CONTAGIONS;
		// Valores del formulario
		var time = document.getElementById('time').value;
		var distance = document.getElementById('distance').value;
		var comboboxDiseases = document.getElementById('disease');
		var disease = comboboxDiseases.options[comboboxDiseases.selectedIndex].value;
		var date = $("#datetimepicker").data("DateTimePicker").date(); // lo devuelve en ¿segundos? desde ... ?
		var comboboxLevel = document.getElementById('level');
		var level = comboboxLevel.options[comboboxLevel.selectedIndex].value;
		var description = document.getElementById('description').value;

		var userData = '{ "time":"' + time + '",' +
						'"distance":"' + distance + '",' +
						'"disease":"' + disease + '",' +
						'"date":"' + date + '",' +
						'"level":"' + level + '",' +
						'"description":"' + description + '"' +
						' }'; // Creamos el txt con el json
		alert(userData);
		
		/*userData = JSON.stringify(userData); // Verificamos que el formato es json

		return $.ajax({
			url: apiURL,
			type: "POST",
			data: userData
		}).done(function (data, textStatus, jqXHR) {
			alert("Contagio dado de alta correctamente");
		}).fail(function (jqXHR, textStatus, errorThrown) {
			alert("Error al buscar usuario.");
		});*/
	}

}