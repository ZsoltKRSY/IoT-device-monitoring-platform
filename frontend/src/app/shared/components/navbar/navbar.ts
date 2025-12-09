import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { WebSocketService } from '../../../core/services/websocket.service';
import { AuthUser } from '../../../core/models/auth-user';
import { User } from '../../../core/models/user';
import { ProfileModal } from '../profile-modal/profile-modal';
import { Chat } from '../../../features/chat/chat';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, ProfileModal, Chat],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  @Input() authUser: AuthUser | null = null;
  user: User | null = null;
  showProfile = false;
  showProfileModal = false;
  showChat = false;

  private overconsumptionSub: Subscription | null = null;

  constructor(
    private auth: AuthService,
    private users: UserService,
    private toast: ToastService,
    private websocket: WebSocketService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authUser = this.auth.getCurrentUser();

    if (this.authUser) {
      this.users.getUserById(this.authUser.id).subscribe({
        next: (user) => { this.user = user; },
        error: () => { this.toast.error('Failed to load user profile.', 'Fetch Error'); }
      });

      this.websocket.connect(this.authUser.id);

      this.overconsumptionSub = this.websocket.onOverconsumption()
        .subscribe(event => {
          this.toast.warning(
            `Your ${event.deviceName} is overconsuming!\nCurrent: ${event.currentConsumption.toFixed(2)} kWh\nMaximum allowed: ${event.maxConsumption} kWh`,
            'Overconsumption Alert',
            0
          );
        });
    }
  }

  ngOnDestroy(): void {
    this.overconsumptionSub?.unsubscribe();
    this.websocket.disconnect();
  }

  toggleProfile() {
    this.showProfile = !this.showProfile;
  }

  openProfileModal() {
    this.showProfile = false;
    this.showProfileModal = true;
  }

  closeProfileModal() {
    this.showProfileModal = false;
  }

  toggleChat() {
    this.showChat = !this.showChat;
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

}
