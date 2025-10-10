import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

export const ApiInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  let url = req.url;
  // Only add /api prefix for relative URLs, skip if already has full URL or starts with /auth
  if (url.startsWith('/') && !url.startsWith('/auth') && !url.startsWith('http')) {
    url = `${environment.apiBase}/api${url}`;
  }

  const cloned = req.clone({ url, withCredentials: true });

  return next(cloned).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        router.navigateByUrl('/login');
      }
      return throwError(() => error);
    })
  );
};
