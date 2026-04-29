import Cookies from 'js-cookie';
import { COOKIE_JWT, type JwtPayload } from '../constants/constants';
import type { CardForm, PaymentMethod, ShippingForm } from '../models/CheckoutModels';

export const getClientIdFromJwt = (): string | null => {
  const token = Cookies.get(COOKIE_JWT);
  if (!token) return null;
  try {
    const payload: JwtPayload = JSON.parse(atob(token.split('.')[1]));
    return payload.userId;
  } catch {
    return null;
  }
};

export const formatCardNumber = (value: string) =>
  value.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim();

export const formatExpiry = (value: string) => {
  const digits = value.replace(/\D/g, '').slice(0, 4);
  if (digits.length >= 3) return `${digits.slice(0, 2)}/${digits.slice(2)}`;
  return digits;
};

export const isShippingValid = (shipping: ShippingForm): boolean =>
  !!(shipping.county.trim() &&
    shipping.city.trim() &&
    shipping.street.trim());

export const isCardValid = (paymentMethod: PaymentMethod, card: CardForm): boolean =>
  paymentMethod !== 'CARD' ||
  !!(card.cardholderName.trim() &&
    card.cardNumber.replace(/\s/g, '').length === 16 &&
    card.expiry.length === 5 &&
    card.cvv.length === 3);
