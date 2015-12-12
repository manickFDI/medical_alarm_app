Git:
---

Ejecutar git clone <dir del repo> en el directorio que se quiera tener el proyecto.

* Para modificar el codigo de android, abrir el proyecto con android_app como raiz.
* Para modificar el codigo de la API RESTful, abrir el proyecto con api_rest como raiz.

Buenas practicas para usar git:

1. git status //Comprobar el estado
2. git fetch <remote> //Carga los cambios pero no los "integra"
3. git pull <remote> //Descarga en integra (puede que pida hacer un merge de los cambios)
4. Programar
5. git status
6. git add . Ó git add <file> Ó git add <dir> //Para decir que cambios se añadieron
7. git commit [-a] -m "comentario sobre los cambios, ser explicito" //Para decir que se subira
8. git push <remote>


NOTA: creo que el remote en nuestro caso sería master, porque creo que al final deberíamos trabajar en una rama salvo que vayamos a modificar mucho para una prueba.

ATENCION: Estos son los pasos que he aprendido, pero antes de tocar, mientras seamos "wannabies" deberíamos descargar a mano un zip del proyecto, por si acaso... Y avisar por burofax cuando vayamos a programar.

