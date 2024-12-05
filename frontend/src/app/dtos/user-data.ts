export interface UserDetailDto {
  id:number;
  firstName: string;
  lastName: string;
  email: string;
  isLocked: boolean;
  isLoggedIn: boolean;
}

export interface UserUpdateReadNewsDto {
  newsId: number;
  email: string;
}
