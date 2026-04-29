import type { AxiosRequestConfig } from "axios";
import axiosInstance from "./axios";

export const api = {
  get: <T = unknown>(url: string, params?: object) =>
    axiosInstance.get<T>(url, { params }),

  post: <T = unknown>(url: string, data?: any, config?: AxiosRequestConfig) =>
    axiosInstance.post<T>(url, data, config),

  put: <T = unknown>(url: string, data?: object, config?: AxiosRequestConfig) =>
    axiosInstance.put<T>(url, data, config),

  patch: <T = unknown>(url: string, data?: object) =>
    axiosInstance.patch<T>(url, data),

  delete: <T = unknown>(url: string) =>
    axiosInstance.delete<T>(url),
};

export default api;