/**
 * Created by Manuel on 7/04/16.
 */

var ENTRYPOINT = "http://localhost:5000/malarm/api/";

var DEBUG = true;
var WHITE_SPACE = " ";

window.onload = load();//function that is called when the page is loaded (general graphics)


/**
 * load all the graphics from the main tab
 */
function load() {
    var apiurl_c = ENTRYPOINT + 'diseases/?type=c&top=5';
    var apiurl_d = ENTRYPOINT + 'diseases/?type=d&top=5';
    var apiurl_u = ENTRYPOINT + 'users/?type=status';

    personalAlert("CARGANDO  ", " --  Generando resultados...Cargando gráficas", "info", 2000, true);

    rankingDead_db(apiurl_d);
    rankingContagion_db(apiurl_c);
    allUsers(apiurl_u);
}



/**
 * search a disease as a result of the searcher
 */
function searchDisease() {
    var enfermedad = document.getElementById("buscadorEnf").value;
    if(correctInput(enfermedad)) {
        //alert("Entrada correcta");
        var apiurl = ENTRYPOINT + 'disease/' + enfermedad;
        getDisease_db(apiurl);
    }
    else {
        personalAlert("ERROR  ", " --  Entrada incorrecta (FORMATO INCORRECTO)", "danger", 2000, false);
    }
}


/**
 * obtain a disease from the db with the name of the disease. It communicate with the rest_API
 * @param apiurl
 * @returns {*}
 */
function getDisease_db(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }

        prepareTable();

        var ref_titulo = document.getElementById("tituloEnf");
        var ref_tabla = document.getElementById("tabla");

        completeDisease(ref_titulo, ref_tabla, data);

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        showEmptyDisease();
        personalAlert("ERROR  ", " --  Enfermedad no encontrada", "danger", 2000, false);
    });
}



/**
 * this function prints the initial state of the disease div
 * It is used when a new search fails
 */
function showEmptyDisease() {
    var ref_enfermedad = document.getElementById("enfermedad");

    var html = '<img id="plantillafondo2" src="assets/img/logo.jpg" alt="Enfermedad no especificada">' +
        '<div class="panel-footer text-muted" id="footerFoto">Aquí se mostrarán los datos de la enfermedad solicitada</div>';

    ref_enfermedad.innerHTML = html;

    if ( $("#contagiosSexo").length && $("#contagiosEdad").length ) {
        $("#contagiosSexo").hide();
        $("#contagiosEdad").hide();
        $("#tituloDisp").hide();
        $("#disper1").hide();
        $("#disper2").hide();
    }
}



/**
 * prepare the titles and the divs to complete them with the information of the disease
 */
function prepareTable() {
    var ref_enfermedad = document.getElementById("enfermedad");
    var html = '<div class="card-content" id="tituloEnf"></div><div class="card-content" id="datosEnf"><div class="span5" id="tabla"></div></div>';
    ref_enfermedad.innerHTML = html;
}



/**
 * show the information of the searched disease. It controls the html page
 * @param ref_titulo
 * @param ref_tabla
 * @param enfermedad
 */

function completeDisease(ref_titulo, ref_tabla, enfermedad) {

    $("#enfermedad").show(500);
    $("#contagiosSexo").show(1000);
    $("#contagiosEdad").show(1000);
    $("#tituloDisp").show(500);
    $("#disper1").show(1000);
    $("#disper2").show(1000);
    generateTitle(ref_titulo, enfermedad);
    generateTable(ref_tabla, enfermedad);
    generateGraphicsDisease(enfermedad);
}



/**
 * generate the title of the main page of the disease in the html
 * @param ref
 * @param enfermedad
 */
function generateTitle(ref, enfermedad) {
    var tituloFormat =  '<h4 class="page-head-line">DATOS DE LA ENFERMEDAD ' +  '(' + enfermedad.name + ')</h4>';
    ref.innerHTML = tituloFormat;
}


/**
 * generate the table with the information of the disease searched by the doctor
 * @param ref
 * @param enfermedad
 */
function generateTable(ref, enfermedad) {

    if(String(enfermedad.eradicated) == "yes") { //str1 > str2 --> number > 0
        //completar con los datos de la enfermedad
        var tabla = '<table class="table table-striped table-condensed"><thead><th>Nº contagiados</th><th>Nº muertes</th><th>Erradicada</th><th>Peso medio</th></thead>' +
            '<tbody><tr><td>' + enfermedad.numcontagions + '</td><td>' + enfermedad.numdeaths + '</td><td><span class="label label-success">Erradicada</td></span><td>' +
            enfermedad.weight + ' Kg</td></tr></tbody></table>';
    } else { //no erradicada
        var tabla = '<table class="table table-striped table-condensed"><thead><th>Nº contagiados</th><th>Nº muertes</th><th>Erradicada</th><th>Peso medio</th></thead>' +
            '<tbody><tr><td>' + enfermedad.numcontagions + '</td><td>' + enfermedad.numdeaths + '</td><td><span class="label label-danger"> No erradicada</td></span><td>' +
            enfermedad.weight + ' Kg</td></tr></tbody></table>';
    }
    ref.innerHTML = tabla;
}


/**
 * Comunicates with the db to obtain the ranking of the deads
 * @param apiurl with the complete url of the API
 * @returns {*}
 */
function rankingDead_db(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }

        generateGraphicsTopDeads(data); //c3.js

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        personalAlert("ERROR  ", " --  No se pudo obtener el ranking de TOP-MUERTES", "danger", 2000, false);
    });
}



/**
 * Comunicates with the db to obtain the ranking of the contagions
 * @param apiurl with the complete url of the API
 * @returns {*}
 */
function rankingContagion_db(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }

        generateGraphicsTopContagions(data); //c3.js

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        personalAlert("ERROR  ", " --  No se pudo obtener el ranking de TOP-CONTAGIOS", "danger", 2000, false);
    });
}



/**
 * Comunicates with the db to obtain the number of eack kind of users of the system
 * @param apiurl with the complete url of the API
 * @returns {*}
 */
function allUsers(apiurl) {
    return $.ajax({
        url: apiurl,
        dataType:"json"
    }).done(function (data, textStatus, jqXHR){
        if (DEBUG) {
            console.log ("RECEIVED RESPONSE: data:",data,"; textStatus:",textStatus)
        }

        generateGraphicsAllUsers(data);

    }).fail(function (jqXHR, textStatus, errorThrown){
        if (DEBUG) {
            console.log ("RECEIVED ERROR: textStatus:",textStatus, ";error:",errorThrown)
        }
        personalAlert("ERROR  ", " --  No se pudo obtener el grafico del estado de todos los usuarios", "danger", 2000, false);
    });
}



/**
 * Is encargated of generate the diagram of the diseases top ranking
 * @param data the information of the disease (json)
 */
function generateGraphicsDisease(data) {

    var aux_h = Number(((data.nummen * 100) / data.numcontagions).toFixed(2));
    var aux_m = Number((100 - aux_h).toFixed(2));

    var aux_n = Number(((data.numchildren * 100) / data.numcontagions).toFixed(2));
    var aux_t = Number(((data.numteenagers * 100) / data.numcontagions).toFixed(2));
    var aux_a = Number(((data.numadults * 100) / data.numcontagions).toFixed(2));
    var aux_v = Number(100 - (aux_n + aux_a + aux_t));


    var chart = c3.generate({
        size: {
            height: 240,
            width: 480
        },
        bindto: '#pie',
        data: {
            // Con esto podríamos crear un grafico a mano
            columns: [
                ['data1', aux_h],
                ['data2', aux_m]
            ],
            names: {
                data1: 'Hombres',
                data2: 'Mujeres'
                //data3: 'Python'
            },
            // Con esta opcion vamos a tratar de hacerlo dinámico
            //json: storedData,
            //keys: {
            //value: ['Java', 'Python', 'Ruby']
            //},
            type : 'pie',
            // Descomentar esto para jugar con los colores
            colors: {
                data1: '#CECB0E',
                data2: '#DF6C07'
                //data3: '#79d3c2'
            }
        }
    });

    var chart = c3.generate({
        size: {
            height: 240,
            width: 480
        },
        bindto: '#donut',
        data: {
            // Con esto podríamos crear un grafico a mano
            columns: [
                ['data1', aux_n],
                ['data2', aux_t],
                ['data3', aux_a],
                ['data4', aux_v]
            ],
            names: {
                data1: 'Niños',
                data2: 'Jóvenes',
                data3: 'Adultos',
                data4: 'Ancianos'
            },
            // Con esta opcion vamos a tratar de hacerlo dinámico
            //json: storedData,
            //keys: {
            //value: ['Java', 'Python', 'Ruby']
            //},
            type : 'donut',
            // Descomentar esto para jugar con los colores
            colors: {
                data1: '#CECB0E',
                data2: '#F09E05',
                data3: '#DF6C07',
                data4: '#A95105'
            }
        }
    });


    /////

    var disp1 = c3.generate({
        data: {
            xs: {
                setosa: 'setosa_x',
                versicolor: 'versicolor_x'
            },
            // iris data from R
            columns: [
                ["setosa_x", 3.5, 3.0, 3.2, 3.1, 3.6, 3.9, 3.4, 3.4, 2.9, 3.1, 3.7, 3.4, 3.0, 3.0, 4.0, 4.4, 3.9, 3.5, 3.8, 3.8, 3.4, 3.7, 3.6, 3.3, 3.4, 3.0, 3.4, 3.5, 3.4, 3.2, 3.1, 3.4, 4.1, 4.2, 3.1, 3.2, 3.5, 3.6, 3.0, 3.4, 3.5, 2.3, 3.2, 3.5, 3.8, 3.0, 3.8, 3.2, 3.7, 3.3],
                ["versicolor_x", 3.2, 3.2, 3.1, 2.3, 2.8, 2.8, 3.3, 2.4, 2.9, 2.7, 2.0, 3.0, 2.2, 2.9, 2.9, 3.1, 3.0, 2.7, 2.2, 2.5, 3.2, 2.8, 2.5, 2.8, 2.9, 3.0, 2.8, 3.0, 2.9, 2.6, 2.4, 2.4, 2.7, 2.7, 3.0, 3.4, 3.1, 2.3, 3.0, 2.5, 2.6, 3.0, 2.6, 2.3, 2.7, 3.0, 2.9, 2.9, 2.5, 2.8],
                ["setosa", 0.2, 0.2, 0.2, 0.2, 0.2, 0.4, 0.3, 0.2, 0.2, 0.1, 0.2, 0.2, 0.1, 0.1, 0.2, 0.4, 0.4, 0.3, 0.3, 0.3, 0.2, 0.4, 0.2, 0.5, 0.2, 0.2, 0.4, 0.2, 0.2, 0.2, 0.2, 0.4, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.2, 0.2, 0.3, 0.3, 0.2, 0.6, 0.4, 0.3, 0.2, 0.2, 0.2, 0.2],
                ["versicolor", 1.4, 1.5, 1.5, 1.3, 1.5, 1.3, 1.6, 1.0, 1.3, 1.4, 1.0, 1.5, 1.0, 1.4, 1.3, 1.4, 1.5, 1.0, 1.5, 1.1, 1.8, 1.3, 1.5, 1.2, 1.3, 1.4, 1.4, 1.7, 1.5, 1.0, 1.1, 1.0, 1.2, 1.6, 1.5, 1.6, 1.5, 1.3, 1.3, 1.3, 1.2, 1.4, 1.2, 1.0, 1.3, 1.2, 1.3, 1.3, 1.1, 1.3]
            ],
            type: 'scatter'
        },
        size: {
            height: 340,
            width: 500
        },
        bindto: '#disp1',
        axis: {
            x: {
                label: 'Sepal.Width',
                tick: {
                    fit: false
                }
            },
            y: {
                label: 'Petal.Width'
            }
        }
    });

    var disp2 = c3.generate({
        data: {
            xs: {
                setosa: 'setosa_x',
                versicolor: 'versicolor_x'
            },
            // iris data from R
            columns: [
                ["setosa_x", 3.5, 3.0, 3.2, 3.1, 3.6, 3.9, 3.4, 3.4, 2.9, 3.1, 3.7, 3.4, 3.0, 3.0, 4.0, 4.4, 3.9, 3.5, 3.8, 3.8, 3.4, 3.7, 3.6, 3.3, 3.4, 3.0, 3.4, 3.5, 3.4, 3.2, 3.1, 3.4, 4.1, 4.2, 3.1, 3.2, 3.5, 3.6, 3.0, 3.4, 3.5, 2.3, 3.2, 3.5, 3.8, 3.0, 3.8, 3.2, 3.7, 3.3],
                ["versicolor_x", 3.2, 3.2, 3.1, 2.3, 2.8, 2.8, 3.3, 2.4, 2.9, 2.7, 2.0, 3.0, 2.2, 2.9, 2.9, 3.1, 3.0, 2.7, 2.2, 2.5, 3.2, 2.8, 2.5, 2.8, 2.9, 3.0, 2.8, 3.0, 2.9, 2.6, 2.4, 2.4, 2.7, 2.7, 3.0, 3.4, 3.1, 2.3, 3.0, 2.5, 2.6, 3.0, 2.6, 2.3, 2.7, 3.0, 2.9, 2.9, 2.5, 2.8],
                ["setosa", 0.2, 0.2, 0.2, 0.2, 0.2, 0.4, 0.3, 0.2, 0.2, 0.1, 0.2, 0.2, 0.1, 0.1, 0.2, 0.4, 0.4, 0.3, 0.3, 0.3, 0.2, 0.4, 0.2, 0.5, 0.2, 0.2, 0.4, 0.2, 0.2, 0.2, 0.2, 0.4, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.2, 0.2, 0.3, 0.3, 0.2, 0.6, 0.4, 0.3, 0.2, 0.2, 0.2, 0.2],
                ["versicolor", 1.4, 1.5, 1.5, 1.3, 1.5, 1.3, 1.6, 1.0, 1.3, 1.4, 1.0, 1.5, 1.0, 1.4, 1.3, 1.4, 1.5, 1.0, 1.5, 1.1, 1.8, 1.3, 1.5, 1.2, 1.3, 1.4, 1.4, 1.7, 1.5, 1.0, 1.1, 1.0, 1.2, 1.6, 1.5, 1.6, 1.5, 1.3, 1.3, 1.3, 1.2, 1.4, 1.2, 1.0, 1.3, 1.2, 1.3, 1.3, 1.1, 1.3]
            ],
            type: 'scatter'
        },
        size: {
            height: 340,
            width: 500
        },
        bindto: '#disp2',
        axis: {
            x: {
                label: 'Sepal.Width',
                tick: {
                    fit: false
                }
            },
            y: {
                label: 'Petal.Width'
            }
        }
    });

    var storedData = [{
        "edad": "23",
        "peso": "82",
        "sexo": "1"
    }, {
        "edad": "55",
        "peso": "72",
        "sexo": "1"
    }, {
        "edad": "14",
        "peso": "55",
        "sexo": "1"
    }];

    var colors = ['#FA0303'];

    var disp3 = c3.generate({
        /*data: {
            xs: {
                setosa: 'setosa_x',
                versicolor: 'versicolor_x'
            },
            // iris data from R
            columns: [
                ["setosa_x", 3.5, 3.0, 3.2, 3.1, 3.6, 3.9, 3.4, 3.4, 2.9, 3.1, 3.7, 3.4, 3.0, 3.0, 4.0, 4.4, 3.9, 3.5, 3.8, 3.8, 3.4, 3.7, 3.6, 3.3, 3.4, 3.0, 3.4, 3.5, 3.4, 3.2, 3.1, 3.4, 4.1, 4.2, 3.1, 3.2, 3.5, 3.6, 3.0, 3.4, 3.5, 2.3, 3.2, 3.5, 3.8, 3.0, 3.8, 3.2, 3.7, 3.3],
                ["versicolor_x", 3.2, 3.2, 3.1, 2.3, 2.8, 2.8, 3.3, 2.4, 2.9, 2.7, 2.0, 3.0, 2.2, 2.9, 2.9, 3.1, 3.0, 2.7, 2.2, 2.5, 3.2, 2.8, 2.5, 2.8, 2.9, 3.0, 2.8, 3.0, 2.9, 2.6, 2.4, 2.4, 2.7, 2.7, 3.0, 3.4, 3.1, 2.3, 3.0, 2.5, 2.6, 3.0, 2.6, 2.3, 2.7, 3.0, 2.9, 2.9, 2.5, 2.8],
                ["setosa", 0.2, 0.2, 0.2, 0.2, 0.2, 0.4, 0.3, 0.2, 0.2, 0.1, 0.2, 0.2, 0.1, 0.1, 0.2, 0.4, 0.4, 0.3, 0.3, 0.3, 0.2, 0.4, 0.2, 0.5, 0.2, 0.2, 0.4, 0.2, 0.2, 0.2, 0.2, 0.4, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.2, 0.2, 0.3, 0.3, 0.2, 0.6, 0.4, 0.3, 0.2, 0.2, 0.2, 0.2],
                ["versicolor", 1.4, 1.5, 1.5, 1.3, 1.5, 1.3, 1.6, 1.0, 1.3, 1.4, 1.0, 1.5, 1.0, 1.4, 1.3, 1.4, 1.5, 1.0, 1.5, 1.1, 1.8, 1.3, 1.5, 1.2, 1.3, 1.4, 1.4, 1.7, 1.5, 1.0, 1.1, 1.0, 1.2, 1.6, 1.5, 1.6, 1.5, 1.3, 1.3, 1.3, 1.2, 1.4, 1.2, 1.0, 1.3, 1.2, 1.3, 1.3, 1.1, 1.3]
            ],
            type: 'scatter'
        },*/

        data: {
            // Con esta opcion vamos a tratar de hacerlo dinámico
            json: storedData,
            keys: {
                x: 'edad',
                value: ['peso']
            },
            type : 'scatter',
            labels: true
            // Descomentar esto para jugar con los colores
            /*color: function (color, d) {
                return colors[d.index];
            }*/
        },
        color: {
            pattern: ['#FA0303']
        },
        size: {
            height: 340,
            width: 1080
        },
        bindto: '#disp3',
        axis: {
            x: {
                label: 'Edad'
            },
            y: {
                label: 'Peso'
            }
        }
    });
}



/**
 * Is encargated of generate the diagram of the deads top ranking
 * @param data the information of the system (json)
 */
function generateGraphicsTopDeads(data) {

    var storedData = data;

    var colors = ['#7710AA', '#3920DE', '#208CDE', '#009688', '#20DBDE'];

    var bar1 = c3.generate({
        size: {
            height: 240,
            width: 480
        },
        bindto: '#bar1',
        data: {
            // Con esta opcion vamos a tratar de hacerlo dinámico
            json: storedData,
            keys: {
                x: 'name',
                value: ["num_deaths"]
            },
            type : 'bar',
            labels: true,
            // Descomentar esto para jugar con los colores
            color: function (color, d) {
                return colors[d.index];
            }
        },
        axis: {
            x: {
                type: 'category'
            },
            y: {
                label: 'Muertes'
            }
        },
        legend: {
            show:false
        }
    });
}



/**
 * Is encargated of generate the diagram of the contagions top ranking
 * @param data the information of the system (json)
 */
function generateGraphicsTopContagions(data) {

    var storedData = data;

    var colors = ['#135802','#1EB241','#23D009', '#87D009', '#B0FDAE'];

    var bar2 = c3.generate({
        size: {
            height: 240,
            width: 480
        },
        bindto: '#bar2',
        data: {
            // Con esta opcion vamos a tratar de hacerlo dinámico
            json: storedData,
            keys: {
                x: 'name',
                value: ["num_contagions"]
            },
            type : 'bar',
            labels: true,
            // Descomentar esto para jugar con los colores
            color: function (color, d) {
                return colors[d.index];
            }
        },
        axis: {
            x: {
                type: 'category'
            },
            y: {
                label: 'Contagios'
            }
        },
        legend: {
            show:false
        }
    });
            /*// Con esta opcion vamos a tratar de hacerlo dinámico
            json: storedData,
            keys: {
                x: 'name',
                value: ['num_contagions']
            },
            type : 'bar',
            labels: true,
            // Descomentar esto para jugar con los colores
            color: function (color, d) {
                return colors[d.index];
            }
        },
        axis: {
            x: {
                type: 'category'
            },
            y: {
                label: 'Contagios'
            }
        },
        legend: {
            show:false
        }
    });*/
}


/**
 * Is encargated of generate the diagram of the diseases top ranking
 * @param data the information of the system (json)
 */
function generateGraphicsAllUsers(data) {

    var storedData = data;

    var bar3 = c3.generate({
        size: {
            height: 340,
            width: 1080
        },
        bindto: '#bar3',
        data: {
            // Con esto podríamos crear un grafico a mano
            columns: [
                ['data1', data.undefined],
                ['data2', data.healthy],
                ['data3', data.cured],
                ['data4', data.infected],
                ['data5', data.dead]
            ],
            names: {
                data1: 'Indefinido',
                data2: 'Sano',
                data3: 'Curado',
                data4: 'Enfermo',
                data5: 'Fallecido'
            },
            // Con esta opcion vamos a tratar de hacerlo dinámico
            //json: storedData,
            //keys: {
            //value: ['Ébola', 'Gripe A', 'Tuberculosis']
            // },
            type : 'bar',
            labels: true,
            // Descomentar esto para jugar con los colores
            colors: {
                data1: '#F7FA26',
                data2: '#fcad02',
                data3: '#DABB0B',
                data4: '#DA8E0B',
                data5: '#754306'
            }
        },
        axis: {
            x: {
                type: 'category',
                categories: ['Estado']
            },
            y: {
                label: 'Nº personas'
            }
        }
    });
}









/**
 * generate the graphics for the web. Use c3.js and d3.js with many types of graphics
 */

function generate() {}

    /*var chart = c3.generate({
        size: {
        height: 240,
        width: 480
        },
    bindto: '#pie',
    data: {
        // Con esto podríamos crear un grafico a mano
        columns: [
        ['data1', 60],
        ['data2', 40]
        ],
        names: {
        data1: 'Hombres',
        data2: 'Mujeres'
        //data3: 'Python'
        },
    // Con esta opcion vamos a tratar de hacerlo dinámico
    //json: storedData,
    //keys: {
        //value: ['Java', 'Python', 'Ruby']
        //},
    type : 'pie',
    // Descomentar esto para jugar con los colores
    colors: {
        data1: '#CECB0E',
        data2: '#DF6C07'
        //data3: '#79d3c2'
        }
    }
    });

    var chart = c3.generate({
        size: {
        height: 240,
        width: 480
        },
    bindto: '#donut',
    data: {
        // Con esto podríamos crear un grafico a mano
        columns: [
        ['data1', 27],
        ['data2', 13],
        ['data3', 60]
        ],
        names: {
        data1: 'Niños',
        data2: 'Adultos',
        data3: 'Ancianos'
        },
    // Con esta opcion vamos a tratar de hacerlo dinámico
    //json: storedData,
    //keys: {
        //value: ['Java', 'Python', 'Ruby']
        //},
    type : 'donut',
    // Descomentar esto para jugar con los colores
    colors: {
        data1: '#CECB0E',
        data2: '#F09E05',
        data3: '#DF6C07'
        }
    }
    });*/

    /*var bar1 = c3.generate({
        size: {
        height: 240,
        width: 480
        },
    bindto: '#bar1',
    data: {
        // Con esto podríamos crear un grafico a mano
        columns: [
        ['data1', 180],
        ['data2', 120],
        ['data3', 27],
        ['data4', 5]
        ],
        names: {
        data1: 'Ébola',
        data2: 'Gripe A',
        data3: 'Tuberculosis',
        data4: 'Neumonía'
        },
    // Con esta opcion vamos a tratar de hacerlo dinámico
    //json: storedData,
    //keys: {
        //value: ['Ébola', 'Gripe A', 'Tuberculosis']
        // },
    type : 'bar',
    labels: true,
    // Descomentar esto para jugar con los colores
    colors: {
        data1: '#20DBDE',
        data2: '#208CDE',
        data3: '#3920DE',
        data4: '#7710AA'
        }
    },
    axis: {
        x: {
        type: 'category',
        categories: ['Enfermedad']
        },
    y: {
        label: 'Muertes'
        }
    }
    });*/

    /*var bar2 = c3.generate({
        size: {
        height: 240,
        width: 480
        },
    bindto: '#bar2',
    data: {
        // Con esto podríamos crear un grafico a mano
        columns: [
        ['data1', 2500],
        ['data2', 1000],
        ['data3', 167],
        ['data4', 99]
        ],
        names: {
        data1: 'Gripe',
        data2: 'Gripe A',
        data3: 'Legionella',
        data4: 'Varicela'
        },
    // Con esta opcion vamos a tratar de hacerlo dinámico
    //json: storedData,
    //keys: {
        //value: ['Ébola', 'Gripe A', 'Tuberculosis']
        // },
    type : 'bar',
    labels: true,
    // Descomentar esto para jugar con los colores
    colors: {
        data1: '#87D009',
        data2: '#23D009',
        data3: '#1EB241',
        data4: '#135802'
        }
    },
    axis: {
        x: {
        type: 'category',
        categories: ['Enfermedad']
        },
    y: {
        label: 'Contagios'
        }
    }
    });*/


    /*var bar3 = c3.generate({
        size: {
        height: 340,
        width: 1080
        },
    bindto: '#bar3',
    data: {
        // Con esto podríamos crear un grafico a mano
        columns: [
        ['data1', 55],
        ['data2', 21],
        ['data3', 432],
        ['data4', 600],
        ['data5', 16]
        ],
        names: {
        data1: 'Indefinido',
        data2: 'Sano',
        data3: 'Curado',
        data4: 'Enfermo',
        data5: 'Fallecido'
        },
    // Con esta opcion vamos a tratar de hacerlo dinámico
    //json: storedData,
    //keys: {
        //value: ['Ébola', 'Gripe A', 'Tuberculosis']
        // },
    type : 'bar',
    labels: true,
    // Descomentar esto para jugar con los colores
    colors: {
        data1: '#F7FA26',
        data2: '#fcad02',
        data3: '#DABB0B',
        data4: '#DA8E0B',
        data5: '#754306'
        }
    },
    axis: {
        x: {
        type: 'category',
        categories: ['Estado']
        },
    y: {
        label: 'Nº personas'
        }
    }
    });*/

    var line = c3.generate({
    size: {
        height: 340,
        width: 1080
    },
    bindto: '#line',
    data: {
        columns: [
            ['A', 25, 10, 465, 600, 41]
        ],
        names: {
            A: 'ESTADO'
        }
    }
});

/*var disp1 = c3.generate({
    data: {
        xs: {
            setosa: 'setosa_x',
            versicolor: 'versicolor_x'
        },
        // iris data from R
        columns: [
            ["setosa_x", 3.5, 3.0, 3.2, 3.1, 3.6, 3.9, 3.4, 3.4, 2.9, 3.1, 3.7, 3.4, 3.0, 3.0, 4.0, 4.4, 3.9, 3.5, 3.8, 3.8, 3.4, 3.7, 3.6, 3.3, 3.4, 3.0, 3.4, 3.5, 3.4, 3.2, 3.1, 3.4, 4.1, 4.2, 3.1, 3.2, 3.5, 3.6, 3.0, 3.4, 3.5, 2.3, 3.2, 3.5, 3.8, 3.0, 3.8, 3.2, 3.7, 3.3],
            ["versicolor_x", 3.2, 3.2, 3.1, 2.3, 2.8, 2.8, 3.3, 2.4, 2.9, 2.7, 2.0, 3.0, 2.2, 2.9, 2.9, 3.1, 3.0, 2.7, 2.2, 2.5, 3.2, 2.8, 2.5, 2.8, 2.9, 3.0, 2.8, 3.0, 2.9, 2.6, 2.4, 2.4, 2.7, 2.7, 3.0, 3.4, 3.1, 2.3, 3.0, 2.5, 2.6, 3.0, 2.6, 2.3, 2.7, 3.0, 2.9, 2.9, 2.5, 2.8],
            ["setosa", 0.2, 0.2, 0.2, 0.2, 0.2, 0.4, 0.3, 0.2, 0.2, 0.1, 0.2, 0.2, 0.1, 0.1, 0.2, 0.4, 0.4, 0.3, 0.3, 0.3, 0.2, 0.4, 0.2, 0.5, 0.2, 0.2, 0.4, 0.2, 0.2, 0.2, 0.2, 0.4, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.2, 0.2, 0.3, 0.3, 0.2, 0.6, 0.4, 0.3, 0.2, 0.2, 0.2, 0.2],
            ["versicolor", 1.4, 1.5, 1.5, 1.3, 1.5, 1.3, 1.6, 1.0, 1.3, 1.4, 1.0, 1.5, 1.0, 1.4, 1.3, 1.4, 1.5, 1.0, 1.5, 1.1, 1.8, 1.3, 1.5, 1.2, 1.3, 1.4, 1.4, 1.7, 1.5, 1.0, 1.1, 1.0, 1.2, 1.6, 1.5, 1.6, 1.5, 1.3, 1.3, 1.3, 1.2, 1.4, 1.2, 1.0, 1.3, 1.2, 1.3, 1.3, 1.1, 1.3]
        ],
        type: 'scatter'
    },
    size: {
        height: 340,
        width: 500
    },
    bindto: '#disp1',
    axis: {
        x: {
            label: 'Sepal.Width',
            tick: {
                fit: false
            }
        },
        y: {
            label: 'Petal.Width'
        }
    }
});

var disp2 = c3.generate({
    data: {
        xs: {
            setosa: 'setosa_x',
            versicolor: 'versicolor_x'
        },
        // iris data from R
        columns: [
            ["setosa_x", 3.5, 3.0, 3.2, 3.1, 3.6, 3.9, 3.4, 3.4, 2.9, 3.1, 3.7, 3.4, 3.0, 3.0, 4.0, 4.4, 3.9, 3.5, 3.8, 3.8, 3.4, 3.7, 3.6, 3.3, 3.4, 3.0, 3.4, 3.5, 3.4, 3.2, 3.1, 3.4, 4.1, 4.2, 3.1, 3.2, 3.5, 3.6, 3.0, 3.4, 3.5, 2.3, 3.2, 3.5, 3.8, 3.0, 3.8, 3.2, 3.7, 3.3],
            ["versicolor_x", 3.2, 3.2, 3.1, 2.3, 2.8, 2.8, 3.3, 2.4, 2.9, 2.7, 2.0, 3.0, 2.2, 2.9, 2.9, 3.1, 3.0, 2.7, 2.2, 2.5, 3.2, 2.8, 2.5, 2.8, 2.9, 3.0, 2.8, 3.0, 2.9, 2.6, 2.4, 2.4, 2.7, 2.7, 3.0, 3.4, 3.1, 2.3, 3.0, 2.5, 2.6, 3.0, 2.6, 2.3, 2.7, 3.0, 2.9, 2.9, 2.5, 2.8],
            ["setosa", 0.2, 0.2, 0.2, 0.2, 0.2, 0.4, 0.3, 0.2, 0.2, 0.1, 0.2, 0.2, 0.1, 0.1, 0.2, 0.4, 0.4, 0.3, 0.3, 0.3, 0.2, 0.4, 0.2, 0.5, 0.2, 0.2, 0.4, 0.2, 0.2, 0.2, 0.2, 0.4, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.2, 0.2, 0.3, 0.3, 0.2, 0.6, 0.4, 0.3, 0.2, 0.2, 0.2, 0.2],
            ["versicolor", 1.4, 1.5, 1.5, 1.3, 1.5, 1.3, 1.6, 1.0, 1.3, 1.4, 1.0, 1.5, 1.0, 1.4, 1.3, 1.4, 1.5, 1.0, 1.5, 1.1, 1.8, 1.3, 1.5, 1.2, 1.3, 1.4, 1.4, 1.7, 1.5, 1.0, 1.1, 1.0, 1.2, 1.6, 1.5, 1.6, 1.5, 1.3, 1.3, 1.3, 1.2, 1.4, 1.2, 1.0, 1.3, 1.2, 1.3, 1.3, 1.1, 1.3]
        ],
        type: 'scatter'
    },
    size: {
        height: 340,
        width: 500
    },
    bindto: '#disp2',
    axis: {
        x: {
            label: 'Sepal.Width',
            tick: {
                fit: false
            }
        },
        y: {
            label: 'Petal.Width'
        }
    }
});

var disp3 = c3.generate({
    data: {
        xs: {
            setosa: 'setosa_x',
            versicolor: 'versicolor_x'
        },
        // iris data from R
        columns: [
            ["setosa_x", 3.5, 3.0, 3.2, 3.1, 3.6, 3.9, 3.4, 3.4, 2.9, 3.1, 3.7, 3.4, 3.0, 3.0, 4.0, 4.4, 3.9, 3.5, 3.8, 3.8, 3.4, 3.7, 3.6, 3.3, 3.4, 3.0, 3.4, 3.5, 3.4, 3.2, 3.1, 3.4, 4.1, 4.2, 3.1, 3.2, 3.5, 3.6, 3.0, 3.4, 3.5, 2.3, 3.2, 3.5, 3.8, 3.0, 3.8, 3.2, 3.7, 3.3],
            ["versicolor_x", 3.2, 3.2, 3.1, 2.3, 2.8, 2.8, 3.3, 2.4, 2.9, 2.7, 2.0, 3.0, 2.2, 2.9, 2.9, 3.1, 3.0, 2.7, 2.2, 2.5, 3.2, 2.8, 2.5, 2.8, 2.9, 3.0, 2.8, 3.0, 2.9, 2.6, 2.4, 2.4, 2.7, 2.7, 3.0, 3.4, 3.1, 2.3, 3.0, 2.5, 2.6, 3.0, 2.6, 2.3, 2.7, 3.0, 2.9, 2.9, 2.5, 2.8],
            ["setosa", 0.2, 0.2, 0.2, 0.2, 0.2, 0.4, 0.3, 0.2, 0.2, 0.1, 0.2, 0.2, 0.1, 0.1, 0.2, 0.4, 0.4, 0.3, 0.3, 0.3, 0.2, 0.4, 0.2, 0.5, 0.2, 0.2, 0.4, 0.2, 0.2, 0.2, 0.2, 0.4, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.2, 0.2, 0.3, 0.3, 0.2, 0.6, 0.4, 0.3, 0.2, 0.2, 0.2, 0.2],
            ["versicolor", 1.4, 1.5, 1.5, 1.3, 1.5, 1.3, 1.6, 1.0, 1.3, 1.4, 1.0, 1.5, 1.0, 1.4, 1.3, 1.4, 1.5, 1.0, 1.5, 1.1, 1.8, 1.3, 1.5, 1.2, 1.3, 1.4, 1.4, 1.7, 1.5, 1.0, 1.1, 1.0, 1.2, 1.6, 1.5, 1.6, 1.5, 1.3, 1.3, 1.3, 1.2, 1.4, 1.2, 1.0, 1.3, 1.2, 1.3, 1.3, 1.1, 1.3]
        ],
        type: 'scatter'
    },
    size: {
        height: 340,
        width: 1080
    },
    bindto: '#disp3',
    axis: {
        x: {
            label: 'Sepal.Width',
            tick: {
                fit: false
            }
        },
        y: {
            label: 'Petal.Width'
        }
    }
});*/