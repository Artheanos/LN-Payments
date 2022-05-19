package pl.edu.pjatk.lnpayments.webservice.transaction.service;

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
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionDetails;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionResponse;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.exception.BroadcastException;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BitcoinService bitcoinService;
    private final NotificationSocketController notificationSocketController;
    private final WalletService walletService;
    private final TransactionConverter transactionConverter;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              BitcoinService bitcoinService,
                              NotificationSocketController notificationSocketController,
                              WalletService walletService,
                              TransactionConverter transactionConverter) {
        this.transactionRepository = transactionRepository;
        this.bitcoinService = bitcoinService;
        this.notificationSocketController = notificationSocketController;
        this.walletService = walletService;
        this.transactionConverter = transactionConverter;
    }

    @Transactional
    public void createTransaction(TransactionRequest transactionRequest) {
        if (transactionRepository.findFirstByStatus(TransactionStatus.PENDING).isPresent()) {
            throw new InconsistentDataException("Pending transaction already exists!");
        }
        Wallet wallet = walletService.getActiveWallet();
        Transaction transaction = bitcoinService.createTransaction(
                wallet.getAddress(),
                transactionRequest.getTargetAddress(),
                transactionRequest.getAmount()
        );
        transaction.setRequiredApprovals(wallet.getMinSignatures());
        transactionRepository.save(transaction);
        List<AdminUser> users = wallet.getUsers();
        List<Notification> notifications = users.stream()
                .map(createNotificationFunction(transaction))
                .collect(Collectors.toList());
        transaction.setNotifications(notifications);
        notificationSocketController.sendAllNotifications(notifications);
    }

    public TransactionResponse findTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAllByStatusNot(TransactionStatus.PENDING, pageable);
        Page<TransactionDetails> details = transactionConverter.convertAllToDto(transactions);
        Optional<Transaction> pendingTransaction = transactionRepository.findFirstByStatus(TransactionStatus.PENDING);
        TransactionDetails pendingDetails = pendingTransaction.map(transactionConverter::convertToDto).orElse(null);
        return new TransactionResponse(pendingDetails, details);
    }

    public void broadcastTransaction(Transaction transaction) {
        Wallet wallet = walletService.getActiveWallet();
        try {
            bitcoinService.broadcast(transaction.getRawTransaction(), wallet.getRedeemScript());
            transaction.setStatus(TransactionStatus.APPROVED);
        } catch (BroadcastException e) {
            transaction.setStatus(TransactionStatus.FAILED);
        }
    }

    public void denyTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.DENIED);
        transaction.getNotifications()
                .stream()
                .filter(notification -> notification.getStatus() == NotificationStatus.PENDING)
                .forEach(notification -> notification.setStatus(NotificationStatus.EXPIRED));
    }

    private Function<AdminUser, Notification> createNotificationFunction(Transaction transaction) {
        return user -> new Notification(
                user, transaction,
                "Transaction confirmation",
                NotificationType.TRANSACTION
        );
    }
}
