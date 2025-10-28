/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.carrentalsystemalpha;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.CardLayout;
import java.awt.Color;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


/**
 *
 * @author Daryl James
 */
public class ViewReserve extends javax.swing.JDialog {

    /**
     * Creates new form ViewReserve
     */
    
    private final int reserveId;
    private int carId;
    private String user;
    private int userID;
    private int balance;
    private int paid;
    private String username;
    private int paymentId;
    private String note;
    private String status;
    private final int AdminID;
    private int AdminIdFetch;
    private String role;
    private boolean pays;
    
    public ViewReserve(java.awt.Frame parent, boolean modal, int reserveid, int adminid, boolean pay) {
        super(parent, modal);
        initComponents();
        this.reserveId = reserveid;
        this.AdminID = adminid;
        this.pays = pay;
        if (pays) {
            tabpane.setSelectedIndex(1);
        }
        
        setTitle("Car Rental System - Reservation Details");
        setLocationRelativeTo(null);
        reservedata(reserveId);
        paymentdata(reserveId);
        admindata();
        
        
    }

    private void admindata() {
        String sql = "SELECT role FROM user_table WHERE user_id = ?";
        try(Connection conn = DatabaseConnection.connect()) {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, AdminID);
        ResultSet rs = ps.executeQuery();
        
        if(rs.next()) {
            role = rs.getString("role");
        }}
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void reservedata(int reserveId) {
        String sql = "SELECT r.*, u.full_name AS made_by_name FROM " +
        "reservations_table r LEFT JOIN user_table u " +
        "ON r.made_by = u.user_id WHERE r.reservation_id = ?";
        try(Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, reserveId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    user = rs.getString("user_id");
                    userID = userId;
                    carId = rs.getInt("car_id");
                    status = rs.getString("status");
                    reservidlbl.setText(rs.getString("reservation_id"));
                    reservstlbl.setText(status);
                    Timestamp start = rs.getTimestamp("start_date");
                    Timestamp end = rs.getTimestamp("end_date");
                    Timestamp made = rs.getTimestamp("date_created");
                    String formattedDate = new SimpleDateFormat("MM/dd/yyyy - HH:mm").format(start);
                    String formattedDate1 = new SimpleDateFormat("MM/dd/yyyy - HH:mm").format(end);
                    String formattedDate2 = new SimpleDateFormat("MM/dd/yyyy - HH:mm").format(made);
                    startdatelbl.setText(formattedDate);
                    enddatelbl.setText(formattedDate1);
                    durationlbl.setText(rs.getString("duration_hrs"));        
                    datelbl.setText(formattedDate2);    
                    madelbl.setText(rs.getString("made_by_name"));   
                    note = rs.getString("note");
                    AdminIdFetch = rs.getInt("made_by");
                    
                    if(note == null){
                        notearea.setText(note);
                        readbtn.setEnabled(false);
                    }
                    else {
                        readbtn.setEnabled(true);
                    }

                    
           
                    if("Cancelled".equalsIgnoreCase(status)) {
                        reservstlbl.setForeground(new Color(255, 38, 38));
                        cancelbtn.setEnabled(false);
                        editbtn.setEnabled(false);
                    }
                    else if("Confirmed".equalsIgnoreCase(status)) {
                        reservstlbl.setForeground(new Color(36,150,59));
                        cancelbtn.setEnabled(false);
                        editbtn.setEnabled(false);
                        
                    }
                    else if ("Completed".equalsIgnoreCase(status)) {
                        reservstlbl.setForeground(new Color(61,77,255));
                        cancelbtn.setEnabled(false);
                        editbtn.setEnabled(false);
                    }
                    
                    else if("Ongoing".equalsIgnoreCase(status)) {
                        reservstlbl.setForeground(new Color(36,150,59));
                        cancelbtn.setEnabled(false);
                        editbtn.setEnabled(false);
                        returnbtn.setEnabled(true);
                    }
                    
                    else {
                        reservstlbl.setForeground(new Color(194, 139, 0));
                    }
                    
                    userdata(userId);
                    vehicledata(carId);
            }
            
        }
        
        catch(SQLException e) {
            e.printStackTrace();
        }}
    
    private void userdata(int userId){
         String sql = "SELECT u.*, d.date_created FROM user_table u JOIN "
                 + "user_data d ON u.user_id = d.user_id WHERE u.user_id = ?";
         try(Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                    username = rs.getString("full_name");
                    namelbl.setText(username);
                    idlbl.setText(rs.getString("user_id"));
                    numlbl.setText(rs.getString("phone_num"));
                    adrslbl.setText(rs.getString("address"));
                    createdlbl.setText(rs.getString("date_created"));          
            }
            
        }
        
        catch(SQLException e) {
            e.printStackTrace();
        }}
    
    private void vehicledata(int carId) {
            String sql = "SELECT * FROM car_table WHERE vehicle_id = ?";
         try(Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                    modellbl.setText(rs.getString("vehicle_model"));
                    caridlbl.setText(rs.getString("vehicle_id"));
                    cartypelbl.setText(rs.getString("vehicle_type"));
                    fuellbl.setText(rs.getString("fuel_type"));
                    seatlbl.setText(rs.getString("seats"));
                    rate12lbl.setText(rs.getString("rate_12"));          
                    rate24lbl.setText(rs.getString("rate_24"));   
            
                    
            
            
            
            }
            
        }
        
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
  
    private void paymentdata(int reserveid) {
        
        String sql = "SELECT r.* FROM payment_tbl r WHERE r.reservation_id = ?";
            CardLayout cl = (CardLayout) CardL.getLayout();
            CardL.add(Paidpanel, "PAID");
            CardL.add(partialpanel, "PARTIAL");
            CardL.add(nopaymentpanel, "NOPAY");
            CardL.add(cancelpnl, "CANCELLED");
        
           try(Connection conn = DatabaseConnection.connect()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, reserveid);
            ResultSet rs = ps.executeQuery();
            DecimalFormat df = new DecimalFormat("#,##0.00"); // 2 decimal places
            if (rs.next()) {
                    int total = rs.getInt("total_cost");
                    int amt = rs.getInt("amount_paid");
                    int fee = rs.getInt("reservation_fee");
                    int deposit = rs.getInt("deposit_amt");
                    paid = rs.getInt("amount_paid");
                    balance = total - paid;
                    paymentId = rs.getInt("payment_id");
                    payidlbl.setText(String.valueOf(paymentId));
                    feelbl.setText(String.valueOf(df.format(fee)));
                    String status = rs.getString("status");
                    depositlbl.setText(String.valueOf(df.format(deposit)));
                    totallbl.setText(String.valueOf(df.format(total)));
                    amtlbl.setText(String.valueOf(df.format(amt)));          
                    otblbl.setText(String.valueOf(df.format(balance)));
                    
                    Timestamp timepaid = rs.getTimestamp("fully_paid_date");
                    
                    LocalDate fullyPaidDate = null;
                    if (timepaid != null) {
                        fullyPaidDate = timepaid.toLocalDateTime().toLocalDate();
                    }
                    
                    if (fullyPaidDate != null) {
                        paydatelbl.setText(fullyPaidDate.toString());
                    }
                    
                    
                    if ("Cancelled".equalsIgnoreCase(status)) {
                        cl.show(CardL, "CANCELLED"); 
                        amtlbl.setForeground(new Color(36,150,59));
                        paymentbtn.setEnabled(false);
                   } else if (paid == 0) {
                        cl.show(CardL, "NOPAY"); 
                       amtlbl.setForeground(new Color(255, 38, 38));
                    } else if (paid > 0 && balance != 0){
                       cl.show(CardL, "PARTIAL"); 
                       amtlbl.setForeground(new Color(194, 139, 0));
                   } else {
                        cl.show(CardL, "PAID");
                       amtlbl.setForeground(new Color(36,150,59));
                       paymentbtn.setEnabled(false);
                    }
                    
            }
            
        }
        
        catch(SQLException e) {
            e.printStackTrace();
        }}
    
      private void reservecancel(int ID) {
         String sql = "SELECT u.full_name FROM reservations_table r "
           + "JOIN user_table u ON r.user_id = u.user_id "
           + "WHERE r.reservation_id = ?";

try (Connection conn = DatabaseConnection.connect();
     PreparedStatement ps = conn.prepareStatement(sql)) {

    ps.setInt(1, ID); 
    try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            String clientName = rs.getString("full_name");
        
            int option = JOptionPane.showConfirmDialog(
        null, // parent component, null if none
        "Are you sure you want to Cancel " + clientName + "'s Reservation?", // message
        "Cancel Reservation", // title
        JOptionPane.YES_NO_OPTION // buttons
);

if (option == JOptionPane.YES_OPTION) {
    sql = "UPDATE reservations_table SET status = 'Cancelled' WHERE reservation_id = ?";
    try (PreparedStatement ps1 = conn.prepareStatement(sql)) {
        ps1.setInt(1, ID);
        int rows = ps1.executeUpdate();
        if (rows > 0) {
            Success dialog = new Success(this, true, "Reservation Cancelled");
            dialog.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Failed to cancel reservation.");
        }
    }
}}}
} catch (SQLException e) {
    e.printStackTrace();
}}
    
     private void fullpaid() {
         Connection conn = null;
         PreparedStatement ps1 = null;
         PreparedStatement ps2 = null; 
     
         try {
             conn = DatabaseConnection.connect();
             conn.setAutoCommit(false);
             String sql = "UPDATE reservations_table SET status = 'Confirmed' WHERE reservation_id = ?";
             ps1 = conn.prepareStatement(sql);
             ps1.setInt(1, reserveId);  
             ps1.executeUpdate();  
             
             int total = balance + paid;
             
             String sql2 = "UPDATE payment_tbl SET amount_paid = ?, status = 'Paid', fully_paid_date = NOW() WHERE payment_id = ?";
                ps2 = conn.prepareStatement(sql2);
                ps2.setDouble(1, total); // works even if paid == 0
                ps2.setInt(2, paymentId);
                ps2.executeUpdate();
             
              conn.commit();
         }
         
         catch(SQLException e) {
               if (conn != null) {
                try {
                    conn.rollback(); // rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
    }
             
             e.printStackTrace();
         } 
        finally {
            try {
                if (ps1 != null) ps1.close();
                if (ps2 != null) ps2.close();
                if (conn != null) conn.setAutoCommit(true); // reset auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
         
         Success dialog = new Success(this,true,"Successfully Marked as Paid!");
         dialog.setVisible(true);
         dispose();
     
        } 
      
    private void markreturn() {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        String sql = "UPDATE reservations_table SET status = 'Completed' WHERE reservation_id = ?";
        String sql2 = "UPDATE car_table SET status = 'Available' WHERE vehicle_id = ?";
        
        try  {
            conn = DatabaseConnection.connect();
            conn.setAutoCommit(false);
            ps1 = conn.prepareStatement(sql);
            ps1.setInt(1, reserveId);
            ps1.executeUpdate();
            
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, carId);
            ps2.executeUpdate();
            
            conn.commit();
            JOptionPane.showMessageDialog(null, "Vehicle is marked as returned successfully");
            dispose();
        }
        
    
        catch(SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating records: " + e.getMessage());
         }
        finally {
            try {
                if (ps1 != null) ps1.close();
                if (ps2 != null) ps2.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }}
    
    private void noteedit(String note) {
       String sql = "UPDATE reservations_table SET note = ? WHERE reservation_id = ?";
       try(Connection conn = DatabaseConnection.connect()) {
       PreparedStatement ps = conn.prepareStatement(sql);
       ps.setString(1,note);
       ps.setInt(2,reserveId);
       ps.executeUpdate();
       
       Success dialog = new Success(this,true, "Successfully added Note.");
       dialog.setVisible(true);
       reservedata(reserveId);
       }
       catch(SQLException e) {
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

        notepad = new javax.swing.JScrollPane();
        notearea = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        tabpane = new javax.swing.JTabbedPane();
        reservinfo = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        reservstlbl = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        editbtn = new javax.swing.JButton();
        cancelbtn = new javax.swing.JButton();
        startdatelbl = new javax.swing.JLabel();
        enddatelbl = new javax.swing.JLabel();
        durationlbl = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        datelbl = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        madelbl = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        reservidlbl = new javax.swing.JLabel();
        editbtn1 = new javax.swing.JButton();
        readbtn = new javax.swing.JButton();
        durationlbl1 = new javax.swing.JLabel();
        paymentinfo = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        CardL = new javax.swing.JPanel();
        partialpanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        Paidpanel = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        paydatelbl = new javax.swing.JLabel();
        nopaymentpanel = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        cancelpnl = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        payidlbl = new javax.swing.JLabel();
        feelbl = new javax.swing.JLabel();
        depositlbl = new javax.swing.JLabel();
        amtlbl = new javax.swing.JLabel();
        otblbl = new javax.swing.JLabel();
        paymentbtn = new javax.swing.JButton();
        totallbl = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        vehicleinfo = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        modellbl = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        caridlbl = new javax.swing.JLabel();
        cartypelbl = new javax.swing.JLabel();
        fuellbl = new javax.swing.JLabel();
        rate24lbl = new javax.swing.JLabel();
        rate12lbl = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        seatlbl = new javax.swing.JLabel();
        returnbtn = new javax.swing.JButton();
        clientinfo = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        namelbl = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        idlbl = new javax.swing.JLabel();
        numlbl = new javax.swing.JLabel();
        adrslbl = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        createdlbl = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        notepad.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notepad.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        notearea.setColumns(20);
        notearea.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        notearea.setLineWrap(true);
        notearea.setRows(10);
        notearea.setWrapStyleWord(true);
        notepad.setViewportView(notearea);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 221, 158));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel3.setBackground(new java.awt.Color(209, 136, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download (7).png"))); // NOI18N

        reservstlbl.setText("null");

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel5.setText("Reservation Status :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(reservstlbl))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reservstlbl))
                    .addComponent(jLabel1))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel2.setText("RESERVATION INFO");

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel6.setText("Start Date & Time :");

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel7.setText("End Date & Time :");

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel14.setText("Duration :");

        editbtn.setText("Edit Reservation");
        editbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editbtnActionPerformed(evt);
            }
        });

        cancelbtn.setText("Cancel Reservation");
        cancelbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelbtnActionPerformed(evt);
            }
        });

        startdatelbl.setText("null");

        enddatelbl.setText("null");

        durationlbl.setText("null");

        jLabel19.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel19.setText("Reservation Made :");

        datelbl.setText("null");

        jLabel21.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel21.setText("Made By:");

        madelbl.setText("Daryl James Bacol");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel3.setText("Reservation ID :");

        reservidlbl.setText("null");

        editbtn1.setText("Leave a Note");
        editbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editbtn1ActionPerformed(evt);
            }
        });

        readbtn.setText("Read the Note");
        readbtn.setEnabled(false);
        readbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readbtnActionPerformed(evt);
            }
        });

        durationlbl1.setText(" hours");

        javax.swing.GroupLayout reservinfoLayout = new javax.swing.GroupLayout(reservinfo);
        reservinfo.setLayout(reservinfoLayout);
        reservinfoLayout.setHorizontalGroup(
            reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reservinfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reservinfoLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reservidlbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(madelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(reservinfoLayout.createSequentialGroup()
                        .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(reservinfoLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(4, 4, 4)
                                .addComponent(startdatelbl))
                            .addGroup(reservinfoLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(durationlbl)
                                .addGap(0, 0, 0)
                                .addComponent(durationlbl1))
                            .addGroup(reservinfoLayout.createSequentialGroup()
                                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel19))
                                .addGap(4, 4, 4)
                                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(datelbl)
                                    .addComponent(enddatelbl))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(reservinfoLayout.createSequentialGroup()
                        .addComponent(readbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editbtn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        reservinfoLayout.setVerticalGroup(
            reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reservinfoLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(madelbl)
                    .addComponent(jLabel3)
                    .addComponent(reservidlbl))
                .addGap(12, 12, 12)
                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(startdatelbl))
                .addGap(12, 12, 12)
                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(enddatelbl))
                .addGap(12, 12, 12)
                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(durationlbl)
                    .addComponent(durationlbl1))
                .addGap(12, 12, 12)
                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(datelbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(reservinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editbtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(readbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabpane.addTab("Reserve Info", reservinfo);

        paymentinfo.setPreferredSize(new java.awt.Dimension(326, 398));

        jLabel12.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel12.setText("PAYMENT INFO");

        jLabel15.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel15.setText("Payment ID :");

        jLabel28.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel28.setText("Reservation Fee :");

        jLabel29.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel29.setText("Deposit Amount :");

        jLabel30.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel30.setText("Amount Paid :");

        jLabel31.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel31.setText("Outstanding Balance :");

        CardL.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        CardL.setLayout(new java.awt.CardLayout());

        partialpanel.setBackground(new java.awt.Color(255, 249, 171));

        jPanel5.setBackground(new java.awt.Color(230, 213, 0));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel34.setText("Client has made a partial payment");

        jLabel35.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel35.setText("Payment Status");

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download.png"))); // NOI18N

        javax.swing.GroupLayout partialpanelLayout = new javax.swing.GroupLayout(partialpanel);
        partialpanel.setLayout(partialpanelLayout);
        partialpanelLayout.setHorizontalGroup(
            partialpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(partialpanelLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(partialpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .addGroup(partialpanelLayout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        partialpanelLayout.setVerticalGroup(
            partialpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, partialpanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, partialpanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(partialpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(partialpanelLayout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addGap(6, 6, 6)))
                .addGap(27, 27, 27))
        );

        CardL.add(partialpanel, "card3");

        Paidpanel.setBackground(new java.awt.Color(181, 245, 194));

        jPanel13.setBackground(new java.awt.Color(36, 150, 59));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/verify.png"))); // NOI18N

        jLabel27.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel27.setText("Payment Status");

        jLabel32.setText("Client has settled the full balance!");

        jLabel40.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel40.setText("Date Fully Paid :");

        paydatelbl.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        paydatelbl.setText("null");

        javax.swing.GroupLayout PaidpanelLayout = new javax.swing.GroupLayout(Paidpanel);
        Paidpanel.setLayout(PaidpanelLayout);
        PaidpanelLayout.setHorizontalGroup(
            PaidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaidpanelLayout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PaidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PaidpanelLayout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paydatelbl)))
                .addGap(0, 37, Short.MAX_VALUE))
        );
        PaidpanelLayout.setVerticalGroup(
            PaidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PaidpanelLayout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(PaidpanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(PaidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PaidpanelLayout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)
                        .addGap(4, 4, 4)
                        .addGroup(PaidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel40)
                            .addComponent(paydatelbl)))
                    .addComponent(jLabel26))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CardL.add(Paidpanel, "card2");

        nopaymentpanel.setBackground(new java.awt.Color(255, 143, 143));

        jPanel14.setBackground(new java.awt.Color(179, 0, 0));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel37.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel37.setText("Payment Status");

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download (5).png"))); // NOI18N

        jLabel39.setText("Client has not made any payment.");

        javax.swing.GroupLayout nopaymentpanelLayout = new javax.swing.GroupLayout(nopaymentpanel);
        nopaymentpanel.setLayout(nopaymentpanelLayout);
        nopaymentpanelLayout.setHorizontalGroup(
            nopaymentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nopaymentpanelLayout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(nopaymentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 44, Short.MAX_VALUE))
        );
        nopaymentpanelLayout.setVerticalGroup(
            nopaymentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nopaymentpanelLayout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(nopaymentpanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(nopaymentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(nopaymentpanelLayout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CardL.add(nopaymentpanel, "card4");

        cancelpnl.setBackground(new java.awt.Color(255, 143, 143));

        jPanel4.setBackground(new java.awt.Color(179, 0, 0));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel41.setText("Client has cancelled the reservation.");

        jLabel42.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel42.setText("Payment Status");

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download (6).png"))); // NOI18N

        javax.swing.GroupLayout cancelpnlLayout = new javax.swing.GroupLayout(cancelpnl);
        cancelpnl.setLayout(cancelpnlLayout);
        cancelpnlLayout.setHorizontalGroup(
            cancelpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cancelpnlLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cancelpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        cancelpnlLayout.setVerticalGroup(
            cancelpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cancelpnlLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cancelpnlLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(cancelpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cancelpnlLayout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41))
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        CardL.add(cancelpnl, "card5");

        payidlbl.setText("null");

        feelbl.setText("null");

        depositlbl.setText("null");

        amtlbl.setText("null");

        otblbl.setText("null");

        paymentbtn.setText("Mark as Paid");
        paymentbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentbtnActionPerformed(evt);
            }
        });

        totallbl.setText("null");

        jLabel33.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel33.setText("Total Cost:");

        jLabel45.setText("₱ ");

        jLabel46.setText("₱ ");

        jLabel47.setText("₱ ");

        jLabel48.setText("₱ ");

        jLabel49.setText("₱ ");

        javax.swing.GroupLayout paymentinfoLayout = new javax.swing.GroupLayout(paymentinfo);
        paymentinfo.setLayout(paymentinfoLayout);
        paymentinfoLayout.setHorizontalGroup(
            paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paymentinfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(paymentbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CardL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymentinfoLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(feelbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymentinfoLayout.createSequentialGroup()
                        .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymentinfoLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(payidlbl))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymentinfoLayout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel45)
                                .addGap(0, 0, 0)
                                .addComponent(otblbl))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymentinfoLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(totallbl, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymentinfoLayout.createSequentialGroup()
                                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, paymentinfoLayout.createSequentialGroup()
                                        .addComponent(jLabel30)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel29))
                                .addGap(0, 0, 0)
                                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(paymentinfoLayout.createSequentialGroup()
                                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(depositlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(amtlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paymentinfoLayout.setVerticalGroup(
            paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymentinfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(9, 9, 9)
                .addComponent(CardL, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(payidlbl)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(feelbl)
                    .addComponent(jLabel49))
                .addGap(12, 12, 12)
                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(depositlbl)
                    .addComponent(jLabel48))
                .addGap(12, 12, 12)
                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(totallbl)
                    .addComponent(jLabel47))
                .addGap(12, 12, 12)
                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(amtlbl)
                    .addComponent(jLabel46))
                .addGap(12, 12, 12)
                .addGroup(paymentinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(otblbl)
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(paymentbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabpane.addTab("Payment Info", paymentinfo);

        vehicleinfo.setPreferredSize(new java.awt.Dimension(358, 398));

        jPanel6.setBackground(new java.awt.Color(161, 198, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel7.setBackground(new java.awt.Color(0, 71, 181));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Carimage.png"))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel10.setText("Vehicle Model ");

        modellbl.setText("null");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(modellbl))
                .addGap(0, 144, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modellbl)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel4.setText("Vehicle ID :");

        jLabel22.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel22.setText("Vehicle Type :");

        jLabel23.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel23.setText("Fuel Type :");

        jLabel24.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel24.setText("12-hour Rate:");

        jLabel25.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel25.setText("24-hour Rate:");

        jLabel9.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel9.setText("VEHICLE INFO");

        caridlbl.setText("null");

        cartypelbl.setText("null");

        fuellbl.setText("null");

        rate24lbl.setText("null");

        rate12lbl.setText("null");

        jLabel44.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel44.setText("Seats :");

        seatlbl.setText("null");

        returnbtn.setText("Mark as Return");
        returnbtn.setEnabled(false);
        returnbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vehicleinfoLayout = new javax.swing.GroupLayout(vehicleinfo);
        vehicleinfo.setLayout(vehicleinfoLayout);
        vehicleinfoLayout.setHorizontalGroup(
            vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vehicleinfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(vehicleinfoLayout.createSequentialGroup()
                        .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vehicleinfoLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fuellbl))
                            .addComponent(jLabel13)
                            .addComponent(jLabel9)
                            .addGroup(vehicleinfoLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(caridlbl))
                            .addGroup(vehicleinfoLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cartypelbl))
                            .addGroup(vehicleinfoLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rate12lbl))
                            .addGroup(vehicleinfoLayout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rate24lbl))
                            .addGroup(vehicleinfoLayout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(seatlbl)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(returnbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        vehicleinfoLayout.setVerticalGroup(
            vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vehicleinfoLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(caridlbl))
                .addGap(12, 12, 12)
                .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cartypelbl))
                .addGap(12, 12, 12)
                .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(fuellbl))
                .addGap(12, 12, 12)
                .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(seatlbl))
                .addGap(12, 12, 12)
                .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(rate12lbl))
                .addGap(12, 12, 12)
                .addGroup(vehicleinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(rate24lbl))
                .addGap(36, 36, 36)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(returnbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabpane.addTab("Vehicle Info", vehicleinfo);

        clientinfo.setPreferredSize(new java.awt.Dimension(358, 398));

        jPanel8.setBackground(new java.awt.Color(181, 245, 194));
        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel12.setBackground(new java.awt.Color(36, 150, 59));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UserIcon.png"))); // NOI18N

        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel17.setText("Client's Name ");

        namelbl.setText("null");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addGap(12, 12, 12)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(namelbl))
                .addGap(0, 147, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addContainerGap())
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(namelbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel18.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel18.setText("Client ID :");

        jLabel20.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel20.setText("Home Address :");

        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel11.setText("CLIENT INFO");

        idlbl.setText("null");

        numlbl.setText("null");

        adrslbl.setText("null");

        jLabel52.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel52.setText("Phone Number :");

        createdlbl.setText("null");

        jLabel58.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel58.setText("Account Created :");

        jButton4.setText("View Client Profile");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout clientinfoLayout = new javax.swing.GroupLayout(clientinfo);
        clientinfo.setLayout(clientinfoLayout);
        clientinfoLayout.setHorizontalGroup(
            clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientinfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, clientinfoLayout.createSequentialGroup()
                        .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(clientinfoLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idlbl))
                            .addGroup(clientinfoLayout.createSequentialGroup()
                                .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel52)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(adrslbl)
                                    .addComponent(numlbl)))
                            .addGroup(clientinfoLayout.createSequentialGroup()
                                .addComponent(jLabel58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(createdlbl)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        clientinfoLayout.setVerticalGroup(
            clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientinfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idlbl)
                    .addComponent(jLabel18))
                .addGap(12, 12, 12)
                .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(numlbl))
                .addGap(12, 12, 12)
                .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(adrslbl))
                .addGap(12, 12, 12)
                .addGroup(clientinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(createdlbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabpane.addTab("Client Info", clientinfo);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabpane)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabpane, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editbtnActionPerformed
            if (AdminID == AdminIdFetch || "S_Admin".equalsIgnoreCase(role)) {
                   Reserve dialog = new Reserve(this, true,reserveId,userID);
                    dialog.setVisible(true);                  
                }
                else {
                JOptionPane.showMessageDialog(
                    null, 
                    "Only Head Admin or Admin who made this can change this Reservation.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE
                );  
            }
        
        
            // TODO add your handling code here:
    }//GEN-LAST:event_editbtnActionPerformed

    private void cancelbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelbtnActionPerformed
            if (AdminID == AdminIdFetch || "S_Admin".equalsIgnoreCase(role)) {
                  reservecancel(reserveId);           
                }
                else {
                JOptionPane.showMessageDialog(
                    null, 
                    "Only Head Admin or Admin who made this can cancel this Reservation.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE
                );  
            }
    }//GEN-LAST:event_cancelbtnActionPerformed

    private void paymentbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentbtnActionPerformed
        if (AdminID == AdminIdFetch || "S_Admin".equalsIgnoreCase(role)) {
                 int option = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to mark " + username + "'s Reservation? as Fully Paid?", // message
                    "Mark as Paid", // title
                    JOptionPane.YES_NO_OPTION 
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        fullpaid();
                    }
                }
                else {
                JOptionPane.showMessageDialog(
                    null, 
                    "Only Head Admin or Admin who made this can mark this Reservation as paid.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE
                );  
            }
        
        
        
        


// TODO add your handling code here:
    }//GEN-LAST:event_paymentbtnActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        UserData dialog = new UserData(this,true,user);    
        dialog.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void returnbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnbtnActionPerformed
        
    int confirm = JOptionPane.showConfirmDialog(null,
        "Mark this vehicle as returned?",
        "Confirm Return",
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        markreturn();
        }    // TODO add your handling code here:
    }//GEN-LAST:event_returnbtnActionPerformed

    private void editbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editbtn1ActionPerformed
     notearea.setEnabled(true);    
     
    if("Cancelled".equalsIgnoreCase(status)){
    JOptionPane.showMessageDialog(
    null,
    "You can't add a note to a cancelled reservation",
    "Message",
    JOptionPane.INFORMATION_MESSAGE
);
    } 
    else if ("Pending".equalsIgnoreCase(status)) {
     JOptionPane.showMessageDialog(
    null,
    "You can't add a note to a pending reservation",
    "Message",
    JOptionPane.INFORMATION_MESSAGE
);  
        
    }  
    
    else if ("Completed".equalsIgnoreCase(status)) {
     JOptionPane.showMessageDialog(
    null,
    "You can't add a note to a finished reservation",
    "Message",
    JOptionPane.INFORMATION_MESSAGE
);  
        
    } 
    else if (AdminID == AdminIdFetch || "S_Admin".equalsIgnoreCase(role)) {
   
    int option = JOptionPane.showConfirmDialog(
    null,
    notepad,
    "Edit Note",
    JOptionPane.OK_CANCEL_OPTION,
    JOptionPane.PLAIN_MESSAGE
);

if (option == JOptionPane.OK_OPTION) {
    String notes = notearea.getText();
    if (notes == null || notes.trim().isEmpty()) {
    JOptionPane.showMessageDialog(
        null,
        "No changes were made, no note was added.",
        "Message",
        JOptionPane.INFORMATION_MESSAGE
    );
   
    }
    else {
    noteedit(notes);
    }
    notearea.setText(note);
}  else {
    JOptionPane.showMessageDialog(
        null,
        "No note was added",
        "Message",
        JOptionPane.INFORMATION_MESSAGE
    );
     notearea.setText(note);
}}  
    else {
    JOptionPane.showMessageDialog(
        null,
        "Only the Head Admin or an Admin who made this Reservation can leave a note.",
        "Message",
        JOptionPane.INFORMATION_MESSAGE
    );}
    }//GEN-LAST:event_editbtn1ActionPerformed

    private void readbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readbtnActionPerformed
    notearea.setEnabled(false);
    notearea.setText(note);
JOptionPane.showMessageDialog(
   
    null,
    notepad,
    "View Note",
    JOptionPane.PLAIN_MESSAGE
);        // TODO add your handling code here:
    }//GEN-LAST:event_readbtnActionPerformed

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

        /* Create and display the dialog */
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CardL;
    private javax.swing.JPanel Paidpanel;
    private javax.swing.JLabel adrslbl;
    private javax.swing.JLabel amtlbl;
    private javax.swing.JButton cancelbtn;
    private javax.swing.JPanel cancelpnl;
    private javax.swing.JLabel caridlbl;
    private javax.swing.JLabel cartypelbl;
    private javax.swing.JPanel clientinfo;
    private javax.swing.JLabel createdlbl;
    private javax.swing.JLabel datelbl;
    private javax.swing.JLabel depositlbl;
    private javax.swing.JLabel durationlbl;
    private javax.swing.JLabel durationlbl1;
    private javax.swing.JButton editbtn;
    private javax.swing.JButton editbtn1;
    private javax.swing.JLabel enddatelbl;
    private javax.swing.JLabel feelbl;
    private javax.swing.JLabel fuellbl;
    private javax.swing.JLabel idlbl;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel madelbl;
    private javax.swing.JLabel modellbl;
    private javax.swing.JLabel namelbl;
    private javax.swing.JPanel nopaymentpanel;
    private javax.swing.JTextArea notearea;
    private javax.swing.JScrollPane notepad;
    private javax.swing.JLabel numlbl;
    private javax.swing.JLabel otblbl;
    private javax.swing.JPanel partialpanel;
    private javax.swing.JLabel paydatelbl;
    private javax.swing.JLabel payidlbl;
    private javax.swing.JButton paymentbtn;
    private javax.swing.JPanel paymentinfo;
    private javax.swing.JLabel rate12lbl;
    private javax.swing.JLabel rate24lbl;
    private javax.swing.JButton readbtn;
    private javax.swing.JLabel reservidlbl;
    private javax.swing.JPanel reservinfo;
    private javax.swing.JLabel reservstlbl;
    private javax.swing.JButton returnbtn;
    private javax.swing.JLabel seatlbl;
    private javax.swing.JLabel startdatelbl;
    private javax.swing.JTabbedPane tabpane;
    private javax.swing.JLabel totallbl;
    private javax.swing.JPanel vehicleinfo;
    // End of variables declaration//GEN-END:variables
}
