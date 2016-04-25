/**
 * Created by Manuel on 9/04/16.
 */

//var ENTRYPOINT = "http://localhost:5000/malarm/api/user/11868634J&callback=?";
var ENTRYPOINT = "http://localhost:8080/";
var HEATMAP = "heatmap";
var DISEASES = "diseases";

var DEBUG = true;
var WHITE_SPACE = " ";

/**
 * Complete the html that belongs to the user writing the information of the user in the specific div and the buttons of the message
 * @param div
 * @param user
 */

function writeDisease(div, disease) {
    var type;

    switch(Number(disease.level)) {
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

    var aux_separator = " || ";

    var $disease = '<li">' + type +
        '<span class="leyenda">' + disease.diseaseKey.name + aux_separator + disease.diseaseKey.location  + aux_separator +
        'Nivel: ' + disease.level + aux_separator + 
        'CDC: ' + disease.cdcCount + aux_separator + 
        'News: ' + disease.newsCount + aux_separator +
        'Twitter: ' + disease.tweetsCount + aux_separator +
        'Last update: ' + disease.lastUpdate + 
        '</span><br>';

    //añadir a la lista cada contagio
    $("#diseasesList").append($disease);
    return $disease;	
}


/**
 * obtain the user from the searcher. It search by the dni.
 */

function getDiseaseByZoneAndDate() {
    var zone = document.getElementById("zoneInput").value;
    var date = document.getElementById("dateInput").value;
    if(!validateDate(date) && date != "") {
        personalAlert("ERROR  ", " --  Date is not properly formatterd (correct format is dd/mm/yyyy)", "danger", 2000, false);
    }
    else {
    	var apiurl = ENTRYPOINT + DISEASES;
    	var params = false;
    	if(date != ""){
    		if(!params) apiurl = apiurl + "?";
    		apiurl = apiurl + "date=" + date;
    	}
    	if(zone != ""){
    		if(!params) apiurl = apiurl + "?";
    		else apiurl = apiurl + "&";
    		apiurl = apiurl + "zone=" + zone;
    	}

        getDiseasesByZoneAndDateFromDB(apiurl);
    }
}

function validateDate(date){
	var patt = new RegExp("^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[012])\/(19|20)\d\d$");
	return patt.test(date);
}

/**
 * Function that communicate with the rest_API to obtain the information of the user
 * @param apiurl
 * @returns {*}
 */

function getDiseasesByZoneAndDateFromDB(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
   }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        var divDiseases = document.getElementById("diseasesList");
        $("#diseasesList").empty();
        for (var i=0; i < data.diseases.length; i++){
            writeDisease(divDiseases, data.diseases[i]);
        }

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        $("#diseasesList").empty();
        personalAlert("ERROR  ", " -- No diseases found", "danger", 2000, false);
    });
}

/**
 * obtain the user from the searcher. It search by the dni.
 */

function getDiseaseByName() {
    var name = document.getElementById("nameInput").value;
	
	var apiurl = ENTRYPOINT + HEATMAP;
	if(name != "")
		apiurl = apiurl + "?" + name;

    getDiseasesByNameFromDB(apiurl);
}

/**
 * function that call to other function chosen to give back the active contagions from the db.
 */
function loadDiseases() {

    personalAlert("LOADING  ", " --  Cargando enfermedades activos...Generando mapa de calor", "info", 2500, true);

    var apiurl = ENTRYPOINT + HEATMAP;
    getDiseasesByNameFromDB(apiurl);
}


function getDiseasesByNameFromDB(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json",
        type: "GET"
   }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }
        var divDiseases = document.getElementById("diseasesList");
        $("#diseasesList").empty();
        initMap(data);

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        $("#diseasesList").empty();
        personalAlert("ERROR  ", " --  Cannot obtain coordinates of diseases", "danger", 2000, false);
    });
}


/**
 * create the map
 * @param data
 */
function initMap(data) {

    var map, heatmap;

    map = new google.maps.Map(document.getElementById('map'), {
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

    for (var i=0; i < data.heatPointList.length; i++){
        latLng =  new google.maps.LatLng(data.heatPointList[i].location.coordinates[1], data.heatPointList[i].location.coordinates[1]);
        loc.push(latLng);
    }

    return loc;
}

 // KEY_API   --->>   AIzaSyDqC2mEHCJ98RqjUjyVKWIF7Y67y9aUaBU