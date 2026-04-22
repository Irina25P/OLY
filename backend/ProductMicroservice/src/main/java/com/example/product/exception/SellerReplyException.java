package com.example.product.exception;

public class SellerReplyException extends RuntimeException {
    public SellerReplyException(String message) {
        super(message);
    }

    public SellerReplyException(String message, Throwable cause) {
        super(message, cause);
    }
}
