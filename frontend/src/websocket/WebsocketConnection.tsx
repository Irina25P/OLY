import React, { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { NotificationDto, NotificationAckDto } from '../models/notification';
import { showNotification } from '../components/layout/ToastNotification';
import { getClientIdFromJwt } from '../validators/checkoutValidations';


const WebSocketConnection: React.FC = () => {
    const clientRef = useRef<Client | null>(null);
    const userId = getClientIdFromJwt();

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("Connected to WebSocket");

                const topics = [
                    { path: '/receivedOrder', title: 'New Order' },
                    { path: '/updateOrder', title: 'Order Status Update' },
                    { path: '/receivedReview', title: 'New Review' }
                ];

                topics.forEach((topic) => {
                    const destination = `/topic/user/${userId}${topic.path}`;
                    
                    stompClient.subscribe(destination, (message) => {
                        const data: NotificationDto = JSON.parse(message.body);
                        
                        showNotification(data.message, topic.title);

                        const ackPayload: NotificationAckDto = {
                            notificationId: data.id,
                            receiverId: userId || ''
                        };

                        stompClient.publish({
                            destination: '/app/notification.ack',
                            body: JSON.stringify(ackPayload)
                        });
                        
                        console.log("ACK sent for:", data.id);
                    });
                });
            },
            onStompError: (frame) => {
                console.error('Broker error: ' + frame.headers['message']);
            }
        });

        stompClient.activate();
        clientRef.current = stompClient;

        return () => {
            if (clientRef.current) {
                clientRef.current.deactivate();
            }
        };
    }, [userId]);

    return null;
};

export default WebSocketConnection;