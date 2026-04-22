import { Routes, Route } from "react-router-dom"
import HomePage from "../pages/public/HomePage"
import LoginPage from "../pages/auth/LoginPage"
import DashboardPage from "../pages/dashboard/DashboardPage"

function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/dashboard" element={<DashboardPage />} />
    </Routes>
  )
}

export default AppRouter