import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Eye, EyeOff, Mail, Lock } from "lucide-react";
import { jwtDecode } from "jwt-decode";
import { useAuth } from "../../context/AuthContext";
import logo from "../../assets/logo.png";

function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(username, password);
      const stored = localStorage.getItem("token");
      const role = stored ? jwtDecode<{ role: string }>(stored).role : null;
      navigate(role === "SELLER" ? "/seller" : "/dashboard");
    } catch {
      setError("Invalid credentials. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-md shadow-lg rounded-2xl px-8 pt-6 pb-6">
        {/* Header — above the card */}
        <div className="flex flex-col items-center px-4">
          <img src={logo} alt="OLY Marketplace" className="h-16 w-auto mb-3" />
          <h1 className="text-3xl font-bold text-gray-900 mt-2">Welcome Back</h1>
        </div>

        {/* Gradient divider */}
        <div className="h-1 w-full bg-gradient-to-r from-[#54457f] to-[#9ba2ff] mt-6 rounded-full" />

        {/* Form card */}
        <div className="px-8 pt-6 pb-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                {error}
              </div>
            )}

            {/* Username */}
            <div>
              <label className="block text-xs font-semibold text-gray-600 uppercase tracking-wider mb-1">
                Username
              </label>
              <div className="flex items-center rounded-xl px-4 py-3 gap-2">
                <Mail className="w-4 h-4 text-gray-400 shrink-0" />
                <input
                  type="text"
                  placeholder="Enter your username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                  className="flex-1 bg-transparent text-sm text-gray-700 outline-none placeholder-gray-400"
                />
              </div>
            </div>

            {/* Password */}
            <div>
              <div className="flex items-center justify-between mb-1">
                <label className="block text-xs font-semibold text-gray-600 uppercase tracking-wider">
                  Password
                </label>
              </div>
              <div className="relative flex items-center rounded-xl px-4 py-3 gap-2">
                <Lock className="w-4 h-4 text-gray-400 shrink-0" />
                <input
                  type={showPassword ? "text" : "password"}
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  /* Increased padding-right (pr-10) to make room for the shifted icon */
                  className="flex-1 bg-transparent text-sm text-gray-700 outline-none placeholder-gray-400 pr-10"
                />
                {/* Changed right-4 to right-6 to move the icon further to the left */}
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  className="absolute right-6 text-gray-400 hover:text-gray-600 flex items-center"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 rounded-xl text-white font-semibold text-sm !bg-gradient-to-r from-[#54457f] to-[#9ba2ff] hover:from-[#54457f]/[0.9] hover:to-[#9ba2ff]/[0.9] transition-all disabled:opacity-60"
            >
              {loading ? "Signing in..." : "Sign In"}
            </button>

            <div className="border-t border-gray-200 pt-4 text-center text-sm text-gray-500">
              Don't have an account?{" "}
              <Link to="/register" className="font-bold text-gray-800 hover:underline">
                Register
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;