package pl.edu.pjatk.lnpayments.webservice.transaction.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationStatus;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.resource.NotificationSocketController;
import pl.edu.pjatk.lnpayments.webservice.transaction.converter.TransactionConverter;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.NewTransactionDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionResponse;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.exception.BroadcastException;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletDataService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BitcoinService bitcoinService;
    private final NotificationSocketController notificationSocketController;
    private final WalletDataService walletDataService;
    private final TransactionConverter transactionConverter;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              BitcoinService bitcoinService,
                              NotificationSocketController notificationSocketController,
                              WalletDataService walletDataService,
                              TransactionConverter transactionConverter) {
        this.transactionRepository = transactionRepository;
        this.bitcoinService = bitcoinService;
        this.notificationSocketController = notificationSocketController;
        this.walletDataService = walletDataService;
        this.transactionConverter = transactionConverter;
    }

    @Transactional
    public void createTransaction(String targetAddress, long amount) {
        Wallet wallet = validateAndObtainWallet();
        Transaction transaction = bitcoinService.createTransaction(
                wallet.getAddress(),
                targetAddress,
                amount
        );
        finishTransaction(wallet, transaction, NotificationType.TRANSACTION);
    }

    /**
     * Creates sweep transaction, that spends all available inputs. As for now only used for wallet recreation.
     * If we ever want to use it as a normal functionality, this component would require refactor
     * (like using template design pattern).
     *
     * @param targetAddress  Address to send funds to.
     */
    @Transactional
    public void createTransaction(String targetAddress) {
        Wallet wallet = validateAndObtainWallet();
        Transaction transaction = bitcoinService.createTransaction(
                wallet.getAddress(),
                targetAddress
        );
        finishTransaction(wallet, transaction, NotificationType.WALLET_RECREATION);
    }

    public TransactionResponse findTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAllByStatusNot(TransactionStatus.PENDING, pageable);
        Page<TransactionDetails> details = transactionConverter.convertAllToDto(transactions);
        Optional<Transaction> pendingTransaction = transactionRepository.findFirstByStatus(TransactionStatus.PENDING);
        TransactionDetails pendingDetails = pendingTransaction.map(transactionConverter::convertToDto).orElse(null);
        return new TransactionResponse(pendingDetails, details);
    }

    public void broadcastTransaction(Transaction transaction) {
        Wallet wallet = walletDataService.getActiveWallet();
        try {
            bitcoinService.broadcast(transaction.getRawTransaction(), wallet.getRedeemScript());
            transaction.setStatus(TransactionStatus.APPROVED);
        } catch (BroadcastException e) {
            log.error("Unable to broadcast transaction", e);
            transaction.setStatus(TransactionStatus.FAILED);
        }
        expirePendingNotifications(transaction);
    }

    public void denyTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.DENIED);
        expirePendingNotifications(transaction);
    }

    public boolean isTransactionInProgress() {
        return transactionRepository.existsByStatus(TransactionStatus.PENDING);
    }

    private Function<AdminUser, Notification> createNotificationFunction(
            Transaction transaction, NotificationType notificationType) {
        return user -> new Notification(
                user, transaction,
                "Transaction confirmation",
                notificationType
        );
    }

    private Wallet validateAndObtainWallet() {
        if (transactionRepository.findFirstByStatus(TransactionStatus.PENDING).isPresent()) {
            throw new InconsistentDataException("Pending transaction already exists!");
        }
        return walletDataService.getActiveWallet();
    }

    private void finishTransaction(Wallet wallet, Transaction transaction, NotificationType type) {
        transaction.setRequiredApprovals(wallet.getMinSignatures());
        transactionRepository.save(transaction);
        List<AdminUser> users = wallet.getUsers();
        List<Notification> notifications = users.stream()
                .map(createNotificationFunction(transaction, type))
                .collect(Collectors.toList());
        transaction.setNotifications(notifications);
        notificationSocketController.sendAllNotifications(notifications);
    }

    /**
     * This method in the future will return current fee estimations, so user well be able to choose between
     * instant or delayed transaction confirmation on blockchain. Right now we only need balance and estimated fee
     * (which is a wrong assumption, but for now will do the job)
     *
     * @return Details required for new transaction creation
     */
    public NewTransactionDetails getNewTransactionDetails() {
        BitcoinWalletBalance balance = bitcoinService.getBalance();
        return new NewTransactionDetails(balance, isTransactionInProgress());
    }

    private void expirePendingNotifications(Transaction transaction) {
        transaction.getNotifications()
                .stream()
                .filter(notification -> notification.getStatus() == NotificationStatus.PENDING)
                .forEach(notification -> notification.setStatus(NotificationStatus.EXPIRED));
    }
}
