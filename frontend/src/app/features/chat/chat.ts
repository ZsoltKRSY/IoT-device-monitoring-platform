import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WebSocketService } from '../../core/services/websocket.service';
import { ChatMessage } from '../../core/models/chat-message';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',
  styleUrl: './chat.css',
})
export class Chat {
  @Input() mode: 'user' | 'admin' = 'user';
  @Input() username: string = '';
  @Input() userId!: number;
  @Input() messages: ChatMessage[] = [];
  @Input() targetUserId?: number;

  newMessage: string = '';
  chatTarget: 'admin' | 'ai' = 'admin';
  loading: boolean = false;

  constructor(private ws: WebSocketService) {
    this.ws.onChatMessages().subscribe((msg) => {
      if (msg.sender === 'system') {
        this.targetUserId = msg.senderId;
      }

      this.loading = false;
      this.messages.push(msg);
    });
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;

    const currentSender: 'user' | 'admin' = this.mode;
    const currentSenderId: number = this.userId;
    let recipient: 'user' | 'admin' | 'ai' = 'ai';
    let recipientId: number | undefined = undefined;

    if (this.mode === 'user') {
      if (this.chatTarget === 'admin') {
        recipient = 'admin';
        recipientId = this.targetUserId;
      } else {
        recipient = 'ai';
        recipientId = undefined;
      }
    } else if (this.mode === 'admin') {
      if (this.targetUserId === undefined) {
        console.error("Admin cannot send message without a targetUserId (the user they are chatting with).");
        return;
      }
      recipient = 'user';
      recipientId = this.targetUserId;
    }

    const msgToSend: Omit<ChatMessage, 'timestamp'> = {
      senderId: currentSenderId,
      recipientId: recipientId,
      sender: currentSender,
      target: recipient,
      text: this.newMessage
    };

    const msgToDisplay: ChatMessage = {
      ...msgToSend,
      timestamp: new Date()
    };
    this.messages.push(msgToDisplay);

    if (msgToSend.target == 'ai') {
      this.loading = true;
    }

    this.ws.sendChatMessage(msgToSend);

    this.newMessage = '';
  }

  getAlignmentClass(msg: ChatMessage): string {
    if (msg.sender === 'system' || msg.sender === 'ai') {
      return 'other';
    }

    const isFromSelf = (this.mode === 'user' && msg.sender === 'user') ||
      (this.mode === 'admin' && msg.sender === 'admin');

    return isFromSelf ? 'self' : 'other';
  }

  switchTarget(target: 'admin' | 'ai') {
    this.chatTarget = target;
  }

}
