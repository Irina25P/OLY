import { Routes, Route } from "react-router-dom"
import { AuthProvider } from "../context/AuthContext"
import { ProtectedRoute } from "../components/ProtectedRoute"

import MainLayout from "../components/layout/MainLayout"
import SellerLayout from "../components/seller/SellerLayout"

import HomePage from "../pages/public/HomePage"
import LoginPage from "../pages/auth/LoginPage"
import RegisterPage from "../pages/auth/RegisterPage"
import DashboardPage from "../pages/dashboard/DashboardPage"
import ProductCatalogPage from "../pages/product/ProductCatalogPage"
import ProductDetailPage from "../pages/product/ProductDetailPage"
import ClientProfile from "../pages/dashboard/ClientProfile"
import CheckoutPage from "../pages/clients/CheckoutPage"

import SellerOverviewPage from "../pages/seller/SellerOverviewPage"
import SellerProductsPage from "../pages/seller/SellerProductsPage"
import SellerOrdersPage from "../pages/seller/SellerOrdersPage"
import SellerAnalyticsPage from "../pages/seller/SellerAnalyticsPage"
import SellerReviewsPage from "../pages/seller/SellerReviewsPage"
import SellerSettingsPage from "../pages/seller/SellerSettingsPage"
import OrderTracking from "../pages/clients/OrderTracking.tsx";

function AppRouter() {
    return (
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                <Route element={<MainLayout />}>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/products" element={<ProductCatalogPage />} />
                    <Route path="/products/:id" element={<ProductDetailPage />} />

                    <Route
                        path="/checkout"
                        element={
                            <ProtectedRoute allowedRoles={["CLIENT"]}>
                                <CheckoutPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/profile"
                        element={
                            <ProtectedRoute allowedRoles={["CLIENT"]}>
                                <ClientProfile />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/orders"
                        element={
                            <ProtectedRoute allowedRoles={["CLIENT"]}>
                                <OrderTracking />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute>
                                <DashboardPage />
                            </ProtectedRoute>
                        }
                    />
                </Route>

                <Route
                    element={
                        <ProtectedRoute allowedRoles={["SELLER"]}>
                            <SellerLayout />
                        </ProtectedRoute>
                    }
                >
                    <Route path="/seller" element={<SellerOverviewPage />} />
                    <Route path="/seller/products" element={<SellerProductsPage />} />
                    <Route path="/seller/orders" element={<SellerOrdersPage />} />
                    <Route path="/seller/analytics" element={<SellerAnalyticsPage />} />
                    <Route path="/seller/reviews" element={<SellerReviewsPage />} />
                    <Route path="/seller/settings" element={<SellerSettingsPage />} />
                </Route>
            </Routes>
        </AuthProvider>
    )
}

export default AppRouter