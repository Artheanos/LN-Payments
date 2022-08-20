# LN-Payments

System for receiving Lightning Network payments and generating single-use tokens, with the funds stored in multisig 
address. Provides complex solution for small enterprises willing to accept micro-payments in bitcoins leveraging
Lightning Network. Entire solution is free and open-source! Project is an engineer thesis of the authors, developed
at Polish-Japanese Academy of Information Technology.

## Features

* Processing bitcoin payment in Lightning Network
* Offering single-use tokens, that can be sent to a selected system
* Automatic payment channel management
* Multisig P2SH address storing funds from payments
* Mobile app storing private keys and signing transactions
* ...and many more!

## Components

Entire system contains of multiple components:

* Server → localed in _webservice_ directory. Handles all the backend of the system, including HTTP API and Websocket
connections.
* Web application → stored in _webapp_ directory. It is a React application used as a system's web application. Allows
to make payments by users. Supports multiple admin-only features like transactions, server settings and wallet 
management.
* Mobile application → located in _mobileapp_ directory. Used only by server admins. Allows to generate private keys
and to sign multisig transactions being sent from the server.

## Getting Started

Firstly you must decide whether you want to use your own Bitcoin node, or you want to use a third party one. 
Having Bitcoin node running, you must start your own instance of LND. You can use _docker-compose_ files located in
_devtools/docker_ directory. It also contains _readme_ file with instructions on how to run them.

Executables can be found in each release files. Minimal version requires you to start webapp and webservice. It is 
recommended to send mobile app installer to all future admins, because it is required to use Bitcoin transfers. 
Server jar file has web application bundled, so you can run both by standard _java -jar_ command (requires Java 17).
It is also possible to start webservice bundled with webapp using docker image. It is available in the registry
under the name `oskar117/lnpayments`. You can run it using _docker-compose_. Instructions can be found in 
_devtools/docker_ readme file. Prior to that, you must prepare certificate and macaroon files. Instructions for that can be found in webservice readme, _set up_ section. These steps are required for both java and docker application 
startups. Mobile application requires at least Android 9. Apple products are not supported at the moment.

When you have application running, you can use default admin account to set up everything. Both username and password
are `admin`. After login, it is recommended to add all server administrators. You should also ask them to change
their password and upload public keys from their mobile apps. After that we recommend to delete initial admin
account. After all admins have uploaded their keys, go to _Wallet_ and create new wallet. Lastly, go to _settings_
tab and set your desired settings like description, price, or automatic transfer limits. By now server should be 
properly configured. You can share the url with your potential clients!

Should you have any questions, please open a new issue. We will try to help you! You can also find additional help
in each component's readme files.

## Contributing

Instructions on development environment setup can be found in each component's folder.

Each change to the `dev` branch should be made so:
- Checkout at `dev` branch
- Create a new branch called `LP-XXX` where XXX is the task number assigned in Jira
- When your work on the branch is done push it (if you haven't already) and create a PR to the `dev` branch
- After successful code review merge the PR and delete your branch

Great, you have just placed your small brick in the house of LN-Payments 👏

If your branch is out of date rebase it onto the `dev` branch and force push it

## Authors

Solution was developed:
* Aleksander Konieczny
* Jan Pieczul
* Sebastian Lewandowski

It was also consulted with our thesis supervisor dr. Tadeusz Puźniakowski.

## License

LN-Payments is licensed under MIT License.
