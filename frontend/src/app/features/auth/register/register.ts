import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { RegisterRequest } from '../../../core/models/register-request';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  username = '';
  password = '';
  passwordConfirm = '';
  firstName = '';
  lastName = '';
  email = '';
  address = '';

  loading = false;

  usernameError: string | null = null;
  passwordError: string | null = null;
  passwordConfirmError: string | null = null;
  emailError: string | null = null;
  registerError: string | null = null;

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
    this.registerError = null;

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

    const request: RegisterRequest = {
      username: this.username,
      password: this.password,
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      address: this.address,
    }

    this.auth.register(request).subscribe({
      next: () => {
        this.toast.success('You have registered successfully.', 'Register Successful');
        this.router.navigate(['/home']);
        this.loading = false;
      },
      error: (error) => {
        if (error?.status === 409) {
          this.registerError = 'Account with this username already exists';
        } else {
          this.registerError = 'Register failed, please try again later';
        }
        this.loading = false;
      }
    });
  }
}

