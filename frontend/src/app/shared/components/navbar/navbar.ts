import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthUser } from '../../../core/models/auth-user';
import { User } from '../../../core/models/user';
import { ProfileModal } from '../profile-modal/profile-modal';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, ProfileModal],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  @Input() authUser: AuthUser | null = null;
  user: User | null = null;
  showProfile = false;
  showProfileModal = false;

  constructor(
    private auth: AuthService,
    private users: UserService,
    private toast: ToastService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authUser = this.auth.getCurrentUser();

    if (this.authUser) {
      this.users.getUserById(this.authUser.id).subscribe({
        next: (user) => {
          this.user = user;
        },
        error: () => {
          this.toast.error('Failed to load user profile.', 'Fetch Error');
        }
      });
    }
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

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

}
