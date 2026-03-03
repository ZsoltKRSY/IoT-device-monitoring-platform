import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chat } from '../../chat/chat';
import { WebSocketService } from '../../../core/services/websocket.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ChatMessage } from '../../../core/models/chat-message';
import { AuthUser } from '../../../core/models/auth-user';
import { Subscription } from 'rxjs';

interface OpenChatSession {
  userId: number;
  targetUserId: number;
  userName: string;
  messages: ChatMessage[];
}

@Component({
  selector: 'app-customer-service',
  standalone: true,
  imports: [CommonModule, Chat],
  templateUrl: './customer-service.html',
  styleUrl: './customer-service.css',
})
export class CustomerService implements OnInit {
  adminId: number = -1
  users: AuthUser[] = [];

  openSessions: OpenChatSession[] = [];
  private subscriptions: Subscription = new Subscription();
  connectionStatus: string = 'Connecting...';

  constructor(private ws: WebSocketService, private auth: AuthService, private toast: ToastService) { }

  ngOnInit(): void {
    this.fetchAllNonAdminUsers();

    this.adminId = this.auth.getCurrentUser()?.id ?? -1;

    if (this.adminId < 0) {
      this.toast.error("Could not fetch admin ID.", "Fetch Error");
    } else {
      this.ws.connect(this.adminId);

      this.subscriptions.add(
        this.ws.onChatMessages().subscribe((msg) => {
          const customerId = msg.senderId;
          this.handleIncomingMessage(customerId, msg);
        })
      );

      setTimeout(() => {
        this.connectionStatus = 'Connected';
      }, 1000);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.ws.disconnect();
  }

  private fetchAllNonAdminUsers() {
    this.auth.getAllNonAdminAuthUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: () => {
        this.toast.error('Failed to fetch user data.', 'Fetch Error');
      }
    });
  }

  private handleIncomingMessage(customerId: number, msg: ChatMessage): void {
    let session = this.openSessions.find(s => s.targetUserId === customerId);

    const username = this.getNameById(customerId);

    if (!session) {
      session = {
        userId: this.adminId,
        targetUserId: customerId,
        userName: username,
        messages: [msg]
      };
      this.openSessions.push(session);
    }
  }

  private getNameById(userId: number) {
    const user = this.users.find(u => u.id === userId);
    return user ? user?.username : '';
  }

  closeSession(targetUserId: number): void {
    this.openSessions = this.openSessions.filter(s => s.targetUserId !== targetUserId);
  }

}
