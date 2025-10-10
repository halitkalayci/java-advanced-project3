import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { catchError, map, of } from 'rxjs';

export const AuthGuard: CanActivateFn = () => {
  const http = inject(HttpClient);
  const router = inject(Router);

  return http.get(`${environment.apiBase}/api/me`, { withCredentials: true }).pipe(
    map(() => true),
    catchError(() => {
      router.navigateByUrl('/login');
      return of(false);
    })
  );
};
