import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TokenStorageService } from '../../services/token-storage.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit, OnDestroy {
  isMenuOpen = false;
  showLogoutModal = false;
  isLoggedIn = false;
  isAdmin = false;
  userName = '';
  private userSubscription?: Subscription;

  constructor(
    private tokenStorageService: TokenStorageService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.checkLoginStatus();
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  checkLoginStatus(): void {
    const token = this.tokenStorageService.getToken();
    this.isLoggedIn = !!token;
    
    if (this.isLoggedIn) {
      this.userSubscription = this.tokenStorageService.getUser().subscribe(user => {
        if (user) {
          this.userName = user.firstName || '';
          this.isAdmin = user.role === 'ADMIN';
        }
      });
    }
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  openLogoutModal(event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.showLogoutModal = true;
    this.isMenuOpen = false;
    document.body.style.overflow = 'hidden';
  }

  closeLogoutModal(event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.showLogoutModal = false;
    document.body.style.overflow = 'auto';
  }

  confirmLogout(event?: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    
    this.authService.logout().subscribe({
      next: () => {
        this.tokenStorageService.logout();
        this.showLogoutModal = false;
        document.body.style.overflow = 'auto';
        this.isLoggedIn = false;
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Logout error:', err);
        this.tokenStorageService.logout();
        this.showLogoutModal = false;
        document.body.style.overflow = 'auto';
        this.isLoggedIn = false;
        this.router.navigate(['/home']);
      }
    });
  }

  scrollToTop() {
    window.scrollTo(0, 0);
  }
}

