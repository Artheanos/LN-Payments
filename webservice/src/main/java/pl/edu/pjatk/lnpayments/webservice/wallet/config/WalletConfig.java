package pl.edu.pjatk.lnpayments.webservice.wallet.config;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static pl.edu.pjatk.lnpayments.webservice.common.Constants.USER_HOME;

@Slf4j
@Configuration
class WalletConfig {

    @Value("${lnp.wallet.network}")
    private String network;

    @Value("${lnp.config.workingDirectory}")
    private String walletDirectory;

    @Value("${lnp.wallet.fileName}")
    private String fileName;

    @Bean
    WalletAppKit walletAppKit() {
        Network network = Network.valueOf(this.network.toUpperCase());
        WalletAppKit walletAppKit = new WalletAppKit(
                network.getParameters(), new File(USER_HOME + walletDirectory), fileName);
        if (network == Network.REGTEST) {
            walletAppKit.connectToLocalHost();
        }
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        log.info("Bitcoin wallet started!");
        return walletAppKit;
    }
}
