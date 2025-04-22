package bg.haskorders.delivery.client;

import bg.haskorders.delivery.model.cart.Cart;

import javax.swing.*;
import java.awt.*;


public class PaymentPage extends JFrame {
    private final Cart cart;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private final Runnable onPaymentComplete;


    public PaymentPage(Cart cart) {
        this(cart, null);
    }

    public PaymentPage(Cart cart, Runnable onPaymentComplete) {
        this.cart = cart;
        this.onPaymentComplete = onPaymentComplete;
        initialize();
    }

    private void initialize() {
        setTitle("Payment Information");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Enter Your Details", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField();
        phoneField = new JTextField();
        addressArea = new JTextArea();
//        addressArea = new JTextArea(4, 20);
//        addressArea.setLineWrap(true);
//        addressArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        formPanel.add(nameField, gbc);

        gbc.gridy++;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridy++;
        formPanel.add(phoneField, gbc);

        gbc.gridy++;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridy++;
        formPanel.add(new JScrollPane(addressArea), gbc);

        JButton proceedToPayment = new JButton("Proceed to Payment");
        proceedToPayment.addActionListener(e -> showPaymentOptions());

        gbc.gridy++;
        formPanel.add(proceedToPayment, gbc);

        add(formPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void showPaymentOptions() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields before proceeding.");
            return;
        }

        getContentPane().removeAll();

        JLabel label = new JLabel("Enter Card Details", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(label, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card number field
        JPasswordField cardField = new JPasswordField();
        cardField.setEchoChar('•');
        cardField.setColumns(16);

        JButton toggleVisibility = new JButton("👁");
        toggleVisibility.setFocusable(false);
        toggleVisibility.addActionListener(e -> {
            if (cardField.getEchoChar() == (char) 0) {
                cardField.setEchoChar('•');
            } else {
                cardField.setEchoChar((char) 0); // show characters
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        form.add(new JLabel("Card Number:"), gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        form.add(cardField, gbc);

        gbc.gridx = 1;
        form.add(toggleVisibility, gbc);

        JButton payBtn = new JButton("Pay Now");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        form.add(payBtn, gbc);

        payBtn.addActionListener(e -> {
            String cardNumber = new String(cardField.getPassword()).trim();
            if (!cardNumber.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Card number must be exactly 16 digits.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            confirmPayment("Card ending in " + cardNumber.substring(12), name, phone, address);
        });

        add(form, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void confirmPayment(String cardInfo, String name, String phone, String address) {
        JOptionPane.showMessageDialog(this,
                "Order confirmed!\n\n" +
                        "Name: " + name + "\n" +
                        "Phone: " + phone + "\n" +
                        "Address: " + address + "\n" +
                        "Payment: " + cardInfo,
                "Success", JOptionPane.INFORMATION_MESSAGE);
        if (onPaymentComplete != null) {
            onPaymentComplete.run();
        }
        cart.clear();
        dispose();
    }
}
