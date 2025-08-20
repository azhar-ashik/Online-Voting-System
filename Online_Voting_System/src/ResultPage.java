import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ResultPage extends JFrame {

    public ResultPage() {
        setTitle("Election Results");
        setSize(420, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);


        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 60));
        panel.setLayout(null);
        add(panel);


        JLabel title = new JLabel("Election Results", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(180, 220, 255));
        title.setBounds(50, 15, 320, 30);
        panel.add(title);


        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultArea.setForeground(Color.WHITE);
        resultArea.setBackground(new Color(40, 40, 70));
        resultArea.setBounds(30, 60, 360, 260);
        panel.add(resultArea);


        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(200, 50, 50));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBounds(150, 330, 120, 30);
        panel.add(backBtn);

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { backBtn.setBackground(new Color(255, 70, 70)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { backBtn.setBackground(new Color(200, 50, 50)); }
        });

        backBtn.addActionListener(e -> dispose());


        try (Connection con = AdminLogin.getConnection()) {
            String sql = "SELECT name, votes FROM candidates ORDER BY votes DESC";
            try (PreparedStatement pst = con.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {

                StringBuilder sb = new StringBuilder("Election Results:\n\n");
                while (rs.next()) {
                    sb.append(rs.getString("Name"))
                            .append(": ")
                            .append(rs.getInt("Votes"))
                            .append(" Votes\n");
                }
                resultArea.setText(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage());
        }

        setVisible(true);
    }

}
