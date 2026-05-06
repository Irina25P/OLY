    import axios from "axios";
    import { COOKIE_JWT } from "../constants/constants.tsx";
    import Cookies from "js-cookie";

    const axiosInstance = axios.create({
        baseURL: "http://localhost/api",
        headers: {
            "Content-Type": "application/json",
        },
    });

    axiosInstance.interceptors.request.use(
        (config) => {
            const token = Cookies.get(COOKIE_JWT) || localStorage.getItem("token");

            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }

            return config;
        },
        (error) => Promise.reject(error)
    );

    export default axiosInstance;