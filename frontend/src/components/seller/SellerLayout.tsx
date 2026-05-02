import {
    BarChart3,
    Box,
    ClipboardList,
    Grid2X2,
    HelpCircle,
    LogOut,
    MessageSquareText,
    Settings,
} from "lucide-react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import logo from "../../assets/logo.png";
import { useAuth } from "../../context/AuthContext";

const navItems = [
    { label: "Dashboard", path: "/seller", icon: Grid2X2 },
    { label: "Inventory", path: "/seller/products", icon: Box },
    { label: "Orders", path: "/seller/orders", icon: ClipboardList },
    { label: "Analytics", path: "/seller/analytics", icon: BarChart3 },
    { label: "Reviews", path: "/seller/reviews", icon: MessageSquareText },
    { label: "Settings", path: "/seller/settings", icon: Settings },
];

function SellerLayout() {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <div className="flex min-h-screen w-full bg-[#F8F8F8] text-[#000000]">
            <aside className="sticky top-0 flex h-screen w-[260px] shrink-0 flex-col overflow-y-auto border-r border-gray-200 bg-[#F4F7FB] px-6 py-6">
                <div>
                    <img src={logo} alt="OLY logo" className="h-16 w-16 object-contain" />

                    <p className="mt-3 text-[11px] font-semibold uppercase tracking-[0.12em] text-[#6B7890]">
                        Marketplace Admin
                    </p>

                    <p className="max-w-[180px] text-2xl font-extrabold leading-6 text-[#000000]">
                        OLY Marketplace
                    </p>
                </div>

                <nav className="mt-9 space-y-2">
                    {navItems.map((item) => {
                        const Icon = item.icon;

                        return (
                            <NavLink
                                key={item.path}
                                to={item.path}
                                end={item.path === "/seller"}
                                className={({ isActive }) =>
                                    `flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition ${
                                        isActive
                                            ? "bg-[#FFFFFF] text-[#54457F] shadow-sm"
                                            : "text-[#65758F] hover:bg-white/70 hover:text-[#54457F]"
                                    }`
                                }
                            >
                                <Icon size={18} />
                                <span>{item.label}</span>
                            </NavLink>
                        );
                    })}
                </nav>

                <div className="mt-auto border-t border-gray-200 pt-5">
                    <button className="flex items-center gap-3 px-4 py-3 text-sm font-medium text-[#65758F] hover:text-[#54457F]">
                        <HelpCircle size={18} />
                        Help Center
                    </button>

                    <button
                        onClick={handleLogout}
                        className="flex items-center gap-3 px-4 py-3 text-sm font-medium text-[#65758F] hover:text-[#E85D75]"
                    >
                        <LogOut size={18} />
                        Logout
                    </button>
                </div>
            </aside>

            <main className="min-w-0 flex-1 px-12 py-10">
                <Outlet />
            </main>
        </div>
    );
}

export default SellerLayout;