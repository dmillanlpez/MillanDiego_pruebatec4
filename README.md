# Prueba técnica - Agencia de viajes

**Credenciales para el empleado:**<br>
**- Usuario: hackaboss**<br>
**- Contrasena: 1234**

Se está desarrollando una aplicación que simula la gestión de una agencia de viajes. En esta aplicación, se manejan diferentes hoteles, y cada hotel tiene varios tipos de habitaciones, como individual, doble y triple. Según las instrucciones, se debe crear un hotel por cada tipo de habitación disponible en ese hotel. Por otro lado, los vuelos se crean solo de ida, si el usuario quiere otro vuelo de vuelta, tendrá que crear otro con otro código diferente al de ida con los nuevos datos de la reserva.

# Explicación de la aplicación
***
## EndPoints (APIs)

Explicación de cada uno de los endpoints de la aplicación

## Hoteles
****
## EndPoints:

### Método POST (Este método tiene que estar autenticado el empleado)
***

http://localhost:8080/agency/hotels/new

En este endpoint se van a generar todos los hoteles mediante la siguiente request:

```json
{

    "name": "Hotel Alda",
    "location": "Sada",
    "roomPrice": 80,
    "roomType": "Individual",
    "avaliableDateFrom": "2024-05-24",
    "avaliableDateTo": "2024-05-30"
}

```
En el contexto mencionado anteriormente, la estrategia consiste en generar un hotel para cada tipo de habitación disponible. Esto implica que al realizar una solicitud para crear un hotel, se incluirán también los datos asociados a cada tipo de habitación. De esta manera, al crear el hotel, la información se distribuirá automáticamente en las respectivas tablas de la base de datos, asegurando que los datos de cada habitación estén debidamente registrados y asociados al hotel correspondiente.

La response de la request anterior es la siguiente:

-Successfully created the hotel.

### Método GET (Listar todos los hoteles)
***

Este método nos permite observar todos los hoteles y aparte ver cuáles están o no reservados.

http://localhost:8080/agency/hotels

En el caso de que no hubiese hoteles en la base de datos o estos estuviesen eliminados, tendríamos la siguiente respuesta:

-There are no active hotels in the database.

### Método GET (Listar los hoteles en un determinado rango de fechas, lugar y buscar por disponibilidad)
***

Este método nos permite devolver hoteles mediante un rango de fechas, lugar y buscar si estos están disponibles.

http://localhost:8080/agency/hotels/search?avaliableDateFrom=2024-05-10&avaliableDateTo=2024-05-30&location=Sada&isBooked=false

En el caso de que el hotel o los hoteles que estuviésemos buscando no estuviesen en las susodichas fechas, recibiríamos como respuesta lo siguiente:

-There are no hotels with these criteria in the database.

### Método GET por ID.
***

Este método simplemente busca hoteles por el ID, en el caso de que no encuentre el hotel por el ID seleccionado, nos dará un error 

http://localhost:8080/agency/hotels/1

Si no encuentra un hotel, obtenemos la siguiente respuesta:

-The hotel with ID 2 does not exist in the database.

### Método PUT. Actualizar el hotel.
***

En este método solo podemos actualizar el nombre o la localización del hotel. La actualización se realiza a través del ID del hotel

```json
{

  "name": "Hotel Betanzos",
  "location": "Betanzos"
}

```

Si todo ha salido correcto, aparte de actualizarse el nombre y la localización, también se actualizará el código alfanumérico del hotel

Si el hotel que intentamos actualizar no se encuentra en la base de datos, nos marcará el siguiente error:

Hotel not found in the database.

### Método DELETE
***

http://localhost:8080/agency/hotels/1

Este método se encarga de hacer un borrado lógico, en la base de datos se encuentran dos columnas, una llamada (is_Deleted) y (last_deleted_date), en las cuales mediante una flag (0 - 1) nos permiten observar si el hotel se encuentra borrado o no. Para eliminar un hotel se hace mediante su ID. En el caso de que un hotel tenga una reserva, este no va a poder ser eliminado hasta que se elimine primero la reserva.

En el caso de que intentemos borrar un hotel que no se encuentra en la base de datos, obtendremos el siguiente error:

-The hotel with ID 3 was not found in the database.
