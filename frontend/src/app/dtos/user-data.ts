export interface UserDetailDto {
  id:number;
  firstName: string;
  lastName: string;
  email: string;
  isLocked: boolean;
  isLoggedIn: boolean;
}

export interface UserResetPasswordDto {
  tokenToResetPassword: string;
  newPassword: string;
  newConfirmedPassword: string;
}
