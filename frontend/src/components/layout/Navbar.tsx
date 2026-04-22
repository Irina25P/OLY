import { Link } from "react-router-dom";
import logo from "../../assets/logo.png";

function Navbar() {
  return (
    <header className="w-full border-b border-gray-200 bg-[#FFFFFF]">
      <div className="flex w-full items-center justify-between px-12 py-4">
        
        {/* LEFT - Logo + Nav */}
        <div className="flex items-center gap-10">
            <Link to="/" className="flex items-center">
                <img
                    src={logo}
                    alt="OLY logo"
                    className="h-10 w-auto object-contain"
                />
            </Link>

          <nav className="flex items-center gap-6">
            <Link
              to="/"
              className="text-sm font-medium text-[#54457F] border-b-2 border-[#54457F] pb-1"
            >
              Shop
            </Link>

            <Link
              to="/collections"
              className="text-sm text-gray-600 hover:text-[#54457F]"
            >
              Collections
            </Link>

            <Link
              to="/about"
              className="text-sm text-gray-600 hover:text-[#54457F]"
            >
              About
            </Link>
          </nav>
        </div>

        {/* RIGHT - Actions */}
        <div className="flex items-center gap-5">
          <Link
            to="/login"
            className="text-sm text-gray-700 hover:text-[#54457F]"
          >
            Sign In
          </Link>

          <Link
            to="/dashboard"
            className="rounded-lg bg-[#54457F] px-4 py-2 text-sm font-semibold text-[#FFFFFF] hover:opacity-90"
          >
            Dashboard
          </Link>
        </div>
      </div>
    </header>
  );
}

export default Navbar;