import { Link, useLocation } from 'react-router-dom';
import { useCart } from '../../contexts/CartContext';
import logo from '../../assets/logo.png';
import { ShoppingBag, X } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const MINIO_BASE = "http://92.5.49.167:9000/disi-images";
const PLACEHOLDER = "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";

const ClientTopBar = () => {
    const {totalItems, cartItems, totalPrice, removeFromCart} = useCart();
    const location = useLocation();

    const {user, isAuthenticated, logout} = useAuth();

    const getImageUrl = (url: string | undefined) => {
        if (!url) return PLACEHOLDER;
        if (url.startsWith("http")) return url;
        return `${MINIO_BASE}/${url}`;
    };

    const getLinkClass = (path: string) => {
        const isActive = location.pathname === path;
        return `pb-1 transition-all duration-200 ease-in-out border-b-2 ${isActive
            ? 'font-bold text-[#54457f] border-[#54457f]'
            : 'font-normal text-gray-600 border-transparent hover:text-[#54457f]'
        }`;
    };

    return (
        <header className="flex items-center justify-between p-4 border-b border-gray-300 bg-white">

            <Link className="flex items-center gap-8" to="/">
                <img src={logo} alt="Logo" className="h-[48px] w-auto object-contain"/>
            </Link>

            {(user === null || user?.role !== "ADMIN") && (
                <nav className="flex gap-8 mx-8">
                    <Link to="/products" className={getLinkClass('/products')}>Products</Link>
                    <Link to="/orders" className={getLinkClass('/orders')}>Orders</Link>
                    <Link to="/profile" className={getLinkClass('/profile')}>Profile</Link>
                </nav>
            )}

            <div className="flex items-center gap-6">

                {(user === null || user?.role !== "ADMIN") && (
                    <div className="relative group py-2">
                        <Link to="/checkout"
                              className="text-gray-800 hover:text-[#9BA2FF] transition-colors flex items-center">
                            <ShoppingBag size={24} strokeWidth={2.5}/>
                            {totalItems > 0 && (
                                <span
                                    className="absolute -top-0.5 -right-2 flex items-center justify-center min-w-[18px] h-[18px] px-1 bg-red-700 text-white rounded-full text-[10px] font-bold border-2 border-white">
                                {totalItems}
                            </span>
                            )}
                        </Link>

                        {/* Hover Popup Menu */}
                        <div
                            className="invisible group-hover:visible opacity-0 group-hover:opacity-100 absolute right-0 mt-2 w-72 bg-white border border-gray-200 rounded-xl shadow-xl transition-all duration-300 z-50 overflow-hidden">
                            <div className="p-4 border-b border-gray-100">
                                <h3 className="text-sm font-bold text-gray-800 m-0">Shopping Cart</h3>
                            </div>

                            <div className="max-h-60 overflow-y-auto">
                                {cartItems.length > 0 ? (
                                    cartItems.map((item) => (
                                        <div key={item.id}
                                             className="group/item flex items-center gap-3 p-3 hover:bg-gray-50 transition-colors relative">
                                            <div
                                                className="w-12 h-12 flex-shrink-0 bg-gray-100 rounded-lg overflow-hidden border border-gray-200">
                                                <img
                                                    src={getImageUrl(item.imageUrls[0])}
                                                    alt={item.title}
                                                    onError={(e) => {
                                                        (e.target as HTMLImageElement).src = PLACEHOLDER;
                                                    }}
                                                    className="w-full h-full object-cover"
                                                />
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="text-xs font-bold text-gray-900 truncate m-0 leading-tight">{item.title}</p>
                                                <p className="text-[10px] text-gray-500 m-0">{item.quantity} x {item.price} $</p>
                                            </div>
                                            <button
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    removeFromCart(item.id);
                                                }}
                                                className="p-1 hover:bg-red-100 hover:text-red-600 text-gray-400 rounded-md transition-all opacity-0 group-hover/item:opacity-100"
                                            >
                                                <X size={14}/>
                                            </button>
                                        </div>
                                    ))
                                ) : (
                                    <div className="p-8 text-center text-gray-400 text-xs">Your cart is empty</div>
                                )}
                            </div>

                            {cartItems.length > 0 && (
                                <div className="p-4 bg-gray-50 border-t border-gray-100">
                                    <div className="flex justify-between mb-3 text-sm font-bold">
                                        <span>Total:</span>
                                        <span className="text-[#54457f]">{totalPrice} $</span>
                                    </div>
                                    <Link to="/checkout"
                                          className="block w-full text-center bg-[#54457f] !text-white py-2 rounded-lg text-xs font-bold hover:bg-[#9BA2FF] hover:!text-black transition-colors">
                                        Proceed to Order
                                    </Link>
                                </div>
                            )}
                        </div>
                    </div>
                )}

                {/* Auth Section */}
                <div className="flex items-center gap-4 border-l pl-6 border-gray-200">
                    {isAuthenticated ? (
                        <>
                            <span className="text-gray-700 font-medium">Hi, {user?.username}</span>
                            <button
                                onClick={logout}
                                className="px-4 py-2 rounded-lg font-semibold !text-[#54457f] hover:!bg-[#9BA2FF] hover:!text-white transition-all"
                            >
                                Logout
                            </button>
                        </>
                    ) : (
                        <Link
                            to="/login"
                            className="px-4 py-2 rounded-lg font-semibold !text-[#54457f] hover:!bg-[#9BA2FF] hover:!text-white transition-all"
                        >
                            Sign In
                        </Link>
                    )}
                </div>
            </div>
        </header>
    );
}

export default ClientTopBar;