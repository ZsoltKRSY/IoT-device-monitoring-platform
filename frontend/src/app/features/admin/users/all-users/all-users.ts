import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ConfirmDialog } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { UserService } from '../../../../core/services/user.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { UserDetail } from '../../../../core/models/user-detail';
import { UserRole } from '../../../../core/models/user-role.enum';
import { AuthUser } from '../../../../core/models/auth-user';
import { User } from '../../../../core/models/user';

@Component({
  selector: 'app-all-users',
  standalone: true,
  imports: [CommonModule, FormsModule, ConfirmDialog],
  templateUrl: './all-users.html',
  styleUrl: './all-users.css',
})
export class AllUsers implements OnInit {
  users: UserDetail[] = [];
  filteredUsers: UserDetail[] = [];
  expandedUserId: number | null = null;
  confirmUser: UserDetail | null = null;

  UserRole = UserRole;
  selectedRole: string = '';

  loading = true;
  error: string | null = null;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private toast: ToastService,
    private router: Router
  ) { }

  ngOnInit() {
    this.fetchAllUsers();
  }

  fetchAllUsers() {
    this.loading = true;
    this.error = null;

    this.authService.getAllAuthUsers().subscribe({
      next: (authUsers) => {
        this.userService.getAllUsers().subscribe({
          next: (userProfiles) => {
            this.users = authUsers.map((auth: any) => {
              const profile = userProfiles.find((u: User) => u.id === auth.id);
              return {
                id: auth.id,
                username: auth.username,
                role: auth.admin ? UserRole.ADMIN : UserRole.USER,
                email: profile?.email ?? '',
                firstName: profile?.firstName ?? '',
                lastName: profile?.lastName ?? '',
                address: profile?.address ?? '',
                expanded: false
              };
            });

            this.filteredUsers = this.users;
            this.loading = false;
          },
          error: () => {
            this.toast.error('Failed to fetch user profiles.', 'Fetch Error');
            this.loading = false;
          }
        });
      },
      error: () => {
        this.toast.error('Failed to fetch authentication users.', 'Fetch Error');
        this.loading = false;
      }
    });
  }

  filterUsers() {
    if (!this.selectedRole) {
      this.filteredUsers = [...this.users];
    } else {
      this.filteredUsers = this.users.filter(
        (u) => u.role === this.selectedRole
      );
    }
  }

  toggleExpand(user: UserDetail) {
    if (this.expandedUserId === user.id) {
      this.expandedUserId = null;
    } else {
      this.expandedUserId = user.id;
    }

    this.users.forEach(u => {
      u.expanded = u.id === this.expandedUserId;
    });
  }

  onAdd(event: Event) {
    event.stopPropagation();
    this.router.navigate(['/admin/create-user']);
  }

  onEdit(user: UserDetail, event: Event) {
    event.stopPropagation();
    this.router.navigate(['/admin/edit-user', user.id]);
  }

  onDelete(user: UserDetail, event: Event) {
    event.stopPropagation();
    this.confirmUser = user;
  }

  deleteUser(user: UserDetail) {
    this.authService.deleteUser(user.id).subscribe({
      next: () => {
        this.toast.success('Successfully deleted user.', 'Delete Success');
        this.users = this.users.filter(u => u.id !== user.id);
        this.filteredUsers = this.filteredUsers.filter(u => u.id !== user.id);
        this.confirmUser = null;
      },
      error: () => {
        this.toast.error('Could not delete selected user.', 'Delete Error');
        this.confirmUser = null;
      }
    });
  }

}
