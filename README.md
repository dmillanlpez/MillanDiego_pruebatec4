# API REST de Gestión de Reservas de Hoteles y Vuelos

## Descripción

Esta API desarrollada en Spring Boot facilita diversas operaciones de gestión relacionadas con hoteles, vuelos y sus respectivas reservas.

## Funcionamiento

Para ejecutar la API, simplemente has de clonar este proyecto, ejecútalo desde tu IDE o editor correspondiente y prueba los siguientes endpoints documentados con Swagger [/doc/swagger-ui.html](/doc/swagger-ui.html):

### Hoteles

- Consultar lista de hoteles: `/agency/hotels`
- Consultar hotel por id: `/agency/hotels/{id}`
- Consultar hotel por rango de fechas y destino: `/agency/hotels/search`
- Crear hotel: `/agency/hotels/new`
- Editar hotel: `/agency/hotels/edit/{id}`
- Eliminar hotel: `/agency/hotels/delete/{id}`

### Vuelos

- Consultar lista de vuelos: `/agency/flights`
- Consultar vuelo por id: `/agency/flights/{id}`
- Consultar vuelo por rango de fechas y origen y destino: `/agency/flights/search`
- Crear vuelo: `/agency/flights/new`
- Editar vuelo: `/agency/flights/edit/{id}`
- Eliminar vuelo: `/agency/flights/delete/{id}`

### Reservas

- Consultar lista de reservas de vuelos: `/agency/flight-booking`
- Crear reserva de vuelo: `/agency/flight-booking/new`
- Editar reserva de vuelo: `/agency/flight-booking/edit/{id}`
- Eliminar reserva de vuelo: `/agency/flight-booking/delete/{id}`

- Consultar lista de reservas de hoteles: `/agency/hotel-booking`
- Crear reserva de hotel: `/agency/hotel-booking/new`
- Editar reserva de hotel: `/agency/hotel-booking/edit/{id}`
- Eliminar reserva de hotel: `/agency/hotel-booking/delete/{id}`


### Seguridad

Se ha implementado Spring Security para autenticar los endpoints de gestión de hoteles, vuelos y reservas, así como las consultas de la lista de reservas, haciendo que solo sean accesibles para usuarios autenticados.

**Credenciales Spring Security:**
- Usuario: hackaboss
- Contraseña: 1234

## Tests

Se han realizado los correspondientes test que se pedian en la prueba tecnica.

## Supuestos y mejoras

Una mejora sería que se pudieran ver la capacidad total del avión; de esta manera, las reservas de algunos vuelos no serían infinitas.

Otra mejora sería que los usuarios no tuvieran el mismo pasaporte. El DNI facilitaría esto como una validación más, ya que este es intransferible y ofrecería un mayor control sobre los usuarios.
