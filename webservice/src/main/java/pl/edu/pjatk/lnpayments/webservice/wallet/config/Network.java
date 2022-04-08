package pl.edu.pjatk.lnpayments.webservice.wallet.config;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;

enum Network {

    MAINNET(MainNetParams.get()),
    TESTNET(TestNet3Params.get()),
    REGTEST(RegTestParams.get());

    private final NetworkParameters parameters;

    Network(NetworkParameters parameters) {
        this.parameters = parameters;
    }

    NetworkParameters getParameters() {
        return parameters;
    }

}
