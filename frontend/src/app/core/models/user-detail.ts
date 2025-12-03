import { UserRole } from "./user-role.enum";

export interface UserDetail {
    id: number;
    username: string;
    email: string;
    role: UserRole;
    firstName?: string;
    lastName?: string;
    address?: string;
    expanded?: boolean;
}