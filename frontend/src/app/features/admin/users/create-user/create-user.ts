import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { UserOperation } from '../../../../core/models/user-operation';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-user.html',
  styleUrl: './create-user.css',
})
export class CreateUser {
  username = '';
  password = '';
  passwordConfirm = '';
  firstName = '';
  lastName = '';
  email = '';
  address = '';
  isAdmin = false;

  loading = false;

  usernameError: string | null = null;
  passwordError: string | null = null;
  passwordConfirmError: string | null = null;
  emailError: string | null = null;
  createError: string | null = null;

  constructor(
    private router: Router,
    private auth: AuthService,
    private toast: ToastService
  ) { }

  onSubmit(form: NgForm) {
    this.usernameError = null;
    this.passwordError = null;
    this.passwordConfirmError = null;
    this.emailError = null;
    this.createError = null;

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;

    if (!this.username) {
      this.usernameError = 'Username is required';
    }

    if (!this.password) {
      this.passwordError = 'Password is required';
    } else if (!passwordRegex.test(this.password)) {
      this.passwordError = 'Password must be at least 8 characters long and include uppercase, lowercase, number, and special character';
    }

    if (this.password !== this.passwordConfirm) {
      this.passwordConfirmError = 'Passwords do not match';
    }

    if (!this.email) {
      this.emailError = 'Email is required';
    }

    if (this.usernameError || this.passwordError || this.emailError || this.passwordConfirmError) {
      return;
    }

    this.loading = true;

    const request: UserOperation = {
      username: this.username,
      password: this.password,
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      address: this.address,
      isAdmin: this.isAdmin
    }

    this.auth.createUser(request).subscribe({
      next: () => {
        this.toast.success('New user added successfully.', 'Create Successful');
        this.router.navigate(['/admin/users']);
        this.loading = false;
      },
      error: (error) => {
        if (error?.status === 409) {
          this.createError = 'Account with this username already exists';
        } else {
          this.createError = 'Creating user failed, please try again later';
        }
        this.loading = false;
      }
    });
  }
}