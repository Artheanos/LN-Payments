"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.WebServiceApi = void 0;
const axios_1 = __importDefault(require("axios"));
const routes_1 = require("./routes");
const time_1 = require("../utils/time");
const defaultConfig = {
    method: 'post'
};
class WebServiceApi {
    authHeader = null;
    host;
    refreshTokenFactory;
    constructor(refreshTokenFactory, host) {
        this.host = host;
        this.refreshTokenFactory = refreshTokenFactory;
    }
    api = {
        auth: {
            login: async (data) => {
                return await this.request(routes_1.routes.auth.login, { data });
            },
            register: (data) => {
                return this.request(routes_1.routes.auth.register, { data });
            },
            refreshToken: () => {
                return this.request(routes_1.routes.auth.refreshToken, { method: 'get' });
            }
        },
        payment: {
            create: async (data) => {
                const response = await this.request(routes_1.routes.payments.index, { data });
                if (response.data) {
                    (0, time_1.datify)(response.data);
                }
                return response;
            },
            info: async () => {
                return this.request(routes_1.routes.payments.info, { method: 'get' });
            },
            history: async (params) => {
                return this.request(routes_1.routes.payments.index, {
                    method: 'get',
                    params
                });
            },
            historyAll: async (params) => {
                return this.request(routes_1.routes.payments.all, {
                    method: 'get',
                    params
                });
            }
        },
        admins: {
            getAll: async (params) => this.request(routes_1.routes.admins.index, { method: 'get', params }),
            create: (data) => this.request(routes_1.routes.admins.index, { data })
        },
        wallet: {
            getInfo: () => this.request(routes_1.routes.wallet.index, { method: 'get' }),
            closeChannels: (withForce) => this.request(routes_1.routes.wallet.closeChannels, {
                params: { withForce: withForce }
            }),
            transfer: () => this.request(routes_1.routes.wallet.transfer),
            create: (data) => this.request(routes_1.routes.wallet.index, { data })
        }
    };
    async reloadAuthHeader() {
        this.authHeader = `Bearer ${await this.refreshTokenFactory()}`;
    }
    async configFactory(url, config) {
        const result = { url, ...defaultConfig, ...config };
        await this.reloadAuthHeader();
        if (this.authHeader) {
            if (!result.headers) {
                result.headers = {};
            }
            if (!result.headers.Authorization) {
                result.headers.Authorization = this.authHeader;
            }
        }
        return result;
    }
    async request(url, config = {}) {
        try {
            url = this.resolveRoute(url);
            const response = await axios_1.default.request(await this.configFactory(url, config));
            return { data: response.data, status: response.status };
        }
        catch (e) {
            if (axios_1.default.isAxiosError(e) && e.response?.status) {
                return { status: e.response.status };
            }
            else {
                throw e;
            }
        }
    }
    resolveRoute(route) {
        return this.host && route.startsWith('/') ? `${this.host}${route}` : route;
    }
}
exports.WebServiceApi = WebServiceApi;
