/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.carrentalsystemalpha;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Daryl James
 */
public class UserData extends javax.swing.JDialog {

    /**
     * Creates new form UserData
     */
    
    public UserData(java.awt.Frame parent, boolean modal, String user_Id) {
        super(parent, modal);
        initComponents();
        this.userid = user_Id;
        setTitle("Car Rental System - Client Profile");
        setLocationRelativeTo(null);
        getData(userid);
    }
    
    public UserData(JDialog parent, boolean modal, String user_Id) {
        super(parent, modal);
        initComponents();
        this.userid = user_Id;
        setTitle("Car Rental System - Client Profile");
        setLocationRelativeTo(null);
        getData(userid);
        
        
    }


    private final String userid;
    private String id_path;
    private JFrame Frame;
    private boolean reserv = false;
    
    private void getData(String userID) {
    CardLayout cl = (CardLayout) Parent.getLayout();
            Parent.add(Nullrsrv, "noreserv");
            Parent.add(Nullpyment, "nopay");
            Parent.add(BanPanel, "banned");
        
     Connection conn = DatabaseConnection.connect();
     String sql = "SELECT u.user_id, u.full_name, u.address, u.phone_num, u.verified, " +
             "d.date_created, d.id_photo, d.verified_requested, d.is_banned, d.ban_reason, d.ban_date, d.made_by " +
             "FROM user_table u " +
             "JOIN user_data d ON u.user_id = d.user_id " +
             "WHERE u.user_id = ?";
    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, userID);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String name = rs.getString("full_name");
                namelbl.setText(name);
                String firstName = name.trim().split("\\s+")[0];
                firstname.setText(firstName);
                addresslbl.setText(rs.getString("address"));
                numlbl.setText(rs.getString("Phone_num"));
                int userId = rs.getInt("user_id");
                String madeby = rs.getString("made_by");
                if (madeby == null) {adminlbl.setText("No Information");}
                else {adminlbl.setText(madeby);}
                
                
                Boolean verified = rs.getBoolean("verified");
                Boolean requested = rs.getBoolean("verified_requested");
                Boolean banned = rs.getBoolean("is_banned");
                
                if(verified) {
                    verifiedlbl.setText("Verified");
                }
                else if (requested) {
                    verifiedlbl.setText("Pending");
                }
                else {
                    verifiedlbl.setText("Not Verified");
                }
                datelbl.setText(rs.getString("date_created"));
                id_path = rs.getString("id_photo");
                String ban_reason = rs.getString("ban_reason");
                String ban_date = rs.getString("ban_date");

                 if (id_path != null && !id_path.isEmpty()) {
                    imgbtn.setEnabled(true);   
                } else {
                      imgbtn.setEnabled(false);      
                }
                
                 
                if(banned) {
                    cl.show(Parent, "banned"); 
                    timebanlbl.setText(ban_date);
                    banreasonlbl.setText(ban_reason);
                    paybtn.setEnabled(false);
                    rsrvbtn.setEnabled(false);
                    
                
                }
                else {
                    cl.show(Parent, "noreserv"); 
                     reservedata(userId);
                     paydata(userId);
                }
               
                 
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }  
    }
    
    private void reservedata(int userId) {
           CardLayout cl = (CardLayout) Parent.getLayout();
            Parent.add(Nullrsrv, "noreserv");
            Parent.add(Nullpyment, "nopay");
            Parent.add(Rsrvationtbl, "reserv");
       try (Connection conn = DatabaseConnection.connect()) {

         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Reservation ID", "Vehicle Model", "Start Date", "End Date", "Status"});

        String sql = "SELECT r.reservation_id, v.vehicle_model AS car_model, " +
        "r.start_date, r.end_date, r.status FROM " +
        "reservations_table r JOIN car_table v " +
        "ON r.car_id = v.vehicle_id WHERE r.user_id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1,userId);
        ResultSet rs = stmt.executeQuery();

        Boolean next = false;
    while (rs.next()) {     
        next = true;
        model.addRow(new Object[]{
            rs.getString("reservation_id"),
            rs.getString("car_model"),
            rs.getString("start_date"),
            rs.getString("end_date"),
            rs.getString("status")
        });
    }
        if (next) {
        reservtbl.setModel(model);
        reservtbl.setDefaultEditor(Object.class, null);
        reserv = true;
         applyStatusColor(reservtbl,4);
         cl.show(Parent, "reserv");
        } else {
            cl.show(Parent, "noreserv");
        }


            reservtbl.setModel(model);
            reservtbl.setDefaultEditor(Object.class, null);
         
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
      private static void applyStatusColor(JTable table, int statusColIndex) {
    TableColumn statusColumn = table.getColumnModel().getColumn(statusColIndex);

    statusColumn.setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString().toLowerCase();

                switch (status) {
                    case "confirmed":
                        c.setForeground(new Color(36,150,59));
                        break;
                    case "completed":
                        c.setForeground(new Color(61,57,255));
                        break;
                    case "pending":
                        c.setForeground(new Color(194, 139, 0));
                        break;
                    case "partial":
                        c.setForeground(new Color(194, 139, 0));
                        break;
                    case "cancelled":
                        c.setForeground(new Color(255, 38, 38));
                        break;
                    case "ongoing":
                        c.setForeground(new Color(36,150,59));
                        break;  
                    case "paid":
                        c.setForeground(new Color(36,150,59));
                        break; 
                    default:
                        c.setForeground(Color.BLACK);
                        break;
                }
            }

            return c;
        }
    });
}
      
    private void paydata(int userId) {
        
       try (Connection conn = DatabaseConnection.connect()) {

         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Payment ID", "Reservation ID", "Total Cost", "Amount Paid", "Status"});

        String sql = "SELECT p.payment_id, p.total_cost, p.amount_paid, p.status, " +
            "r.reservation_id, r.user_id FROM payment_tbl p JOIN " +
            "reservations_table r ON p.reservation_id = r.reservation_id " +
            "WHERE r.user_id = ?;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

  
    while (rs.next()) {
        model.addRow(new Object[]{
            rs.getString("payment_id"),
            rs.getString("reservation_id"),
            rs.getString("total_cost"),
            rs.getString("amount_paid"),
            rs.getString("status")
        });
    }

            paytbl.setModel(model);
            paytbl.setDefaultEditor(Object.class, null);
            applyStatusColor(paytbl,4);
      
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        imgbtn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        rsrvbtn = new javax.swing.JButton();
        paybtn = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        Parent = new javax.swing.JPanel();
        Nullrsrv = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        paymenttbl = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        paytbl = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        Rsrvationtbl = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        reservtbl = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        Nullpyment = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        BanPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        banreasonlbl = new javax.swing.JTextArea();
        timebanlbl = new javax.swing.JLabel();
        namelbl = new javax.swing.JLabel();
        addresslbl = new javax.swing.JLabel();
        numlbl = new javax.swing.JLabel();
        verifiedlbl = new javax.swing.JLabel();
        datelbl = new javax.swing.JLabel();
        firstname = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        adminlbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel1.setText("'s Profile");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel2.setText("Username :");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel3.setText("Home Address:");

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel4.setText("Phone Number :");

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel5.setText("Verified :");

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel6.setText("ID Photo :");

        imgbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        imgbtn.setText("View ID");
        imgbtn.setEnabled(false);
        imgbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imgbtnActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel7.setText("Created by :");

        rsrvbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        rsrvbtn.setText("Reservation History");
        rsrvbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rsrvbtnActionPerformed(evt);
            }
        });

        paybtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        paybtn.setText("Payment History");
        paybtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paybtnActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton4.setText("OK");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        Parent.setLayout(new java.awt.CardLayout());

        Nullrsrv.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setText("This user hasn't made any reservations yet.");

        javax.swing.GroupLayout NullrsrvLayout = new javax.swing.GroupLayout(Nullrsrv);
        Nullrsrv.setLayout(NullrsrvLayout);
        NullrsrvLayout.setHorizontalGroup(
            NullrsrvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NullrsrvLayout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jLabel11)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        NullrsrvLayout.setVerticalGroup(
            NullrsrvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NullrsrvLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel11)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        Parent.add(Nullrsrv, "card3");

        jScrollPane3.setBorder(null);

        paytbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        paytbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Payment ID", "Payment Date", "Duration", "Status"
            }
        ));
        paytbl.setRowHeight(26);
        paytbl.setShowHorizontalLines(false);
        paytbl.setShowVerticalLines(false);
        paytbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(paytbl);

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel14.setText("Payment History");

        javax.swing.GroupLayout paymenttblLayout = new javax.swing.GroupLayout(paymenttbl);
        paymenttbl.setLayout(paymenttblLayout);
        paymenttblLayout.setHorizontalGroup(
            paymenttblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
            .addGroup(paymenttblLayout.createSequentialGroup()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        paymenttblLayout.setVerticalGroup(
            paymenttblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paymenttblLayout.createSequentialGroup()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Parent.add(paymenttbl, "card6");

        jScrollPane2.setBorder(null);

        reservtbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        reservtbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Reservation ID", "Car Model", "Start Date", "End Date", "Status"
            }
        ));
        reservtbl.setRowHeight(26);
        reservtbl.setShowHorizontalLines(false);
        reservtbl.setShowVerticalLines(false);
        reservtbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(reservtbl);

        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel13.setText("Reservation History");

        javax.swing.GroupLayout RsrvationtblLayout = new javax.swing.GroupLayout(Rsrvationtbl);
        Rsrvationtbl.setLayout(RsrvationtblLayout);
        RsrvationtblLayout.setHorizontalGroup(
            RsrvationtblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RsrvationtblLayout.createSequentialGroup()
                .addGroup(RsrvationtblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(RsrvationtblLayout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 267, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        RsrvationtblLayout.setVerticalGroup(
            RsrvationtblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RsrvationtblLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Parent.add(Rsrvationtbl, "card5");

        Nullpyment.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setText("This user hasn't made any payments yet.");

        javax.swing.GroupLayout NullpymentLayout = new javax.swing.GroupLayout(Nullpyment);
        Nullpyment.setLayout(NullpymentLayout);
        NullpymentLayout.setHorizontalGroup(
            NullpymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NullpymentLayout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jLabel15)
                .addContainerGap(102, Short.MAX_VALUE))
        );
        NullpymentLayout.setVerticalGroup(
            NullpymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NullpymentLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel15)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        Parent.add(Nullpyment, "card4");

        BanPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel8.setText("This user has been banned at");

        jLabel9.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel9.setText("Ban Reason : ");

        jScrollPane1.setBorder(null);

        banreasonlbl.setEditable(false);
        banreasonlbl.setBackground(new java.awt.Color(240, 240, 240));
        banreasonlbl.setColumns(20);
        banreasonlbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        banreasonlbl.setLineWrap(true);
        banreasonlbl.setWrapStyleWord(true);
        banreasonlbl.setBorder(null);
        jScrollPane1.setViewportView(banreasonlbl);

        timebanlbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        timebanlbl.setText("Time");

        javax.swing.GroupLayout BanPanelLayout = new javax.swing.GroupLayout(BanPanel);
        BanPanel.setLayout(BanPanelLayout);
        BanPanelLayout.setHorizontalGroup(
            BanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BanPanelLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(3, 3, 3)
                        .addComponent(timebanlbl)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(BanPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)))
                .addContainerGap())
        );
        BanPanelLayout.setVerticalGroup(
            BanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(timebanlbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(BanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        Parent.add(BanPanel, "card2");

        namelbl.setText("Placeholder");

        addresslbl.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        addresslbl.setText("Placeholder");

        numlbl.setText("Placeholder");

        verifiedlbl.setText("Placeholder");

        datelbl.setText("Placeholder");

        firstname.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        firstname.setText("Placeholder");

        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel10.setText("Date Created :");

        adminlbl.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        adminlbl.setText("Placeholder");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addComponent(firstname)
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Parent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(imgbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(125, 125, 125))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(namelbl))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(verifiedlbl))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(addresslbl))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(numlbl)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(adminlbl)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(datelbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel10)
                                            .addGap(68, 68, 68))))
                                .addGap(34, 34, 34))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rsrvbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(paybtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(firstname))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(namelbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(addresslbl))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(numlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(verifiedlbl)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(datelbl)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(13, 13, 13)
                        .addComponent(adminlbl)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(imgbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Parent, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rsrvbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(paybtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rsrvbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rsrvbtnActionPerformed
        if (reserv) {
        Parent.removeAll();
        Parent.add(Rsrvationtbl);       
        Parent.revalidate();
        Parent.repaint();
        }
        else {
        Parent.removeAll();
        Parent.add(Nullrsrv);       
        Parent.revalidate();
        Parent.repaint();
        }
        
      // TODO add your handling code here:
    }//GEN-LAST:event_rsrvbtnActionPerformed

    private void paybtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paybtnActionPerformed
        if (reserv) {
        Parent.removeAll();
        Parent.add(paymenttbl);       
        Parent.revalidate();
        Parent.repaint();
        }
        else {
               Parent.removeAll();
               Parent.add(Nullpyment);       
               Parent.revalidate();
               Parent.repaint();
               }

// TODO add your handling code here:
    }//GEN-LAST:event_paybtnActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void imgbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imgbtnActionPerformed
            File imgFile = new File(id_path);
    ImageIcon icon = new ImageIcon(
        new ImageIcon(imgFile.getAbsolutePath())
            .getImage()
            .getScaledInstance(400, 300, Image.SCALE_SMOOTH)
    );

    JLabel imgLabel = new JLabel(icon);
    JOptionPane.showMessageDialog(Frame, imgLabel, 
            "Preview Image", JOptionPane.PLAIN_MESSAGE);        // TODO add your handling code here:
    }//GEN-LAST:event_imgbtnActionPerformed

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
    private javax.swing.JPanel BanPanel;
    private javax.swing.JPanel Nullpyment;
    private javax.swing.JPanel Nullrsrv;
    private javax.swing.JPanel Parent;
    private javax.swing.JPanel Rsrvationtbl;
    private javax.swing.JLabel addresslbl;
    private javax.swing.JLabel adminlbl;
    private javax.swing.JTextArea banreasonlbl;
    private javax.swing.JLabel datelbl;
    private javax.swing.JLabel firstname;
    private javax.swing.JButton imgbtn;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel namelbl;
    private javax.swing.JLabel numlbl;
    private javax.swing.JButton paybtn;
    private javax.swing.JPanel paymenttbl;
    private javax.swing.JTable paytbl;
    private javax.swing.JTable reservtbl;
    private javax.swing.JButton rsrvbtn;
    private javax.swing.JLabel timebanlbl;
    private javax.swing.JLabel verifiedlbl;
    // End of variables declaration//GEN-END:variables
}
