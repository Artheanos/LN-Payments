export declare const routes: {
    payments: {
        all: string;
        ws: (host?: string | undefined) => string;
        index: string;
        info: string;
    };
    auth: {
        register: string;
        login: string;
        refreshToken: string;
    };
    admins: {
        index: string;
    };
    wallet: {
        index: string;
        closeChannels: string;
        transfer: string;
    };
};
