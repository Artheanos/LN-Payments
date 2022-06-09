export enum Role {
  ADMIN = 'ROLE_ADMIN',
  USER = 'ROLE_USER',
  TEMPORARY = 'TEMPORARY_USER',
}

export interface User {
  email: string
  fullName: string
  role: Role
}
