"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.datify = void 0;
const API_DATE_REGEX = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d+Z$/;
const datify = (_object) => {
    const object = _object;
    Object.entries(object).forEach(([key, value]) => {
        if (typeof value === 'string' && value.match(API_DATE_REGEX)) {
            ;
            object[key] = new Date(value);
        }
    });
    return object;
};
exports.datify = datify;
