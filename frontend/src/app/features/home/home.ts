import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AuthUser } from '../../core/models/auth-user';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  user: AuthUser | null = null;

  constructor(private auth: AuthService) { }

  ngOnInit(): void {
    this.user = this.auth.getCurrentUser();
  }
}
