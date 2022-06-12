import { User } from './user'

export interface LoginForm {
  email: string
  password: string
  hostUrl: string
}

export interface KeyUploadForm {
  publicKey: string
}

export interface LoginResponse extends User {
  notificationChannelId: string
  token: string
}
