import { Link } from "react-router-dom";

function Footer() {
  return (
    <footer className="w-full border-t border-gray-200 bg-[#FFFFFF]">
      
      {/* TOP */}
      <div className="grid w-full grid-cols-3 gap-10 px-12 py-12">
        
        {/* Brand */}
        <div>
          <h3 className="text-xl font-bold text-[#54457F]">OLY</h3>
          <p className="mt-4 text-sm text-gray-500 max-w-sm">
            Magazin Virtual – Shop & More. A simple and modern online shop
            experience.
          </p>
        </div>

        {/* Shop */}
        <div>
          <h4 className="text-xs font-bold uppercase text-gray-400">
            Shop
          </h4>

          <ul className="mt-4 space-y-2 text-sm text-gray-600">
            <li>
              <Link to="/products">All Products</Link>
            </li>
            <li>
              <Link to="/collections">Collections</Link>
            </li>
            <li>
              <Link to="/dashboard">Dashboard</Link>
            </li>
          </ul>
        </div>

        {/* Support */}
        <div>
          <h4 className="text-xs font-bold uppercase text-gray-400">
            Support
          </h4>

          <ul className="mt-4 space-y-2 text-sm text-gray-600">
            <li>
              <Link to="/help">Help Center</Link>
            </li>
            <li>
              <Link to="/contact">Contact</Link>
            </li>
          </ul>
        </div>
      </div>

      {/* BOTTOM */}
      <div className="border-t border-gray-200">
        <div className="flex w-full items-center justify-between px-12 py-5 text-xs text-gray-400">
          <p>© 2026 OLY. All rights reserved.</p>

          <div className="flex gap-4">
            <Link to="/privacy">Privacy</Link>
            <Link to="/terms">Terms</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}

export default Footer;