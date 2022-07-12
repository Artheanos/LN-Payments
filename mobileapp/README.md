# Mobile app
Mobile client for the webservice made with `ReactNative`.
It is meant **solely for android devices.**

## Requirements
* Android Studio > 2022.1.1 https://developer.android.com/
* NodeJS > 14 https://nodejs.org/en/
* Yarn > 1.22 https://yarnpkg.com/

## Setup
Prepare the environment according to the [React Native](https://reactnative.dev/docs/environment-setup) docs, except:
* Use `yarn` instead of `npx`
* Skip the "Creating a new application" part

## Development

Compile the Java code and run the metro server
```bash
yarn android
```
If you have set up env paths according to the [docs](https://reactnative.dev/docs/environment-setup) this will also run the Android emulator.

If you haven't made any changes to the Java part of the repo you can run the metro server without compiling Java
```bash
yarn start
```
Keep in mind this command will not open the Android emulator.
You have to open it before running the command.

## Testing

### Unit testing
```bash
yarn test
```

### Checking for syntax and type errors
```bash
yarn lint
```
