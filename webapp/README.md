# Frontend app

Client for the webservice made with `React` on `Vite`.

## Requirements
* NodeJS > 14 https://nodejs.org/en/
* Yarn > 1.22 https://yarnpkg.com/

## Tools used

- [ReactJS](https://reactjs.org)
- [Vite](https://vitejs.dev)
- [TypeScript](https://www.typescriptlang.org)
- [Jest](https://jestjs.io)
- [Testing Library](https://testing-library.com)
- [Tailwindcss](https://tailwindcss.com)
- [Eslint](https://eslint.org)
- [Prettier](https://prettier.io)

## Setup

Install dependencies.

```bash
yarn install
```

## Development
Before running the client make sure the `webservice` is running.

Serve with hot reload at http://localhost:3000.

```bash
yarn dev
```

## Testing

### Unit testing
```bash
yarn test:unit
```

### Checking for syntax errors
(Add `--fix` to fix errors which can be automatically fixed)
```bash
yarn lint
```

## Building
To build for production run
```bash
yarn build
```
This step also runs TypeScript because `Vite` does not check for type errors.
To serve the compiled files run
```bash
yarn serve
```

## License

This project is licensed under the MIT License.
