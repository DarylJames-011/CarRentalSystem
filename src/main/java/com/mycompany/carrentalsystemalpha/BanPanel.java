/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.carrentalsystemalpha;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Daryl James
 */
public class BanPanel extends javax.swing.JDialog {

    /**
     * Creates new form BanPanel
     */
   private final int userid;
    
   public BanPanel(Frame parent, boolean modal, int userID) {
    super(parent, modal);
    initComponents();
    setLocationRelativeTo(null);
    setTitle("Car Rental System - Ban Manager");
    loaddata(userID);
    this.userid = userID;
    bandata();
}

    public BanPanel(Dialog parent, boolean modal, int userID) {
        super(parent, modal);
        initComponents();
    setLocationRelativeTo(null);
    setTitle("Car Rental System - Ban Manager");
    this.userid = userID;
    loaddata(userID);
     System.out.println(userID);
     bandata();
     
    }
    
    private int clientid;
    private String clientname;
   
    private void banselect() {
         bantbl.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting() && bantbl.getSelectedRow() != -1) {
        int selectedRow = bantbl.getSelectedRow();
        clientid = Integer.parseInt(bantbl.getValueAt(selectedRow, 0).toString());

        String sql = "SELECT full_name FROM user_table WHERE user_ID = ?";
        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clientid);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("full_name");
                userlbl.setText(name);
                clientname = name;
                unbanbtn.setEnabled(true);
            }
            
        }
        catch (SQLException a) {
            a.printStackTrace();
        }
        
    }
    });}
    
    
    private void bandata() {
        String sql = "SELECT u.user_ID, u.full_name, d.ban_reason, d.ban_date FROM user_table u "
                + "JOIN user_data d ON u.user_id = d.user_id WHERE u.role = 'client' AND d.is_banned = TRUE;";
        
        try (Connection conn = DatabaseConnection.connect()) {

         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Client ID", "Client Name", "Ban Reason", "Ban Date"});

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

  
    while (rs.next()) {
        model.addRow(new Object[]{
            rs.getString("user_ID"),
            rs.getString("full_name"),
            rs.getString("ban_reason"),
            rs.getString("ban_date"),
        });
    }

            bantbl.setModel(model);
            bantbl.setDefaultEditor(Object.class, null);
            banselect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    
    
    
    }
    
    
    private void loaddata(int userId) {
    try (Connection conn = DatabaseConnection.connect()) {
                String sql = "SELECT full_name FROM user_table WHERE user_id = ?";
                java.sql.PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, userId);
                ResultSet rs = pst.executeQuery();
               
                if (rs.next()) {
                    String name = rs.getString("full_name");
                    userlbl2.setText(name);
                pst.close();
                conn.close();
                }
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }  
 
    }
    
    private void bancheck(int userID) {
   try(Connection conn = DatabaseConnection.connect()) {
        String sql = "SELECT is_banned FROM user_data WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userID);
        ResultSet rs = ps.executeQuery(); 
        
            if(rs.next()) {
                Boolean banned = rs.getBoolean("is_banned");
                if (banned) {
                    JOptionPane.showMessageDialog(null, "This user is already banned from the System!");
                    return;
                }
                else {
                    banclient(userID);
                }
            
            }
        

    } 
     catch (Exception e) {
        e.printStackTrace();
     }
    }
    
    
    private void banclient(int userId) {
     try(Connection conn = DatabaseConnection.connect()) {
        String sql = "UPDATE user_data " +
             "SET is_banned = TRUE, ban_reason = ?, ban_date = CURRENT_TIMESTAMP " +
             "WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        String reason = banreason2.getText();
        ps.setString(1,reason);
        ps.setInt(2,userId);
        ps.executeUpdate(); 
        
            JOptionPane.showMessageDialog(
          null,
          "User is successfully banned from the System!",
          "User Banned",
          JOptionPane.INFORMATION_MESSAGE
      );
        this.dispose();
    } 
     catch (Exception e) {
        e.printStackTrace();
    }
    }
    
    private void unbanclient() {
        String sql = "UPDATE user_data SET is_banned = FALSE, ban_reason = NULL, ban_date = NULL WHERE user_id = ? ";
    
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientid); // assuming userId is an int
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
               JOptionPane.showMessageDialog(
                   null,
                   "User is successfully unbanned from the System!",
                   "User Unbanned",
                   JOptionPane.INFORMATION_MESSAGE
               );
               dispose();
            } else {

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        userlbl2 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        banreason2 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        exitbtn2 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        bantbl = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        unbanbtn = new javax.swing.JButton();
        userlbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel15.setText("You are About to Ban : ");

        userlbl2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        userlbl2.setText("Placeholder");

        jLabel16.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel16.setText("Reason :");

        jScrollPane3.setBorder(null);

        banreason2.setBackground(new java.awt.Color(240, 240, 240));
        banreason2.setColumns(20);
        banreason2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        banreason2.setLineWrap(true);
        banreason2.setRows(1);
        jScrollPane3.setViewportView(banreason2);

        jButton3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton3.setText("Ban User");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3jButton1ActionPerformed(evt);
            }
        });

        exitbtn2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        exitbtn2.setText("Cancel");
        exitbtn2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        exitbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitbtn2exitbtnActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 249, 171));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download (5).png"))); // NOI18N

        jLabel18.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel18.setText("WARNING!");

        jLabel19.setText("Banning this user will permanently prevent them from");

        jLabel20.setText("making any reservations, and hides their name from ");

        jLabel21.setText("the system.");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel20)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel21)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(userlbl2))))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(83, 83, 83)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(exitbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 37, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(userlbl2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exitbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Ban User", jPanel6);

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel1.setText("Banned Clients - please select one of the clients to unban");

        bantbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        bantbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Client ID", "Client Name", "Ban reason", "Ban Date"
            }
        ));
        bantbl.setRowHeight(26);
        bantbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        bantbl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        bantbl.setShowHorizontalLines(false);
        bantbl.setShowVerticalLines(false);
        bantbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(bantbl);

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel2.setText("You are about to unban :");

        jPanel1.setBackground(new java.awt.Color(135, 219, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download.png"))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(0, 141, 201));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 7, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel4.setText("NOTE");

        jLabel22.setText("Unbanning a client restores their visibility in the system,");

        jLabel23.setText("allowing them to appear in client lists and perform");

        jLabel24.setText("transactions again.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(0, 0, 0)
                .addComponent(jLabel22)
                .addGap(0, 0, 0)
                .addComponent(jLabel23)
                .addGap(0, 0, 0)
                .addComponent(jLabel24)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        unbanbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        unbanbtn.setText("Unban Client");
        unbanbtn.setEnabled(false);
        unbanbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unbanbtnActionPerformed(evt);
            }
        });

        userlbl.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        userlbl.setText("no user selected");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(userlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(unbanbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(userlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21)
                .addComponent(unbanbtn)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Unban User", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3jButton1ActionPerformed
        if (banreason2 == null)  {
            JOptionPane.showMessageDialog(null,
                "Please add a reason of the clients banning.");
            
        }
        else {
            bancheck(userid);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3jButton1ActionPerformed

    private void exitbtn2exitbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitbtn2exitbtnActionPerformed
        this.dispose();    // TODO add your handling code here:
    }//GEN-LAST:event_exitbtn2exitbtnActionPerformed

    private void unbanbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unbanbtnActionPerformed
     int confirm = JOptionPane.showConfirmDialog(
        null, 
        "Are you sure you want to unban " + clientname + "?", 
        "Confirm Unban", 
        JOptionPane.YES_NO_OPTION
);

        if (confirm == JOptionPane.YES_OPTION) {
            unbanclient();  // your method to delete
        }        // TODO add your handling code here:
    }//GEN-LAST:event_unbanbtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        //</editor-fold>
              try {
            // Apply modern Look and Feel for all Swing components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BanPanel dialog = new BanPanel(new javax.swing.JFrame(), true,0);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea banreason2;
    private javax.swing.JTable bantbl;
    private javax.swing.JButton exitbtn2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton unbanbtn;
    private javax.swing.JLabel userlbl;
    private javax.swing.JLabel userlbl2;
    // End of variables declaration//GEN-END:variables
}
