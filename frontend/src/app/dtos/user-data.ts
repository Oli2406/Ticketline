export interface UserDetailDto {
  id:string;
  firstName: string;
  lastName: string;
  email: string;
  isLocked: boolean;
  isLoggedIn: boolean;
  points: number
}

export interface UserUpdateReadNewsDto {
  newsId: number;
  email: string;
}

export interface UserResetPasswordDto {
  tokenToResetPassword: string;
  newPassword: string;
  newConfirmedPassword: string;
}

export interface DeleteUserDto {
  email: string;
  authToken: string;
}
