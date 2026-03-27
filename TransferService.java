// ===================== TRANSFER SERVICE =====================
public class TransferService {

    public static void transfer(Account from, Account to, double amount) {
        if (from == null || to == null) {
            System.out.println("Transfer failed: source or destination account is missing.");
            return;
        }
        if (amount <= 0) {
            System.out.println("Transfer amount must be positive.");
            return;
        }
        try {
            if (from.getAccountNumber().equals(to.getAccountNumber())) {
                System.out.println("Cannot transfer to the same account.");
                return;
            }
            if (from.withdrawForTransfer(amount, to.getAccountNumber())) {
                to.depositForTransfer(amount, from.getAccountNumber());
                System.out.printf("Transfer successful! $%.2f sent from Acc#%s to Acc#%s%n",
                    amount, from.getAccountNumber(), to.getAccountNumber());
                System.out.printf("  From balance: $%.2f  |  To balance: $%.2f%n",
                    from.getBalance(), to.getBalance());
            } else {
                System.out.println("Transfer failed: check source account status or funds.");
            }
        } catch (RuntimeException ex) {
            System.out.println("Transfer failed due to an unexpected error: " + ex.getMessage());
        }
    }
}
