package constants;

public class ApiEndpoints {
    // Auth endpoints
    public static final String AUTH_REGISTER = "/auth/register";
    public static final String AUTH_REGISTER_SELLER = "/auth/register/seller";
    public static final String AUTH_REGISTER_CLIENT = "/auth/register/client";
    public static final String AUTH_LOGIN = "/auth/login";

    // User endpoints
    public static final String USERS_SELLER_PROFILE = "/users/seller/profile";
    public static final String USERS_CLIENT_PROFILE = "/users/client/profile";

    // Image endpoints
    public static final String IMAGES_UPLOAD = "/images";

    // Product endpoints
    public static final String PRODUCTS = "/products";
    public static final String PRODUCTS_SEARCH = "/products/search";

    // Review endpoints
    public static final String REVIEWS = "/reviews";

    // Order endpoints
    public static final String ORDERS = "/orders";
    public static final String ORDERS_CLIENT = "/orders/client";
    public static final String ORDERS_SELLER = "/orders/seller";
}

