import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VotingPage extends JFrame {

    private String voterNID;

    public VotingPage(String voterNID) {
        this.voterNID = voterNID;


        setTitle("Cast Your Vote");
        setSize(420, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // modern look

        //Main panel
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 60));
        panel.setLayout(null);
        add(panel);

        //Title
        JLabel title = new JLabel("Select Your Candidate");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(80, 15, 300, 30);
        panel.add(title);

        //Cand. buttons
        ButtonGroup group = new ButtonGroup();
        List<JRadioButton> options = new ArrayList<>();

        int y = 70;
        try (Connection con = AdminLogin.getConnection()) {
            String sql = "SELECT name FROM candidates";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                JRadioButton rb = new JRadioButton(name);
                rb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                rb.setForeground(Color.WHITE);
                rb.setBackground(new Color(30, 30, 60));
                rb.setFocusPainted(false);
                rb.setBounds(60, y, 300, 30);
                group.add(rb);
                panel.add(rb);
                options.add(rb);
                y += 40;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading candidates: " + ex.getMessage());
        }

        //Vote button
        JButton voteBtn = new JButton("Vote");
        voteBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        voteBtn.setBackground(new Color(33, 150, 243));
        voteBtn.setForeground(Color.WHITE);
        voteBtn.setFocusPainted(false);
        voteBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        voteBtn.setBounds(140, y + 10, 120, 35);
        panel.add(voteBtn);

        voteBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { voteBtn.setBackground(new Color(25, 118, 210)); }
            public void mouseExited(MouseEvent evt) { voteBtn.setBackground(new Color(33, 150, 243)); }
        });


        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(200, 50, 50));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBounds(140, y + 60, 120, 30);
        panel.add(backBtn);

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { backBtn.setBackground(new Color(255, 70, 70)); }
            public void mouseExited(MouseEvent evt) { backBtn.setBackground(new Color(200, 50, 50)); }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new VoterLogin();
        });


        voteBtn.addActionListener(e -> {
            String selected = null;
            for (JRadioButton rb : options) {
                if (rb.isSelected()) {
                    selected = rb.getText();
                    break;
                }
            }

            if (selected != null) {
                castVote(selected);
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Please select a candidate.");
            }
        });

        setVisible(true);
    }

    private void castVote(String candidateName) {
        try (Connection con = AdminLogin.getConnection()) {


            String checkSql = "SELECT has_voted FROM voters WHERE nid = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                checkStmt.setString(1, voterNID);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getBoolean("has_voted")) {
                    JOptionPane.showMessageDialog(this, "You have already voted!");
                    dispose();
                    return;
                }
            }


            String voteSql = "UPDATE candidates SET votes = votes + 1 WHERE name = ?";
            try (PreparedStatement voteStmt = con.prepareStatement(voteSql)) {
                voteStmt.setString(1, candidateName);
                voteStmt.executeUpdate();
            }


            String markSql = "UPDATE voters SET has_voted = 1 WHERE nid = ?";
            try (PreparedStatement markStmt = con.prepareStatement(markSql)) {
                markStmt.setString(1, voterNID);
                markStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "✅ Vote cast successfully!");
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error casting vote: " + ex.getMessage());
        }
    }

}
