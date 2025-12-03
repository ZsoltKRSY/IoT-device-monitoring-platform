import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { UserService } from '../../../../core/services/user.service';
import { ToastService } from '../../../../core/services/toast.service';
import { UserOperation } from '../../../../core/models/user-operation';
import { AuthUser } from '../../../../core/models/auth-user';

@Component({
  selector: 'app-edit-user',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './edit-user.html',
  styleUrl: './edit-user.css',
})
export class EditUser implements OnInit {
  userId!: number;

  username = '';
  firstName = '';
  lastName = '';
  email = '';
  address = '';
  isAdmin = false;

  loading = false;

  usernameError: string | null = null;
  emailError: string | null = null;
  editError: string | null = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private auth: AuthService,
    private users: UserService,
    private toast: ToastService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.userId = +id;
        this.fetchUserDetails();
      }
    });
  }

  fetchUserDetails() {
    this.auth.getAuthUserById(this.userId).subscribe({
      next: (userAuth: any) => {
        this.users.getUserById(this.userId).subscribe({
          next: (userProfile) => {
            console.log(userProfile);
            this.username = userAuth.username;
            this.isAdmin = userAuth.admin;
            this.firstName = userProfile.firstName;
            this.lastName = userProfile.lastName;
            this.email = userProfile.email;
            this.address = userProfile.address;
          },
          error: () => {
            this.toast.error('Failed to fetch user profile.', 'Fetch Error');
          }
        })
      },
      error: () => {
        this.toast.error('Failed to fetch user authentication data.', 'Fetch Error');
      }
    });
  }

  onSubmit(form: NgForm) {
    this.usernameError = null;
    this.emailError = null;
    this.editError = null;

    if (!this.username) {
      this.usernameError = 'Username is required';
    }

    if (!this.email) {
      this.emailError = 'Email is required';
    }

    if (this.usernameError || this.emailError) {
      return;
    }

    this.loading = true;

    const updatedUser: Partial<UserOperation> = {
      username: this.username,
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      address: this.address,
      isAdmin: this.isAdmin
    }

    this.auth.updateUser(this.userId, updatedUser).subscribe({
      next: () => {
        this.toast.success('Device updated successfully.', 'Update Success');
        this.router.navigate(['/admin/users']);
        this.loading = false;
      },
      error: () => {
        this.editError = 'Failed to update device, please try again later';
        this.loading = false;
      }
    });
  }
}
