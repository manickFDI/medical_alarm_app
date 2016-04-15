/**
 * Created by Manuel on 7/04/16.
 */

var ENTRYPOINT = "http://localhost:5000/malarm/api/";

var DEBUG = true;
var WHITE_SPACE = " ";

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
        alert("Entrada incorrecta (FORMATO INCORRECTO)");
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
        alert ("Error al obtener enfermedad");
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
    generateTitle(ref_titulo, enfermedad);
    generateTable(ref_tabla, enfermedad);
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

    //completar con los datos de la enfermedad
    var tabla = '<table class="table table-striped table-condensed"><thead><th>Nº contagiados</th><th>Nº muertes</th><th>Erradicada</th><th>Peso medio</th></thead>' +
        '<tbody><tr><td>' + enfermedad.numcontagions + '</td><td>' + enfermedad.numdeaths + '</td><td><span class="label label-success">Erradicada</td></span><td>' +
        enfermedad.weight + ' Kg</td></tr></tbody></table>';

    ref.innerHTML = tabla;
}



/**
 * generate the graphics for the web. Use c3.js and d3.js with many types of graphics
 */

function generate() {}

var storedData = [
            {"Java": 2, "Python": 3}
];

    var chart = c3.generate({
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
    });

    var bar1 = c3.generate({
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
    });

    var bar2 = c3.generate({
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
    });


    var bar3 = c3.generate({
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
    });

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
    });
