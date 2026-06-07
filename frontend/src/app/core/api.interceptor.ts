import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { ToastService } from './toast.service';

export const ApiInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const toast = inject(ToastService);

  let url = req.url;
  // Only add /api prefix for relative URLs, skip if already absolute or /auth.
  if (url.startsWith('/') && !url.startsWith('/auth') && !url.startsWith('http')) {
    url = `${environment.apiBase}/api${url}`;
  }

  const cloned = req.clone({ url, withCredentials: true });

  return next(cloned).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        if (error.status === 401) {
          router.navigateByUrl('/login');
        } else if (error.status === 0) {
          toast.error('Sunucuya ulaşılamıyor. Lütfen tekrar deneyin.');
        } else {
          toast.error(extractMessage(error));
        }
      }
      return throwError(() => error);
    })
  );
};

function extractMessage(error: HttpErrorResponse): string {
  const body = error.error as { detail?: string; message?: string } | string | null;
  if (body && typeof body === 'object' && (body.detail || body.message)) {
    return body.detail ?? body.message ?? `Hata: ${error.status}`;
  }
  return `İşlem başarısız (${error.status})`;
}
