import {User} from "./user";

export interface RegisterForm {
    email: string
    fullName: string
    password: string
}

export interface LoginForm {
    email: string
    password: string
}

export interface RefreshTokenResponse {
    token: string
}

export interface LoginResponse extends User {
    token: string
}
