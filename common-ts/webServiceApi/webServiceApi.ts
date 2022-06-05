import axios, {AxiosRequestConfig} from 'axios'

import {routes} from './routes'
import {datify} from '../utils/time'
import {Pageable, PageRequest} from "./interface/pageable";
import {PaymentDetails, PaymentForm, PaymentInfo, PublicPaymentDetails} from "./interface/payment";
import {KeyUploadForm, LoginForm, LoginResponse, RefreshTokenResponse, RegisterForm} from "./interface/auth";
import {WalletForm, WalletInfo} from "./interface/wallet";
import {AdminUser} from "./interface/user";

export type Response<T> = {
    data?: T
    status: number
}

const defaultConfig: AxiosRequestConfig = {
    method: 'post'
}

type RefreshTokenFactory = () => (string | Promise<string | null>)

export class WebServiceApi {
    public authHeader: string | null = null

    private readonly host?: string
    private readonly refreshTokenFactory: RefreshTokenFactory

    constructor(refreshTokenFactory: RefreshTokenFactory, host?: string) {
        this.host = host
        this.refreshTokenFactory = refreshTokenFactory
    }

    public api = {
        auth: {
            login: async (data: LoginForm): Promise<Response<LoginResponse>> => {
                return await this.request(routes.auth.login, {data})
            },
            register: (data: RegisterForm): Promise<Response<number>> => {
                return this.request(routes.auth.register, {data})
            },
            refreshToken: (): Promise<Response<RefreshTokenResponse>> => {
                return this.request(routes.auth.refreshToken, {method: 'get'})
            }
        },
        payment: {
            create: async (data: PaymentForm): Promise<Response<PaymentDetails>> => {
                const response: Response<PaymentDetails> = await this.request(
                    routes.payments.index,
                    {data}
                )

                if (response.data) {
                    datify(response.data)
                }

                return response
            },
            info: async (): Promise<Response<PaymentInfo>> => {
                return this.request(routes.payments.info, {method: 'get'})
            },
            history: async (
                params: PageRequest
            ): Promise<Response<Pageable<PaymentDetails>>> => {
                return this.request(routes.payments.index, {
                    method: 'get',
                    params
                })
            },
            historyAll: async (
                params: PageRequest
            ): Promise<Response<Pageable<PublicPaymentDetails>>> => {
                return this.request(routes.payments.all, {
                    method: 'get',
                    params
                })
            }
        },
        admins: {
            getAll: async (
                params?: PageRequest
            ): Promise<Response<Pageable<AdminUser>>> =>
                this.request(routes.admins.index, {method: 'get', params}),

            create: (data: RegisterForm): Promise<Response<unknown>> =>
                this.request(routes.admins.index, {data}),

            remove: (data: AdminUser): Promise<Response<unknown>> =>
                this.request(routes.admins.index, {method: 'delete', data}),

            uploadKeys: (data: KeyUploadForm): Promise<Response<unknown>> =>
                this.request(routes.admins.keys, {method: 'patch', data})
        },
        wallet: {
            getInfo: (): Promise<Response<WalletInfo>> =>
                this.request(routes.wallet.index, {method: 'get'}),

            closeChannels: (withForce: boolean): Promise<Response<unknown>> =>
                this.request(routes.wallet.closeChannels, {
                    params: {withForce: withForce}
                }),

            transfer: (): Promise<Response<unknown>> =>
                this.request(routes.wallet.transfer),

            create: (data: WalletForm): Promise<Response<unknown>> =>
                this.request(routes.wallet.index, {data})
        }
    }

    private async reloadAuthHeader() {
        this.authHeader = `Bearer ${await this.refreshTokenFactory()}`
    }

    private async configFactory(url: string, config: AxiosRequestConfig) {
        const result = {url, ...defaultConfig, ...config}

        await this.reloadAuthHeader()
        if (this.authHeader) {
            if (!result.headers) {
                result.headers = {}
            }
            if (!result.headers.Authorization) {
                result.headers.Authorization = this.authHeader
            }
        }

        return result
    }

    private async request<D, T>(
        url: string,
        config: AxiosRequestConfig<D> = {}
    ): Promise<Response<T>> {
        try {
            url = this.resolveRoute(url)
            const response = await axios.request(await this.configFactory(url, config))
            return {data: response.data, status: response.status}
        } catch (e) {
            if (axios.isAxiosError(e) && e.response?.status) {
                return {status: e.response.status}
            } else {
                throw e
            }
        }
    }

    private resolveRoute(route: string) {
        return this.host && route.startsWith('/') ? `${this.host}${route}` : route;
    }
}
