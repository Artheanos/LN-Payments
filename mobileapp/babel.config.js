const moduleResolver = [
  'module-resolver',
  {
    alias: {
      api: './src/api',
      bitcoin: './src/bitcoin',
      components: './src/components',
      constants: './src/constants',
      res: './src/res',
      utils: './src/utils',
      webService: './src/webService',
    },
  },
]

module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  plugins: [moduleResolver, 'react-native-reanimated/plugin'],
}
