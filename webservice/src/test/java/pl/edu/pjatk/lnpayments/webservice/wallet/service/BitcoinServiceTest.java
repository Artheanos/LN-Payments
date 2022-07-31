package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import com.google.common.util.concurrent.ListenableFutureTask;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.TransactionBroadcast;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.exception.BroadcastException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;

import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.bitcoinj.core.Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BitcoinServiceTest {

    @Mock
    private WalletAppKit walletAppKit;

    @InjectMocks
    private BitcoinService bitcoinService;

    @Test
    void shouldCreateWallet() {
        String publicKey = "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb";
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        admin1.setPublicKey(publicKey);
        admin2.setPublicKey(publicKey);
        List<AdminUser> adminUsers = List.of(admin1, admin2);
        when(walletAppKit.params()).thenReturn(RegTestParams.get());
        when(walletAppKit.wallet()).thenReturn(Mockito.mock(org.bitcoinj.wallet.Wallet.class));

        Wallet wallet = bitcoinService.createWallet(adminUsers, 2);

        assertThat(wallet.getAddress()).isEqualTo("2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv");
        assertThat(wallet.getRedeemScript()).isEqualTo("52210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae");
        assertThat(wallet.getScriptPubKey()).isEqualTo("a9148c0b2c246ce6738f00f5dd47948966f791042ad887");
        assertThat(wallet.getUsers()).isEqualTo(adminUsers);
        assertThat(wallet.getMinSignatures()).isEqualTo(2);
    }

    @Test
    void shouldObtainWalletBalance() {
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletMock.getBalance(BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(1000L));
        when(walletMock.getBalance(BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(900L));
        when(walletAppKit.wallet()).thenReturn(walletMock);

        BitcoinWalletBalance balance = bitcoinService.getBalance();
        assertThat(balance.getAvailableBalance()).isEqualTo(1000L);
        assertThat(balance.getUnconfirmedBalance()).isEqualTo(100L);
        assertThat(balance.getCurrentReferenceFee()).isEqualTo(REFERENCE_DEFAULT_MIN_TX_FEE.value);
    }

    @Test
    void shouldCreateTransaction() {
        RegTestParams params = RegTestParams.get();
        org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000"));
        String destinationAddress = "2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv";
        String sourceAddress = "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4";
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletAppKit.wallet()).thenReturn(walletMock);
        when(walletAppKit.params()).thenReturn(params);
        when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());
        when(walletMock.getBalance(BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(10000L));
        when(walletMock.getBalance(BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));

        Transaction transaction = bitcoinService.createTransaction(sourceAddress, destinationAddress, 100L);

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(transaction.getFee()).isEqualTo(1000L);
        assertThat(transaction.getInputValue()).isEqualTo(100L);
        assertThat(transaction.getSourceAddress()).isEqualTo(sourceAddress);
        assertThat(transaction.getTargetAddress()).isEqualTo(destinationAddress);
        assertThat(transaction.getRawTransaction()).isEqualTo("0100000001102b444becb78e1fbb79d7fda0a42f78594ee5fd86a8ddf753a65ce5012787350100000000ffffffff02640000000000000017a9148c0b2c246ce6738f00f5dd47948966f791042ad887781e00000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000");
    }

    @Test
    void shouldCreateSweepTransaction() {
        RegTestParams params = RegTestParams.get();
        org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000"));
        String destinationAddress = "2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv";
        String sourceAddress = "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4";
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletAppKit.wallet()).thenReturn(walletMock);
        when(walletAppKit.params()).thenReturn(params);
        when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());

        Transaction transaction = bitcoinService.createTransaction(sourceAddress, destinationAddress);

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
        //Can't be checked really, because it works only for transactions created by
        assertThat(transaction.getFee()).isEqualTo(1000L);
        assertThat(transaction.getInputValue()).isEqualTo(7900L);
        assertThat(transaction.getSourceAddress()).isEqualTo(sourceAddress);
        assertThat(transaction.getTargetAddress()).isEqualTo(destinationAddress);
        assertThat(transaction.getRawTransaction()).isEqualTo("0100000001102b444becb78e1fbb79d7fda0a42f78594ee5fd86a8ddf753a65ce5012787350100000000ffffffff01dc1e00000000000017a9148c0b2c246ce6738f00f5dd47948966f791042ad88700000000");
    }

    @Test
    void shouldThrowExceptionWhenInvalidInputValueProvided() {
        RegTestParams params = RegTestParams.get();
        org.bitcoinj.core.Transaction transaction1 = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000"));
        String destinationAddress = "2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv";
        String sourceAddress = "2N9fb1HjNWYs77MHhvnWGHunDeFwMMeYxo4";
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletAppKit.wallet()).thenReturn(walletMock);
        when(walletAppKit.params()).thenReturn(params);
        when(walletMock.getWatchedOutputs(true)).thenReturn(transaction1.getOutputs());
        when(walletMock.getBalance(BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(10L));
        when(walletMock.getBalance(BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(0L));

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> bitcoinService.createTransaction(sourceAddress, destinationAddress, 100L))
                .withMessage("Transaction amount is higher than balance with fees");
    }

    @ParameterizedTest
    @MethodSource("verificationData")
    void shouldReturnTrueForProperlySignedValues(String tx, String key, boolean expected) {
        when(walletAppKit.params()).thenReturn(TestNet3Params.get());
        boolean result = bitcoinService.verifySignature(tx, key);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldBroadcastTransaction() {
        RegTestParams params = RegTestParams.get();
        org.bitcoinj.core.Transaction transaction = new org.bitcoinj.core.Transaction(params, HexFormat.of().parseHex("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000"));
        PeerGroup peerGroup = Mockito.mock(PeerGroup.class);
        TransactionBroadcast transactionBroadcast = Mockito.mock(TransactionBroadcast.class);
        ListenableFutureTask<org.bitcoinj.core.Transaction> dumbTask = ListenableFutureTask.create(() -> transaction);
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(dumbTask);
        when(transactionBroadcast.broadcast()).thenReturn(dumbTask);
        String transactionHex = "01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000";
        String redeemScript = "522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae";
        when(peerGroup.broadcastTransaction(transaction)).thenReturn(transactionBroadcast);
        when(walletAppKit.params()).thenReturn(params);
        when(walletAppKit.peerGroup()).thenReturn(peerGroup);

        bitcoinService.broadcast(transactionHex, redeemScript);

        verify(peerGroup).broadcastTransaction(any());
    }

    @Test
    void shouldThrowExceptionWhenCannotBroadcast() {
        RegTestParams params = RegTestParams.get();
        String transactionHex = "01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000";
        String redeemScript = "522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae";
        when(walletAppKit.params()).thenReturn(params);
        when(walletAppKit.peerGroup()).thenThrow(ScriptException.class);

        assertThatExceptionOfType(BroadcastException.class)
                .isThrownBy(() -> bitcoinService.broadcast(transactionHex, redeemScript))
                .withCauseInstanceOf(ScriptException.class)
                .withMessage("Failed to broadcast transaction!: " + transactionHex);
    }

    private static Stream<Arguments> verificationData() {
        return Stream.of(
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb", true),
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f300000000d9004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c260147304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "02ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a", true),
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000000ffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb", false),
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000000ffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "02ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a", false),
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb", true),
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092000047304402200a505e3526df75a3addf672c366f79fe28b3f0220f063f78db8ae4a921d0e97a02207aefa698d8f1a984b93fff4480cd30b5e174832e3dab84365a4766615845c8580147522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "02ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a", false),
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c26010047522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb", false),
                Arguments.of("01000000012a3c2133f9b678877ae4afd5a982bdc453d5e86749722fe189acd5a2c97660f30000000092004730440220407e37b9e31d1200f2abe0e393338db0aa1bd21783ccc06f68aee92aae529a790220627b7052a9bc8dd4dc5c55e4f631a97296b3e1ddfe19fb2b5528cb0d130f0c26010047522102ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52aeffffffff026400000000000000160014a9619fc4a9c6d2d36eb6ace4d5bc9abdbebc8f5cc42200000000000017a914b41d8a3e10f6a407ae80ceea45a8eae867ac78598700000000", "02ab7358f9fba8b2661dc6b489ced6cac0d620eb1de82100e6cf40c404ee44dc3a", true)
        );
    }
}
