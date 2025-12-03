import { Injectable, signal } from "@angular/core";
import { Toast } from "../models/toast";
import { ToastType } from "../models/toast-type.enum";

@Injectable({ providedIn: 'root' })
export class ToastService {
    private _toasts = signal<Toast[]>([]);
    toasts = this._toasts.asReadonly();

    show(message: string, type: ToastType = ToastType.INFO, title?: string, duration: number = 3000) {
        const id = Date.now().toString();
        const toast: Toast = { id, type, title, message, duration };
        this._toasts.set([...this._toasts(), toast]);

        if (duration > 0) {
            setTimeout(() => this.remove(id), duration);
        }

        return id;
    }

    success(message: string, title?: string, duration = 3000) {
        return this.show(message, ToastType.SUCCESS, title, duration);
    }

    info(message: string, title?: string, duration = 3000) {
        return this.show(message, ToastType.INFO, title, duration);
    }

    warning(message: string, title?: string, duration = 5000) {
        return this.show(message, ToastType.WARNING, title, duration);
    }

    error(message: string, title?: string, duration = 10000) {
        return this.show(message, ToastType.ERROR, title, duration);
    }

    remove(id: string) {
        this._toasts.set(this._toasts().filter(toast => toast.id !== id));
    }

    clear() {
        this._toasts.set([]);
    }

}
