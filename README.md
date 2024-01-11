# Prueba Técnica - Agencia de Viajes

Credenciales para el empleado:
- **Usuario:** hackaboss
- **Contraseña:** 1234

Esta aplicación simula una agencia de viajes en la cual se pueden crear hoteles, vuelos, reservas de hoteles y de vuelos.

## Hoteles

### Crear un nuevo hotel

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/a54180d1-3d95-4e24-8f9c-777cec2c397a)

En este endpoint se permite crear un hotel en la base de datos mediante la siguiente request.

{
    "name": "Hotel Paraiso",
    "location": "Coruna",
    "roomPrice": 150.0,
    "roomType": "doble",
    "avaliableDateFrom": "2024-05-15",
    "avaliableDateTo": "2024-05-25"
}

Listar todos los hoteles 

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/9897d6b3-96bc-4074-bd4b-31e395e2ba7f)

Como podemos observar nos da todos los hoteles que hemos registrado mediante los endpoints y se encuentran en la base de datos.

Listar hoteles por un determinado rando de fechas, lugar y por su disponibilidad

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/6e7094f3-1dc5-41c7-8720-d7e73417c0b5)

Podemos observar que si le ponemos los datos correctos en el rango nos va a devolver el hotel correspondiente. Si por el caso alguno de los filtros estuviese mal puesto nos daria lo siguiente:

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/39c1d6eb-98cb-408e-8810-b6a4941d2d4f)

Se ha cambiado el booked de false a true, y podemos observar que ahora ya no nos aparece ninguno.

Obtener un hotel por su ID

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/77d285eb-59d2-469f-ab95-40f75fb7cec5)

Este metodo nos va a permitir obtener cualquier hotel por su ID, siempre que se encuentre en la base de datos. En caso de ser negativo, tendremos el siguiente error

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/21a6cb72-9ccc-4bea-92a9-c27597b019b9)

Actualizar el hotel

Este metodo nos permite actualizar la informacion de un hotel, para ello tenemos que estar autenticcados.

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/ec1453cf-3a09-4e54-8726-00950c242ff1)

Podemos observar que si obtenemos este hotel por su ID, ahora nos muestra la nueva informacion.
![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/07ec7bcb-73ec-43e8-9c0e-cb0b1329cac4)

Eliminacion de un hotel

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/cc90fa6d-4ebe-41f2-96eb-6bd9f5bf71e6)

Este metodo nos permite eliminar un hotel de manera logica, en la base de datos se encuentrar unas columnas a mayores y mediante unos numeros binarios (1 para true) y (0 para false) permite saber cual se encuentra en activo y cual esta eliminado.

![image](https://github.com/dmillanlpez/MillanDiego_pruebatec4/assets/97486464/1ca0adcd-ed59-49d1-a4fc-341da018dc73)

VUELOS 



