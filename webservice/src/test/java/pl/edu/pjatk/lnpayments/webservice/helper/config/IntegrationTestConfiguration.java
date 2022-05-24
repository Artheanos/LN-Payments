package pl.edu.pjatk.lnpayments.webservice.helper.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.wallet.Wallet;
import org.lightningj.lnd.wrapper.AsynchronousLndAPI;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.AddInvoiceResponse;
import org.lightningj.lnd.wrapper.message.GetInfoResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.AbstractMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    SynchronousLndAPI synchronousLndAPI() throws StatusException, ValidationException {
        SynchronousLndAPI api = Mockito.mock(SynchronousLndAPI.class);
        GetInfoResponse getInfoResponse = new GetInfoResponse();
        getInfoResponse.setIdentityPubkey("key");
        when(api.getInfo()).thenReturn(getInfoResponse);
        AddInvoiceResponse invoice = new AddInvoiceResponse();
        invoice.setPaymentRequest("invoice");
        when(api.addInvoice(any())).thenReturn(invoice);
        return api;
    }

    @Bean
    AsynchronousLndAPI asynchronousLndAPI() {
        return Mockito.mock(AsynchronousLndAPI.class);
    }

    @Bean
    WalletAppKit walletAppKit() {
        WalletAppKit walletAppKit = mock(WalletAppKit.class);
        when(walletAppKit.wallet()).thenReturn(Wallet.createBasic(RegTestParams.get()));
        when(walletAppKit.params()).thenReturn(RegTestParams.get());
        return walletAppKit;
    }

    @Bean
    Configuration testSettings() throws Exception {
        Map<String, Object> params = Map.ofEntries(
                new AbstractMap.SimpleEntry<>("price", 100),
                new AbstractMap.SimpleEntry<>("description", "Super opis"),
                new AbstractMap.SimpleEntry<>("invoiceMemo", "memo"),
                new AbstractMap.SimpleEntry<>("paymentExpiryInSeconds", 900),
                new AbstractMap.SimpleEntry<>("autoChannelCloseLimit", 100000L),
                new AbstractMap.SimpleEntry<>("autoTransferLimit", 100000L),
                new AbstractMap.SimpleEntry<>("lastModification", 1));
        BasicConfigurationBuilder<PropertiesConfiguration> builder =
                new BasicConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(new Parameters().properties()
                                .setThrowExceptionOnMissing(true));
        PropertiesConfiguration configuration = builder.getConfiguration();
        params.forEach(configuration::setProperty);
        return configuration;
    }

}
