export interface UserOperation {
    username: string;
    password: string;
    email: string;
    firstName?: string;
    lastName?: string;
    address?: string;
    isAdmin: boolean;
}