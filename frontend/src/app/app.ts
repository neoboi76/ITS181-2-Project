import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd, Event } from '@angular/router';
import { CommonModule, ViewportScroller } from '@angular/common';
import { filter } from 'rxjs/operators';
import { NavbarComponent } from './components/navbar.component/navbar.component';
import { FooterComponent } from './components/footer.component/footer.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, NavbarComponent, FooterComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  showNavbarFooter = true;

  constructor(
    private router: Router,
    private viewportScroller: ViewportScroller
  ) {}

  ngOnInit() {
    this.router.events
      .pipe(filter((e: Event): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        const authRoutes = ['/login', '/register', '/reset-password', '/forgot-password'];
        const currentUrl = event.urlAfterRedirects || event.url;

        this.showNavbarFooter = !authRoutes.some(route => 
          currentUrl === route || currentUrl.startsWith(route + '?')
        );
        
        setTimeout(() => {
          this.viewportScroller.scrollToPosition([0, 0]);
        }, 100);
      });
  }
}
