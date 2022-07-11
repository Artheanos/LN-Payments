export enum Role {
  ADMIN = 'ROLE_ADMIN',
  USER = 'ROLE_USER',
  TEMPORARY = 'TEMPORARY_USER'
}

export interface User {
  email: string
  fullName: string
  role: Role
  createdAt?: Date
}

export interface AdminUser extends User {
  hasKey: boolean
  isAssignedToWallet: boolean
}

export interface TempUserRequest {
  email: string
}

export interface TempUserResponse {
  emailId: string
  token: string
}
