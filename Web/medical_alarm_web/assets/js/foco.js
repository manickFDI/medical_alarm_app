ENTRYPOINT_FOCUS = "malarm/api/focus/",
ENTRYPOINT_USERS = "malarm/api/users/"

/*
	Realiza la peticion AJAX para obtener la lista de focos activos.
	Delega a createTable() la funcion de crear la tabla correctamente con los datos devueltos por la peticion AJAX
*/
function loadFocusTable() {
	/*var apiURL = ENTRYPOINT_FOCUS;

	return $.ajax({
		url: apiURL
	}).always(function() {
		$("#focusTable").remove(); // Eliminamos la tabla inicial para crear la nueva tabla
	}).done(function (data, textStatus, jqXHR) {
		createTable(data);
	}).fail(function (jqXHR, textStatus, errorThrown) {
		alert("Error al buscar focos.");
	});*/

	createTable(); // OJO!! quitar esta linea cuando funcione la peticion AJAX
}


/*
	Crea la tabla de focos a partir de la informacion pasada por parametro.
	* Params: data - la respuesta AJAX con la lista de focos activos
*/
function createTable(data) {
	$("#focusTable").remove();
	// Crea el elemento <table>, el elemento <thead> y el elemento <tbody>
	var tabla = document.createElement("table");
	var tblThead = document.createElement("thead");
	var tblBody = document.createElement("tbody");
	// Añadimos la cabecera
	var cabecera = document.createElement("tr");
	var celda1 = document.createElement("th");
	var textoCabecera1 = document.createTextNode("Lugar:");
	celda1.appendChild(textoCabecera1);
	var celda2 = document.createElement("th");
	var textoCabecera2 = document.createTextNode("Nº personas:");
	celda2.appendChild(textoCabecera2);
	var celda3 = document.createElement("th");
	var textoCabecera3 = document.createTextNode("Fecha:");
	celda3.appendChild(textoCabecera3);
	var celda4 = document.createElement("th");
	var textoCabecera4 = document.createTextNode("Descripción:");
	celda4.appendChild(textoCabecera4);
	var celda5 = document.createElement("th");
	var textoCabecera5 = document.createTextNode("Eliminar foco:");
	celda5.appendChild(textoCabecera5);
	// appends
	cabecera.appendChild(celda1);
	cabecera.appendChild(celda2);
	cabecera.appendChild(celda3);
	cabecera.appendChild(celda4);
	cabecera.appendChild(celda5);
	tblThead.appendChild(cabecera);

	// Crea las celdas
	for (var i=0; i<5; i++) { //filas
		var fila = document.createElement("tr");

		for (var j=0; j<5; j++) { //columnas
			var celda = document.createElement("td");

			if(j == 4) { // creamos el boton y el span de "Confirmar revision"
				var button = document.createElement("button");
				button.setAttribute("class", "btn btn-danger");
				var focusId = i; // OJO!! focusId vendrá en la respuesta de la peticion AJAX
				button.setAttribute("id", focusId);	// usamos como id del boton el id del usuario para que cuando pulsemos
													// en "Confirmar revisión" podamos actualizar su estado
				button.setAttribute("onclick", "removeFocus(" + focusId + ", this)");
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

	tabla.setAttribute("id", "focusTable");
	tabla.setAttribute("class", "table table-striped table-bordered");

	// appends <table> into <div>
	$("#divFocusTable").append(tabla);	
}


/*
	Elimina el foco seleccionado (pasado por parametro) realizando la peticion AJAX DELETE.
	Delega en removeRowFromTable() la funcion de eliminar la fila correspondiente de la tabla.
	* Params: focusId - id del foco a eliminar de la bd
			  row - la fila a quitar de la tabla
*/
function removeFocus(focusId, row) {
	var apiURL = ENTRYPOINT_FOCUS + focusId + "/";

	/*return $.ajax({
		url: apiURL,
		type: "DELETE"
	}).done(function (data, textStatus, jqXHR) {
		removeUserFromTable(row);
	}).fail(function (jqXHR, textStatus, errorThrown) {
		alert("Error al buscar usuario.");
	});*/

	removeRowFromTable('focusTable', row); // OJO!! quitar esta linea cuando funcione la peticion AJAX
}


/*
	Quita de la tabla "tblName" la fila "row"
*/
function removeRowFromTable(tblName, row) {
	var i = row.parentNode.parentNode.rowIndex;
    document.getElementById(tblName).deleteRow(i);
}


/*
	Realiza la peticion AJAX para obtener el usuario que se quiere añadir al foco.
	Delega en updateUsersToFocusTable() la funcion de actualizar la tabla
*/
function getUserToFocus() {
	var userDNI = document.getElementById('inputTxt').value; // Cogemos el dni del usuario del input
	if(userDNI != "") {
		if(validateDNI(userDNI)) {
			/*var apiURL = ENTRYPOINT_USERS + userDNI + "/";

			return $.ajax({
				url: apiURL
			}).done(function (data, textStatus, jqXHR) {
				updateUsersToFocusTable(data);
			}).fail(function (jqXHR, textStatus, errorThrown) {
				alert("Error al buscar usuario.");
			});*/
			
			updateUsersToFocusTable(); // OJO!! quitar esta linea cuando funcione la peticion AJAX
		}
		else {
			alert("DNI no válido.");
		}
	}
	else {
		alert("Debe introducir el DNI del usuario.");
	}
}


/*
	Añade una nueva fila a la tabla de usuarios del foco a partir de los datos que recibe como parametro
	* Params: data - la respuesta de la peticion AJAX
*/
function updateUsersToFocusTable(data) {
	var tblBody = document.getElementById('tableBody');
	var fila = document.createElement("tr");

	for (var i=0; i<3; i++) { //columnas
		var celda = document.createElement("td");
		if(i == 2) {
			var button = document.createElement("button");
			button.setAttribute("class", "btn btn-danger");
			var userId = Math.random(); // OJO!! userId vendrá en la respuesta de la peticion AJAX
			button.setAttribute("id", userId);
			button.setAttribute("onclick", "removeRowFromTable('tableUsersToFocus', this)");
			button.innerHTML = "Quitar";
			celda.appendChild(button);
		}
		else {
			var textoCelda = document.createTextNode("celda en la columna " + i);
			celda.appendChild(textoCelda);
		}
		fila.appendChild(celda);
	}
	tblBody.appendChild(fila);
}


/*
	Obtiene el listado de ids de usuarios de la tabla (almacenados en las id de los botones de "Quitar") y la
	descipcion para realizar la peticion AJAX de dar de alta un nuevo foco.
	Delega en createTableFocusFound() la funcion de actualizar la tabla de focos encontrados.
*/
function confirmSubmit() {
	var apiURL = ENTRYPOINT_FOCUS;
	var table = document.getElementById('tableUsersToFocus');
	var numFilas = table.rows.length-1; // numFilas-1 para quitar la cabecera

	if(numFilas > 1) {
		var description = document.getElementById('focusDescription').value; // Cogemos el campo descripcion del form
		//if(description != "") {
			if(confirm("¿Esta seguro de que quiere dar de alta este contagio?")) {
				var userData = '{ "numUsers":"' + numFilas + '","usersId":[';

				for(i=1; i<numFilas; i++) { // Empezamos en i=1 para no contar la cabecera
					var userId = table.rows[i].cells[2].firstChild.id; // Cogemos el id de los botones que contienen el id del usuario
					if(i == numFilas-1)
						userData += '{"id":"' + userId + '"}'; // Si es la ultima fila no ponemos la coma en el json
					else
						userData += '{"id":"' + userId + '"},';
				}

				userData += '],"description":"' + description + '" }';

				console.log(userData);

				/*userData = JSON.stringify(userData); // Verificamos que el formato es json

				return $.ajax({
					url: apiURL,
					type: "POST",
					data: userData
				}).done(function (data, textStatus, jqXHR) {
					alert("Contagio dado de alta correctamente");
					createTableFocusFound(data);
					updateGoogleMap(data);
				}).fail(function (jqXHR, textStatus, errorThrown) {
					alert("Error al buscar usuario.");
				});*/

				createTableFocusFound(); // OJO!! quitar esta linea cuando funcione la peticion AJAX
				location.href = "#focusFound";
			}
		/*}
		else {
			alert("Debe introducir una descripcion.");
		}*/
	}
	else {
		alert("Debe introducir al menos dos usuarios para dar de alta el foco.");
	}
}


/*
	Añade tantas filas a la tabla como focos se hayan encontrado.
	* Params: data - la respuesta a la peticion AJAX
*/
function createTableFocusFound(data) {
	var tblBody = document.getElementById('tbodyFocusFound');
	var numFilas = 3; // OJO!!! numFilas viene en data

	for(var i=0; i<numFilas; i++) {
		var fila = document.createElement("tr");
		var celda = document.createElement("td");
		var textoCelda = document.createTextNode(i+1);
		celda.appendChild(textoCelda);
		var celda2 = document.createElement("td");
		var textoCelda2 = document.createTextNode("celda en la columna 1");
		celda2.appendChild(textoCelda2);

		fila.appendChild(celda);
		fila.appendChild(celda2);
		tblBody.appendChild(fila);
	}
}


/*
	Comprueba que el dni tenga el formato correcto
*/
function validateDNI(dni) {
    var numero;
    var letra;
    var letraSet;
    var expresion_regular_dni;
    var ret = false;

    expresion_regular_dni = /^\d{8}[a-zA-Z]$/; //expresion regular formada por 8 digitos y una letra (mayus o minus)

    if(expresion_regular_dni.test (dni) == true){ //test -> comprueba que dni es correcto
        numero = dni.substr(0,dni.length-1); //nos quedamos con los digitos
        letra = dni.substr(dni.length-1,1);//nos quedamos con la letra

        numero = numero % 23; //necesario para saber si la letra es valida (proceso)

        letraSet='TRWAGMYFPDXBNJZSQVHLCKET'; //todas las letras posibles (ni Ñ ni I ni O)

        letraSet=letraSet.substring(numero,numero+1);

        if (letraSet==letra.toUpperCase()) {
            ret = true;
        }
    }
    return ret;
}


/*
	Situa en el mapa los puntos de los focos encontrados
*/
function updateGoogleMap(data) {
	/*var myLatLng = {lat: -25.363, lng: 131.044};

	var map = new google.maps.Map(document.getElementById('divGoogleMap'), {
		zoom: 4,
		center: myLatLng
	});

	var marker = new google.maps.Marker({
		position: myLatLng,
		map: map,
		title: 'Foco 1'
	});*/
}