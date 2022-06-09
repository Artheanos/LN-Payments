import { User } from './user'

export interface LoginForm {
  email: string
  password: string
}

export interface KeyUploadForm {
  publicKey: string
}

export interface LoginResponse extends User {
  token: string
}
