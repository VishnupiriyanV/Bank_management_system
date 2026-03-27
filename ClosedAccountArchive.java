import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ===================== CLOSED ACCOUNT ARCHIVE =====================
public class ClosedAccountArchive {

    private static final String ARCHIVE_FILE = "closed_accounts.txt";

    public static void save(Customer owner, Account acc, double refundAmount) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVE_FILE, true))) {
            pw.println("================================================================");
            pw.println("  CLOSED ACCOUNT RECORD — " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            pw.println("================================================================");
            pw.println("CUSTOMER DETAILS:");
            pw.print(owner.toArchiveString());
            pw.println("\nACCOUNT DETAILS:");
            pw.print(acc.toArchiveString());
            pw.printf("  Refund Issued   : $%.2f (returned to customer)%n", refundAmount);
            pw.println("----------------------------------------------------------------");
            pw.println();
            System.out.println("[Archive] Closed account #" + acc.getAccountNumber()
                + " details saved to '" + ARCHIVE_FILE + "'.");
        } catch (IOException | SecurityException e) {
            System.out.println("[Archive ERROR] Could not write to " + ARCHIVE_FILE
                + ": " + e.getMessage());
        }
    }
}
