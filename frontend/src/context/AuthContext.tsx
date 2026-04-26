import { createContext, useContext, useState, useEffect, type ReactNode } from "react";
import { jwtDecode } from "jwt-decode";
import api from "../services/api";

export type UserRole = "CLIENT" | "SELLER" | "ADMIN";

interface JwtPayload {
  sub: string;
  userId: string;
  role: UserRole;
  exp: number;
}

export interface AuthUser {
  username: string;
  userId: string;
  role: UserRole;
}

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  login: (username: string, password: string) => Promise<void>;
  registerClient: (data: RegisterClientData) => Promise<void>;
  registerSeller: (data: RegisterSellerData) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

export interface RegisterClientData {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  password: string;
  region: string;
  age: number;
  phoneNumber: string;
  interests: string[];
}

export interface RegisterSellerData {
  name: string;
  email: string;
  username: string;
  password: string;
  imageName: string;
  description: string;
  region: string;
}

const AuthContext = createContext<AuthContextType | null>(null);

function parseToken(token: string): AuthUser | null {
  try {
    const decoded = jwtDecode<JwtPayload>(token);
    if (decoded.exp * 1000 < Date.now()) return null;
    return {
      username: decoded.sub,
      userId: decoded.userId,
      role: decoded.role,
    };
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("token"));
  const [user, setUser] = useState<AuthUser | null>(() => {
    const stored = localStorage.getItem("token");
    return stored ? parseToken(stored) : null;
  });

  useEffect(() => {
    if (token) {
      const parsed = parseToken(token);
      setUser(parsed);
      if (!parsed) {
        localStorage.removeItem("token");
        setToken(null);
      }
    }
  }, [token]);

  const login = async (username: string, password: string) => {
    const response = await api.post<{ accessToken: string }>("/v1/auth/login", {
      username,
      password,
    });
    const accessToken = response.data.accessToken;
    localStorage.setItem("token", accessToken);
    setToken(accessToken);
    setUser(parseToken(accessToken));
  };

  const registerClient = async (data: RegisterClientData) => {
    await api.post("/v1/auth/register/client", data);
  };

  const registerSeller = async (data: RegisterSellerData) => {
    await api.post("/v1/auth/register/seller", data);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        registerClient,
        registerSeller,
        logout,
        isAuthenticated: !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
