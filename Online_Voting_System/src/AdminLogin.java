import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame implements user {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    private JPasswordField passField;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 20, 40),
                        getWidth(), getHeight(), new Color(40, 40, 70));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(null);
        add(panel);

        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setBounds(50, 20, 300, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(180, 220, 255));
        panel.add(title);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField();
        passField = new JPasswordField();

        userLabel.setForeground(Color.LIGHT_GRAY);
        passLabel.setForeground(Color.LIGHT_GRAY);

        userLabel.setBounds(50, 70, 80, 25);
        passLabel.setBounds(50, 110, 80, 25);
        userField.setBounds(150, 70, 120, 25);
        passField.setBounds(150, 110, 120, 25);

        panel.add(userLabel);
        panel.add(passLabel);
        panel.add(userField);
        panel.add(passField);

        // Show/Hide password checkbox
        JCheckBox showPass = new JCheckBox("Show");
        showPass.setBounds(280, 110, 70, 25);
        showPass.setBackground(new Color(30, 30, 50));
        showPass.setForeground(Color.WHITE);
        showPass.addActionListener(e -> {
            if (showPass.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });
        panel.add(showPass);

        JButton loginBtn = createModernButton("Login", new Color(50, 130, 250));
        loginBtn.setBounds(150, 160, 80, 30);
        panel.add(loginBtn);

        JButton backBtn = createModernButton("Back", new Color(220, 50, 50));
        backBtn.setBounds(240, 160, 80, 30);
        panel.add(backBtn);


        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (user.equals(USERNAME) && pass.equals(PASSWORD)) {
                showAdminPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        backBtn.addActionListener(e -> {
            dispose();
            new MainMenu();
        });

        setVisible(true);
    }

    public void showAdminPanel() {
        JFrame adminPanel = new JFrame("Admin Panel");
        adminPanel.setSize(500, 550);
        adminPanel.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 20, 40),
                        getWidth(), getHeight(), new Color(40, 40, 70));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(null);
        adminPanel.add(panel);

        JLabel title = new JLabel("Admin Panel", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(180, 220, 255));
        title.setBounds(50, 20, 400, 30);
        panel.add(title);


        JButton addCandidateBtn = createModernButton("Add Candidate", new Color(50, 130, 250));
        JButton deleteCandidateBtn = createModernButton("Delete Candidate", new Color(220, 80, 80));
        JButton toggleResultBtn = createModernButton("Publish/Unpublish Result", new Color(50, 130, 250));
        JButton viewResultBtn = createModernButton("View Result", new Color(50, 130, 250));
        JButton viewCandidatesBtn = createModernButton("View Candidates", new Color(50, 130, 250));
        JButton backBtn = createModernButton("Back", new Color(220, 50, 50));

        addCandidateBtn.setBounds(150, 70, 200, 40);
        deleteCandidateBtn.setBounds(150, 120, 200, 40);
        toggleResultBtn.setBounds(150, 170, 200, 40);
        viewResultBtn.setBounds(150, 220, 200, 40);
        viewCandidatesBtn.setBounds(150, 270, 200, 40);
        backBtn.setBounds(150, 320, 200, 40);

        panel.add(addCandidateBtn);
        panel.add(deleteCandidateBtn);
        panel.add(toggleResultBtn);
        panel.add(viewResultBtn);
        panel.add(viewCandidatesBtn);
        panel.add(backBtn);

        // Admin Panel Back action
        backBtn.addActionListener(e -> {
            adminPanel.dispose();
            this.setVisible(true);
        });

        this.setVisible(false);


        addCandidateBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(adminPanel, "Enter candidate name:");
            if (name != null && !name.isEmpty()) {
                try (Connection con = getConnection()) {
                    String sql = "INSERT INTO candidates (name) VALUES (?)";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, name);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(adminPanel, "Candidate added.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(adminPanel, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        deleteCandidateBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(adminPanel, "Enter candidate name to delete:");
            if (name != null && !name.isEmpty()) {
                try (Connection con = getConnection()) {
                    String sql = "DELETE FROM candidates WHERE name = ?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, name);
                        int rows = pst.executeUpdate();
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(adminPanel, "Candidate deleted.");
                        } else {
                            JOptionPane.showMessageDialog(adminPanel, "Candidate not found.");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(adminPanel, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        toggleResultBtn.addActionListener(e -> {
            try (Connection con = getConnection()) {
                boolean published = false;
                String checkSql = "SELECT result_published FROM settings WHERE id = 1";
                try (PreparedStatement pst = con.prepareStatement(checkSql);
                     ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) published = rs.getBoolean("result_published");
                }
                String updateSql = "UPDATE settings SET result_published = ? WHERE id = 1";
                try (PreparedStatement pst = con.prepareStatement(updateSql)) {
                    pst.setBoolean(1, !published);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(adminPanel,
                            !published ? "Result published!" : "Result unpublished!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(adminPanel, "Error updating result status: " + ex.getMessage());
            }
        });

        viewResultBtn.addActionListener(e -> new ResultPage());

        viewCandidatesBtn.addActionListener(e -> {
            try (Connection con = getConnection()) {
                String sql = "SELECT name FROM candidates";
                try (PreparedStatement pst = con.prepareStatement(sql);
                     ResultSet rs = pst.executeQuery()) {

                    StringBuilder candidatesList = new StringBuilder("Candidates:\n");
                    while (rs.next()) {
                        candidatesList.append("- ").append(rs.getString("name")).append("\n");
                    }

                    JOptionPane.showMessageDialog(adminPanel,
                            candidatesList.length() > 11 ? candidatesList.toString() : "No candidates found.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(adminPanel, "Error fetching candidates: " + ex.getMessage());
            }
        });

        adminPanel.setVisible(true);
    }


    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/vote";
            String user = "root";
            String password = "T@mim764";
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private JButton createModernButton(String text, Color accent) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setForeground(Color.WHITE);
        b.setBackground(accent);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(accent.brighter()); }
            public void mouseExited(MouseEvent e) { b.setBackground(accent); }
        });
        return b;
    }

}
