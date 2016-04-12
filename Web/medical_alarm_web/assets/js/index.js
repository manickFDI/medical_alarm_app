/**
 * Created by Manuel on 9/04/16.
 */

var ENTRYPOINT = "/malarm/api/users/";
var ENTRYPOINT_CONT = "/malarm/api/contagios/";

var DEBUG = true;

function validarDNI(dni) {
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

//data accediendo a los campos
function completarUsuario(div, user) {

    //user.nombre
    //user.dni ...

    div.innerHTML = "<h3>Manuel Martínez Sánchez</h3>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='dni'>DNI/NIE: 11868634-J</h6>";
    div.innerHTML += "<h6 id='email'>Email: manuma02@ucm.es</h6>";
    div.innerHTML += "<h6>Edad: 30 años</h6>";
    div.innerHTML += "<h6>Sexo: masculino</h6>";
    div.innerHTML += "<h6>Peso: 82 Kg</h6>";
    div.innerHTML += "<hr />";
    div.innerHTML += "<h6 id='estado'>Estado: <strong>INDEFINIDO</strong></h6>";
    div.innerHTML += "<hr />";
}


function obtenerUsuario() {
    var dni = document.getElementById("buscadorUsuario").value;
    if(validarDNI(dni)) {
        //alert(dni);
        var apiurl = ENTRYPOINT + '/dni';
        //pedirUsuario(apiurl);
        completarUsuario(document.getElementById("infoUsuario"), dni);
        mostrarBotonesUsuario();
    }
    else {
        alert("Error al introducir el DNI (FORMATO INCORRECTO)");
    }
}

function pedirUsuario(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
    }).always(function (data, textStatus, jqXHR){
        $("#infoUsuario").empty();

    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }

        var divUser = document.getElementById("infoUsuario");
        var user = data.collection.items; //debe haber uno
        var user_data = user.data;

        completarUsuario(divUser, user_data);

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        alert ("Error al obtener usuario")
    });
}

function cambiarEstado() {
    $('#estadosList li a').addEventListener('click', function(){

        var nuevoEstado = $(this).text().toUpperCase()
        var ret = confirm("¿Está seguro que desea cambiar el estado del paciente a " + nuevoEstado + "?");
        if (ret) {
            var estado = document.getElementById("estado");
            estado.innerHTML = "<h6 id='estado'>Estado: <strong>" + nuevoEstado + "</strong></h6>";

            //query UPDATE
            var apiurl = ENTRYPOINT + "/" + document.getElementById("email");
            //actualizarEstado(apiurl, nuevoEstado); //solo el estado o  el usuario entero
        }
    });
}


$('#estadosList li a').on('click', function(){

    var nuevoEstado = $(this).text().toUpperCase()
    var ret = confirm("¿Está seguro que desea cambiar el estado del paciente a " + nuevoEstado + "?");
    if (ret) {
        var estado = document.getElementById("estado");
        estado.innerHTML = "<h6 id='estado'>Estado: <strong>" + nuevoEstado + "</strong></h6>";

        //query UPDATE
        var apiurl = ENTRYPOINT + "/" + document.getElementById("email");
        //actualizarEstado(apiurl, nuevoEstado); //solo el estado o  el usuario entero
    }
});


function actualizarEstado(apiurl, userData) {
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

function getContagios() {
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

function addContagioToList(contagio) {
    //poner contagio.enfermedad ...

    var aux_li = "'enf_" + contagio + "'";
    var aux_btn = "enf_btn_" + contagio;

    var $contagio = '<li id="' + aux_li + '">' + '<span class="sm-box bggreen2" >' + '</span>' +
        '<span class="leyenda">Enfermedad: Gripe A || Madrid-Centro || Contagios: 12</span>' +
        '<a class="btn btn-info btn-xs" onclick="erradicarContagio(' + aux_li + ')" id="' + aux_btn + '">Erradicar </a>';

    //añadir a la lista cada contagio
    $("#contagiosList").append($contagio);
    return $contagio;
}

function ocultarMensaje() {
    $("#messageUser").hide(500);
}

function mostrarMensaje() {
    $("#messageUser").show(1000);
}

function mostrarBotonesUsuario() {
    $("#botonesPanelUsuario").show();
}

function contagios() {
    for (var j=0; j<4;j++){
        addContagioToList(j);
    }
}

function erradicarContagio(id) {
    var aux = "'" + id + "'"
    var li = document.getElementById(aux);
    li.parentNode.removeChild(li);
}