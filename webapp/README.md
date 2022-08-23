# Frontend app

Client for the webservice made with `React` on `Vite`.
It allows to make payments in exchange for tokens.
Users can register an account. If the user is registered they will have access to their payment history.

This client is also includes the admin's control panel.
Users with a role `admin` have access to the incoming payments, outgoing transactions, server configuration and wallet statistics.

When running the application for the first time a new wallet must be created and assigned admins to before statistics can be shown.
Keep in mind that only admins who have their access keys generated using the mobile app can be assigned to the wallet.

## Requirements
* NodeJS > 14 https://nodejs.org/en/
* Yarn > 1.22 https://yarnpkg.com/

## Tools used

- [ReactJS](https://reactjs.org)
- [Vite](https://vitejs.dev)
- [TypeScript](https://www.typescriptlang.org)
- [Jest](https://jestjs.io)
- [Testing Library](https://testing-library.com)
- [Eslint](https://eslint.org)
- [Prettier](https://prettier.io)
- [Tailwindcss](https://tailwindcss.com)

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
