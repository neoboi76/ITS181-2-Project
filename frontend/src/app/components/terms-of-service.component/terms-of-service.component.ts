import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-terms-of-service',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './terms-of-service.component.html',
    styleUrl: './terms-of-service.component.css'
})
export class TermsOfServiceComponent implements OnInit {
    lastUpdated = 'November 5, 2025';

    ngOnInit(): void {
        window.scrollTo(0, 0);
    }
}