/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.carrentalsystemalpha;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Daryl James
 */
public class Verifyclient extends javax.swing.JDialog {

    /**
     * Creates new form Verifyclient
     */

     public Verifyclient(java.awt.Frame parent, boolean modal, int admin_id) {
        super(parent, modal);
        initComponents();
        setTitle("Car Rental System - Verify Clients");
        setLocationRelativeTo(null);
        loadalldata(null);
        this.admin_Id = admin_id;
        userselector();
        getAdmindata();
    }
    
    
        public Verifyclient(JDialog owner, boolean modal, int admin_id) {
        super(owner, modal);
        initComponents();
        setTitle("Car Rental System - Verify Clients");
        setLocationRelativeTo(null);
        loadalldata(null);
        this.admin_Id = admin_id;
        userselector();
        getAdmindata();
    }
    
    private final int admin_Id;
    private JFrame frame;
    private String id_img; 
    private int user_id;
    private boolean verified;
    private String role;
    
    
    private void getAdmindata() {
        try(Connection conn = DatabaseConnection.connect()) {
        String sql = "SELECT role FROM user_table WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1,admin_Id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            role = rs.getString("role");   
        }  
        }
        catch(Exception e) {
        e.printStackTrace();
        } 
    }
 
        
    public String baseSql = 
        "SELECT u.user_id, u.full_name, u.verified, d.id_photo, d.verified_requested " +
             "FROM user_table u " +
             "JOIN user_data d ON u.user_id = d.user_id " +
             "WHERE u.role = 'Client' AND d.is_banned = FALSE";
        
        
        
     private void loadalldata(String filter) {
     try (Connection conn = DatabaseConnection.connect()) {
         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Client ID", "Client Name", "Status", "Has ID Photo"});
        String sql = baseSql;
        

           if ("verified".equals(filter)) {
                sql += " AND u.verified = TRUE";
            } else if ("pending".equals(filter)) {
                sql += " AND d.verified_requested = TRUE AND u.verified = FALSE";
            } else if ("unverified".equals(filter)) {
                sql += " AND d.verified_requested = FALSE AND u.verified = FALSE";
            }   
           
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);   
           
        while (rs.next()) {
              boolean verified = rs.getBoolean("verified"); 
              boolean requestverify = rs.getBoolean("verified_requested");  
              String id_img = rs.getString("id_photo");
                     
             
            String status;
            if (verified) {
                status = "Verified";
            }
            else if (requestverify) {
                status = "Pending";
            }
            else {
                status = "Not Verified";
            }
          
            
             String status1;
            if (id_img == null || id_img.isEmpty()) {
                status1 = "No"; 
            } else {
                status1 = "Yes";
            }
            
            model.addRow(new Object[]{
                rs.getString("user_id"),
                rs.getString("full_name"),
                status,
                status1
            });
        }

         usertbl.setModel(model); 
        usertbl.setDefaultEditor(Object.class, null);
             
         } catch (Exception e) {
            e.printStackTrace();
        }
     }  
           
        private void userselector() {
       usertbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
       usertbl.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() && usertbl.getSelectedRow() != -1) {
        int selectedRow = usertbl.getSelectedRow();
        
           
        String userId = usertbl.getValueAt(selectedRow, 0).toString(); 
        loadimage(userId); 
        deselectbtn.setEnabled(true);
    }
    });
   }    

    private void loadimage(String userId) {
    Connection conn = DatabaseConnection.connect();
    String sql = "SELECT d.id_photo, u.verified " +
             "FROM user_data d " +
             "JOIN user_table u ON d.user_id = u.user_id " +
             "WHERE u.user_id = ?";
    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, userId);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                id_img = rs.getString("id_photo");
                 verified = rs.getBoolean("verified");
                 int num  = Integer.parseInt(userId);
                 user_id = num ;
                 
                
                 if (id_img != null && !id_img.isEmpty()) {
                      viewidbtn.setEnabled(true);     
                } else {
                    viewidbtn.setEnabled(false);  
                }
                
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    } 
    } 
    
    private void approve(int userId) {
        try(Connection conn = DatabaseConnection.connect()) {
        String sql = "UPDATE user_table SET verified = TRUE WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1,userId);
        ps.executeUpdate();
        
        String sql1 = "UPDATE user_data SET verified_requested = FALSE WHERE user_id = ?";
        PreparedStatement ps1 = conn.prepareStatement(sql1);
        ps1.setInt(1,userId);
        ps1.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "ID approved successfully!");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    private void reject(int userId) {
        try(Connection conn = DatabaseConnection.connect()) {
        String sql = "UPDATE user_table SET verified_requested = FALSE WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1,userId);
        ps.executeUpdate();
        
        
        JOptionPane.showMessageDialog(null, "Client's ID has been rejected!");
    } catch (Exception e) {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usertbl = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        resetbtn = new javax.swing.JButton();
        verifiedbtn = new javax.swing.JRadioButton();
        pendingbtn = new javax.swing.JRadioButton();
        unverifiedbtn = new javax.swing.JRadioButton();
        viewidbtn = new javax.swing.JButton();
        deselectbtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Verify Clients", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14))); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/verify.png"))); // NOI18N

        jLabel2.setText("Users cannot rent or make a reservations without");

        jLabel3.setText("their account being verified. If you find any fake");

        jLabel4.setText("ID being used for verification, you can ban the");

        jLabel5.setText("Client for falsifying information.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel3)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel4)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel5)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        usertbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        usertbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Customer ID", "Customer Name", "Status", "Has ID Photo"
            }
        ));
        usertbl.setRowHeight(26);
        usertbl.setShowHorizontalLines(false);
        usertbl.setShowVerticalLines(false);
        usertbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(usertbl);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N

        resetbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        resetbtn.setText("Reset ");
        resetbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetbtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(verifiedbtn);
        verifiedbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        verifiedbtn.setText("Verified");
        verifiedbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifiedbtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(pendingbtn);
        pendingbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        pendingbtn.setText("Pending");
        pendingbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendingbtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(unverifiedbtn);
        unverifiedbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        unverifiedbtn.setText("Unverified");
        unverifiedbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unverifiedbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(verifiedbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pendingbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(unverifiedbtn)
                .addGap(10, 10, 10)
                .addComponent(resetbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(verifiedbtn)
                    .addComponent(pendingbtn)
                    .addComponent(unverifiedbtn))
                .addContainerGap())
        );

        viewidbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        viewidbtn.setText("View the ID ");
        viewidbtn.setEnabled(false);
        viewidbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewidbtnActionPerformed(evt);
            }
        });

        deselectbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        deselectbtn.setText("Deselect User");
        deselectbtn.setEnabled(false);
        deselectbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(viewidbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deselectbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deselectbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(viewidbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deselectbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectbtnActionPerformed
        usertbl.clearSelection();
        viewidbtn.setEnabled(false); 
        deselectbtn.setEnabled(false);// TODO add your handling code here:
    }//GEN-LAST:event_deselectbtnActionPerformed

    private void resetbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetbtnActionPerformed
        buttonGroup1.clearSelection();
        loadalldata(null);  
    }//GEN-LAST:event_resetbtnActionPerformed

    private void verifiedbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifiedbtnActionPerformed
       loadalldata("verified");            // TODO add your handling code here:
    }//GEN-LAST:event_verifiedbtnActionPerformed

    private void pendingbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendingbtnActionPerformed
      loadalldata("pending");         // TODO add your handling code here:
    }//GEN-LAST:event_pendingbtnActionPerformed

    private void unverifiedbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unverifiedbtnActionPerformed
       loadalldata("unverified");            // TODO add your handling code here:
    }//GEN-LAST:event_unverifiedbtnActionPerformed

    private void viewidbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewidbtnActionPerformed
        File imgFile = new File(id_img); 
        ImageIcon icon = new ImageIcon(
        new ImageIcon(imgFile.getAbsolutePath())
            .getImage()
            .getScaledInstance(400, 300, Image.SCALE_SMOOTH)
    );

    JLabel imgLabel = new JLabel(icon);
    Object[] options = {"Approve", "Reject", "Ban Client"};

int choice = JOptionPane.showOptionDialog(
        frame,                        // parent
        imgLabel,                     // content (your preview)
        "Preview Image",              // title
        JOptionPane.DEFAULT_OPTION,   // option type
        JOptionPane.PLAIN_MESSAGE,    // message type
        null,                         // custom icon (null = none)
        options,                      // button labels
        options[0]                    // default selected
);

switch (choice) {
    case 0: // Approve
        if (verified) {
            JOptionPane.showMessageDialog(null, 
                "This client is already verified!");
        } else {
            approve(user_id);
        }
        break;

    case 1: // Reject
        if (verified) {
            JOptionPane.showMessageDialog(null, 
                "You can't reject a client that is already verified!");
        } else {
            reject(user_id);
        }
        break;  
    case 2: 
        if("admin".equalsIgnoreCase(role)) {
            JOptionPane.showMessageDialog(null, 
                "You don't have a permission to ban clients.");
        }
        else {
        BanPanel ban = new BanPanel(this,true,user_id);
        ban.setVisible(true);
        }
        break;
    default:
        break;
}// TODO add your handling code here:
    }//GEN-LAST:event_viewidbtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
           try {
            // Apply modern Look and Feel for all Swing components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //</editor-fold>


    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton deselectbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton pendingbtn;
    private javax.swing.JButton resetbtn;
    private javax.swing.JRadioButton unverifiedbtn;
    private javax.swing.JTable usertbl;
    private javax.swing.JRadioButton verifiedbtn;
    private javax.swing.JButton viewidbtn;
    // End of variables declaration//GEN-END:variables
}
