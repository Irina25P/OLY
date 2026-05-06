import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShoppingBag, CreditCard, Truck, CheckCircle, X } from 'lucide-react';
import { useCart } from '../../contexts/CartContext';
import { api } from '../../services/api';
import type { CardForm, PaymentMethod, ShippingForm } from '../../models/CheckoutModels';
import {
  formatCardNumber,
  formatExpiry,
  getClientIdFromJwt,
  isCardValid,
  isShippingValid,
} from '../../validators/checkoutValidations';
import { COUNTY_NAMES, formatCountyForBackend, ROMANIA_COUNTIES } from '../../models/romaniaData';

const CheckoutPage = () => {
  const { cartItems, totalPrice, clearCart } = useCart();
  const navigate = useNavigate();

  const [shipping, setShipping] = useState<ShippingForm>({
    county: '',
    city: '',
    street: ''
  });

  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CASH_ON_DELIVERY');

  const [card, setCard] = useState<CardForm>({
    cardholderName: '',
    cardNumber: '',
    expiry: '',
    cvv: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const MINIO_BASE = "http://92.5.49.167:9000/disi-images";
  const PLACEHOLDER = "https://images.unsplash.com/photo-1560393464-5c69a73c5770?auto=format&fit=crop&w=900&q=80";

  const handleShippingChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    if (name === 'county') {
      setShipping((prev) => ({ ...prev, county: value, city: '' }));
    } else {
      setShipping((prev) => ({ ...prev, [name]: value }));
    }
  };

    const getImageUrl = (url: string | undefined) => {
        if (!url) return PLACEHOLDER;
        if (url.startsWith("http")) return url;
        return `${MINIO_BASE}/${url}`;
    };

  const handleCardChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    if (name === 'cardNumber') {
      setCard((prev) => ({ ...prev, cardNumber: formatCardNumber(value) }));
    } else if (name === 'expiry') {
      setCard((prev) => ({ ...prev, expiry: formatExpiry(value) }));
    } else if (name === 'cvv') {
      setCard((prev) => ({ ...prev, cvv: value.replace(/\D/g, '').slice(0, 3) }));
    } else {
      setCard((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handlePlaceOrder = async () => {
    console.log(!isShippingValid(shipping))
    if (!isShippingValid(shipping)) {
      setError('Please fill in all required shipping fields.');
      return;
    }
    console.log('DA')
    if (!isCardValid(paymentMethod, card)) {
      setError('Please fill in all card details correctly.');
      return;
    }

    const clientId = getClientIdFromJwt();
    console.log(clientId);
    console.log(getClientIdFromJwt());
    if (!clientId) {
      setError('You must be logged in to place an order.');
      return;
    }

    setError(null);
    setLoading(true);

    console.log(cartItems);

    // Group cart items by sellerId
    const itemsBySeller = cartItems.reduce<Record<string, typeof cartItems>>((acc, item) => {
      const key = item.sellerId;
      if (!acc[key]) acc[key] = [];
      acc[key].push(item);
      return acc;
    }, {});
    console.log(itemsBySeller)

    console.log(formatCountyForBackend(shipping.county.trim()));
    console.log(clientId, paymentMethod, shipping.city.trim(), shipping.street.trim());

    try {
      const requests = Object.entries(itemsBySeller).map(([sellerId, items]) =>
        api.post('/v1/orders', {
          order: {
            clientId,
            sellerId,
            statusType: "PLACED",
            payment: paymentMethod,
            county: formatCountyForBackend(shipping.county.trim()),
            city: shipping.city.trim(),
            street: shipping.street.trim(),
          },
          orderProducts: items.map((item) => ({
            productId: item.id,
            quantity: item.quantity,
          })),
        })
      );

      const responses = await Promise.all(requests);
      console.log("Comenzi trimise cu succes!", responses);
      clearCart();
      setSuccess(true);
    } catch {
      setError('Failed to place your order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center max-w-md">
          <CheckCircle size={64} className="mx-auto mb-4 text-green-500" />
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Order Placed!</h1>
          <p className="text-gray-500 mb-6">
            Thank you for your purchase. You'll receive a confirmation soon.
          </p>
          <button
            onClick={() => navigate('/products')}
            className="px-6 py-2 bg-[#54457f] !text-white rounded-lg font-semibold hover:bg-[#9BA2FF] transition-colors"
          >
            Continue Shopping
          </button>
        </div>
      </div>
    );
  }

  if (cartItems.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center max-w-md">
          <ShoppingBag size={64} className="mx-auto mb-4 text-gray-300" />
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Your cart is empty</h1>
          <p className="text-gray-500 mb-6">Add some products before checking out.</p>
          <button
            onClick={() => navigate('/products')}
            className="px-6 py-2 bg-[#54457f] !text-white rounded-lg font-semibold hover:bg-[#9BA2FF] transition-colors"
          >
            Browse Products
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4">
        <h1 className="text-2xl font-bold text-gray-900 !mb-8">Checkout</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

          {/* Left: Items + Shipping */}
          <div className="lg:col-span-2 space-y-6">

            {/* Order Items */}
            <section className="bg-white rounded-xl border border-gray-200 overflow-hidden">
              <div className="p-5 border-b border-gray-100">
                <h2 className="font-bold text-gray-900 flex items-center gap-2">
                  <ShoppingBag size={18} className="text-[#54457f]" />
                  Order Items ({cartItems.length})
                </h2>
              </div>
              <div className="divide-y divide-gray-100">
                {cartItems.map((item) => (
                  <div key={item.id} className="flex items-center gap-4 p-4">
                    <div className="w-14 h-14 flex-shrink-0 rounded-lg overflow-hidden bg-gray-100 border border-gray-200">
                        <img
                            src={getImageUrl(item.imageUrls?.[0])}
                            alt={item.title}
                            onError={(e) => {
                                (e.currentTarget.src = PLACEHOLDER);
                            }}
                            className="w-full h-full object-cover"
                        />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-gray-900 truncate text-sm">{item.title}</p>
                      <p className="text-xs text-gray-500 mt-0.5">Qty: {item.quantity}</p>
                    </div>
                    <p className="font-bold text-[#54457f] text-sm whitespace-nowrap">
                      {(item.price * item.quantity).toFixed(2)} $
                    </p>
                  </div>
                ))}
              </div>
            </section>

            {/* Shipping Details */}
            <section className="bg-white rounded-xl border border-gray-200 overflow-hidden">
              <div className="p-5 border-b border-gray-100">
                <h2 className="font-bold text-gray-900 flex items-center gap-2">
                  <Truck size={18} className="text-[#54457f]" />
                  Shipping Details
                </h2>
              </div>
              <div className="p-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">
                    County <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="county"
                    value={shipping.county}
                    onChange={handleShippingChange}
                    className="w-full pr-5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-[#54457f] focus:border-transparent bg-white"
                  >
                    <option value="">Select county</option>
                    {COUNTY_NAMES.map((c) => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-600 mb-1">
                    City <span className="text-red-500">*</span>
                  </label>
                  <select
                    name="city"
                    value={shipping.city}
                    onChange={handleShippingChange}
                    disabled={!shipping.county}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-[#54457f] focus:border-transparent bg-white disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <option value="">Select city</option>
                    {(ROMANIA_COUNTIES[shipping.county] ?? []).map((city) => (
                      <option key={city} value={city}>{city}</option>
                    ))}
                  </select>
                </div>
                <div className="sm:col-span-2">
                  <label className="block text-xs font-semibold text-gray-600 mb-1">
                    Street <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="street"
                    value={shipping.street}
                    onChange={handleShippingChange}
                    placeholder="Street name..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-[#54457f] focus:border-transparent"
                  />
                </div>
              </div>
            </section>
          </div>

          {/* Right: Payment + Summary */}
          <div className="space-y-6">

            {/* Payment Method */}
            <section className="bg-white rounded-xl border border-gray-200 overflow-hidden">
              <div className="p-5 border-b border-gray-100">
                <h2 className="font-bold text-gray-900 flex items-center gap-2">
                  <CreditCard size={18} className="text-[#54457f]" />
                  Payment Method
                </h2>
              </div>
              <div className="p-5 space-y-2">
                {/* Cash on Delivery */}
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="payment"
                    value="CASH_ON_DELIVERY"
                    checked={paymentMethod === 'CASH_ON_DELIVERY'}
                    onChange={() => setPaymentMethod('CASH_ON_DELIVERY')}
                    className="accent-[#54457f] !w-8"
                  />
                  <Truck size={16} className="text-gray-500 " />
                  <span className="text-lg text-gray-800">Cash on Delivery</span>
                </label>

                {/* Card */}
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="payment"
                    value="CARD"
                    checked={paymentMethod === 'CARD'}
                    onChange={() => setPaymentMethod('CARD')}
                    className="accent-[#54457f] !w-8"
                  />
                  <CreditCard size={16} className="text-gray-500" />
                  <span className="text-lg text-gray-800">Card</span>
                </label>

                {/* Mock Card Form */}
                {paymentMethod === 'CARD' && (
                  <div className="mt-2 p-4 bg-gradient-to-br from-[#54457f] to-[#9BA2FF] rounded-xl space-y-3">
                    <div>
                      <label className="block text-[10px] font-semibold text-white/70 mb-1 uppercase tracking-wide">
                        Cardholder Name
                      </label>
                      <input
                        type="text"
                        name="cardholderName"
                        value={card.cardholderName}
                        onChange={handleCardChange}
                        placeholder="JOHN DOE"
                        className="w-full px-3 py-2 bg-white/20 border border-white/30 rounded-lg text-sm text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-white/50"
                      />
                    </div>
                    <div>
                      <label className="block text-[10px] font-semibold text-white/70 mb-1 uppercase tracking-wide">
                        Card Number
                      </label>
                      <input
                        type="text"
                        name="cardNumber"
                        value={card.cardNumber}
                        onChange={handleCardChange}
                        placeholder="0000 0000 0000 0000"
                        inputMode="numeric"
                        className="w-full px-3 py-2 bg-white/20 border border-white/30 rounded-lg text-sm text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-white/50 tracking-widest"
                      />
                    </div>
                    <div className="flex gap-3">
                      <div className="flex-1">
                        <label className="block text-[10px] font-semibold text-white/70 mb-1 uppercase tracking-wide">
                          Expiry
                        </label>
                        <input
                          type="text"
                          name="expiry"
                          value={card.expiry}
                          onChange={handleCardChange}
                          placeholder="MM/YY"
                          inputMode="numeric"
                          className="w-full px-3 py-2 bg-white/20 border border-white/30 rounded-lg text-sm text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-white/50"
                        />
                      </div>
                      <div className="flex-1">
                        <label className="block text-[10px] font-semibold text-white/70 mb-1 uppercase tracking-wide">
                          CVV
                        </label>
                        <input
                          type="password"
                          name="cvv"
                          value={card.cvv}
                          onChange={handleCardChange}
                          placeholder="•••"
                          inputMode="numeric"
                          className="w-full px-3 py-2 bg-white/20 border border-white/30 rounded-lg text-sm text-white placeholder-white/50 focus:outline-none focus:ring-2 focus:ring-white/50"
                        />
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </section>

            {/* Order Summary */}
            <section className="bg-white rounded-xl border border-gray-200 overflow-hidden">
              <div className="p-5 border-b border-gray-100">
                <h2 className="font-bold text-gray-900">Order Summary</h2>
              </div>
              <div className="p-5 space-y-3">
                {cartItems.map((item) => (
                  <div key={item.id} className="flex justify-between text-sm">
                    <span className="text-gray-600 truncate mr-2">
                      {item.title} <span className="text-gray-400">x{item.quantity}</span>
                    </span>
                    <span className="text-gray-900 font-medium whitespace-nowrap">
                      {(item.price * item.quantity).toFixed(2)} $
                    </span>
                  </div>
                ))}
                <div className="border-t border-gray-100 pt-3 flex justify-between font-bold text-base">
                  <span>Total</span>
                  <span className="text-[#54457f]">{totalPrice.toFixed(2)} $</span>
                </div>
              </div>

              {error && (
                <div className="mx-5 mb-4 flex items-start gap-2 p-3 bg-red-50 border border-red-200 rounded-lg text-xs text-red-600">
                  <X size={14} className="flex-shrink-0 mt-0.5" />
                  {error}
                </div>
              )}

              <div className="px-5 pb-5">
                <button
                  onClick={handlePlaceOrder}
                  disabled={loading}
                  className="w-full py-3 !bg-[#54457f] !text-white rounded-lg font-bold text-sm hover:!bg-[#9BA2FF] transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
                >
                  {loading ? 'Placing Order...' : 'Place Order'}
                </button>
              </div>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
