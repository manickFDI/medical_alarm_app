/**
 * Created by Manuel on 23/04/16.
 */


/**
 * Create an personal alert message with a concrete params
 * @param title
 * @param message
 * @param type
 * @param delay
 * @param progress
 */
function personalAlert(title, message, type, delay, progress) {

    $.notify({
            // options
            //icon: 'glyphicon glyphicon-warning-sign',
            title: title,
            message: message
            //url: 'https://github.com/mouse0270/bootstrap-notify',
            //target: '_blank'
        },{
            // settings
            element: 'body',
            position: null,
            type: type,
            allow_dismiss: true,
            newest_on_top: false,
            showProgressbar: progress,
            placement: {
                from: "top",
                align: "center"
            },
            offset: 50,
            spacing: 10,
            z_index: 1031,
            delay: delay,
            timer: 1000,
            //url_target: '_blank',
            //mouse_over: null,
            animate: {
                enter: "animated fadeInDown",
                exit: "animated fadeOutUp"
            }
        }
    );
}



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
 * Verify if a string contains only numbers. This prevent SQL Injection
 * @param enfermedad
 * @returns {boolean}
 */
function correctInput(enfermedad) {
    var ret = false;
    var exp = /^[A-Za-z\-\.\s\xF1\xD1]+$/; //alfabetico con espacios

    if (exp.test(enfermedad)) {
        ret = true
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

