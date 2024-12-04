export interface UserDetailDto {
  id:number;
  firstName: string;
  lastName: string;
  email: string;
  isLocked: boolean;
  isLoggedIn: boolean;
}

export interface UserResetPasswordDto {
  email: string;
  newPassword: string;
  newConfirmedPassword: string;
}
