"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.routes = void 0;
exports.routes = {
    payments: {
        all: '/api/payments/all',
        ws: (host) => `ws://${host || window.location.host}/api/payment`,
        index: '/api/payments',
        info: '/api/payments/info'
    },
    auth: {
        register: '/api/auth/register',
        login: '/api/auth/login',
        refreshToken: '/api/auth/refreshToken'
    },
    admins: {
        index: '/api/admins'
    },
    wallet: {
        index: '/api/wallet',
        closeChannels: '/api/wallet/closeChannels',
        transfer: '/api/wallet/transfer'
    }
};
