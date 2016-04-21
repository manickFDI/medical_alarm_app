/**
 * Created by Manuel on 9/04/16.
 */

//var ENTRYPOINT = "http://localhost:5000/malarm/api/user/11868634J&callback=?";
var ENTRYPOINT = "http://localhost:5000/malarm/api/";

var DEBUG = true;
var WHITE_SPACE = " ";


//window.onload = cargarContagios; //llamar a esta funcion cuando se cargue la página

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
 * Calculate the age of a person. Receive the date in a string with a correct spanish format (dd-mm-aaaa || dd/mm/aaaa)
 * @param fecha
 * @returns {*} integer with the age. Returns false if date is incorrect or greater than current day
 */
function calculateAge(date){

    var BARRA = "/";
    var GUION = "-";

    var age;

    //calculo la fecha actual
    var today = new Date();
    //alert(today);

    //calculo la fecha que recibo si viene con / o -
    var separacionG = date.indexOf(GUION);
    var separacionB = date.indexOf(BARRA);

    if(separacionG != -1) {
        //La descompongo en un array
        var array_fecha = date.split(GUION);
        //si el array no tiene tres partes, la fecha es incorrecta
        if (array_fecha.length!=3) {
            age = -1;
        }
    } else if(separacionB != -1) {
        //La descompongo en un array
        var array_fecha = date.split(BARRA);
        //si el array no tiene tres partes, la fecha es incorrecta
        if (array_fecha.length!=3) {
            age = -1;
        }
    } else {
        age = -1;
    }

    //compruebo que los ano, mes, dia son correctos
    var year;
    year = parseInt(array_fecha[2]);
    if (isNaN(year))
        age = -1;

    var month;
    month = parseInt(array_fecha[1]);
    if (isNaN(month))
        age = -1;

    var day;
    day = parseInt(array_fecha[0]);
    if (isNaN(day))
        age = -1;

    //resto los años de las dos fechas
    var aux = today.getFullYear();//ciudado con getYear despues del año 2000 no funciona
    age = aux- year - 1; //-1 porque no se si ha cumplido años ya este año

    //si resto los meses y me da menor que 0 entonces no ha cumplido años. Si da mayor si ha cumplido
    if (today.getMonth() + 1 - month < 0) {
        age = age;
    } else if (today.getMonth() + 1 - month > 0) { //+ 1 porque los meses empiezan en 0
        age = age + 1;
    } else if((today.getUTCDate() - day) >= 0) {//entonces es que eran iguales. miro los dias. Si resto los dias y me da menor que 0 entonces no ha cumplido años. Si da mayor o igual si ha cumplido
        age = age + 1;
    }

    return age;
}



/**
 * Convert the number of the corresponding sex in the db to string
 * @param number
 * @returns {*} the string converted
 */
function convertToSex(number) {

    var ret;

    switch(number) {
        case 0:
            ret = "Hombre";
            break;
        case 1:
            ret = "Mujer";
            break;
        default:
            ret = "Undefined";
    }
    return ret;
}



/**
 * Convert the number in the db of the user state  to a string format
 * @param number
 * @returns {*} the string converted
 */
function convertToState(number) {

    var ret;

    switch(number) {
        case 0:
            ret = "Indefinido";
            break;
        case 1:
            ret = "Sano";
            break;
        case 2:
            ret = "Curado";
            break;
        case 3:
            ret = "Enfermo";
            break;
        case 4:
            ret = "Fallecido";
            break;
        default:
            ret = "Undefined";
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
    var edad = calculateAge(user.birthday);
    var sexo = convertToSex(parseInt(user.gender));
    var state = convertToState(parseInt(user.state));

    div.innerHTML = "<h3>" + nombreCompleto + "</h3>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='dni'>DNI/NIE: " + user.idnumber + "</h6>";
    div.innerHTML += "<h6 id='email'>Email: " + user.email + "</h6>";
    div.innerHTML += "<h6>Fecha de nacimiento: " + user.birthday + "</h6>";
    div.innerHTML += "<h6>Edad: " + edad + "</h6>";
    div.innerHTML += "<h6>Sexo: " + sexo + "</h6>";
    div.innerHTML += "<h6>Peso: " + user.weight + " Kg</h6>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='estado'>Estado: <strong>" + state.toUpperCase() + "</strong></h6>";
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

    var nuevoEstado = $(this).text();
    var ret = confirm("¿Está seguro que desea cambiar el estado del paciente a " + nuevoEstado + "?");
    if (ret) {
        var estado = document.getElementById("estado");
        estado.innerHTML = "<h6 id='estado'>Estado: <strong>" + nuevoEstado.toUpperCase() + "</strong></h6>";

        //query UPDATE
        //split dni
        //var apiurl = ENTRYPOINT + "/" + document.getElementById("dni");
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
 * function that call to other function chosen to give back the active contagions from the db.
 */
function cargarContagios() {
    var apiurl = ENTRYPOINT + "contagions";
    getContagions(apiurl);
}


/**
 * Connect with the api_REST and obtain all the active contagions in the system with the necessary information
 * @returns {*}
 */

function getContagions(apiurl) {
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
        '<span class="leyenda">Enfermedad: Gripe A || Madrid-Centro || Nivel: 1 || Contagios: 12</span>' +
        '<a class="btn btn-info btn-xs" onclick="deleteContagion(' + aux_li + ')" id="' + aux_btn + '">Erradicar </a>';

    //añadir a la lista cada contagio
    $("#contagiosList").append($contagio);
    return $contagio;
}



/**
 * delete an active contagion from the html
 * @param id
 */
function deleteContagion(id) {
    var aux = "'" + id + "'"
    var li = document.getElementById(aux);
    li.parentNode.removeChild(li);
    //$.notify("Hello");
    /*$.notify('Hello World', {
        offset: {
            x: 50,
            y: 100
        }
    });*/
    //la eliminamos de verdad
    //var apiurl = ENTRYPOINT + "contagion/" + id;
    //removeContagion_db(apiurl);
}


/**
 * remove or set the level of the contagion to 0 and delete the contagion from the db
 * @param apiurl
 * @returns {*}
 */
function removeContagion_db(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        alert ("Contagio eliminado de la lista");
    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        alert ("Error. No se ha podido eliminar el contagio");
    });
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


function loadMap() {

}



 // KEY_API   --->>   AIzaSyDqC2mEHCJ98RqjUjyVKWIF7Y67y9aUaBU
