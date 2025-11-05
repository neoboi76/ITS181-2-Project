import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd, NavigationStart, Event } from '@angular/router';
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
    ) {
        this.viewportScroller.setOffset([0, 0]);
    }

    ngOnInit() {
        this.router.events
            .pipe(filter((e: Event): e is NavigationStart => e instanceof NavigationStart))
            .subscribe(() => {
                window.scrollTo(0, 0);
            });

        this.router.events
            .pipe(filter((e: Event): e is NavigationEnd => e instanceof NavigationEnd))
            .subscribe((event: NavigationEnd) => {
                const authRoutes = ['/login', '/register', '/reset-password', '/forgot-password'];
                const currentUrl = event.urlAfterRedirects || event.url;

                this.showNavbarFooter = !authRoutes.some(route => 
                    currentUrl === route || currentUrl.startsWith(route + '?')
                );
            });
    }
}