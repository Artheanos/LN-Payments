function at(n) {
  n = Math.trunc(n) || 0
  if (n < 0) n += this.length
  if (n < 0 || n >= this.length) return undefined
  return this[n]
}

Object.defineProperty(Array.prototype, 'at', {
  value: at,
  writable: true,
  enumerable: false,
  configurable: true
})
