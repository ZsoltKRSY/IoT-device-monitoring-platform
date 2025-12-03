import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs";
import { User } from "../models/user";

@Injectable({ providedIn: 'root' })
export class UserService {
    private baseUrl = `${environment.apiUrl}/users`;

    constructor(private http: HttpClient) { }

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.baseUrl}`);
    }

    getUserById(id: number): Observable<User> {
        return this.http.get<User>(`${this.baseUrl}/${id}`);
    }

}