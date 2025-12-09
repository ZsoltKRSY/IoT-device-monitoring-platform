import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { OverconsumptionDetailedEvent } from '../models/overconsumption-detailed-event';
import { ChatMessage } from '../models/chat-message';

@Injectable({
    providedIn: 'root'
})
export class WebSocketService {
    private client: Client;

    private overconsumptionSubject: Subject<OverconsumptionDetailedEvent> = new Subject();
    private chatSubject = new Subject<ChatMessage>();

    private subscriptions: StompSubscription[] = [];

    constructor() {
        this.client = new Client({
            brokerURL: 'ws://localhost:8086/ws',
            reconnectDelay: 5000,
        });

        this.client.onConnect = (frame) => {
            console.log('Connected to WebSocket broker', frame);
        };

        this.client.onStompError = (frame) => {
            console.error('Broker reported error:', frame.headers['message']);
            console.error('Details:', frame.body);
        };
    }

    connect(userId: number): void {
        if (!this.client.active) {
            this.client.activate();
        }

        this.client.onConnect = () => {
            console.log('%cWebSocket connected', 'color: green');

            //overconsumption
            this.subscriptions.push(
                this.client.subscribe(
                    `/queue/user-${userId}/overconsumption`,
                    (msg: IMessage) => {
                        try {
                            this.overconsumptionSubject.next(JSON.parse(msg.body));
                        } catch (err) {
                            console.error('Bad overconsumption event:', err);
                        }
                    }
                )
            );

            //user subscription
            this.subscriptions.push(
                this.client.subscribe(
                    `/topic/chat.user.${userId}`,
                    (msg: IMessage) => {
                        try {
                            const payload = JSON.parse(msg.body);

                            const chatMessage: ChatMessage = {
                                senderId: payload.senderId,
                                recipientId: payload.recipientId,
                                sender: payload.sender as 'admin' | 'ai' | 'system',
                                target: payload.target as 'user',
                                text: payload.text,
                                timestamp: payload.timestamp
                            };

                            this.chatSubject.next(chatMessage);
                        } catch (err) {
                            console.error('Bad chat message:', err);
                        }
                    }
                )
            );

            //admin chat messages
            this.subscriptions.push(
                this.client.subscribe(
                    `/queue/admin.${userId}.inbox`,
                    (msg: IMessage) => {
                        try {
                            const payload = JSON.parse(msg.body);

                            const chatMessage: ChatMessage = {
                                senderId: payload.senderId,
                                recipientId: payload.recipientId,
                                sender: payload.sender as 'user',
                                target: payload.target as 'user',
                                text: payload.text,
                                timestamp: payload.timestamp
                            };
                            this.chatSubject.next(chatMessage);
                        } catch (err) {
                            console.error('Bad admin-inbox chat message:', err);
                        }
                    }
                )
            );
        };
    }

    onOverconsumption(): Observable<OverconsumptionDetailedEvent> {
        return this.overconsumptionSubject.asObservable();
    }

    onChatMessages(): Observable<ChatMessage> {
        return this.chatSubject.asObservable();
    }

    sendChatMessage(message: Omit<ChatMessage, 'timestamp'>) {
        if (!this.client.connected) {
            console.warn('Trying to send chat message before WebSocket connected');
            return;
        }

        const payload: ChatMessage = {
            ...message,
            timestamp: new Date()
        };

        this.client.publish({
            destination: '/app/chat.send',
            body: JSON.stringify(payload)
        });
    }

    disconnect(): void {
        this.subscriptions.forEach((s) => s.unsubscribe());
        this.subscriptions = [];

        if (this.client.active) {
            this.client.deactivate();
        }
    }
}
