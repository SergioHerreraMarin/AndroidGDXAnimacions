
const webSockets = require('./appWS.js')

var websocket = new webSockets()
const port = process.env.PORT || 3000


function appListen() {
  console.log(`Listening for HTTP queries on: http://localhost:${port}`)
}

// Close connections when process is killed
process.on('SIGTERM', shutDown);
process.on('SIGINT', shutDown);

function shutDown() {
  console.log('Received kill signal, shutting down gracefully');
  websocket.end()
  process.exit(0);
}

websocket.init(port) // Start websockets









