export interface ChatMessage {
    senderId: number;
    recipientId: number | undefined;
    sender: 'user' | 'admin' | 'ai' | 'system';
    target: 'user' | 'admin' | 'ai';
    text: string;
    timestamp: Date;
}