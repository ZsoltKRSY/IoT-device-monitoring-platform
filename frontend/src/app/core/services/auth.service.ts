import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { CookieService } from "ngx-cookie-service";
import { BehaviorSubject, Observable, tap } from "rxjs";
import { jwtDecode } from "jwt-decode";
import { UserRole } from "../models/user-role.enum";
import { AuthUser } from "../models/auth-user";
import { RegisterRequest } from "../models/register-request";
import { UserOperation } from "../models/user-operation";

@Injectable({ providedIn: 'root' })
export class AuthService {
    private baseUrl = `${environment.apiUrl}/auth`;

    private userSubject: BehaviorSubject<AuthUser | null>;
    currentUser$: Observable<AuthUser | null>;

    constructor(private http: HttpClient, private cookieService: CookieService) {
        const currentUser = this.getCurrentUser();
        this.userSubject = new BehaviorSubject<AuthUser | null>(currentUser);
        this.currentUser$ = this.userSubject.asObservable();
    }


    getAllAuthUsers(): Observable<AuthUser[]> {
        return this.http.get<AuthUser[]>(`${this.baseUrl}`);
    }

    getAllNonAdminAuthUsers(): Observable<AuthUser[]> {
        return this.http.get<AuthUser[]>(`${this.baseUrl}/non-admin`);
    }

    getAuthUserById(id: number): Observable<AuthUser> {
        return this.http.get<AuthUser>(`${this.baseUrl}/${id}`);
    }

    createUser(user: UserOperation): Observable<AuthUser> {
        return this.http.post<AuthUser>(`${this.baseUrl}`, user);
    }

    updateUser(id: number, user: Partial<UserOperation>): Observable<AuthUser> {
        return this.http.put<AuthUser>(`${this.baseUrl}/${id}`, user);
    }

    deleteUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    login(username: string, password: string): Observable<any> {
        return this.http.post<any>(`${this.baseUrl}/login`, { username, password }).pipe(
            tap((response: any) => {
                const token = response?.accessToken;
                if (token) {
                    this.setToken(token);
                }
            })
        );
    }

    register(request: RegisterRequest): Observable<any> {
        return this.http.post<any>(`${this.baseUrl}/register`, request).pipe(
            tap((response: any) => {
                const token = response?.accessToken;
                if (token) {
                    this.setToken(token);
                }
            })
        );
    }

    private setToken(token: string): void {
        this.cookieService.set('jwt', token, {
            path: '/',
            sameSite: 'Lax',
            secure: environment.production
        });

        this.userSubject.next(this.getCurrentUser());
    }

    getToken(): string | null {
        return this.cookieService.get('jwt') || null;
    }

    getCurrentUser(): AuthUser | null {
        try {
            const token = this.cookieService.get('jwt');
            if (!token) return null;

            const payload: any = jwtDecode(token);
            const role = payload.role as keyof typeof UserRole;

            return {
                id: Number(payload.sub),
                username: payload.username,
                isAdmin: role === UserRole.ADMIN
            };
        } catch (e) {
            console.warn('Failed to decode JWT:', e);
            return null;
        }
    }

    logout(): void {
        this.cookieService.delete('jwt', '/');
        this.userSubject.next(null);
    }

    isLoggedIn(): boolean {
        return this.getToken() !== null;
    }

}