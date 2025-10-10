import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

export const ApiInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  let url = req.url;
  if (url.startsWith('/')) {
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
