import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { Toast } from './shared/components/toast/toast';
import { Navbar } from './shared/components/navbar/navbar';
import { AuthUser } from './core/models/auth-user';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet, Toast, Navbar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');

  showNavbar = false;
  user: AuthUser | null = null;

  constructor(private router: Router, private authService: AuthService) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const hiddenRoutes = ['/login', '/register'];
      this.showNavbar = !hiddenRoutes.includes(event.urlAfterRedirects);
      this.user = this.authService.getCurrentUser();
    });

    const hiddenRoutes = ['/login', '/register'];
    this.showNavbar = !hiddenRoutes.includes(this.router.url);
    this.user = this.authService.getCurrentUser();
  }

}
