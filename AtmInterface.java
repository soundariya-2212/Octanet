import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtmInterface {
    private static List<String> transactionHistory = new ArrayList<>();
    private static JTextArea textArea;
    private static Map<String, String> accountDetails = new HashMap<>();

    public static void main(String[] args) {
        accountDetails.put("10051972", "2226");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Automated Teller Machine");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            textArea = new JTextArea();
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel inputPanel = new JPanel(new GridLayout(2, 2));
            JTextField accountNumberField = new JTextField();
            JPasswordField pinField = new JPasswordField();
            inputPanel.add(new JLabel("Account Number:"));
            inputPanel.add(accountNumberField);
            inputPanel.add(new JLabel("PIN:"));
            inputPanel.add(pinField);

            JPanel buttonPanel = new JPanel(new GridLayout(1, 5));

            String[] buttonLabels = {"Withdraw", "Deposit", "Check Balance", "Transaction History", "Exit"};

            ButtonClickListener buttonClickListener = new ButtonClickListener(accountNumberField, pinField);

            for (String label : buttonLabels) {
                JButton button = new JButton(label);
                button.addActionListener(buttonClickListener);
                buttonPanel.add(button);
            }

            frame.add(inputPanel, BorderLayout.NORTH);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }

    static class ButtonClickListener implements ActionListener {
        private JTextField accountNumberField;
        private JPasswordField pinField;
        private int balance;

        public ButtonClickListener(JTextField accountNumberField, JPasswordField pinField) {
            this.accountNumberField = accountNumberField;
            this.pinField = pinField;
            this.balance = 100000; 
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            String enteredAccountNumber = accountNumberField.getText();
            String enteredPin = new String(pinField.getPassword());

            if (!accountDetails.containsKey(enteredAccountNumber) || !accountDetails.get(enteredAccountNumber).equals(enteredPin)) {
                displayResult("Invalid account number or PIN. Access denied.");
                return;
            }

            switch (command) {
                case "Withdraw":
                    withdrawal(enteredAccountNumber);
                    break;
                case "Deposit":
                    deposition(enteredAccountNumber);
                    break;
                case "Check Balance":
                    checkBalance(enteredAccountNumber);
                    break;
                case "Transaction History":
                    displayTransactionHistory();
                    break;
                case "Exit":
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }

        public void withdrawal(String accountNumber) {
            String withdrawAmountStr = JOptionPane.showInputDialog("Enter the amount to withdraw:");
            try {
                int withdrawAmount = Integer.parseInt(withdrawAmountStr);
                if (withdrawAmount > 0 && withdrawAmount <= balance) {
                    balance -= withdrawAmount;
                    displayResult("Withdrawal successful. Amount: " + withdrawAmount + "\nCurrent balance: " + balance);
                    logTransaction(accountNumber, "Withdrawal of " + withdrawAmount);
                } else {
                    displayResult("Invalid amount or insufficient funds. Please try again.");
                }
            } catch (NumberFormatException ex) {
                displayResult("Invalid input. Please enter a valid amount.");
            }
        }

        private void deposition(String accountNumber) {
            String depositAmountStr = JOptionPane.showInputDialog("Enter the amount to deposit:");
            try {
                int depositAmount = Integer.parseInt(depositAmountStr);
                if (depositAmount > 0) {
                    balance += depositAmount;
                    displayResult("Deposit successful. Amount: " + depositAmount + "\nCurrent balance: " + balance);
                    logTransaction(accountNumber, "Deposit of " + depositAmount);
                } else {
                    displayResult("Invalid amount. Please try again.");
                }
            } catch (NumberFormatException ex) {
                displayResult("Invalid input. Please enter a valid amount.");
            }
        }

        private void checkBalance(String accountNumber) {
            displayResult("Your current balance: " + balance);
            logTransaction(accountNumber, "Balance check");
        }

        private void displayTransactionHistory() {
            StringBuilder history = new StringBuilder("Transaction History:\n");
            if (transactionHistory.isEmpty()) {
                history.append("No transactions yet.");
            } else {
                for (String transaction : transactionHistory) {
                    history.append(transaction).append("\n");
                }
            }
            displayResult(history.toString());
        }

        private void displayResult(String message) {
            JOptionPane.showMessageDialog(null, message);
        }

        private void logTransaction(String accountNumber, String transaction) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            String transactionDetails = formattedDateTime + " - user: " + accountNumber + ", Transaction: " + transaction;
            transactionHistory.add(transactionDetails);

            SwingUtilities.invokeLater(() -> {
                textArea.append(transactionDetails + "\n");
            });
        }
    }
}
