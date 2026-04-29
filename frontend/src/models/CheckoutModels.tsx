export type PaymentMethod = 'CARD' | 'CASH_ON_DELIVERY';

export interface ShippingForm {
  county: string;
  city: string;
  street: string;
}

export interface CardForm {
  cardholderName: string;
  cardNumber: string;
  expiry: string;
  cvv: string;
}
