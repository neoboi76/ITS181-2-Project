import { CommonModule, NgClass } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TokenStorageService } from '../../services/token-storage.service';
import { LoginModel } from '../../models/login.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, RouterLink, NgClass, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  forgotPasswordForm!: FormGroup;
  submitted = false;
  errorMessage = '';
  successMessage = '';
  showForgotPasswordModal = false;
  isRequestingReset = false;
  forgotPasswordMessage = '';
  showPassword: boolean = false;

  constructor(
    private tokenStorageService: TokenStorageService,
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  ngOnInit(): void {
    if (this.tokenStorageService.getToken()) {
      this.authService.isLoggedIn = true;
      this.router.navigate(['/home']);
    }

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.minLength(6)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });

    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  get fp() {
    return this.forgotPasswordForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.loginForm.invalid) return;

    const {email, password} = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: (data: LoginModel) => {
        this.tokenStorageService.saveToken(data.token);
        this.tokenStorageService.saveUser(data);
        this.successMessage = 'Login was a success!';
        setTimeout(() => this.router.navigate(['/home']), 1000);
      },
      error: err => {
        console.log(err);
        this.errorMessage = 'Invalid Username or Password';
      }
    });
  }

  openForgotPasswordModal(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.showForgotPasswordModal = true;
    this.forgotPasswordMessage = '';
    this.forgotPasswordForm.reset();
    document.body.style.overflow = 'hidden';
  }

  closeForgotPasswordModal(): void {
    this.showForgotPasswordModal = false;
    this.forgotPasswordMessage = '';
    this.isRequestingReset = false;
    document.body.style.overflow = 'auto';
  }

  submitForgotPassword(): void {
    if (this.forgotPasswordForm.invalid) return;

    this.isRequestingReset = true;
    this.forgotPasswordMessage = '';
    const email = this.forgotPasswordForm.value.email;

    this.authService.requestForgotPassword(email).subscribe({
      next: (response) => {
        this.isRequestingReset = false;
        this.forgotPasswordMessage = 'Password reset link has been sent to your email. Please check your inbox and follow the instructions.';
        
        setTimeout(() => {
          this.closeForgotPasswordModal();
        }, 1500);
      },
      error: (err) => {
        console.error('Error requesting password reset:', err);
        this.isRequestingReset = false;
        this.forgotPasswordMessage = 'Failed to send reset email. Please try again or contact support.';
      }
    });
  }
}