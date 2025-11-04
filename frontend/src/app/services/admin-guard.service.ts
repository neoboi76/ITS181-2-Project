import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(
    private tokenStorageService: TokenStorageService,
    private router: Router
  ) {}

  canActivate(): boolean {
    const token = this.tokenStorageService.getToken();
    const role = this.tokenStorageService.getUserRole();
    
    if (token && role === 'ADMIN') {
      return true;
    } else {
      this.router.navigate(['/home']);
      return false;
    }
  }
}