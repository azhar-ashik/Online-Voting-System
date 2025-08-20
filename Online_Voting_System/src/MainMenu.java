import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainMenu extends JFrame {

    private JButton showResultBtn;

    public MainMenu() {
        setTitle("Online Voting System - Main Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2)); // left + right


        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 10, 30),        // dark navy
                        getWidth(), getHeight(), new Color(25, 25, 50) // slightly lighter
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(60, 48, 48, 48));

        JLabel title = new JLabel("Online Voting System");
        title.setForeground(new Color(200, 220, 255));
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("<html>Welcome!<br>Choose an action on the right to continue.</html>");
        sub.setForeground(new Color(180, 200, 230));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(sub);
        left.add(Box.createVerticalGlue());


        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(new Color(15, 15, 25));

        JPanel card = new JPanel();
        card.setBackground(new Color(20, 20, 40));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80)),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        ));
        card.setMaximumSize(new Dimension(420, 9999));

        JLabel actions = new JLabel("Main Menu");
        actions.setFont(new Font("Segoe UI", Font.BOLD, 22));
        actions.setForeground(new Color(100, 180, 255));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);


        JButton btnAdmin   = createModernButton("Admin Login",    new Color(50, 130, 250));
        JButton btnVReg    = createModernButton("Voter Register", new Color(50, 130, 250));
        JButton btnVLogin  = createModernButton("Voter Login",    new Color(50, 130, 250));
        showResultBtn      = createModernButton("Show Result",    new Color(50, 130, 250));
        JButton btnExit    = createOutlineButton("Exit",          new Color(220, 50, 50));


        btnAdmin.addActionListener(e -> new AdminLogin());
        btnVReg.addActionListener(e -> new VoterRegister());
        btnVLogin.addActionListener(e -> new VoterLogin());
        btnExit.addActionListener(e -> System.exit(0));

        showResultBtn.addActionListener(e -> {
            try (Connection con = getConnection()) {
                String sql = "SELECT result_published FROM settings WHERE id = 1";
                try (PreparedStatement pst = con.prepareStatement(sql);
                     ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        boolean published = rs.getBoolean("result_published");
                        if (published) {
                            new ResultPage();
                        } else {
                            JOptionPane.showMessageDialog(this, "Results have not been published yet!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Settings row not found!");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error checking result status.");
            }
        });

        checkResultStatus();
        Timer timer = new Timer(2000, e -> checkResultStatus());
        timer.start();


        card.add(actions);
        card.add(Box.createVerticalStrut(18));
        card.add(btnAdmin);
        card.add(Box.createVerticalStrut(12));
        card.add(btnVReg);
        card.add(Box.createVerticalStrut(12));
        card.add(btnVLogin);
        card.add(Box.createVerticalStrut(12));
        card.add(showResultBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(btnExit);

        rightWrapper.add(card);

        add(left);
        add(rightWrapper);

        setVisible(true);
    }

    private void checkResultStatus() {
        try (Connection con = getConnection()) {
            String sql = "SELECT result_published FROM settings WHERE id = 1";
            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    boolean published = rs.getBoolean("result_published");
                    showResultBtn.setEnabled(published);
                } else {
                    showResultBtn.setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showResultBtn.setEnabled(false);
        }
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
        JButton b = new JButton(text) {
            boolean hover = false;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 26;
                g2.setColor(hover ? accent.brighter() : accent);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        styleButtonCommon(b, accent, true);
        return b;
    }

    private JButton createOutlineButton(String text, Color color) {
        JButton b = new JButton(text) {
            boolean hover = false;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 26;
                g2.setColor(new Color(30,30,30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(hover ? color.brighter() : color);
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, arc, arc);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        styleButtonCommon(b, color, false);
        return b;
    }

    private void styleButtonCommon(JButton b, Color accent, boolean filled) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setForeground(filled ? Color.WHITE : accent);
        b.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setHover(b, true); }
            @Override public void mouseExited(MouseEvent e)  { setHover(b, false); }
            private void setHover(JButton btn, boolean h) {
                try {
                    var f = btn.getClass().getDeclaredField("hover");
                    f.setAccessible(true);
                    f.setBoolean(btn, h);
                    btn.repaint();
                } catch (Exception ignored) { }
            }
        });
    }

}
