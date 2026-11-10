import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
    constructor() {
        this.client = null;
        this.subscriptions = new Map();
        // Intentar obtener la URL base, asumiendo HTTP -> WS. Si API_ENDPOINTS no la expone directamente,
        // ajustamos para remover subrutas /api.
    }

    getBaseUrl() {
        // En tu entorno vite, env vars o constants.js
        const base = import.meta.env.VITE_API_URL || "http://localhost:8080";
        return base;
    }

    connect(onConnected, onError) {
        if (this.client && this.client.connected) {
            if (onConnected) onConnected();
            return;
        }

        const socketUrl = `${this.getBaseUrl()}/ws`;

        this.client = new Client({
            webSocketFactory: () => new SockJS(socketUrl),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000
        });

        this.client.onConnect = (frame) => {
            console.log('Connected: ' + frame);
            if (onConnected) onConnected();
        };

        this.client.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
            if (onError) onError(frame);
        };

        this.client.activate();
    }

    subscribeToAvistamientos(desaparecidoId, callback) {
        if (!this.client || !this.client.connected) {
            console.warn("WebSocket not connected yet. Subscription will fail.");
            return null;
        }

        const topic = desaparecidoId 
            ? `/topic/avistamientos/${desaparecidoId}`
            : `/topic/avistamientos`;

        if (this.subscriptions.has(topic)) {
            // Already subscribed
            return this.subscriptions.get(topic);
        }

        const subscription = this.client.subscribe(topic, (message) => {
            if (message.body) {
                const avistamiento = JSON.parse(message.body);
                callback(avistamiento);
            }
        });

        this.subscriptions.set(topic, subscription);
        return subscription;
    }

    unsubscribe(topic) {
        if (this.subscriptions.has(topic)) {
            this.subscriptions.get(topic).unsubscribe();
            this.subscriptions.delete(topic);
        }
    }

    unsubscribeAll() {
        this.subscriptions.forEach(sub => sub.unsubscribe());
        this.subscriptions.clear();
    }

    disconnect() {
        if (this.client) {
            this.unsubscribeAll();
            this.client.deactivate();
        }
    }
}

export const webSocketService = new WebSocketService();
