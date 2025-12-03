import { AuthUser } from "./auth-user";

export interface DeviceDetail {
    id: number;
    name: string;
    maxConsumption: number;
    owner: AuthUser | null;
    manufacturer?: string;
    model?: string;
    description?: string;
    expanded?: boolean;
}