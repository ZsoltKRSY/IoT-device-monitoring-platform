import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  username = '';
  password = '';
  loading = false;
  usernameError: string | null = null;
  passwordError: string | null = null;
  loginError: string | null = null;

  constructor(
    private router: Router,
    private auth: AuthService,
    private toast: ToastService
  ) { }

  onSubmit(form: NgForm) {
    this.usernameError = null;
    this.passwordError = null;
    this.loginError = null;

    if (!this.username) {
      this.usernameError = 'Username is required';
    }

    if (!this.password) {
      this.passwordError = 'Password is required';
    }

    if (this.usernameError || this.passwordError) {
      return;
    }

    this.loading = true;

    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        this.toast.success('You have logged in successfully.', 'Login Successful');
        this.router.navigate(['/home']);
        this.loading = false;
      },
      error: (error) => {
        if (error?.status === 404) {
          this.loginError = 'Invalid username or password';
        } else {
          this.loginError = 'Login failed, please try again later';
        }
        this.loading = false;
      }
    });
  }
}
