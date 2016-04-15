/**
 * Created by Manuel on 9/04/16.
 */

//var ENTRYPOINT = "http://localhost:5000/malarm/api/user/11868634J&callback=?";
var ENTRYPOINT = "http://localhost:5000/malarm/api/";
var ENTRYPOINT_CONT = "/malarm/api/contagios/";

var DEBUG = true;
var WHITE_SPACE = " ";

/**
 * Verify if the param dni has a good format of dni with 8 digits and a letter (correct letter)
 * @param dni
 * @returns {boolean}
 */

function validateDNI(dni) {
    var number;
    var letter;
    var letraSet;
    var expresion_regular_dni;
    var ret = false;

    expresion_regular_dni = /^\d{8}[a-zA-Z]$/; //expresion regular formada por 8 digitos y una letra (mayus o minus)

    if(expresion_regular_dni.test (dni) == true){ //test -> comprueba que dni es correcto
        number = dni.substr(0,dni.length-1); //nos quedamos con los digitos
        letter = dni.substr(dni.length-1,1);//nos quedamos con la letra

        number = number % 23; //necesario para saber si la letra es valida (proceso)

        letraSet='TRWAGMYFPDXBNJZSQVHLCKET'; //todas las letras posibles (ni Ñ ni I ni O)

        letraSet=letraSet.substring(number,number+1);

        if (letraSet==letter.toUpperCase()) {
            ret = true;
        }
    }
    return ret;
}


/**
 * Complete the html that belongs to the user writing the information of the user in the specific div and the buttons of the message
 * @param div
 * @param user
 */

function writeUser(div, user) {

    var nombre = String(user.name);
    var apellidos = String(user.lastname);
    var nombreCompleto = nombre.concat(WHITE_SPACE, apellidos);

    div.innerHTML = "<h3>" + nombreCompleto + "</h3>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='dni'>DNI/NIE: " + user.idnumber + "</h6>";
    div.innerHTML += "<h6 id='email'>Email: " + user.email + "</h6>";
    div.innerHTML += "<h6>Edad: " + user.birthday + "</h6>";
    div.innerHTML += "<h6>Sexo: " + user.gender + "</h6>";
    div.innerHTML += "<h6>Peso: " + user.weight + " Kg</h6>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='estado'>Estado: <strong>INDEFINIDO</strong></h6>";
    div.innerHTML += "<hr />";

    showButtonsUser();
}


/**
 * obtain the user from the searcher. It search by the dni.
 */

function getUser() {
    var dni = document.getElementById("buscadorUsuario").value;
    if(validateDNI(dni)) {
        var apiurl = ENTRYPOINT + "user/" + dni;
        getUser_db(apiurl);
    }
    else {
        alert("Error al introducir el DNI (FORMATO INCORRECTO)");
    }
}


/**
 * Function that communicate with the rest_API to obtain the information of the user
 * @param apiurl
 * @returns {*}
 */

function getUser_db(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
   }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        var divUser = document.getElementById("infoUsuario");
        $("#infoUsuario").empty();
        writeUser(divUser, data);

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        $("#infoUsuario").empty();
        showEmptyUser();
        alert ("Error al obtener usuario")
    });
}



/////
////
//// problem with th function. Inside onclick appears twice. Without function
$('#estadosList li a').on('click', function(){

    var nuevoEstado = $(this).text().toUpperCase()
    var ret = confirm("¿Está seguro que desea cambiar el estado del paciente a " + nuevoEstado + "?");
    if (ret) {
        var estado = document.getElementById("estado");
        estado.innerHTML = "<h6 id='estado'>Estado: <strong>" + nuevoEstado + "</strong></h6>";

        //query UPDATE
        var apiurl = ENTRYPOINT + "/" + document.getElementById("email");
        //updateEstate(apiurl, nuevoEstado); //solo el estado o  el usuario entero
    }
});


/**
 * function that communicate with the api_REST. It update the state of a user when the doctor changes it
 * @param apiurl
 * @param userData
 * @returns {*}
 */

function updateEstate(apiurl, userData) {
    userData = JSON.stringify(userData);
    return $.ajax({
        url: apiurl,
        type: "PUT",
        data:userData,
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        alert ("usuario actualizado con exito");

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        alert ("Usuario no actualizado");
    });
}


/**
 * Connect with the api_REST and obtain all the active contagions in the system with the necessary information
 * @returns {*}
 */

function getContagions() {
    var apiurl = ENTRYPOINT_CONT;
    return $.ajax({
        url: apiurl,
        dataType:"json"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }

        var contagios = data.collection.items;
        for (var i=0; i < contagios.length; i++){
            var contagio = contagios[i];

            var contagio_data = contagio.data;
            for (var j=0; j<contagio_data.length;j++){
                addContagioToList(contagio_data[j].value);
            }
        }
    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        alert ("Error en la consulta de los contagios");
    });
}


/**
 * add a new contagion to the list of the html
 * @param contagio
 * @returns {string}
 */
function addContagioToList(contagio) {
    //poner contagio.enfermedad ...

    var aux_li = "'enf_" + contagio + "'";
    var aux_btn = "enf_btn_" + contagio;

    var $contagio = '<li id="' + aux_li + '">' + '<span class="sm-box bggreen2" >' + '</span>' +
        '<span class="leyenda">Enfermedad: Gripe A || Madrid-Centro || Contagios: 12</span>' +
        '<a class="btn btn-info btn-xs" onclick="deleteContagion(' + aux_li + ')" id="' + aux_btn + '">Erradicar </a>';

    //añadir a la lista cada contagio
    $("#contagiosList").append($contagio);
    return $contagio;
}


/**
 * this function prints the initial state of the user div
 * It is used when a new search fails
 */
function showEmptyUser() {

    var divUser = document.getElementById("infoUsuario");

    var html = '<img id="plantillafondo" src="assets/img/plantilla1.png" alt="Usuario no especificado">' +
        '<div class="panel-footer text-muted">Aquí se mostrará la información del paciente una vez realizada la búsqueda</div>';

    divUser.innerHTML = html;

    if ( $("#botonesPanelUsuario").length ) {
        $("#botonesPanelUsuario").hide();
    }
}



/**
 * hide the template of the email if you cancel the button
 */
function hideMessage() {
    $("#messageUser").hide(500);
}


/**
 * shows the message to email with a user
 */
function showMessage() {
    $("#messageUser").show(1000);
}


/**
 * a partial function that shows the panel with the buttons when a search is done
 */
function showButtonsUser() {
    $("#botonesPanelUsuario").show();
}


/**
 * temporal function that complete the table with the contagions
 */
function contagions() {
    for (var j=0; j<4;j++){
        addContagioToList(j);
    }
}


/**
 * delete an active contagion from the html
 * @param id
 */
function deleteContagion(id) {
    var aux = "'" + id + "'"
    var li = document.getElementById(aux);
    li.parentNode.removeChild(li);
}