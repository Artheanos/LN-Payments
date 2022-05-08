import { Pageable, PageRequest } from "./interface/pageable";
import { PaymentDetails, PaymentForm, PaymentInfo, PublicPaymentDetails } from "./interface/payment";
import { LoginForm, LoginResponse, RefreshTokenResponse, RegisterForm } from "./interface/auth";
import { WalletForm, WalletInfo } from "./interface/wallet";
import { AdminUser } from "./interface/user";
export declare type Response<T> = {
    data?: T;
    status: number;
};
declare type RefreshTokenFactory = () => (string | Promise<string | null>);
export declare class WebServiceApi {
    authHeader: string | null;
    private readonly host?;
    private readonly refreshTokenFactory;
    constructor(refreshTokenFactory: RefreshTokenFactory, host?: string);
    api: {
        auth: {
            login: (data: LoginForm) => Promise<Response<LoginResponse>>;
            register: (data: RegisterForm) => Promise<Response<number>>;
            refreshToken: () => Promise<Response<RefreshTokenResponse>>;
        };
        payment: {
            create: (data: PaymentForm) => Promise<Response<PaymentDetails>>;
            info: () => Promise<Response<PaymentInfo>>;
            history: (params: PageRequest) => Promise<Response<Pageable<PaymentDetails>>>;
            historyAll: (params: PageRequest) => Promise<Response<Pageable<PublicPaymentDetails>>>;
        };
        admins: {
            getAll: (params?: PageRequest | undefined) => Promise<Response<Pageable<AdminUser>>>;
            create: (data: RegisterForm) => Promise<Response<unknown>>;
        };
        wallet: {
            getInfo: () => Promise<Response<WalletInfo>>;
            closeChannels: (withForce: boolean) => Promise<Response<unknown>>;
            transfer: () => Promise<Response<unknown>>;
            create: (data: WalletForm) => Promise<Response<unknown>>;
        };
    };
    private reloadAuthHeader;
    private configFactory;
    private request;
    private resolveRoute;
}
export {};
