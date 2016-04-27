/**
 * Created by Manuel on 9/04/16.
 */

//var ENTRYPOINT = "http://localhost:5000/malarm/api/user/11868634J&callback=?";
var ENTRYPOINT = "http://localhost:5000/malarm/api/";

var DEBUG = true;
var WHITE_SPACE = " ";
var INFECTED = 3;

//window.onload = loadContagions();

//window.onload = cargarContagios; //llamar a esta funcion cuando se cargue la página



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

    div.innerHTML = "<h3 id='paciente'>" + nombreCompleto + "</h3>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='dni'>DNI/NIE: " + user.idnumber + "</h6>";
    div.innerHTML += "<h6 id='email'>Email: " + user.email + "</h6>";
    div.innerHTML += "<h6>Fecha de nacimiento: " + user.birthday + "</h6>";
    div.innerHTML += "<h6>Edad: " + edad + "</h6>";
    div.innerHTML += "<h6>Sexo: " + sexo + "</h6>";
    div.innerHTML += "<h6>Peso: " + user.weight + " Kg</h6>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='estado'>Estado general: <strong>" + state.toUpperCase() + "</strong></h6>";
    div.innerHTML += "<hr />";

    if(user.state == INFECTED) {
        div.innerHTML+= "<h4 id='titEnf'>Lista de enfermedades</h4>";
        div.innerHTML += "<hr />";
        createListDiseasesUser(user, div);
        //div.innerHTML += "<ul><li style='font-size:16px';>Ebola <a class='btn btn-success btn-sm' id='btnCurar' onclick='confirmCuredDisease()'>Curar</a></li></ul>"
        div.innerHTML += "<hr />";
        div.innerHTML += "<hr />";
        /*for(var i = 0; i < user.length; i++) {
            div.innerHTML+= "<h6>user[i].disease</h6>";
            <a class="btn btn-success" onclick="confirmCuredDisease()"> Usuario curado</a>
            div.innerHTML += "<hr />";
        }*/

    }
    showButtonsUser(user.idnumber);
}


/**
 * Create the html of a list with diseases user
 * @param user
 * @param div
 */
function createListDiseasesUser(user, div) {
    var str = "<ul id='listDiseasesUser'>";
    for(var i = 0; i < user.contagions.length; i++) {
        str += "<li id='list" + i + "' style='font-size:16px';>" + user.contagions[i].disease.name +
            "<a class='btn btn-success btn-sm' id='btnCurar' onclick='confirmCuredDisease(" + user.user_id + ", " + user.contagions[i].contagion_id + ", " + i + ")'>Curar</a></li>";
    }
    str += "</ul>";

    div.innerHTML += str;
}


function confirmCuredDisease(idUser, idContagion, id_li) {

    var aux = "list" + id_li;
    var len = $('#listDiseasesUser').children().length;
    var data = '{"user_contagion":{ "user_id":"' + idUser + '",' + '"contagion_id":"' + idContagion + '"}}'; // Creamos el txt con el json
    //data = JSON.stringify(userData); // Verificamos que el formato es json --> OJO, no usar!!
    var apiURL = ENTRYPOINT + "users/contagions/";

    return $.ajax({
        url: apiURL,
        type: "DELETE",
        data: data
    }).done(function (data, textStatus, jqXHR) {
        var aux_li = document.getElementById(aux);
        aux_li.parentNode.removeChild(aux_li);
        personalAlert("AVISO  ", " --  El paciente ya no está contagiado de esta enfermedad", "warning", 1000, false);

        //si la tabla esta vacia cambiar el estado y ocultar enfermedades
        if(len == 1) {
            var estado = document.getElementById("estado");
            var pac = document.getElementById("paciente").innerHTML;

            estado.innerHTML = "<h6 id='estado'>Estado general: <strong>" + "CURADO" + "</strong></h6>";

            personalAlert("AVISO  ", " --  Se ha cambiado el estado del paciente " + pac + " a " + "CURADO", "warning", 2000, false);

            var del = document.getElementById("titEnf");
            del.remove();

        }
    }).fail(function (jqXHR, textStatus, errorThrown) {
        //alert("Error al buscar usuario.");
        personalAlert("ERROR  ", " --  Error al confirmar cura de contagio.", "danger", 2000, false);
    });

}


function confirmDead(dni_user) {

    var data = '{"user":{ "new_status":"dead"}}'; // Creamos el txt con el json
    var apiURL = ENTRYPOINT + "user/" + dni_user + "/";

    return $.ajax({
        url: apiURL,
        type: "PUT",
        data:data
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        var estado = document.getElementById("estado");
        estado.innerHTML = "<h6 id='estado'>Estado general: <strong>" + "FALLECIDO" + "</strong></h6>";
        personalAlert("AVISO IMPORTANTE ", " --  El estado del paciente ha sido modificado a FALLECIDO ", "warning", 2000, false);
        var del = document.getElementById("titEnf");
        var del2 = document.getElementById("listDiseasesUser");
        if((del != null) && (del2 != null)) {
            del.remove();
            del2.remove();
        }
    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        personalAlert("ERROR  ", " --  Error al cambiar el estado del paciente", "danger", 2000, false);
    });
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
        personalAlert("ERROR  ", " --  Formato de DNI incorrecto", "danger", 2000, false);
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
        personalAlert("ERROR  ", " -- Usuario no encontrado", "danger", 2000, false);
    });
}



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
        data:userData
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
function loadContagions() {

    personalAlert("CARGANDO  ", " --  Cargando contagios activos...Generando mapa de calor", "info", 2500, true);

    var apiurl = ENTRYPOINT + "contagions?type=contagions";
    getContagions(apiurl);
}


/**
 * function that call to other function chosen to give back the active contagions from the db reloading the data and cleaning the previous data.
 */
function reLoadContagions() {
    $("#contagiosList").empty();//vaciamos la lista actual y recargamos
    loadContagions();
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

        var new_apiurl = ENTRYPOINT + "contagions/?type=coordinates";
        loadMap(new_apiurl);

        for (var i=0; i < data.length; i++){
            addContagioToList(data[i]);
        }
    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        personalAlert("ERROR  ", " --  No se han podido cargar los contagios activos", "danger", 2000, false);
    });
}


/**
 * add a new contagion to the list of the html
 * @param contagio
 * @returns {string}
 */
function addContagioToList(contagio) {

    var type;

    switch(Number(contagio.level)) {
        case 1:
            type = '<span class="sm-box bgyellow2" >' + '</span>';
            break;
        case 2:
            type = '<span class="sm-box bgorange2" >' + '</span>';
            break;
        case 3:
            type = '<span class="sm-box bgred2" >' + '</span>';
            break;
        default:
            type = '<span class="sm-box bgblue2" >' + '</span>';
            break;
    }


    var aux_li = contagio.contagion_id //+ "-" + contagio.disease_id + "-" + contagio.doctor_id;
    var aux_btn = "btn-" + aux_li;
    var aux_separator = " || ";

    var $contagio = '<li id="' + aux_li + '">' + type +
        '<span class="leyenda">' + contagio.disease.name + aux_separator + contagio.place + aux_separator +
        'Nivel: ' + contagio.level + aux_separator + 'Contagios: ' + contagio.disease.num_contagions + aux_separator + 'Muertes: ' + contagio.disease.num_deaths + '</span>' +
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

    var apiurl = ENTRYPOINT + "contagion/" + id + "/"; //cuidado con la barra final
    //la eliminamos de verdad poniendo el nivel a 0 -->se eliminara de manera definitiva con un trigger pasado un tiempo
    removeContagion_db(apiurl); //eliminamos de la bd poniendo el nivel a 0 (no se elimina)

    //var aux = id.toString().split("-"); problema con id, parametro no coge guiones

    var li = document.getElementById(id);
    li.parentNode.removeChild(li);

    reLoadContagions();
}


/**
 * remove or set the level of the contagion to 0 and delete the contagion from the db
 * @param apiurl
 * @returns {*}
 */
function removeContagion_db(apiurl) {
    return $.ajax({
        url: apiurl,
        type: "PUT"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        personalAlert("ÉXITO  ", " --  Contagio eliminado de la lista. NIVEL = 0", "success", 1000, false);
    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        personalAlert("ERROR  ", " --  No se ha podido eliminar el contagio", "danger", 2000, false);
    });
}


/**
 * this function prints the initial state of the user div
 * It is used when a new search fails
 */
function showEmptyUser(dni_user) {

    var divUser = document.getElementById("infoUsuario");

    var html = '<img id="plantillafondo" src="assets/img/plantilla1.png" alt="Usuario no especificado">' +
        '<div class="panel-footer text-muted">Aquí se mostrará la información del paciente una vez realizada la búsqueda</div>';

    divUser.innerHTML = html;

    createButtonsUser(dni_user);

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
function showButtonsUser(dni_user) {
    createButtonsUser(dni_user);
    $("#botonesPanelUsuario").show();
}


function createButtonsUser(dni_user) {
    //var aux = "'" + dni_user + "'";
    var div = document.getElementById("botonesPanelUsuario");
    var str = '<a class="btn btn-warning" onclick="confirmDead(' + "'" + dni_user + "'" + ')"> Usuario fallecido</a>' +
    "<a class='btn btn-send' onclick='showMessage()'><span class='glyphicon glyphicon-envelope'></span> Enviar mensaje </a>";

    div.innerHTML = str;
}


/**
 * give back the coordenates of the active contagions
 * @param apiurl
 * @returns {*}
 */
function loadMap(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json",
        type: "GET"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        initMap(data);
    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        personalAlert("ERROR  ", " --  No se han podido obtener las coordenadas de los lugares contagiados", "danger", 2000, false);
    });
}


/**
 * create the map
 * @param data
 */
function initMap(data) {

    var map, heatmap;

    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 5,
        center: {lat: 40.489, lng: -3.682}, //CENTRO DEL MAPA MADRID
        mapTypeId: google.maps.MapTypeId.TERRAIN, //TERRAIN or ROADMAP
        zoomControl: false,
        scaleControl: false,
        streetViewControl: true
        //rotateControl: true
    });

    heatmap = new google.maps.visualization.HeatmapLayer({
        data: getPoints(data),
        map: map
    });
}


// Heatmap data example: barcelona y madrid
/**
 * put the points in data into a correct format to paint them in the map
 * @param data
 * @returns {Array}
 */
function getPoints(data) {

    var loc = [];
    var latLng;

    for (var i=0; i < data.length; i++){
        latLng =  new google.maps.LatLng(data[i].lat, data[i].lng);
        loc.push(latLng);
    }

    return loc;

    /*return [
        new google.maps.LatLng(40.458820, -3.698383), //tetuan
        new google.maps.LatLng(40.398852, -3.710394), //arganzuela
        new google.maps.LatLng(40.372771, -3.728498), //carabanchel
        new google.maps.LatLng(40.363859, -3.758456), //peseta
        new google.maps.LatLng(40.330053, -3.771248), //leganes
        new google.maps.LatLng(40.340892, -3.837245), //alcorcon
        new google.maps.LatLng(41.403608, 2.188574), //la sagrada familia
        new google.maps.LatLng(41.391715, 2.166115), //ensanche barcelona
        new google.maps.LatLng(41.356223, 2.147619), //montjuic
        new google.maps.LatLng(40.369044, -4.340382) //pelayos
    ];*/
}

 // KEY_API   --->>   AIzaSyDqC2mEHCJ98RqjUjyVKWIF7Y67y9aUaBU
