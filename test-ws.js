const { Client } = require('@stomp/stompjs');
const WebSocket = require('ws');

// ========================================================================
// 1. TEST DATA: These have been pre-filled with real data from your local DB
// ========================================================================
const JWT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBoZWxwaHViLmxrIiwiaWF0IjoxNzc0MjUzOTkyLCJleHAiOjE3NzQyNTQ4OTJ9.0JG2xMVgvY7B99CsYl90-ZXlgNs6DtfmyF2cqSY0DNI";
const JOB_ID = "7fa85f64-5717-4562-b3fc-2c963f66afa6"; 
// ========================================================================

const client = new Client({
    // Important: Spring withSockJS() usually handles the /websocket endpoint directly for raw sockets
    webSocketFactory: () => new WebSocket('ws://localhost:8080/ws/websocket'),
    connectHeaders: {
        Authorization: JWT_TOKEN
    },
    debug: function (str) {
        console.log('STOMP DEUBG: ' + str);
    },
    onConnect: () => {
        console.log('✅ Connected to WebSocket Server Successfully!');

        // Subscribe to the Job's location topic to listen for broadcasts
        console.log(`📡 Subscribing to /topic/jobs/${JOB_ID}/location...`);
        client.subscribe(`/topic/jobs/${JOB_ID}/location`, (message) => {
            console.log(`\n📥 [RECEIVED] Location update payload:`);
            console.log(message.body);
            
            // Disconnect after successfully receiving the message to end the script
            console.log('\n🛑 Disconnecting...');
            client.deactivate();
        });

        // Send a mock location update to the server
        const payload = { lat: 6.9271, lng: 79.8612 };
        console.log(`\n📤 [SENDING] Location update to /app/jobs/${JOB_ID}/location:`);
        console.log(payload);
        
        client.publish({
            destination: `/app/jobs/${JOB_ID}/location`,
            body: JSON.stringify(payload)
        });
    },
    onStompError: (frame) => {
        console.error('❌ Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    },
    onWebSocketError: (event) => {
        console.error('❌ WebSocket error observed:', event);
    },
    onWebSocketClose: () => {
        console.log('🔌 WebSocket connection closed');
    }
});

console.log('🚀 Attempting to connect...');
client.activate();
