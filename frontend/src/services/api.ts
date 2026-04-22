import axiosInstance from "./axios";

export const api = {
  get: <T = unknown>(url: string, params?: object) =>
    axiosInstance.get<T>(url, { params }),

  post: <T = unknown>(url: string, data?: object) =>
    axiosInstance.post<T>(url, data),

  put: <T = unknown>(url: string, data?: object) =>
    axiosInstance.put<T>(url, data),

  patch: <T = unknown>(url: string, data?: object) =>
    axiosInstance.patch<T>(url, data),

  delete: <T = unknown>(url: string) =>
    axiosInstance.delete<T>(url),
};

export default api;