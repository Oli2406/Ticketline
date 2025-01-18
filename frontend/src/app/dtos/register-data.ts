export interface RegisterData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmedPassword: string;
}

export interface UserRegistrationDto {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface AdminRegisterData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmedPassword: string;
  isAdmin: boolean;
}

export interface UserToUpdateDto {
  id:String;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmedPassword: string;
  currentAuthToken: string;
  version:number;
}

export interface AdminUserRegistrationDto {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  isAdmin: boolean;
}

export interface RegisterUser {
  firstName: string;
  lastName: string;
  email: string;
  isAdmin: boolean;
}
