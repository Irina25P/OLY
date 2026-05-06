import React, { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { NotificationDto, NotificationAckDto } from '../models/notification';
import { showNotification } from '../components/layout/ToastNotification';
import { useAuth } from '../context/AuthContext'; // Importă hook-ul tău

const WebSocketConnection: React.FC = () => {
    const clientRef = useRef<Client | null>(null);
    const { user, isAuthenticated } = useAuth();

    useEffect(() => {
        if (!isAuthenticated || !user?.userId) {
            return;
        }

        const socket = new SockJS('http://localhost/ws', null, {
            transports: ['websocket', 'xhr-streaming', 'xhr-polling']
            });
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("Connected to WebSocket for user:", user.userId);

                const topics = [
                    { path: '/receivedOrder', title: 'New Order' },
                    { path: '/updateOrder', title: 'Order Status Update' },
                    { path: '/receivedReview', title: 'New Review' }
                ];

                topics.forEach((topic) => {
                    const destination = `/topic/user/${user.userId}${topic.path}`;

                    stompClient.subscribe(destination, (message) => {
                        try {
                            const data: NotificationDto = JSON.parse(message.body);
                            showNotification(data.message, topic.title);

                            const ackPayload: NotificationAckDto = {
                                notificationId: data.id,
                                receiverId: user.userId
                            };

                            stompClient.publish({
                                destination: '/app/notification.ack',
                                body: JSON.stringify(ackPayload)
                            });
                        } catch (err) {
                            console.error("Error parsing message body", err);
                        }
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
                console.log("Deactivating WebSocket...");
                clientRef.current.deactivate();
                clientRef.current = null;
            }
        };
    }, [user?.userId, isAuthenticated]);

    return null;
};

export default WebSocketConnection;
