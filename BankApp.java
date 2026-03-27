import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// ===================== MAIN APPLICATION (Entry Point) =====================
public class BankApp {

    private static final Scanner        scanner   = new Scanner(System.in);
    private static final List<Customer> customers = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== Advanced Bank Management System ===");
        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt();
            try {
                switch (choice) {
                    case 1:  createAccount();          break;
                    case 2:  deposit();                break;
                    case 3:  withdraw();               break;
                    case 4:  addInterest();            break;
                    case 5:  transferMoney();          break;
                    case 6:  viewTransactionHistory(); break;
                    case 7:  viewCustomer();           break;
                    case 8:  manageAccountStatus();    break;
                    case 9:  manageCreditCard();       break;
                    case 10: manageInternetBanking();  break;
                    case 11: running = false;          break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
        scanner.close();
        System.out.println("Thank you for using Bank Management System!");
    }

    // ── Menu ─────────────────────────────────────────────────────────

    private static void showMenu() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║       ADVANCED BANK MANAGEMENT SYSTEM        ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  1.  Create Account                          ║");
        System.out.println("║  2.  Deposit                                 ║");
        System.out.println("║  3.  Withdraw                                ║");
        System.out.println("║  4.  Add Interest (Savings accounts)         ║");
        System.out.println("║  5.  Transfer Money                          ║");
        System.out.println("║  6.  View Last 5 Transactions                ║");
        System.out.println("║  7.  View Customer Details                   ║");
        System.out.println("║  8.  Manage Account Status                   ║");
        System.out.println("║  9.  Credit Card Operations (Premium/Plat.)  ║");
        System.out.println("║  10. Internet Banking (Platinum only)        ║");
        System.out.println("║  11. Exit                                    ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.print("Choose: ");
    }

    // ── Create Account ────────────────────────────────────────────────

    private static void createAccount() {
        System.out.println("\n-- Create Account --");
        System.out.print("Name             : "); String name    = scanner.nextLine();
        System.out.print("Contact No       : "); long   contact = readLong();
        System.out.print("Email            : "); String email   = scanner.nextLine();
        System.out.print("Gender           : "); String gender  = scanner.nextLine();
        System.out.print("DOB (DD/MM/YYYY) : "); String dob     = scanner.nextLine();

        Customer cust = new Customer(name, contact, email, gender, dob);
        customers.add(cust);
        System.out.println("Customer '" + name + "' created!");

        System.out.print("\nAccount Number   : "); String accNum  = scanner.nextLine();
        System.out.print("Initial Balance  : $"); double initBal = readDouble();

        System.out.println("\nAccount Types:");
        System.out.println("  BS - Basic Savings   (withdrawal limit $500, 3 free txns/month)");
        System.out.println("  PS - Premium Savings (withdrawal limit $5000, min balance $1000, Credit Card)");
        System.out.println("  PL - Platinum Savings (withdrawal limit $50000, min balance $10000, Credit Card + Internet Banking)");
        System.out.println("  C  - Current         (overdraft supported)");
        System.out.print("Account Type: ");
        String typeChoice = scanner.nextLine().trim().toUpperCase();

        switch (typeChoice) {
            case "BS": {
                System.out.print("Interest Rate (%): ");
                double interest = readDouble();
                cust.addAccount(new BasicSavingsAccount(accNum, initBal, interest));
                break;
            }
            case "PS": {
                System.out.print("Interest Rate (%): ");
                double interest = readDouble();
                cust.addAccount(new PremiumSavingsAccount(accNum, initBal, interest, name));
                break;
            }
            case "PL": {
                System.out.print("Interest Rate (%): ");
                double interest = readDouble();
                System.out.print("Internet Banking Username: ");
                String ibUser = scanner.nextLine();
                System.out.print("Internet Banking Password: ");
                String ibPass = scanner.nextLine();
                cust.addAccount(new PlatinumSavingsAccount(
                    accNum, initBal, interest, name, ibUser, ibPass));
                break;
            }
            case "C": {
                System.out.print("Overdraft Limit: $");
                double overdraft = readDouble();
                cust.addAccount(new CurrentAccount(accNum, initBal, overdraft));
                break;
            }
            default:
                System.out.println("Invalid account type. Account not created.");
        }
    }

    // ── Deposit ───────────────────────────────────────────────────────

    private static void deposit() {
        System.out.print("\nEnter Account Number: ");
        Account acc = findAccountGlobally(scanner.nextLine());
        if (acc == null) return;
        System.out.print("Amount: $");
        double amount = readDouble();
        acc.deposit(amount);
    }

    // ── Withdraw ──────────────────────────────────────────────────────

    private static void withdraw() {
        System.out.print("\nEnter Account Number: ");
        Account acc = findAccountGlobally(scanner.nextLine());
        if (acc == null) return;
        System.out.print("Amount: $");
        double amount = readDouble();
        acc.withdraw(amount);
    }

    // ── Add Interest ──────────────────────────────────────────────────

    private static void addInterest() {
        System.out.print("\nEnter Account Number: ");
        String accNum = scanner.nextLine();
        for (Customer c : customers) {
            for (Account acc : c.getAccounts()) {
                if (!acc.getAccountNumber().equals(accNum)) continue;
                if (acc instanceof PlatinumSavingsAccount) {
                    ((PlatinumSavingsAccount) acc).addInterest(); return;
                } else if (acc instanceof PremiumSavingsAccount) {
                    ((PremiumSavingsAccount) acc).addInterest(); return;
                } else if (acc instanceof BasicSavingsAccount) {
                    ((BasicSavingsAccount) acc).addInterest(); return;
                }
                System.out.println("This account type does not support interest.");
                return;
            }
        }
        System.out.println("Savings account not found.");
    }

    // ── Transfer Money ────────────────────────────────────────────────

    private static void transferMoney() {
        System.out.println("\n-- Transfer Money --");
        System.out.print("Source Account Number     : ");
        Account fromAcc = findAccountGlobally(scanner.nextLine());
        if (fromAcc == null) return;

        System.out.print("Destination Account Number: ");
        Account toAcc = findAccountGlobally(scanner.nextLine());
        if (toAcc == null) return;

        System.out.print("Transfer Amount: $");
        double amount = readDouble();
        TransferService.transfer(fromAcc, toAcc, amount);
    }

    // ── View Transaction History ──────────────────────────────────────

    private static void viewTransactionHistory() {
        System.out.print("\nEnter Account Number: ");
        Account acc = findAccountGlobally(scanner.nextLine());
        if (acc != null) acc.printLastFiveTransactions();
    }

    // ── View Customer Details ─────────────────────────────────────────

    private static void viewCustomer() {
        System.out.print("Enter Account Number to find customer: ");
        String accNum = scanner.nextLine();
        Customer cust = findCustomerByAccount(accNum);
        if (cust != null) cust.display();
        else System.out.println("Customer not found.");
    }

    // ── Manage Account Status ─────────────────────────────────────────

    private static void manageAccountStatus() {
        System.out.println("\n-- Manage Account Status --");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine();

        Customer ownerFound = null;
        Account  accFound   = null;
        outer:
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (a.getAccountNumber().equals(accNum)) {
                    ownerFound = c;
                    accFound   = a;
                    break outer;
                }
            }
        }

        if (accFound == null) {
            System.out.println("Account not found.");
            return;
        }
        if (accFound.isClosed()) {
            System.out.println("Account #" + accNum + " does not exist (this account has been closed).");
            return;
        }

        System.out.println("Account #" + accNum + " is currently: " + accFound.getStatus());
        System.out.println("\nOptions:");
        System.out.println("  1. Set ACTIVE");
        System.out.println("  2. Set INACTIVE");
        System.out.println("  3. CLOSE Account");
        System.out.println("  4. Check if ACTIVE");
        System.out.print("Choose: ");

        int choice = readInt();
        switch (choice) {
            case 1: accFound.setActive();                   break;
            case 2: accFound.setInactive();                 break;
            case 3: closeAccount(ownerFound, accFound);     break;
            case 4:
                System.out.println("Account #" + accNum + " active? → "
                    + (accFound.isActive() ? "YES (ACTIVE)" : "NO (" + accFound.getStatus() + ")"));
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    // ── Credit Card Operations ────────────────────────────────────────

    private static void manageCreditCard() {
        System.out.println("\n-- Credit Card Operations --");
        System.out.print("Enter Account Number (Premium or Platinum only): ");
        Account acc = findAccountGlobally(scanner.nextLine());
        if (acc == null) return;

        CreditCard card = null;
        if (acc instanceof PlatinumSavingsAccount) {
            card = ((PlatinumSavingsAccount) acc).getCreditCard();
        } else if (acc instanceof PremiumSavingsAccount) {
            card = ((PremiumSavingsAccount) acc).getCreditCard();
        } else {
            System.out.println("Credit card is only available for Premium and Platinum accounts.");
            return;
        }

        System.out.println("\nCredit Card Menu:");
        System.out.println("  1. View Credit Card Details");
        System.out.println("  2. Spend on Credit Card");
        System.out.println("  3. Pay Credit Card Bill");
        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1:
                card.display();
                break;
            case 2:
                System.out.print("Spend Amount: $");
                double spendAmt = readDouble();
                if (acc instanceof PlatinumSavingsAccount)
                    ((PlatinumSavingsAccount) acc).spendOnCreditCard(spendAmt);
                else
                    ((PremiumSavingsAccount) acc).spendOnCreditCard(spendAmt);
                break;
            case 3:
                System.out.printf("Outstanding balance: $%.2f%n", card.getUsedCredit());
                System.out.print("Payment Amount: $");
                double payAmt = readDouble();
                if (acc instanceof PlatinumSavingsAccount)
                    ((PlatinumSavingsAccount) acc).payCreditCardBill(payAmt);
                else
                    ((PremiumSavingsAccount) acc).payCreditCardBill(payAmt);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // ── Internet Banking ──────────────────────────────────────────────

    private static void manageInternetBanking() {
        System.out.println("\n-- Internet Banking (Platinum Accounts Only) --");
        System.out.print("Enter Platinum Account Number: ");
        Account acc = findAccountGlobally(scanner.nextLine());
        if (acc == null) return;

        if (!(acc instanceof PlatinumSavingsAccount)) {
            System.out.println("Internet Banking is only available for Platinum Savings Accounts.");
            return;
        }

        PlatinumSavingsAccount platAcc = (PlatinumSavingsAccount) acc;

        System.out.println("\nInternet Banking Menu:");
        System.out.println("  1. Login");
        System.out.println("  2. View Account Summary Online");
        System.out.println("  3. Pay Bill Online");
        System.out.println("  4. Change Internet Banking Password");
        System.out.println("  5. Logout");
        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1:
                System.out.print("Username: "); String user = scanner.nextLine();
                System.out.print("Password: "); String pass = scanner.nextLine();
                platAcc.loginInternetBanking(user, pass);
                break;
            case 2:
                platAcc.viewAccountSummaryOnline();
                break;
            case 3:
                System.out.print("Biller Name  : "); String biller = scanner.nextLine();
                System.out.print("Bill Amount  : $"); double billAmt = readDouble();
                platAcc.payBillOnline(biller, billAmt);
                break;
            case 4:
                System.out.print("Old Password: "); String oldP = scanner.nextLine();
                System.out.print("New Password: "); String newP = scanner.nextLine();
                platAcc.changeInternetBankingPassword(oldP, newP);
                break;
            case 5:
                platAcc.logoutInternetBanking();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // ── Close Account ─────────────────────────────────────────────────

    private static void closeAccount(Customer owner, Account acc) {
        if (acc.isClosed()) {
            System.out.println("Account #" + acc.getAccountNumber() + " is already CLOSED.");
            return;
        }
        System.out.println("\nAccount #" + acc.getAccountNumber()
            + " — Closing will zero the balance and refund the customer.");
        System.out.print("Are you sure? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes")) {
            System.out.println("Close operation cancelled.");
            return;
        }

        double refundAmount = acc.getBalance();
        ClosedAccountArchive.save(owner, acc, refundAmount);  // archive before zeroing
        acc.balance = 0.0;
        acc.status  = AccountStatus.CLOSED;

        System.out.printf("%n✔ Account #%s has been CLOSED.%n", acc.getAccountNumber());
        System.out.printf("  Refund of $%.2f has been returned to %s.%n",
            refundAmount, owner.getName());
        System.out.println("  Account data saved to 'closed_accounts.txt'.");
        System.out.println("  This account is no longer accessible.");
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private static Account findAccountGlobally(String accNum) {
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (a.getAccountNumber().equals(accNum)) {
                    if (a.isClosed()) {
                        System.out.println("Account #" + accNum
                            + " does not exist (this account has been closed).");
                        return null;
                    }
                    return a;
                }
            }
        }
        System.out.println("Account #" + accNum + " not found.");
        return null;
    }

    private static Customer findCustomerByAccount(String accNum) {
        for (Customer c : customers)
            for (Account a : c.getAccounts())
                if (a.getAccountNumber().equals(accNum)) return c;
        return null;
    }

    private static int readInt() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.print("Invalid input. Please enter a whole number: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                System.out.print("Invalid input. Please enter a numeric value: ");
            }
        }
    }

    private static long readLong() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException ex) {
                System.out.print("Invalid input. Please enter digits only: ");
            }
        }
    }
}
