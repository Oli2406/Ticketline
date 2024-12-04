export class AuthRequest {
  constructor(
    public email: string,
    public password: string
  ) {}
}

export interface ResetPasswordTokenDto {
  email: string;
  code: string;
}
