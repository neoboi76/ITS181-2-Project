import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-about',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './about.component.html',
    styleUrl: './about.component.css'
})
export class AboutComponent {
    faqs = [
        {
            question: 'What is the adoption process?',
            answer: 'The adoption process begins with filling out an application form. Once submitted, our team reviews your application. If approved, you\'ll be invited to visit our facility to meet the dogs in person. After meeting your chosen pet and ensuring it\'s a good match, we\'ll schedule a home visit. Once approved, you can take your new family member home!',
            isOpen: false
        },
        {
            question: 'What are the adoption requirements?',
            answer: 'Applicants must be at least 21 years old, have a stable income, and provide a safe and loving home environment. We require proof of residence and may conduct a home visit. If renting, landlord approval for pets is necessary.',
            isOpen: false
        },
        {
            question: 'Is there an adoption fee?',
            answer: 'Yes, there is a minimal adoption fee to help cover the cost of veterinary care, vaccinations, and spaying/neutering. The fee is ₱1,000 for dogs. This helps us continue our rescue operations.',
            isOpen: false
        },
        {
            question: 'Are the dogs vaccinated?',
            answer: 'Yes, all our dogs are fully vaccinated, dewormed, and have received necessary medical treatments. Most are also spayed or neutered before adoption.',
            isOpen: false
        },
        {
            question: 'Can I return a dog if it doesn\'t work out?',
            answer: 'While we work hard to ensure good matches, we understand circumstances can change. We have a return policy and will always welcome our dogs back if adoption doesn\'t work out. We ask that you contact us first to discuss any concerns.',
            isOpen: false
        },
        {
            question: 'How long does the adoption process take?',
            answer: 'The process typically takes 1-2 weeks, depending on application review, scheduled visits, and home check completion. We want to ensure the best match for both you and the dog.',
            isOpen: false
        },
        {
            question: 'Can I adopt if I have other pets?',
            answer: 'Yes! We encourage pet meet-and-greets to ensure compatibility. Please mention your current pets in the application so we can help find the perfect match.',
            isOpen: false
        },
        {
            question: 'What if I live in an apartment or condo?',
            answer: 'You can still adopt! Please ensure your building allows pets and provide landlord approval. Consider the size and energy level of the dog to match your living space.',
            isOpen: false
        }
    ];

    ngOnInit(): void {
        window.scrollTo(0, 0);
    }

    toggleFaq(index: number): void {
        this.faqs[index].isOpen = !this.faqs[index].isOpen;
    }
}