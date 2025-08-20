import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class VoterRegister extends JFrame {
    public static Map<String, String> voterDB = new HashMap<>();
    public static Set<String> hasVoted = new HashSet<>();

    public VoterRegister() {
        setTitle("Voter Registration");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 15, 35),
                        getWidth(), getHeight(), new Color(40, 40, 70));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(null);
        add(panel);


        JLabel title = new JLabel("Voter Registration", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(180, 220, 255));
        title.setBounds(50, 20, 350, 30);
        panel.add(title);


        JLabel nidLabel = new JLabel("N.I.D Number:");
        JLabel nameLabel = new JLabel("Name:");
        JTextField nidField = new JTextField("Enter NID number");
        JTextField nameField = new JTextField("Enter Name");

        nidLabel.setForeground(Color.LIGHT_GRAY);
        nameLabel.setForeground(Color.LIGHT_GRAY);

        nidLabel.setBounds(50, 80, 100, 25);
        nameLabel.setBounds(50, 120, 100, 25);
        nidField.setBounds(160, 80, 200, 25);
        nameField.setBounds(160, 120, 200, 25);

        panel.add(nidLabel);
        panel.add(nameLabel);
        panel.add(nidField);
        panel.add(nameField);


        JButton registerBtn = createModernButton("Register", new Color(50, 130, 250));
        registerBtn.setBounds(80, 190, 120, 35);
        panel.add(registerBtn);

        JButton backBtn = createOutlineButton("Back", new Color(220, 50, 50));
        backBtn.setBounds(250, 190, 120, 35);
        panel.add(backBtn);


        registerBtn.addActionListener(e -> {
            String nid = nidField.getText().trim();
            String name = nameField.getText().trim();

            if (nid.isEmpty() || name.isEmpty()
                    || nid.equals("Enter NID number")
                    || name.equals("Enter Name")) {
                showNotification("Please enter all details!", Color.RED);
                return;
            }

            try (Connection con = getConnection()) {
                if (con == null) {
                    showNotification("Database connection failed!", Color.RED);
                    return;
                }
                String sql = "INSERT INTO voters (nid, name) VALUES (?, ?)";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, nid);
                    pst.setString(2, name);
                    pst.executeUpdate();
                }
                showNotification("Registered successfully!", new Color(33, 150, 243));
                nidField.setText("Enter NID number");
                nameField.setText("Enter Name");
            } catch (SQLIntegrityConstraintViolationException ex) {
                showNotification("NID already exists!", Color.RED);
            } catch (Exception ex) {
                showNotification("Error: " + ex.getMessage(), Color.RED);
                ex.printStackTrace();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new MainMenu();
        });


        nidField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (nidField.getText().equals("Enter NID number")) nidField.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (nidField.getText().isEmpty()) nidField.setText("Enter NID number");
            }
        });

        //Name
        nameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (nameField.getText().equals("Enter Name")) nameField.setText("");
            }
            public void focusLost(FocusEvent e) {
                if (nameField.getText().isEmpty()) nameField.setText("Enter Name");
            }
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

    private void showNotification(String message, Color bg) {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setSize(350, 80);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(bg);

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBounds(0, 0, 350, 80);
        dialog.add(lbl);

        Timer timer = new Timer(1500, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }


    public static Connection getConnection() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String databaseUrl = "jdbc:mysql://localhost:3306/vote"; // your DB name
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
