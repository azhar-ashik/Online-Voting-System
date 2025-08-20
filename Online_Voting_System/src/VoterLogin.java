import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VoterLogin extends JFrame {

    public VoterLogin() {
        setTitle("Voter Login");
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


        JLabel title = new JLabel("Voter Login", SwingConstants.CENTER);
        title.setBounds(50, 20, 300, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(180, 220, 255));
        panel.add(title);


        JLabel nidLabel = new JLabel("NID:");
        JLabel nameLabel = new JLabel("Name:");
        JTextField nidField = new JTextField();
        JTextField nameField = new JTextField();

        nidLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setForeground(Color.LIGHT_GRAY);

        nidLabel.setBounds(50, 70, 80, 25);
        nameLabel.setBounds(50, 110, 80, 25);
        nidField.setBounds(150, 70, 180, 25);
        nameField.setBounds(150, 110, 180, 25);

        panel.add(nidLabel);
        panel.add(nameLabel);
        panel.add(nidField);
        panel.add(nameField);


        JButton loginBtn = createModernButton("Login", new Color(50, 130, 250));
        loginBtn.setBounds(80, 160, 100, 35);
        panel.add(loginBtn);

        JButton backBtn = createOutlineButton("Back", new Color(220, 50, 50));
        backBtn.setBounds(220, 160, 100, 35);
        panel.add(backBtn);

        //login
        loginBtn.addActionListener(e -> {
            String nid = nidField.getText().trim();
            String name = nameField.getText().trim();

            if (nid.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter NID and Name!");
                return;
            }

            //login db
            try (Connection con = getConnection()) {
                if (con == null) {
                    JOptionPane.showMessageDialog(this, "Database connection failed!");
                    return;
                }

                String sql = "SELECT * FROM voters WHERE nid = ? AND name = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, nid);
                    pst.setString(2, name);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        if (VoterRegister.hasVoted.contains(nid)) {
                            JOptionPane.showMessageDialog(this, "You have already voted!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Login successful!");
                            new VotingPage(nid);
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid NID or Name!");
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


        backBtn.addActionListener(e -> {
            dispose();
            new MainMenu();
        });

        setVisible(true);
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

    private JButton createOutlineButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(color);
        b.setBackground(new Color(30, 30, 30));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(color, 2, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(color.brighter()); }
            public void mouseExited(MouseEvent e) { b.setForeground(color); }
        });
        return b;
    }


    public static Connection getConnection() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String databaseUrl = "jdbc:mysql://localhost:3306/vote"; // your DB
            String userName = "root";
            String password = "T@mim764"; // your MySQL password
            Class.forName(driver);
            return DriverManager.getConnection(databaseUrl, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
