const moduleResolver = [
  'module-resolver',
  {
    alias: {
      api: './api',
      components: './components',
    },
  },
]

module.exports = (api) => {
  api.cache(true)
  return {
    presets: ['babel-preset-expo'],
    plugins: ['react-native-reanimated/plugin', moduleResolver],
  }
}
