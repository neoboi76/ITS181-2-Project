import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TokenStorageService } from '../../services/token-storage.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent implements OnInit, OnDestroy {
  currentYear = new Date().getFullYear();
  isLoggedIn = false;
  private userSubscription?: Subscription;
  userName: any;

  constructor(
    private tokenStorageService: TokenStorageService
  ) {}

  checkLoginStatus(): void {
    const token = this.tokenStorageService.getToken();
    this.isLoggedIn = !!token;
    
    if (this.isLoggedIn) {
      this.userSubscription = this.tokenStorageService.getUser().subscribe(user => {
        if (user) {
          this.userName = user.firstName || '';
        }
      });
    }
  }

  ngOnInit(): void {
    this.checkLoginStatus();
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  scrollToTop() {
    window.scrollTo(0, 0);
  }
}