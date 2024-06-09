En esta práctica se ha desarrollado un chat cliente-servidor
El servidor acepta nuevas conexiones y registra a los clientes para operaciones de lectura/escritura. También se encarga de leer los mensajes de los clientes y difundir (broadcast) esos mensajes a todos los clientes conectados.

La interfaz gráfica del cliente permite a los usuarios enviar mensajes al servidor y actualiza la lista de usuarios conectados cada vez que se recibe un mensaje.

La lista de usuarios en la interfaz gráfica gestiona una lista de usuarios conectados utilizando un JList. Permite añadir y eliminar usuarios de la lista.

La conexión del cliente establece una conexión con el servidor utilizando la clase MiServidorSocket, una clase casi identica a la implementada en la anterior práctica.
