import { Injectable } from '@angular/core';
import { LoginModel } from '../models/login.model';
import { BehaviorSubject, Observable } from 'rxjs';

const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private userSubject: BehaviorSubject<LoginModel | null>;
  public user$: Observable<LoginModel | null>;

  constructor() {
    const storedUser = this.getUserFromStorage();
    this.userSubject = new BehaviorSubject<LoginModel | null>(storedUser);
    this.user$ = this.userSubject.asObservable();
  }

  private getUserFromStorage(): LoginModel | null {
    const userJson = window.sessionStorage.getItem(USER_KEY);
    return userJson ? JSON.parse(userJson) : null;
  }

  public saveToken(token: string): void {
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, token);
  }

  public getToken(): string | null {
    return window.sessionStorage.getItem(TOKEN_KEY);
  }

  public saveUser(user: LoginModel): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
    this.userSubject.next(user);
  }
  
  public getUser(): Observable<LoginModel | null> {
    return this.user$;
  }

  public getUserSync(): LoginModel | null {
    return this.userSubject.value;
  }

  public getUsrId(): number {
    const user = this.userSubject.value;
    return user?.id || 0;
  }

  public getUserRole(): string {
    const user = this.userSubject.value;
    return user?.role || '';
  }

  logout(): void {
    window.sessionStorage.clear();
    this.userSubject.next(null);
  }
}