package pl.edu.pjatk.lnpayments.webservice.transaction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.notification.model.Notification;
import pl.edu.pjatk.lnpayments.webservice.notification.model.NotificationType;
import pl.edu.pjatk.lnpayments.webservice.notification.service.NotificationService;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.Transaction;
import pl.edu.pjatk.lnpayments.webservice.transaction.model.TransactionStatus;
import pl.edu.pjatk.lnpayments.webservice.transaction.repository.TransactionRepository;
import pl.edu.pjatk.lnpayments.webservice.transaction.resource.dto.TransactionRequest;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.BitcoinService;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BitcoinService bitcoinService;
    private final NotificationService notificationService;
    private final WalletService walletService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              BitcoinService bitcoinService,
                              NotificationService notificationService, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.bitcoinService = bitcoinService;
        this.notificationService = notificationService;
        this.walletService = walletService;
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
        notificationService.sendAllNotifications(notifications);
    }

    private Function<AdminUser, Notification> createNotificationFunction(Transaction transaction) {
        return user -> new Notification(
                user, transaction,
                "Transaction confirmation",
                NotificationType.TRANSACTION
        );
    }
}
