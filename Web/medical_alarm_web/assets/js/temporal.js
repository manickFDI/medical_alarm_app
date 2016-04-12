/**
 * Created by Manuel on 12/04/16.
 */


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