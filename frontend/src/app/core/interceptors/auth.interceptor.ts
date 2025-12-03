import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const toast = inject(ToastService);

  const token = auth.getToken();

  let request = req;
  if (token && !req.url.includes('/auth/login') && !req.url.includes('/auth/register')) {
    request = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(request).pipe(
    catchError((error) => {
      if (error?.status === 0) {
        toast.error('Please check your internet connection.', 'Network Error');
      } else if (error?.status >= 500) {
        toast.error('Please try again later.', 'Server Error');
      } else if (error?.status === 401) {
        toast.error('Please log in.', 'Unauthorized');
      } else if (error?.status === 403) {
        toast.error('Access denied.', 'Forbidden');
      }
      return throwError(() => error);
    })
  );
};
