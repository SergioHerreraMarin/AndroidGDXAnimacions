// Description: WebSocket server for the app

const WebSocket = require('ws')
const { v4: uuidv4 } = require('uuid')

class Obj {

    init(port) {
        // Run WebSocket server
        this.websocketServer = new WebSocket.Server({ port: port })
        this.socketsClients = new Map() //Map de clientes conectados. 
        console.log(`Listening for WebSocket queries on ${port}`)

        /* Cuando se conecta un cliente, se activa el evento connection, recibe un objeto ws en una funcion anónima 
        y esta función llama a la función newConnection*/
        this.websocketServer.on('connection', (ws) => { this.newConnection(ws) })
        this.isGameStart = false;
    }

    end() {
        this.websocketServer.close()
    }

    // A websocket client connects
    newConnection(ws) {

        console.log("Client connected")

        // Add client to the clients list
        const id = uuidv4()
        const color = Math.floor(Math.random() * 360)
        const metadata = { id, color }
        this.socketsClients.set(ws, metadata) //Almacena la conexción del cliente y la data. 

        // Send clients list to everyone
        this.sendClients()

        //Cuando el cliente se desconecta, activa el evento close, y borra al cliente del map. 
        ws.on("close", () => { this.socketsClients.delete(ws) })

        //Cuando recibe un mensaje de un cliente, activa el evento message y ejecuta el método newMessage pasando el mensaje. 
        ws.on('message', (bufferedMessage) => { this.newMessage(ws, id, bufferedMessage) })
    }


    // Send clientsIds to everyone connected with websockets
    sendClients() {
        console.log('Client connected');
        var clients = []
        this.socketsClients.forEach((value, key) => {
            clients.push(value.id)
        })
        this.websocketServer.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                var id = this.socketsClients.get(client).id
                var messageAsString = JSON.stringify({ type: "clientes conectados", id: id, list: clients, size: clients.length })
                client.send(messageAsString)
            }
        })
    }

    // Send a message to all websocket clients
    broadcast(obj) {
        this.websocketServer.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                var messageAsString = JSON.stringify(obj)
                client.send(messageAsString)
            }
        })
    }

    // Send a private message to a specific websocket client
    private(obj) {
        this.websocketServer.clients.forEach((client) => {
            if (this.socketsClients.get(client).id == obj.destination && client.readyState === WebSocket.OPEN) {
                var messageAsString = JSON.stringify(obj)
                client.send(messageAsString)
                return
            }
        })
    }


    // A message is received from a websocket client, le pasa conexción del cliente, la id y el mensaje
    newMessage(ws, id, bufferedMessage) {
        var message = bufferedMessage.toString()
        console.log(message);
    }
}

module.exports = Obj

