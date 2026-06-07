import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';

/**
 * Caches the authentication check so route navigations don't hit the BFF on
 * every single transition. The cache is short-lived (TTL) and invalidated on
 * logout.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly ttlMs = 60_000;
  private cached: boolean | null = null;
  private lastCheck = 0;

  isAuthenticated(): Observable<boolean> {
    const now = Date.now();
    if (this.cached !== null && now - this.lastCheck < this.ttlMs) {
      return of(this.cached);
    }
    return this.http.get(`${environment.apiBase}/auth/me`, { withCredentials: true }).pipe(
      map(() => this.remember(true)),
      catchError(() => of(this.remember(false)))
    );
  }

  invalidate(): void {
    this.cached = null;
  }

  private remember(value: boolean): boolean {
    this.cached = value;
    this.lastCheck = Date.now();
    return value;
  }
}
