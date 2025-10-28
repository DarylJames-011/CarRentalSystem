package com.mycompany.carrentalsystemalpha;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.*;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 * 
 * @author Daryl James
 * Below is an example of inheritance where AdminA is a child 
 * extend from (or parent to) JFrame
 */
public class AdminA extends javax.swing.JFrame { //Class
    // Encapsulation  (using private property so it'll not be accessed outside from class
    private final int userId;  //Admin Reference
    private int madeId;
    private String role;  // Property
    private int carid;
    private int reservID;
    private int payID;
    private int balance;
    private int paid;
    private int AdminIDfetch;
     private JFrame frame;
     private String carimgpath;
     private String ClientID;
     private int ClientID1;
     private String Name;
    /**
     * Creates new form AdminA
     * @param userId
     */
   
    public AdminA(int userId) {
        this.userId = userId;   
        initComponents();
        setLocationRelativeTo(null);
        database();
         if ("S_Admin".equalsIgnoreCase(role)) {
        setTitle("Car Rental System - Owner | " + Name);
        }
        else {
        setTitle("Car Rental System - Administrator | " + Name);
        }
        usertbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        VehicleTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ReservTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recentTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paytbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carselector();       
        clientselect();  
        reserveselector();
        recentreservselector();
        payselector();
        recentreserve();  
    }
      
    private int getSelectedReservationId() {
    
    int selectedRow = ReservTbl.getSelectedRow();
    if (selectedRow == -1) {
        return -1; 
    }
    return (int) ReservTbl.getModel().getValueAt(selectedRow, 0);
    }
    

private void recentreservselector() {
    recentTbl.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) { // double-click
            int selectedRow = recentTbl.getSelectedRow();
            if (selectedRow != -1) {
   
                int reservationId = (int) recentTbl.getValueAt(selectedRow, 0); 


                ViewReserve dialog = new ViewReserve(
                    (java.awt.Frame) SwingUtilities.getWindowAncestor(recentTbl),
                    true,
                    reservationId,
                    userId,
                    false
                );
                dialog.setVisible(true);
            }
        }
    }
});
}
    
    
    
    private void reserveselector() {
         ReservTbl.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int reservationId = getSelectedReservationId();
            if (reservationId != -1) {
                reservestatus(reservationId);
                viewrsrvbtn.setEnabled(true);
                dslctbtn.setEnabled(true);    
            }
        }
    });
    }
    
    
     private void reservestatus(int ID) {
         String sql = "SELECT status , made_by FROM reservations_table WHERE reservation_id = ?";
         Connection conn = DatabaseConnection.connect();
         try(PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setInt(1, ID);
             ResultSet rs = ps.executeQuery();
             
             if (rs.next()) {
                 String status = rs.getString("status");
                 madeId = rs.getInt("made_by");
                 if("Confirmed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status) || "Ongoing".equalsIgnoreCase(status)||
                     "Completed".equalsIgnoreCase(status)) {
                     cancelbtn1.setEnabled(false);
                     editbtn.setEnabled(false);  
                 }
                 else {
                     cancelbtn1.setEnabled(true);
                     editbtn.setEnabled(true);  
                 }
             }
             
         }
         catch (SQLException e) {
             e.printStackTrace();
         }
     }
    
     
     private void reservecancel(int ID) {
         ReservTbl.clearSelection();
         viewrsrvbtn.setEnabled(false);
         dslctbtn.setEnabled(false);
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
        } else {
            JOptionPane.showMessageDialog(null, "Failed to cancel reservation.");
        }
    }
}}}
} catch (SQLException e) {
    e.printStackTrace();
}}
    
  


   
     private void carrefresh() {
      try (Connection conn = DatabaseConnection.connect()) {

         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Vehicle ID", "Vehicle Brand", "Vehicle Model", "Plate No", "Status"});

        String sql = "SELECT vehicle_id, vehicle_brand, vehicle_model, plate_num, status FROM car_table";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("vehicle_id"),
                rs.getString("vehicle_brand"),
                rs.getString("vehicle_model"),
                rs.getString("plate_num"),
                rs.getString("status")
            });
        }

        VehicleTbl.setModel(model); 
        VehicleTbl.setDefaultEditor(Object.class, null);
        applyStatusColor(VehicleTbl,4);
             
        } catch (Exception e) {
            e.printStackTrace();
        }
     
     }
     
  private void updateCarStatusInDatabase(int carId, String newStatus) {
    String sql = "UPDATE car_table SET status = ? WHERE vehicle_id = ?";
    try (Connection conn = DatabaseConnection.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, newStatus);
        pst.setInt(2, carId);
        pst.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to update status.");
    }
}   
     
     
  private void deleteCar(int carId) {
          Connection conn = DatabaseConnection.connect();
          String sql = "DELETE FROM car_table WHERE vehicle_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, carId);  // carId is the ID of the car to delete
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Yes");
                Success dialog = new Success(this,true,"Car deleted successfully.");
                dialog.setVisible(true);
                disablebtn();
                
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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
                        c.setForeground(new Color(61, 77, 255));
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
                    case "rented":
                        c.setForeground(new Color(61, 77, 255));
                        break;  
                    case "verified":
                        c.setForeground(new Color(36,150,59));
                        break;
                    case "paid":
                        c.setForeground(new Color(36,150,59));
                        break; 
                    case "not verified":
                        c.setForeground(new Color(255, 38, 38));
                        break;
                     case "available":
                        c.setForeground(new Color(36,150,59));
                        break;    
                    case "maintenance":
                        c.setForeground(new Color(255, 38, 38));
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

  
  private void disablebtn() {
      setStatusbtn.setEnabled(false);
      editvhclbtn.setEnabled(false);
      delbtn.setEnabled(false);    
      viewimgbtn.setEnabled(false);
      deselectbtn.setEnabled(false);
      CardLayout cl = (CardLayout) cardL.getLayout();
      cl.show(cardL, "TIP");  
  }
  
   private void carselector() {
       VehicleTbl.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting() && VehicleTbl.getSelectedRow() != -1) {
        int selectedRow = VehicleTbl.getSelectedRow();
        
        CardLayout cl = (CardLayout) cardL.getLayout();
        cardL.add(TipPanel, "TIP");        // default tip
        cardL.add(cardetails, "DETAILS");
        cl.show(cardL, "DETAILS"); 
           
        String carId = VehicleTbl.getValueAt(selectedRow, 0).toString(); 
        loadCarDetailsFromDatabase(carId); 
        deselectbtn.setEnabled(true);
    }
    });
   }    
   
      private void payselector() {
       
       paytbl.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting() && paytbl.getSelectedRow() != -1) {
        int selectedRow = paytbl.getSelectedRow();
        
           
   int payid = Integer.parseInt(paytbl.getValueAt(selectedRow, 0).toString());
   int reservid = Integer.parseInt(paytbl.getValueAt(selectedRow, 1).toString());
        reservID = reservid;
        deselectbtn2.setEnabled(true);
        payID = payid;
        paydata(payid);
    }
    });
   }    
      
   private void paydata(int payid) {
    Connection conn = DatabaseConnection.connect();
String sql = """
SELECT 
    r.reservation_id,
    p.status,
    r.made_by,
    p.total_cost,
    p.amount_paid
FROM 
    payment_tbl p
JOIN 
    reservations_table r ON p.reservation_id = r.reservation_id
WHERE 
    p.payment_id = ?;
""";

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setInt(1, payid);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                String status = rs.getString("status");
                int total = rs.getInt("total_cost");
                    paid = rs.getInt("amount_paid");
                    balance = total - paid;
                    AdminIDfetch = rs.getInt("made_by");
                if("Paid".equalsIgnoreCase(status)) {
                    receiptbtn.setEnabled(true);
                    paidbtn.setEnabled(false);
                }
                else if ("Cancelled".equalsIgnoreCase(status)) {
                    receiptbtn.setEnabled(false);
                    paidbtn.setEnabled(false);
                }
                 else {
                    receiptbtn.setEnabled(false);
                    paidbtn.setEnabled(true);
                }
                 paydbtn.setEnabled(true);

            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
   
   
   }
      
    
   public void loadCarDetailsFromDatabase(String carId) {
    Connection conn = DatabaseConnection.connect();
    String sql = "SELECT * FROM car_table WHERE vehicle_id = ?";
    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, carId);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                idlabel.setText(rs.getString("vehicle_id"));
                cartypelbl.setText(rs.getString("vehicle_type"));
                modellbl.setText(rs.getString("vehicle_model"));
                carbndlbl.setText(rs.getString("vehicle_brand"));
                platenolbl.setText(rs.getString("plate_num"));
                fueltyplbl.setText(rs.getString("fuel_type"));
                translbl.setText(rs.getString("transmission"));
                seatlbl.setText(rs.getString("seats"));
                rate12lbl.setText(rs.getString("rate_12"));
                rate24lbl.setText(rs.getString("rate_24"));
                String status = rs.getString("status");
                statuslbl.setText(status);
                
                if("Rented".equalsIgnoreCase(status)) {
                    setStatusbtn.setEnabled(false);
                    delbtn.setEnabled(false);
                    editvhclbtn.setEnabled(false);
                }
                else {
                    setStatusbtn.setEnabled(true);
                    delbtn.setEnabled(true);
                    editvhclbtn.setEnabled(true);
                }
                
                if (!"S_Admin".equalsIgnoreCase(role)) {
                    delbtn.setEnabled(false);
                    editvhclbtn.setEnabled(false);
                }
                
                
                desclbl.setText(rs.getString("description"));
                carimgpath = rs.getString("carimg_path");
                int num = Integer.parseInt(carId);
                carid = num;
                
                 if (carimgpath != null && !carimgpath.isEmpty()) {
                    viewimgbtn.setEnabled(true);   
                } else {
                    viewimgbtn.setEnabled(false);
                }
                
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}
     

   private void database() {  
   try (Connection conn = DatabaseConnection.connect()) {
        String sql1 = "SELECT COUNT(*) AS total_users FROM user_table WHERE role = 'Client'";
        PreparedStatement pst = conn.prepareStatement(sql1);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            int totalUsers = rs.getInt("total_users");
            TotalUsr.setText(String.valueOf(totalUsers)); 
            TotalUsr1.setText(String.valueOf(totalUsers)); 
        }
         String sql = "SELECT COUNT(*) AS total_cars FROM car_table";
        PreparedStatement pst2 = conn.prepareStatement(sql);
        ResultSet rs2 = pst2.executeQuery();
        if (rs2.next()) {
            int totalcars = rs2.getInt("total_cars");
            carlbl1.setText(String.valueOf(totalcars)); 
        }
        
        
        rs.close(); 
        pst.close();

        String sql2 = "SELECT * FROM user_table WHERE user_id = ?";
        PreparedStatement pst1 = conn.prepareStatement(sql2);
        pst1.setInt(1, userId); 
        ResultSet rs1 = pst1.executeQuery();
        if (rs1.next()) {
            role = rs1.getString("role");
            Name = rs1.getString("full_name");
            if (!"s_admin".equalsIgnoreCase(role)) {
                btnAddA.setEnabled(false);
                addbtn.setEnabled(false);
            }
        }
        rs1.close(); 
        pst1.close();

            String sql3 = "SELECT COUNT(*) AS available_count FROM car_table WHERE status = ?";
            PreparedStatement pst3 = conn.prepareStatement(sql3);
            pst3.setString(1, "Available");
            ResultSet rs3 = pst3.executeQuery();

     if (rs3.next()) {
         int availableCars = rs3.getInt("available_count");
         availlbl.setText(String.valueOf(availableCars));
     }
        rs3.close();
        pst3.close();
        
        String sql4 = "SELECT COUNT(*) AS rent_count FROM car_table WHERE status = ?";
            PreparedStatement pst4 = conn.prepareStatement(sql4);
            pst4.setString(1, "Rented");
            ResultSet rs4 = pst4.executeQuery();

     if (rs4.next()) {
         int rentCars = rs4.getInt("rent_count");
         rentlbl.setText(String.valueOf(rentCars));
     }
        rs4.close();
        pst4.close();
        
        
        String sql5 = "SELECT COUNT(*) AS ban_count FROM user_data WHERE is_banned = TRUE";
            PreparedStatement pst5 = conn.prepareStatement(sql5);
            ResultSet rs5 = pst5.executeQuery();
        if (rs5.next()) {
            int totalban = rs5  .getInt("ban_count");
            banlbl.setText(String.valueOf(totalban)); 
        }

        rs5.close();
        pst5.close();
        
         String sql6 = "SELECT COUNT(*) AS verify_count FROM user_table WHERE verified = TRUE AND role = 'Client'";
            PreparedStatement pst6 = conn.prepareStatement(sql6);
            ResultSet rs6 = pst6.executeQuery();
        if (rs6.next()) {
            int totalverified = rs6.getInt("verify_count");
            verifylbl.setText(String.valueOf(totalverified)); 
        }

        rs6.close();
        pst6.close();
        
         String sql7 = "SELECT COUNT(*) AS ongoing_count FROM reservations_table WHERE status = 'Ongoing'";
            PreparedStatement pst7 = conn.prepareStatement(sql7);
            ResultSet rs7 = pst7.executeQuery();
        if (rs7.next()) {
            int totalverified = rs7.getInt("ongoing_count");
            ongoinglbl.setText(String.valueOf(totalverified)); 
        }

        rs7.close();
        pst7.close();
        
          String sql8 = "SELECT COUNT(*) AS pending_count FROM reservations_table WHERE status = 'Pending'";
            PreparedStatement pst8 = conn.prepareStatement(sql8);
            ResultSet rs8 = pst8.executeQuery();
        if (rs8.next()) {
            int totalverified = rs8.getInt("pending_count");
            pendinglbl.setText(String.valueOf(totalverified)); 
        }

        rs8.close();
        pst8.close();
        
        
        String sql9 = "SELECT SUM(amount_paid) AS total FROM payment_tbl";
        PreparedStatement pst9 = conn.prepareStatement(sql9);
        ResultSet rs9 = pst9.executeQuery();
        if (rs9.next()) {
            int totalverified = rs9.getInt("total");
            DecimalFormat df = new DecimalFormat("#,###.00"); 
            totalvalue.setText(String.valueOf(df.format(totalverified))); 
        }
        
        rs9.close();
        pst9.close();
        
        String sql10 = "SELECT SUM(total_cost) - SUM(amount_paid) AS balance FROM payment_tbl";
        PreparedStatement pst10 = conn.prepareStatement(sql10);
        ResultSet rs10 = pst10.executeQuery();
        if (rs10.next()) {
            int balance = rs10.getInt("balance");
            DecimalFormat df = new DecimalFormat("#,###.00");  
            if (balance == 0) {
            balancelbl.setText("00.00"); 
            balancelbl.setForeground(new Color(0,153,0));
            rslvbtn.setEnabled(false);
            }
            else {
            balancelbl.setText(String.valueOf(df.format(balance))); 
             balancelbl.setForeground(new Color(204,0,0));
            rslvbtn.setEnabled(true);
            }
        }
        
        rs9.close();
        pst9.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
   }
     
    private javax.swing.JButton activeButton = null;
    
    
    private void setActiveButton(javax.swing.JButton clickedButton) {
    javax.swing.border.Border raised = 
        javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED);
    javax.swing.border.Border lowered = 
        javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED);

    // Reset the previously active button
    if (activeButton != null) {
        activeButton.setBorder(raised);
    }

    // Set the new active button
    clickedButton.setBorder(lowered);
    activeButton = clickedButton;
}
    
    private void clientselect(){
    usertbl.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting() && usertbl.getSelectedRow() != -1) {
        int selectedRow = usertbl.getSelectedRow(); 
        String clientid = usertbl.getValueAt(selectedRow, 0).toString(); 
        ClientID = clientid;
        int num = Integer.parseInt(clientid);
        ClientID1 = num;
        deselectbtn1.setEnabled(true);
        clientbtn.setEnabled(true);
        editbtn1.setEnabled(true);
        
        if(!"S_Admin".equalsIgnoreCase(role)) {
        banbtn.setEnabled(false);
        editbtn1.setEnabled(false);
        }
        else {
        banbtn.setEnabled(true);
       
        }
       
    }
    }); 
    }

    private void clientdata() 
    { try (Connection conn = DatabaseConnection.connect()) 
    { DefaultTableModel model = new DefaultTableModel();
    model.setColumnIdentifiers(new String[]{"Client ID","Full Name", "Address", "Phone Number", "Date Registered", "Status"}); 
    String sql = "SELECT u.user_id, u.full_name, u.address, u.Phone_num, " + 
            "u.verified, d.date_created, d.verified_requested " + 
            "FROM user_table u JOIN user_data d ON u.user_id = d.user_id WHERE role = 'Client'; "; 
    PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery(sql); 
    while (rs.next()) 
    { Boolean verify = rs.getBoolean("verified"); 
    Boolean requested = rs.getBoolean("verified_requested"); 
    String status; 
    
    if (verify) { status = "Verified"; } 
    else if (requested) { status = "Pending"; } 
    else { status = "Not Verified"; } 
    model.addRow(
            new Object[]{ 
                rs.getString("user_id"), 
                rs.getString("full_name"), 
                rs.getString("address"), 
                rs.getString("Phone_num"), 
                rs.getString("date_created"), 
                status });
    } 
    
    usertbl.setModel(model); 
    applyStatusColor(usertbl,5);
    usertbl.setDefaultEditor(Object.class, null); } 
    
    catch (Exception e) 
    { e.printStackTrace(); 
    } 
    } 
    
    private void reserve(String filter) {
        

            
        
        String mainsql = "SELECT r.reservation_id,  u.full_name AS client_name, "
                + "c.vehicle_model AS vehicle_model, r.start_date, r.end_date, "
                + "r.status FROM reservations_table r JOIN user_table u ON r.user_id "
                + "= u.user_ID JOIN car_table c ON r.car_id = c.vehicle_id ";
        
        
        
        try (Connection conn = DatabaseConnection.connect()) {

       
         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID","Client Name", "Vehicle Model", "Start Date", "End Date", "Status"});

            if("Pending".equalsIgnoreCase(filter)) {
                mainsql += " WHERE r.status = 'Pending'";
            } else if("Confirmed".equalsIgnoreCase(filter)) {
                mainsql += " WHERE r.status = 'Confirmed'";
            }
            else if("Cancelled".equalsIgnoreCase(filter)) {
                mainsql += " WHERE r.status = 'Cancelled'";
            }
            else if("Completed".equalsIgnoreCase(filter)) {
                mainsql += " WHERE r.status = 'Completed'";
            }
       
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(mainsql);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("reservation_id"),
                rs.getString("client_name"),
                rs.getString("vehicle_model"),
                rs.getTimestamp("start_date"),
                rs.getTimestamp("end_date"),
                rs.getString("status")
            });
        }
        
      

        ReservTbl.setModel(model); 
        ReservTbl.getColumnModel().getColumn(0).setMinWidth(0);
        ReservTbl.getColumnModel().getColumn(0).setMaxWidth(0);
        ReservTbl.getColumnModel().getColumn(0).setWidth(0);  
        ReservTbl.setDefaultEditor(Object.class, null);
        
        applyStatusColor(ReservTbl,5);
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    private void fullpaid() {
         Connection conn = null;
         PreparedStatement ps1 = null;
         PreparedStatement ps2 = null; 
     
         try {
             conn = DatabaseConnection.connect();
             conn.setAutoCommit(false);
             String sql = "UPDATE reservations_table SET status = 'Confirmed' WHERE reservation_id = ?";
             ps1 = conn.prepareStatement(sql);
             ps1.setInt(1, reservID);  
             ps1.executeUpdate();  
             
             int total = balance + paid;
             
             String sql2 = "UPDATE payment_tbl SET amount_paid = ?, status = 'Paid', fully_paid_date = NOW() WHERE payment_id = ?";
                ps2 = conn.prepareStatement(sql2);
                ps2.setDouble(1, total); // works even if paid == 0
                ps2.setInt(2, payID);
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
      
     
        } 
    
    
    private void recentreserve() {
        String sql = "SELECT r.reservation_id,  u.full_name AS client_name, " +
                        "c.vehicle_model AS vehicle_model, r.start_date, r.end_date, " +
                        "r.status FROM reservations_table r JOIN user_table u ON " +
                        "r.user_id = u.user_ID JOIN car_table c ON r.car_id = " +
                        "c.vehicle_id ORDER BY date_created DESC " +
                        "LIMIT 3";
        
        try (Connection conn = DatabaseConnection.connect()) {

       
         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID","Client Name", "Vehicle Model", "Start Date", "End Date", "Status"});

       
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("reservation_id"),
                rs.getString("client_name"),
                rs.getString("vehicle_model"),
                rs.getTimestamp("start_date"),
                rs.getTimestamp("end_date"),
                rs.getString("status")
            });
        }
        
      

        recentTbl.setModel(model); 
        recentTbl.getColumnModel().getColumn(0).setMinWidth(0);
        recentTbl.getColumnModel().getColumn(0).setMaxWidth(0);
        recentTbl.getColumnModel().getColumn(0).setWidth(0);  
        recentTbl.setDefaultEditor(Object.class, null);
        applyStatusColor(recentTbl,5);

             
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    
    }
    
    private void paymentdata() {
String sql = "SELECT p.payment_id, p.reservation_id, u.full_name AS client_name, " +
             "r.date_created, p.amount_paid, p.status " +
             "FROM payment_tbl p " +
             "JOIN reservations_table r ON p.reservation_id = r.reservation_id " +
             "JOIN user_table u ON r.user_id = u.user_id WHERE 1=1";

             
        if (paystatus.getSelectedIndex() != 0) {
            sql += " AND p.status = ?";
        }

        if (reservstatus.getSelectedIndex() != 0) {
            sql += " AND r.status = ?";
        }

    try (Connection conn = DatabaseConnection.connect()) {

        
       
         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Payment ID","Reservation ID", "Client Name", "Payment Date", "Amount Paid", "Status"});
        PreparedStatement ps = conn.prepareStatement(sql);
        int paramIndex = 1;
        if (paystatus.getSelectedIndex() != 0) {
            ps.setString(paramIndex++, paystatus.getSelectedItem().toString());
        }
        if (reservstatus.getSelectedIndex() != 0) {
            ps.setString(paramIndex++, reservstatus.getSelectedItem().toString());
        }
        ResultSet rs = ps.executeQuery();
        
        boolean data = false;
        while (rs.next()) {
            data = true;
            model.addRow(new Object[]{
                rs.getInt("payment_id"),
                rs.getString("reservation_id"),
                rs.getString("client_name"),
                rs.getTimestamp("date_created"),
                rs.getString("amount_paid"),
                rs.getString("status")
            });
        }
        if (!data) {
            JOptionPane.showMessageDialog(null, "No records found.");
        }
      

        paytbl.setModel(model); 
        paytbl.setDefaultEditor(Object.class, null);
        applyStatusColor(paytbl,5);

             
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
        Parent = new javax.swing.JPanel();
        Dashboard = new javax.swing.JPanel();
        ListP14 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        carlbl1 = new javax.swing.JLabel();
        ListP9 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        rentlbl = new javax.swing.JLabel();
        ListP10 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        TotalUsr = new javax.swing.JLabel();
        ListP11 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        availlbl = new javax.swing.JLabel();
        QuickA = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnAddA = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        recentTbl = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        totalvalue = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        balancelbl = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        rslvbtn = new javax.swing.JButton();
        jLabel46 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        Reservations = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        ongoinglbl = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        pendinglbl = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jButton16 = new javax.swing.JButton();
        viewrsrvbtn = new javax.swing.JButton();
        editbtn = new javax.swing.JButton();
        cancelbtn1 = new javax.swing.JButton();
        dslctbtn = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        ReservTbl = new javax.swing.JTable();
        jPanel16 = new javax.swing.JPanel();
        pendingbtn = new javax.swing.JRadioButton();
        confirmedbtn = new javax.swing.JRadioButton();
        cancelbtn = new javax.swing.JRadioButton();
        resetbtn = new javax.swing.JButton();
        completebtn = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        CarM = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        VehicleTbl = new javax.swing.JTable();
        cardL = new javax.swing.JPanel();
        TipPanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        cardetails = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        seatlbl = new javax.swing.JLabel();
        fueltyplbl = new javax.swing.JLabel();
        translbl = new javax.swing.JLabel();
        modellbl = new javax.swing.JLabel();
        carbndlbl = new javax.swing.JLabel();
        platenolbl = new javax.swing.JLabel();
        cartypelbl = new javax.swing.JLabel();
        idlabel = new javax.swing.JLabel();
        statuslbl = new javax.swing.JLabel();
        rate24lbl = new javax.swing.JLabel();
        rate12lbl = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        desclbl = new javax.swing.JTextArea();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        addbtn = new javax.swing.JButton();
        delbtn = new javax.swing.JButton();
        editvhclbtn = new javax.swing.JButton();
        setStatusbtn = new javax.swing.JButton();
        viewimgbtn = new javax.swing.JButton();
        deselectbtn = new javax.swing.JButton();
        Billings = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jButton21 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        paystatus = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        reservstatus = new javax.swing.JComboBox<>();
        jPanel24 = new javax.swing.JPanel();
        paydbtn = new javax.swing.JButton();
        paidbtn = new javax.swing.JButton();
        deselectbtn2 = new javax.swing.JButton();
        receiptbtn = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        paytbl = new javax.swing.JTable();
        Customers = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        usertbl = new javax.swing.JTable();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        TotalUsr1 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jPanel29 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        verifylbl = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        jButton19 = new javax.swing.JButton();
        clientbtn = new javax.swing.JButton();
        banbtn = new javax.swing.JButton();
        editbtn1 = new javax.swing.JButton();
        deselectbtn1 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        banlbl = new javax.swing.JLabel();
        BorderLeft = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        DashboardBttn = new javax.swing.JButton();
        CarMBttn = new javax.swing.JButton();
        ReservationBttn = new javax.swing.JButton();
        CustomerBttn = new javax.swing.JButton();
        BillingBttn = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        SettingBttn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(238, 238, 238));

        Parent.setLayout(new java.awt.CardLayout());

        Dashboard.setBackground(new java.awt.Color(238, 238, 238));
        Dashboard.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DASHBOARD", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14), new java.awt.Color(0, 102, 255))); // NOI18N
        Dashboard.setForeground(new java.awt.Color(0, 102, 255));

        ListP14.setBackground(new java.awt.Color(212, 226, 255));
        ListP14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel28.setBackground(new java.awt.Color(33, 105, 255));

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel1.setText("Total Vehicles Listed");

        carlbl1.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        carlbl1.setText("0");

        javax.swing.GroupLayout ListP14Layout = new javax.swing.GroupLayout(ListP14);
        ListP14.setLayout(ListP14Layout);
        ListP14Layout.setHorizontalGroup(
            ListP14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListP14Layout.createSequentialGroup()
                .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(ListP14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(carlbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ListP14Layout.setVerticalGroup(
            ListP14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(ListP14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(carlbl1)
                .addGap(38, 38, 38))
        );

        ListP9.setBackground(new java.awt.Color(181, 245, 194));
        ListP9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel18.setBackground(new java.awt.Color(36, 150, 59));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel3.setText("Active Rentals");

        rentlbl.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        rentlbl.setText("0");

        javax.swing.GroupLayout ListP9Layout = new javax.swing.GroupLayout(ListP9);
        ListP9.setLayout(ListP9Layout);
        ListP9Layout.setHorizontalGroup(
            ListP9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListP9Layout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(ListP9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ListP9Layout.createSequentialGroup()
                        .addComponent(rentlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ListP9Layout.setVerticalGroup(
            ListP9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(ListP9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(0, 0, 0)
                .addComponent(rentlbl)
                .addGap(38, 38, 38))
        );

        ListP10.setBackground(new java.awt.Color(255, 221, 212));
        ListP10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel19.setBackground(new java.awt.Color(255, 84, 38));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel5.setText("Total Registered Users");

        TotalUsr.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        TotalUsr.setText("0");

        javax.swing.GroupLayout ListP10Layout = new javax.swing.GroupLayout(ListP10);
        ListP10.setLayout(ListP10Layout);
        ListP10Layout.setHorizontalGroup(
            ListP10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListP10Layout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(ListP10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ListP10Layout.createSequentialGroup()
                        .addComponent(TotalUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ListP10Layout.setVerticalGroup(
            ListP10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(ListP10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(0, 0, 0)
                .addComponent(TotalUsr)
                .addGap(38, 38, 38))
        );

        ListP11.setBackground(new java.awt.Color(255, 249, 171));
        ListP11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel20.setBackground(new java.awt.Color(230, 213, 0));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel7.setText("Available Vehicles");

        availlbl.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        availlbl.setText("0");

        javax.swing.GroupLayout ListP11Layout = new javax.swing.GroupLayout(ListP11);
        ListP11.setLayout(ListP11Layout);
        ListP11Layout.setHorizontalGroup(
            ListP11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListP11Layout.createSequentialGroup()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(ListP11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(availlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ListP11Layout.setVerticalGroup(
            ListP11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(ListP11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(0, 0, 0)
                .addComponent(availlbl)
                .addGap(38, 38, 38))
        );

        QuickA.setBackground(new java.awt.Color(240, 240, 240));
        QuickA.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quick actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14))); // NOI18N

        jButton1.setText("Add new Client");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Add a Vehicle");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnAddA.setText("Add new admin");
        btnAddA.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAActionPerformed(evt);
            }
        });

        jButton4.setText("Verify Clients");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout QuickALayout = new javax.swing.GroupLayout(QuickA);
        QuickA.setLayout(QuickALayout);
        QuickALayout.setHorizontalGroup(
            QuickALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, QuickALayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(QuickALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAddA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        QuickALayout.setVerticalGroup(
            QuickALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, QuickALayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(15, 15, 15))
        );

        recentTbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        recentTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Client Name", "Vehicle Model", "Start Date", "End Date", "Status"
            }
        ));
        recentTbl.setRowHeight(26);
        recentTbl.setShowGrid(false);
        recentTbl.getTableHeader().setReorderingAllowed(false);
        recentTbl.setUpdateSelectionOnSort(false);
        recentTbl.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(recentTbl);

        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel11.setText("Recent Reservations");

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel15.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 153, 0));
        jLabel15.setText("Total Revenue");

        totalvalue.setFont(new java.awt.Font("Segoe UI Semibold", 0, 30)); // NOI18N
        totalvalue.setForeground(new java.awt.Color(0, 153, 0));
        totalvalue.setText("0");

        jLabel21.setFont(new java.awt.Font("Segoe UI Semibold", 0, 30)); // NOI18N
        jLabel21.setText("₱");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel23.setText("Sum of confirmed payment");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(0, 91, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalvalue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(totalvalue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jLabel23)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel42.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(204, 0, 0));
        jLabel42.setText("Outstanding Balance");

        balancelbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 30)); // NOI18N
        balancelbl.setForeground(new java.awt.Color(204, 0, 0));
        balancelbl.setText("0");

        jLabel44.setFont(new java.awt.Font("Segoe UI Semibold", 0, 30)); // NOI18N
        jLabel44.setText("₱");

        jLabel45.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N

        rslvbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        rslvbtn.setText("Resolve");
        rslvbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rslvbtn.setEnabled(false);
        rslvbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rslvbtnActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel46.setText("shows where payments has not yet been recieved");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rslvbtn))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(balancelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel45)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel46)))
                        .addGap(0, 45, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(rslvbtn))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(balancelbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(jLabel46))
                .addContainerGap())
        );

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout DashboardLayout = new javax.swing.GroupLayout(Dashboard);
        Dashboard.setLayout(DashboardLayout);
        DashboardLayout.setHorizontalGroup(
            DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashboardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DashboardLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addGroup(DashboardLayout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(DashboardLayout.createSequentialGroup()
                        .addGroup(DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ListP14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ListP11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ListP9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ListP10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addComponent(QuickA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        DashboardLayout.setVerticalGroup(
            DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DashboardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(DashboardLayout.createSequentialGroup()
                        .addComponent(ListP14, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ListP11, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DashboardLayout.createSequentialGroup()
                        .addComponent(ListP10, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ListP9, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(QuickA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(DashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        Parent.add(Dashboard, "card2");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RESERVATIONS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14), new java.awt.Color(252, 3, 57))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(181, 245, 194));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel8.setBackground(new java.awt.Color(36, 150, 59));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel12.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel12.setText("Ongoing Reservations");

        ongoinglbl.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        ongoinglbl.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ongoinglbl, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(0, 0, 0)
                .addComponent(ongoinglbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 252, 189));
        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel10.setBackground(new java.awt.Color(196, 188, 4));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 91, Short.MAX_VALUE)
        );

        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel13.setText("Pending Reservations");

        pendinglbl.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        pendinglbl.setText("0");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(pendinglbl))
                .addGap(0, 48, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(0, 0, 0)
                .addComponent(pendinglbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(255, 143, 143));
        jPanel11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel12.setBackground(new java.awt.Color(179, 0, 0));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel14.setText("Cancelled Reservations");

        jLabel17.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        jLabel17.setText("0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel17))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(0, 0, 0)
                .addComponent(jLabel17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N

        jButton16.setText("Add Reservation");
        jButton16.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        viewrsrvbtn.setText("View Reservation");
        viewrsrvbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viewrsrvbtn.setEnabled(false);
        viewrsrvbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewrsrvbtnActionPerformed(evt);
            }
        });

        editbtn.setText("Edit Reservation");
        editbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editbtn.setEnabled(false);
        editbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editbtnActionPerformed(evt);
            }
        });

        cancelbtn1.setText("Cancel Reservation");
        cancelbtn1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelbtn1.setEnabled(false);
        cancelbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelbtn1ActionPerformed(evt);
            }
        });

        dslctbtn.setText("Deselect");
        dslctbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dslctbtn.setEnabled(false);
        dslctbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dslctbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viewrsrvbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelbtn1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(dslctbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButton16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewrsrvbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelbtn1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dslctbtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ReservTbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        ReservTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Client Name", "Car Model", "Start Date", "End Date", "Status"
            }
        ));
        ReservTbl.setRowHeight(30);
        ReservTbl.setShowGrid(false);
        ReservTbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(ReservTbl);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N

        buttonGroup1.add(pendingbtn);
        pendingbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 11)); // NOI18N
        pendingbtn.setText("Pending");
        pendingbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pendingbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendingbtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(confirmedbtn);
        confirmedbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 11)); // NOI18N
        confirmedbtn.setText("Confirmed");
        confirmedbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        confirmedbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmedbtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(cancelbtn);
        cancelbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 11)); // NOI18N
        cancelbtn.setText("Cancelled");
        cancelbtn.setToolTipText("");
        cancelbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelbtnActionPerformed(evt);
            }
        });

        resetbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        resetbtn.setText("Clear Filter");
        resetbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        resetbtn.setEnabled(false);
        resetbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetbtnActionPerformed(evt);
            }
        });

        buttonGroup1.add(completebtn);
        completebtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 11)); // NOI18N
        completebtn.setText("Completed");
        completebtn.setToolTipText("");
        completebtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        completebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completebtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resetbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confirmedbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pendingbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(completebtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(pendingbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmedbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(completebtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resetbtn)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        jLabel4.setText("Reservation History");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        javax.swing.GroupLayout ReservationsLayout = new javax.swing.GroupLayout(Reservations);
        Reservations.setLayout(ReservationsLayout);
        ReservationsLayout.setHorizontalGroup(
            ReservationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ReservationsLayout.setVerticalGroup(
            ReservationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Parent.add(Reservations, "card4");

        CarM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "VEHICLE MANAGEMENT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14), new java.awt.Color(255, 200, 0))); // NOI18N

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));

        VehicleTbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        VehicleTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Vehicle Brand", "Vehicle Model", "Plate Number", "Status"
            }
        ));
        VehicleTbl.setRowHeight(30);
        VehicleTbl.setShowGrid(false);
        VehicleTbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(VehicleTbl);

        cardL.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Vehicle Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N
        cardL.setLayout(new java.awt.CardLayout());

        jLabel16.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel16.setText("No vehicle selected yet.");

        javax.swing.GroupLayout TipPanelLayout = new javax.swing.GroupLayout(TipPanel);
        TipPanel.setLayout(TipPanelLayout);
        TipPanelLayout.setHorizontalGroup(
            TipPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TipPanelLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLabel16)
                .addContainerGap(145, Short.MAX_VALUE))
        );
        TipPanelLayout.setVerticalGroup(
            TipPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TipPanelLayout.createSequentialGroup()
                .addGap(113, 113, 113)
                .addComponent(jLabel16)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        cardL.add(TipPanel, "card2");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel2.setText("Vehicle Id :");

        jLabel19.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel19.setText("Type :");

        jLabel27.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel27.setText("Model :");

        jLabel28.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel28.setText("Brand :");

        jLabel29.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel29.setText("Plate No. :");

        jLabel30.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel30.setText("Fuel Type :");

        jLabel31.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel31.setText("Transmission :");

        jLabel32.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel32.setText("Seats:");

        jLabel33.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel33.setText("12-Hour Rate:");

        jLabel34.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel34.setText("24-Hour Rate:");

        jLabel35.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel35.setText("Description:");

        jLabel37.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel37.setText("Status:");

        seatlbl.setText("-");

        fueltyplbl.setText("-");

        translbl.setText("-");

        modellbl.setText("-");

        carbndlbl.setText("-");

        platenolbl.setText("-");

        cartypelbl.setText("-");

        idlabel.setText("-");

        statuslbl.setText("-");

        rate24lbl.setText("-");

        rate12lbl.setText("-");

        jScrollPane8.setBorder(null);

        desclbl.setEditable(false);
        desclbl.setBackground(new java.awt.Color(240, 240, 240));
        desclbl.setColumns(20);
        desclbl.setFont(new java.awt.Font("Segoe UI Symbol", 0, 12)); // NOI18N
        desclbl.setLineWrap(true);
        desclbl.setRows(5);
        desclbl.setText("-");
        desclbl.setWrapStyleWord(true);
        desclbl.setAutoscrolls(false);
        desclbl.setCaretColor(new java.awt.Color(240, 240, 240));
        desclbl.setOpaque(false);
        jScrollPane8.setViewportView(desclbl);

        jLabel49.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel49.setText("₱ ");

        jLabel50.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel50.setText("₱ ");

        javax.swing.GroupLayout cardetailsLayout = new javax.swing.GroupLayout(cardetails);
        cardetails.setLayout(cardetailsLayout);
        cardetailsLayout.setHorizontalGroup(
            cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cardetailsLayout.createSequentialGroup()
                        .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cartypelbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(modellbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(cardetailsLayout.createSequentialGroup()
                                        .addComponent(carbndlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(3, 3, 3)))))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statuslbl, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(cardetailsLayout.createSequentialGroup()
                                        .addComponent(jLabel34)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel50))
                                    .addGroup(cardetailsLayout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel49)))
                                .addGap(0, 0, 0)
                                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rate24lbl, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                    .addComponent(rate12lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())
                    .addGroup(cardetailsLayout.createSequentialGroup()
                        .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(translbl, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fueltyplbl, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(platenolbl, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(cardetailsLayout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(seatlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 49, Short.MAX_VALUE))))
        );
        cardetailsLayout.setVerticalGroup(
            cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel33)
                        .addComponent(idlabel)
                        .addComponent(rate12lbl)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cartypelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(jLabel34)
                        .addComponent(rate24lbl)
                        .addComponent(jLabel50)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel37)
                    .addComponent(modellbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statuslbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel35)
                    .addComponent(carbndlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cardetailsLayout.createSequentialGroup()
                        .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(platenolbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fueltyplbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(translbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cardetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(seatlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(56, Short.MAX_VALUE))
                    .addGroup(cardetailsLayout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        cardL.add(cardetails, "card3");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OTHER OPTIONS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N

        addbtn.setText("Add new Vehicle");
        addbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbtnActionPerformed(evt);
            }
        });

        delbtn.setText("Remove Vehicle");
        delbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        delbtn.setEnabled(false);
        delbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delbtnActionPerformed(evt);
            }
        });

        editvhclbtn.setText("Edit Vehicle");
        editvhclbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editvhclbtn.setEnabled(false);
        editvhclbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editvhclbtnActionPerformed(evt);
            }
        });

        setStatusbtn.setText("Set Status");
        setStatusbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setStatusbtn.setEnabled(false);
        setStatusbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setStatusbtnActionPerformed(evt);
            }
        });

        viewimgbtn.setText("View Vehicle Image");
        viewimgbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        viewimgbtn.setEnabled(false);
        viewimgbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewimgbtnActionPerformed(evt);
            }
        });

        deselectbtn.setText("Deselect Vehicle");
        deselectbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deselectbtn.setEnabled(false);
        deselectbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(delbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editvhclbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(setStatusbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(viewimgbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(deselectbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(addbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editvhclbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setStatusbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewimgbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deselectbtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout CarMLayout = new javax.swing.GroupLayout(CarM);
        CarM.setLayout(CarMLayout);
        CarMLayout.setHorizontalGroup(
            CarMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CarMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CarMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(CarMLayout.createSequentialGroup()
                        .addComponent(cardL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        CarMLayout.setVerticalGroup(
            CarMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CarMLayout.createSequentialGroup()
                .addGroup(CarMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cardL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addContainerGap())
        );

        Parent.add(CarM, "card3");

        Billings.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "BILLINGS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14))); // NOI18N

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N

        jButton21.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton21.setText("Apply");
        jButton21.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel25.setText("By Payment Status");

        paystatus.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        paystatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Paid", "Pending", "Partial" }));
        paystatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel26.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel26.setText("By Reservation Status");

        reservstatus.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        reservstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Cancelled", "Comfirmed", "Completed", "Ongoing", "Pending" }));
        reservstatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reservstatus, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paystatus, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paystatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reservstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton21)
                .addContainerGap())
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N

        paydbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        paydbtn.setText("Payment Details");
        paydbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        paydbtn.setEnabled(false);
        paydbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paydbtnActionPerformed(evt);
            }
        });

        paidbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        paidbtn.setText("Mark as Paid");
        paidbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        paidbtn.setEnabled(false);
        paidbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paidbtnActionPerformed(evt);
            }
        });

        deselectbtn2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        deselectbtn2.setText("Deselect Cell");
        deselectbtn2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deselectbtn2.setEnabled(false);
        deselectbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectbtn2ActionPerformed(evt);
            }
        });

        receiptbtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        receiptbtn.setText("Generate Receipt");
        receiptbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        receiptbtn.setEnabled(false);
        receiptbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiptbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paydbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paidbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deselectbtn2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(receiptbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(paydbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paidbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(deselectbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane7.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        paytbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        paytbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Payment ID", "Reservation ID", "Client Name", "Payment Date", "Total Cost", "Status"
            }
        ));
        paytbl.setRowHeight(26);
        paytbl.setShowGrid(false);
        paytbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(paytbl);

        javax.swing.GroupLayout BillingsLayout = new javax.swing.GroupLayout(Billings);
        Billings.setLayout(BillingsLayout);
        BillingsLayout.setHorizontalGroup(
            BillingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BillingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(BillingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        BillingsLayout.setVerticalGroup(
            BillingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BillingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(BillingsLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Parent.add(Billings, "card6");

        Customers.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CUSTOMERS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14), new java.awt.Color(179, 51, 0))); // NOI18N

        usertbl.setAutoCreateRowSorter(true);
        usertbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        usertbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Client ID", "Full Name", "Home Address", "Phone Number", "Date Registered", "Verified"
            }
        ));
        usertbl.setRowHeight(26);
        usertbl.setShowGrid(false);
        usertbl.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(usertbl);

        jPanel25.setBackground(new java.awt.Color(255, 249, 171));
        jPanel25.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel26.setBackground(new java.awt.Color(196, 188, 4));
        jPanel26.setPreferredSize(new java.awt.Dimension(6, 0));

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel18.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(38, 38, 38));
        jLabel18.setText("Total Registered Users");

        TotalUsr1.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        TotalUsr1.setForeground(new java.awt.Color(38, 38, 38));
        TotalUsr1.setText("0");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(TotalUsr1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addGap(0, 0, 0)
                .addComponent(TotalUsr1)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel27.setBackground(new java.awt.Color(181, 245, 194));
        jPanel27.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel27.setPreferredSize(new java.awt.Dimension(168, 87));

        jPanel29.setBackground(new java.awt.Color(36, 150, 59));
        jPanel29.setPreferredSize(new java.awt.Dimension(6, 0));

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel20.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(38, 38, 38));
        jLabel20.setText("Total Verified Users");

        verifylbl.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        verifylbl.setForeground(new java.awt.Color(38, 38, 38));
        verifylbl.setText("0");

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(verifylbl))
                .addGap(0, 51, Short.MAX_VALUE))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel20)
                .addGap(0, 0, 0)
                .addComponent(verifylbl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 13))); // NOI18N

        jButton19.setText("Add Client");
        jButton19.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        clientbtn.setText("Client's Profile");
        clientbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clientbtn.setEnabled(false);
        clientbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientbtnActionPerformed(evt);
            }
        });

        banbtn.setText("Ban Client");
        banbtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        banbtn.setEnabled(false);
        banbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                banbtnActionPerformed(evt);
            }
        });

        editbtn1.setText("Edit Client's Info");
        editbtn1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editbtn1.setEnabled(false);
        editbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editbtn1ActionPerformed(evt);
            }
        });

        deselectbtn1.setText("Deselect Client");
        deselectbtn1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deselectbtn1.setEnabled(false);
        deselectbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectbtn1ActionPerformed(evt);
            }
        });

        jButton29.setText("Verify Client");
        jButton29.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editbtn1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                    .addComponent(banbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clientbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deselectbtn1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(banbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editbtn1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deselectbtn1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBackground(new java.awt.Color(255, 143, 143));
        jPanel17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel31.setBackground(new java.awt.Color(179, 0, 0));
        jPanel31.setPreferredSize(new java.awt.Dimension(6, 0));

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 6, Short.MAX_VALUE)
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel22.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(38, 38, 38));
        jLabel22.setText("Total Banned Users");

        banlbl.setFont(new java.awt.Font("Segoe UI Symbol", 0, 30)); // NOI18N
        banlbl.setForeground(new java.awt.Color(38, 38, 38));
        banlbl.setText("0");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(banlbl))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(banlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout CustomersLayout = new javax.swing.GroupLayout(Customers);
        Customers.setLayout(CustomersLayout);
        CustomersLayout.setHorizontalGroup(
            CustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        CustomersLayout.setVerticalGroup(
            CustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CustomersLayout.createSequentialGroup()
                .addGroup(CustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6)
                    .addGroup(CustomersLayout.createSequentialGroup()
                        .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        Parent.add(Customers, "card5");

        BorderLeft.setBackground(new java.awt.Color(238, 238, 238));
        BorderLeft.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ADMINISTRATOR", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Semibold", 0, 14))); // NOI18N

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Logo2.png"))); // NOI18N

        DashboardBttn.setBackground(new java.awt.Color(232, 244, 255));
        DashboardBttn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        DashboardBttn.setText("Dashboard");
        DashboardBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        DashboardBttn.setContentAreaFilled(false);
        DashboardBttn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DashboardBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DashboardBttnActionPerformed(evt);
            }
        });

        CarMBttn.setBackground(new java.awt.Color(232, 244, 255));
        CarMBttn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        CarMBttn.setText("Manage Vehicles");
        CarMBttn.setToolTipText("");
        CarMBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CarMBttn.setContentAreaFilled(false);
        CarMBttn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CarMBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CarMBttnActionPerformed(evt);
            }
        });

        ReservationBttn.setBackground(new java.awt.Color(232, 244, 255));
        ReservationBttn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        ReservationBttn.setText("Reservations");
        ReservationBttn.setToolTipText("");
        ReservationBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ReservationBttn.setContentAreaFilled(false);
        ReservationBttn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ReservationBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReservationBttnActionPerformed(evt);
            }
        });

        CustomerBttn.setBackground(new java.awt.Color(232, 244, 255));
        CustomerBttn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        CustomerBttn.setText("Customers");
        CustomerBttn.setToolTipText("");
        CustomerBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CustomerBttn.setContentAreaFilled(false);
        CustomerBttn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        CustomerBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomerBttnActionPerformed(evt);
            }
        });

        BillingBttn.setBackground(new java.awt.Color(232, 244, 255));
        BillingBttn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        BillingBttn.setText("Billing");
        BillingBttn.setToolTipText("");
        BillingBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        BillingBttn.setContentAreaFilled(false);
        BillingBttn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BillingBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BillingBttnActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(232, 244, 255));
        jButton10.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jButton10.setText("Log Out");
        jButton10.setToolTipText("");
        jButton10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton10.setContentAreaFilled(false);
        jButton10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        SettingBttn.setBackground(new java.awt.Color(232, 244, 255));
        SettingBttn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        SettingBttn.setText("Settings");
        SettingBttn.setToolTipText("");
        SettingBttn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        SettingBttn.setContentAreaFilled(false);
        SettingBttn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        SettingBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SettingBttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BorderLeftLayout = new javax.swing.GroupLayout(BorderLeft);
        BorderLeft.setLayout(BorderLeftLayout);
        BorderLeftLayout.setHorizontalGroup(
            BorderLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BorderLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(BorderLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BorderLeftLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BorderLeftLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel9))
                    .addComponent(DashboardBttn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(CarMBttn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ReservationBttn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CustomerBttn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BillingBttn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SettingBttn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        BorderLeftLayout.setVerticalGroup(
            BorderLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BorderLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DashboardBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CarMBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ReservationBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CustomerBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BillingBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SettingBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(77, 77, 77)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BorderLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Parent, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Parent, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(BorderLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAActionPerformed
     AddAdminA dialog1 = new AddAdminA(this,true);      
     dialog1.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_btnAddAActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
       Question dialog1 = new Question(this,true); //object
       dialog1.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void SettingBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingBttnActionPerformed
        AdminSettings dialog = new AdminSettings(this,true, userId);
        dialog.setVisible(true);
        setActiveButton(SettingBttn);         // TODO add your handling code here:
    }//GEN-LAST:event_SettingBttnActionPerformed

    private void paidbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paidbtnActionPerformed
           if (userId == AdminIDfetch || "S_Admin".equalsIgnoreCase(role)) {
                 int option = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to mark this Payment as Fully Paid?", // message
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
            paydbtn.setEnabled(false);
            receiptbtn.setEnabled(false);
            paidbtn.setEnabled(false);
            deselectbtn2.setEnabled(false);
            paytbl.clearSelection();   
              paymentdata();// TODO add your handling code here:
    }//GEN-LAST:event_paidbtnActionPerformed

    private void DashboardBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DashboardBttnActionPerformed
        Parent.removeAll();
        Parent.add(Dashboard);
        Parent.repaint();
        Parent.revalidate();
        setActiveButton(DashboardBttn);
        recentreserve();
        disablebtn();
        database();
        // TODO add your handling code here:
    }//GEN-LAST:event_DashboardBttnActionPerformed

    private void CarMBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CarMBttnActionPerformed
        Parent.removeAll();
        Parent.add(CarM);
        Parent.repaint();
        Parent.revalidate();   // TODO add your handling code here:
        setActiveButton(CarMBttn);       
         carrefresh();
         disablebtn();
         
    }//GEN-LAST:event_CarMBttnActionPerformed

    private void ReservationBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReservationBttnActionPerformed
        Parent.removeAll();
        Parent.add(Reservations);
        Parent.repaint();
        Parent.revalidate();
        setActiveButton(ReservationBttn); 
        ReservTbl.clearSelection();
        cancelbtn1.setEnabled(false);
        editbtn.setEnabled(false);
        viewrsrvbtn.setEnabled(false);
        dslctbtn.setEnabled(false);
        reserve(null);
        disablebtn();// TODO add your handling code here:
    }//GEN-LAST:event_ReservationBttnActionPerformed

    private void CustomerBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomerBttnActionPerformed
        
        Parent.removeAll();
        Parent.add(Customers);
        Parent.repaint();
        Parent.revalidate();
        setActiveButton(CustomerBttn);
        clientdata();
        disablebtn();   
         usertbl.clearSelection();    
                deselectbtn1.setEnabled(false);
                clientbtn.setEnabled(false);
                banbtn.setEnabled(false);
                editbtn1.setEnabled(false); 
            // TODO add your handling code here:
    }//GEN-LAST:event_CustomerBttnActionPerformed

    private void BillingBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BillingBttnActionPerformed

        paymentdata();
        Parent.removeAll();
        Parent.add(Billings);
        Parent.repaint();
        Parent.revalidate();    
        setActiveButton(BillingBttn); 
        paydbtn.setEnabled(false);
        receiptbtn.setEnabled(false);
        paidbtn.setEnabled(false);
        deselectbtn2.setEnabled(false);
        paytbl.clearSelection();   
        disablebtn();// TODO add your handling code here:
    }//GEN-LAST:event_BillingBttnActionPerformed

    private void deselectbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectbtn2ActionPerformed
        paydbtn.setEnabled(false);
        receiptbtn.setEnabled(false);
        paidbtn.setEnabled(false);
        deselectbtn2.setEnabled(false);
        paytbl.clearSelection();        // TODO add your handling code here:
    }//GEN-LAST:event_deselectbtn2ActionPerformed

    private void receiptbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiptbtnActionPerformed
        Receipt dialog = new Receipt(this,true, payID);
        dialog.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_receiptbtnActionPerformed

    private void paydbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paydbtnActionPerformed
        ViewReserve dialog = new ViewReserve(this,true, reservID, userId, true);  
        dialog.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_paydbtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        AddClientB dialog1 = new AddClientB(this,true,null, userId);
        dialog1.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Verifyclient dialog3 = new Verifyclient(this,true,userId);  
        dialog3.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        CarADD dialog2 = new CarADD(this,true);
        dialog2.setVisible(true); 
          try (Connection conn = DatabaseConnection.connect()) {
        String sql = "SELECT COUNT(*) AS total_cars FROM car_table";
        PreparedStatement pst2 = conn.prepareStatement(sql);
        ResultSet rs2 = pst2.executeQuery();
        if (rs2.next()) {
            int totalcars = rs2.getInt("total_cars");
            availlbl.setText(String.valueOf(totalcars));
            carlbl1.setText(String.valueOf(totalcars)); 
        }
       
        
        rs2.close(); 
        pst2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }   // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
 
    
    private void addbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbtnActionPerformed
        CarADD dialog2 = new CarADD(this,true);
        dialog2.setVisible(true);   
         try (Connection conn = DatabaseConnection.connect()) {
        String sql = "SELECT COUNT(*) AS total_cars FROM car_table";
        PreparedStatement pst2 = conn.prepareStatement(sql);
        ResultSet rs2 = pst2.executeQuery();
        if (rs2.next()) {
            int totalcars = rs2.getInt("total_cars");
            availlbl.setText(String.valueOf(totalcars));
            carlbl1.setText(String.valueOf(totalcars)); 
        }
        rs2.close(); 
        pst2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        try (Connection conn = DatabaseConnection.connect()) {

         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Vehicle ID", "Vehicle Brand", "Vehicle Model", "Plate No", "Status"});

        String sql = "SELECT vehicle_id, vehicle_brand, vehicle_model, plate_num, status FROM car_table";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("vehicle_id"),
                rs.getString("vehicle_brand"),
                rs.getString("vehicle_model"),
                rs.getString("plate_num"),
                rs.getString("status")
            });
        }

        VehicleTbl.setModel(model); 
        VehicleTbl.setDefaultEditor(Object.class, null);
        applyStatusColor(VehicleTbl,4);
             
        } catch (Exception e) {
            e.printStackTrace();
        }  
        database();
    }//GEN-LAST:event_addbtnActionPerformed

    private void viewimgbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewimgbtnActionPerformed
         
      File imgFile = new File(carimgpath);
    ImageIcon icon = new ImageIcon(
        new ImageIcon(imgFile.getAbsolutePath())
            .getImage()
            .getScaledInstance(400, 300, Image.SCALE_SMOOTH)
    );

    JLabel imgLabel = new JLabel(icon);
    JOptionPane.showMessageDialog(frame, imgLabel, 
            "Preview Image", JOptionPane.PLAIN_MESSAGE);
  // TODO add your handling code here
    }//GEN-LAST:event_viewimgbtnActionPerformed

    private void deselectbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectbtnActionPerformed
        VehicleTbl.clearSelection();
        disablebtn();
    }//GEN-LAST:event_deselectbtnActionPerformed

    
    
    private void delbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delbtnActionPerformed

        int confirm = JOptionPane.showConfirmDialog(
        frame, 
        "Are you sure you want to delete this Vehicle?", 
        "Confirm Delete", 
        JOptionPane.YES_NO_OPTION
);

        if (confirm == JOptionPane.YES_OPTION) {
            deleteCar(carid);  // your method to delete
        }
        
        try (Connection conn = DatabaseConnection.connect()) {

         DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Vehicle ID", "Vehicle Brand", "Vehicle Model", "Plate No", "Status"});

        String sql = "SELECT vehicle_id, vehicle_brand, vehicle_model, plate_num, status FROM car_table";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("vehicle_id"),
                rs.getString("vehicle_brand"),
                rs.getString("vehicle_model"),
                rs.getString("plate_num"),
                rs.getString("status")
            });
        }

        VehicleTbl.setModel(model); 
        VehicleTbl.setDefaultEditor(Object.class, null);
             
        } catch (Exception e) {
            e.printStackTrace();
        }  
         disablebtn();// TODO add your handling code here:
    }//GEN-LAST:event_delbtnActionPerformed

    private void editvhclbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editvhclbtnActionPerformed
        int selectedRow = VehicleTbl.getSelectedRow();
        int carId = Integer.parseInt(VehicleTbl.getValueAt(selectedRow, 0).toString());
        CarEdit dialog1 = new CarEdit(new javax.swing.JFrame(), true, carId);
        dialog1.setVisible(true);
        carrefresh();
        disablebtn();
        
        
        
        // method to fetch full car details        // TODO add your handling code here:
    }//GEN-LAST:event_editvhclbtnActionPerformed

    private void setStatusbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setStatusbtnActionPerformed
        String[] options = {"Available", "Maintenance"};
String currentStatus = statuslbl.getText(); // current status
String newStatus = (String) JOptionPane.showInputDialog(
        this, 
        "Select new status:", 
        "Change Vehicle Status", 
        JOptionPane.PLAIN_MESSAGE, 
        null, 
        options, 
        currentStatus
);

if (newStatus != null && !newStatus.equals(currentStatus)) {
    int selectedRow = VehicleTbl.getSelectedRow();
    int carId = Integer.parseInt(VehicleTbl.getValueAt(selectedRow, 0).toString());
    updateCarStatusInDatabase(carId, newStatus);
    statuslbl.setText(newStatus); 
}  

carrefresh();
database();
// TODO add your handling code here:
    }//GEN-LAST:event_setStatusbtnActionPerformed

    





    
    private void editbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editbtn1ActionPerformed
         AddClientB dialog = new AddClientB(this,true, ClientID, 0);
        dialog.setVisible(true); 
        clientdata();// TODO add your handling code here:
    }//GEN-LAST:event_editbtn1ActionPerformed

    private void deselectbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectbtn1ActionPerformed
                usertbl.clearSelection();    
                deselectbtn1.setEnabled(false);
                clientbtn.setEnabled(false);
                banbtn.setEnabled(false);
                editbtn1.setEnabled(false); //ODO add your handling code here:
    }//GEN-LAST:event_deselectbtn1ActionPerformed

    private void clientbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientbtnActionPerformed
        UserData dialog = new UserData(this,true, ClientID);
        dialog.setVisible(true);                      // TODO add your handling code here:
    }//GEN-LAST:event_clientbtnActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
    AddClientB dialog1 = new AddClientB(this,true, null, userId);
    dialog1.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton19ActionPerformed

    private void banbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_banbtnActionPerformed
      BanPanel dialog = new BanPanel(this,true, ClientID1);
        dialog.setVisible(true);     // TODO add your handling code here:
    }//GEN-LAST:event_banbtnActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
       Verifyclient dialog1 = new Verifyclient(this,true,userId);
       dialog1.setVisible(true);            // TODO add your handling code here:
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        Reserve dialog = new Reserve(this, true,null,userId);
        dialog.setVisible(true); 
        reserve(null);  // TODO add your handling code here:
    }//GEN-LAST:event_jButton16ActionPerformed

    private void rslvbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rslvbtnActionPerformed
        paymentdata();
        Parent.removeAll();
        Parent.add(Billings);
        Parent.repaint();
        Parent.revalidate();    
        setActiveButton(BillingBttn);  
        disablebtn();        // TODO add your handling code here:
    }//GEN-LAST:event_rslvbtnActionPerformed

    private void editbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editbtnActionPerformed
                int reservationId = getSelectedReservationId();
                if (reservationId != -1) {
                if (madeId == userId || "S_Admin".equalsIgnoreCase(role)) {
                 Reserve dialog = new Reserve(this, true,reservationId,userId);
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
                   
                
                }
            // TODO add your handling code here:
    }//GEN-LAST:event_editbtnActionPerformed

    private void viewrsrvbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewrsrvbtnActionPerformed
        int reservationId = getSelectedReservationId();
        ViewReserve dialog = new ViewReserve(this,true,reservationId, userId, false);    
        dialog.setVisible(true);   
        ReservTbl.clearSelection();
        cancelbtn1.setEnabled(false);
        editbtn.setEnabled(false);
        viewrsrvbtn.setEnabled(false);
        dslctbtn.setEnabled(false);
        reserve(null);  // TODO add your handling code here:
    }//GEN-LAST:event_viewrsrvbtnActionPerformed

    private void dslctbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dslctbtnActionPerformed
        ReservTbl.clearSelection();
        cancelbtn1.setEnabled(false);
        editbtn.setEnabled(false);
        viewrsrvbtn.setEnabled(false);
        dslctbtn.setEnabled(false);
        reserve(null);        // TODO add your handling code here:
    }//GEN-LAST:event_dslctbtnActionPerformed

    private void cancelbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelbtn1ActionPerformed
        int reservationId = getSelectedReservationId();
        if (reservationId != -1) {
            if (madeId == userId || "S_Admin".equalsIgnoreCase(role)) {
            reservecancel(reservationId);
            }
            else {
                JOptionPane.showMessageDialog(
                    null, 
                    "Only Head Admin or Admin who made this can cancel this Reservation.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE
                );  
            }
        }    
        ReservTbl.clearSelection();
        cancelbtn1.setEnabled(false);
        editbtn.setEnabled(false);
        viewrsrvbtn.setEnabled(false);
        dslctbtn.setEnabled(false);
        reserve(null); 
    }//GEN-LAST:event_cancelbtn1ActionPerformed

    private void completebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completebtnActionPerformed
         reserve("Completed");
        resetbtn.setEnabled(true);        // TODO add your handling code here:
    }//GEN-LAST:event_completebtnActionPerformed

    private void resetbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetbtnActionPerformed
        buttonGroup1.clearSelection();
        reserve(null);
        resetbtn.setEnabled(false);// TODO add your handling code here:
    }//GEN-LAST:event_resetbtnActionPerformed

    private void cancelbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelbtnActionPerformed
        reserve("Cancelled");
        resetbtn.setEnabled(true);// TODO add your handling code here:
    }//GEN-LAST:event_cancelbtnActionPerformed

    private void confirmedbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmedbtnActionPerformed
        reserve("Confirmed");
        resetbtn.setEnabled(true);// TODO add your handling code here:
    }//GEN-LAST:event_confirmedbtnActionPerformed

    private void pendingbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendingbtnActionPerformed
        reserve("Pending");
        resetbtn.setEnabled(true);// TODO add your handling code here:
    }//GEN-LAST:event_pendingbtnActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        paymentdata();
    }//GEN-LAST:event_jButton21ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
          try {
            // Apply modern Look and Feel for all Swing components
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
              //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BillingBttn;
    private javax.swing.JPanel Billings;
    private javax.swing.JPanel BorderLeft;
    private javax.swing.JPanel CarM;
    private javax.swing.JButton CarMBttn;
    private javax.swing.JButton CustomerBttn;
    private javax.swing.JPanel Customers;
    private javax.swing.JPanel Dashboard;
    private javax.swing.JButton DashboardBttn;
    private javax.swing.JPanel ListP10;
    private javax.swing.JPanel ListP11;
    private javax.swing.JPanel ListP14;
    private javax.swing.JPanel ListP9;
    private javax.swing.JPanel Parent;
    private javax.swing.JPanel QuickA;
    private javax.swing.JTable ReservTbl;
    private javax.swing.JButton ReservationBttn;
    private javax.swing.JPanel Reservations;
    private javax.swing.JButton SettingBttn;
    private javax.swing.JPanel TipPanel;
    private javax.swing.JLabel TotalUsr;
    private javax.swing.JLabel TotalUsr1;
    private javax.swing.JTable VehicleTbl;
    private javax.swing.JButton addbtn;
    private javax.swing.JLabel availlbl;
    private javax.swing.JLabel balancelbl;
    private javax.swing.JButton banbtn;
    private javax.swing.JLabel banlbl;
    private javax.swing.JButton btnAddA;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cancelbtn;
    private javax.swing.JButton cancelbtn1;
    private javax.swing.JLabel carbndlbl;
    private javax.swing.JPanel cardL;
    private javax.swing.JPanel cardetails;
    private javax.swing.JLabel carlbl1;
    private javax.swing.JLabel cartypelbl;
    private javax.swing.JButton clientbtn;
    private javax.swing.JRadioButton completebtn;
    private javax.swing.JRadioButton confirmedbtn;
    private javax.swing.JButton delbtn;
    private javax.swing.JTextArea desclbl;
    private javax.swing.JButton deselectbtn;
    private javax.swing.JButton deselectbtn1;
    private javax.swing.JButton deselectbtn2;
    private javax.swing.JButton dslctbtn;
    private javax.swing.JButton editbtn;
    private javax.swing.JButton editbtn1;
    private javax.swing.JButton editvhclbtn;
    private javax.swing.JLabel fueltyplbl;
    private javax.swing.JLabel idlabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton29;
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
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel modellbl;
    private javax.swing.JLabel ongoinglbl;
    private javax.swing.JButton paidbtn;
    private javax.swing.JButton paydbtn;
    private javax.swing.JComboBox<String> paystatus;
    private javax.swing.JTable paytbl;
    private javax.swing.JRadioButton pendingbtn;
    private javax.swing.JLabel pendinglbl;
    private javax.swing.JLabel platenolbl;
    private javax.swing.JLabel rate12lbl;
    private javax.swing.JLabel rate24lbl;
    private javax.swing.JButton receiptbtn;
    private javax.swing.JTable recentTbl;
    private javax.swing.JLabel rentlbl;
    private javax.swing.JComboBox<String> reservstatus;
    private javax.swing.JButton resetbtn;
    private javax.swing.JButton rslvbtn;
    private javax.swing.JLabel seatlbl;
    private javax.swing.JButton setStatusbtn;
    private javax.swing.JLabel statuslbl;
    private javax.swing.JLabel totalvalue;
    private javax.swing.JLabel translbl;
    private javax.swing.JTable usertbl;
    private javax.swing.JLabel verifylbl;
    private javax.swing.JButton viewimgbtn;
    private javax.swing.JButton viewrsrvbtn;
    // End of variables declaration//GEN-END:variables
}
