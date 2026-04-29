export type UserRoleType = "ADMIN" | "CLIENT" | "SELLER";

export type LoginPayload = {
  accessToken: string
}

export type JwtPayload = {
  role: UserRoleType;
  userId: string;
  sub: string;
  iat: number;
  exp: number;
};

export const COOKIE_JWT = "jwtSecret";
export const CART_STORAGE_KEY = "cart";