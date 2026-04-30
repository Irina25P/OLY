import { Routes, Route } from "react-router-dom"
import { AuthProvider } from "../context/AuthContext"
import { ProtectedRoute } from "../components/ProtectedRoute"
import MainLayout from "../components/layout/MainLayout"
import HomePage from "../pages/public/HomePage"
import LoginPage from "../pages/auth/LoginPage"
import RegisterPage from "../pages/auth/RegisterPage"
import DashboardPage from "../pages/dashboard/DashboardPage"
import ClientProfile from "../pages/dashboard/ClientProfile"
import CheckoutPage from "../pages/clients/CheckoutPage"

function AppRouter() {
  return (
    <AuthProvider>
      <Routes>
        {/* Auth pages — no MainLayout, no authentication required */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* App pages — with MainLayout */}
        <Route element={<MainLayout />}>
          {/* Public page */}
          <Route path="/" element={<HomePage />} />
          <Route path="/checkout" element={<ProtectedRoute allowedRoles={["CLIENT"]}><CheckoutPage /></ProtectedRoute>} />
          {/* Protected pages — requires authentication */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            }
          />

          {/* Client-only pages */}
          <Route
            path="/profile"
            element={
              <ProtectedRoute allowedRoles={["CLIENT"]}>
                <ClientProfile />
              </ProtectedRoute>
            }
          />

          {/* Role-specific routes examples (uncomment and add pages as needed) */}
          {/* Client-only page */}
          {/* <Route path="/orders" element={<ProtectedRoute allowedRoles={["CLIENT"]}><OrdersPage /></ProtectedRoute>} /> */}

          {/* Seller-only page */}
          {/* <Route path="/products" element={<ProtectedRoute allowedRoles={["SELLER"]}><ProductsPage /></ProtectedRoute>} /> */}

          {/* Admin-only page */}
          {/* <Route path="/admin" element={<ProtectedRoute allowedRoles={["ADMIN"]}><AdminPage /></ProtectedRoute>} /> */}

          {/* Multiple roles allowed */}
          {/* <Route path="/analytics" element={<ProtectedRoute allowedRoles={["ADMIN", "SELLER"]}><AnalyticsPage /></ProtectedRoute>} /> */}
        </Route>
      </Routes>
    </AuthProvider>
  )
}

export default AppRouter