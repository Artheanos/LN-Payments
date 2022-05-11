const API_DATE_REGEX = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d+Z$/

export const datify = <T>(_object: T) => {
    const object = _object as Record<string, unknown>
    Object.entries(object).forEach(([key, value]) => {
        if (typeof value === 'string' && value.match(API_DATE_REGEX)) {
            ;(object[key] as Date) = new Date(value)
        }
    })

    return object as T
}
