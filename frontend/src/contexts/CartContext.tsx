import { createContext, useState, useContext, type ReactNode, useEffect } from "react";
import type { Product } from "../models/Product";
import { CART_STORAGE_KEY } from "../constants/constants";

export interface CartItem extends Product {
    quantity: number;
}

interface CartContextType {
    cartItems: CartItem[];
    addToCart: (product: Product) => void;
    removeFromCart: (productId: string) => void;
    clearCart: () => void;
    totalItems: number;
    totalPrice: number;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

interface CartProviderProps {
    children: ReactNode;
}

interface StoredCart {
    items: CartItem[];
    expiry: number; // The timestamp
}

export const CartProvider = ({ children }: CartProviderProps) => {
    const [cartItems, setCartItems] = useState<CartItem[]>([]);

    useEffect(() => {
        const storedCart = localStorage.getItem(CART_STORAGE_KEY);
        if (storedCart) {
            const parsedCart: StoredCart = JSON.parse(storedCart);
            const now = new Date().getTime();

            if (now > parsedCart.expiry) {
                localStorage.removeItem(CART_STORAGE_KEY);
            } else {
                setCartItems(parsedCart.items);
            }
        }
    }, []);

    useEffect(() => {
        if (cartItems.length === 0) {
            localStorage.removeItem(CART_STORAGE_KEY);
            return;
        }
        const expiryTime = new Date().getTime() + 24 * 60 * 60 * 1000; // 24 hours
        const cartData: StoredCart = {
            items: cartItems,
            expiry: expiryTime,
        };
        localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cartData));
    }, [cartItems]);

    const addToCart = (product: Product) => {
        setCartItems((prevItems) => {
            const existingItem = prevItems.find(item => item.id === product.id);

            if (existingItem) {
                return prevItems.map(item =>
                    item.id === product.id
                        ? { ...item, quantity: item.quantity + 1 }
                        : item
                );
            }
            // If not in cart, add it with a quantity of 1
            return [...prevItems, { ...product, quantity: 1 }];
        });
    };

    const removeFromCart = (productId: string) => {
      setCartItems((prevItems) => prevItems.filter(item => item.id !== productId));
    };

    const clearCart = () => {
        setCartItems([]);
    };

    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);

    return (
        <CartContext.Provider value={{ cartItems, addToCart, removeFromCart, clearCart, totalItems, totalPrice }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) {
        throw new Error("useCart must be used within a CartProvider");
    }
    return context;
};