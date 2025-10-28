/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.carrentalsystemalpha;

/**
 *
 * @author Daryl James
 */

import com.formdev.flatlaf.FlatLightLaf;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.optionalusertools.TimeVetoPolicy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalTime;
import javax.swing.UIManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.swing.JDialog;
import javax.swing.JOptionPane;



public final class Reserve extends javax.swing.JDialog {

    
  private String carId;
  private int userId; 
  private double totalprice;
  private double reservfee;
  private double depositamt;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private long durations;
  private final Integer reserveId;
  private final int adminID;
  private int payId;
    /**
     * Creates new form Reserve
     * @param parent
     * @param modal
     * @param reservationId
     * @param adminID
     */

    public Reserve(java.awt.Frame parent, boolean modal, Integer reservationId, int adminID) {
    super(parent, modal);
    this.reserveId = reservationId;
    this.adminID = adminID;
    initComponents();

    setLocationRelativeTo(null);
    if (reservationId != null) {
        ReserveEdit(reserveId);
        labelrsv.setText("Edit Reservation");
        setTitle("Car Rental System - Edit Reservation");
        editdate();
        paymentId(); 
        
    }
    else {
        setTitle("Car Rental System - Add Reservation");
        date();
    }
   
}
 
    public Reserve(JDialog parent, boolean modal, Integer reservationId, int adminID) {
    super(parent, modal);
    this.reserveId = reservationId;
    this.adminID = adminID;
    initComponents();

    setLocationRelativeTo(null);
    if (reservationId != null) {
        ReserveEdit(reserveId);
        labelrsv.setText("Edit Reservation");
        setTitle("Car Rental System - Edit Reservation");
        editdate();
        paymentId();
    }
    else {
        setTitle("Car Rental System - Add Reservation");
        date();}}    
    
    private void ReserveEdit(int reserveId) { 
        changebtn.setEnabled(false);
        
        
       String sql = "SELECT r.reservation_id, r.user_id, u.full_name AS client_name, "
           + "c.vehicle_model AS vehicle_model, r.car_id, r.start_date, r.end_date, "
           + "r.status, p.amount_paid "
           + "FROM reservations_table r "
           + "JOIN user_table u ON r.user_id = u.user_id "
           + "JOIN car_table c ON r.car_id = c.vehicle_id "
           + "JOIN payment_tbl p ON r.reservation_id = p.reservation_id "
           + "WHERE r.reservation_id = ?;";
        try(Connection conn = DatabaseConnection.connect()) {
          PreparedStatement ps = conn.prepareStatement(sql);
          ps.setInt(1,reserveId);
          ResultSet rs = ps.executeQuery();
          if (rs.next()) {
                userId = rs.getInt("user_id");
                userlbl.setText(rs.getString("client_name"));
                carlbl.setText(rs.getString("vehicle_model"));
                carId = rs.getString("car_id");
                Timestamp ts = rs.getTimestamp("start_date");
                Timestamp tse = rs.getTimestamp("end_date");
                LocalDateTime startDate = ts.toLocalDateTime();
                LocalDate date = startDate.toLocalDate();
                LocalTime time = startDate.toLocalTime();    
                LocalDateTime EndDate = tse.toLocalDateTime();
                LocalDate endDate = EndDate.toLocalDate();
                LocalTime endtime = EndDate.toLocalTime();    
                amountlbl.setText(rs.getString("amount_paid"));
                
                datechooser.setDate(date);
                timepicker1.setTime(time);
                enddate.setDate(endDate);
                timepicker2.setTime(endtime);
                startDateTime = startDate;
                endDateTime = EndDate;
                String durationText = getDurationText(date, time, endDate, endtime);
                durationlbl.setText(durationText);
                date();
               
                
          }} catch (SQLException e) {
            e.printStackTrace();
     
    }}
    
    
    

    private String getDurationText(LocalDate startDate, LocalTime startTime,
                               LocalDate endDate, LocalTime endTime) {
    if (startDate == null || startTime == null || endDate == null || endTime == null) {
        return ""; // Nothing to show yet
    }


    LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
    LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

    Duration duration = Duration.between(startDateTime, endDateTime);

    if (duration.isNegative() || duration.isZero()) {
        return ""; 
    }

    long totalHours = duration.toHours();
    long days = totalHours / 24;
    long hours = totalHours % 24;
    updatePrice(days, hours);
    durations = totalHours;
    if (days == 0) {
        return hours + " hour(s)";
    } else {
        return days + " day(s) " + hours + " hour(s)";
    }
  
}
    

public void updatePrice(long totalDays, long remainingHours) {
    double hourlyRate = 200;
    double rate12 = 0;
    double rate24 = 0;
    double deposit = 0;
    try (Connection conn = DatabaseConnection.connect()) {
        String sql = "SELECT rate_12, rate_24, vehicle_type FROM car_table WHERE vehicle_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, carId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            depositfield.setEnabled(false);
            rate12 = rs.getDouble("rate_12");
            rate24 = rs.getDouble("rate_24");
            String type = rs.getString("vehicle_type");
            
            if("pick up".equalsIgnoreCase(type)) {
                deposit = 350;
                depositamt = deposit;
            }
            else if("motor".equalsIgnoreCase(type)) {
                deposit = 300;
                depositamt = deposit;
            }
            else if("Sedan".equalsIgnoreCase(type)) {
                deposit = 450;
                depositamt = deposit;
            }
            else if("suv".equalsIgnoreCase(type)) {
                deposit = 500;
                depositamt = deposit;
            }
            else {
                depositfield.setText("");
                depositamt = 0;
                depositfield.setEnabled(true);
            }
            
        
        }
        

        
        
    } catch (SQLException e) {
        e.printStackTrace();
    }

    double price = 0;
    DecimalFormat df = new DecimalFormat("#,##0.00");

    if (totalDays == 0) {
         rate24cost.setText(df.format(00.00));
        if (remainingHours <= 0) {
            price = 0;
        } else if (remainingHours <= 6) {
            price = remainingHours * hourlyRate; // short rental   
            excesslbl.setText(df.format(remainingHours * hourlyRate));
        } else if (remainingHours <= 12) {
            price = rate12; // flat 12-hour rate
            rate12cost.setText(df.format(rate12));
            excesslbl.setText(df.format(00.00));
        } else if (remainingHours <= 24) {
            price = rate24; // flat 24-hour rate
            rate24cost.setText(df.format(rate24));
            rate12cost.setText(df.format(00.00));
            excesslbl.setText(df.format(00.00));
            
        }
    } else {
        // more than a day
        rate12cost.setText(df.format(00.00));
        rate24cost.setText(df.format(rate24));
        price = (totalDays * rate24);

        // add leftover hours after full days
        if (remainingHours > 0 && remainingHours <= 6) {
            price += remainingHours * hourlyRate;    
            excesslbl.setText(df.format(remainingHours * hourlyRate));    
        } else if (remainingHours <= 0) {
           price -= remainingHours * hourlyRate;
           excesslbl.setText(df.format(remainingHours * hourlyRate));    
        }
        else if (remainingHours > 6 && remainingHours <= 12) {
            rate12cost.setText(df.format(rate12));
            price += rate12;
            excesslbl.setText(df.format(00.00));
        } else if (remainingHours > 12) {
            price += rate24; // next full day equivalent
            rate24cost.setText(df.format(rate24 * totalDays));
            rate12cost.setText(df.format(00.00));
            excesslbl.setText(df.format(00.00));
        }
    }

    // reservation fee (25% of total)
    double reservefee = price * 0.10;
    double total = price + reservefee + deposit;
    totalprice = total;
    reservfee = reservefee;

    
    totallbl.setText(df.format(total));
    depositfield.setText(df.format(deposit));
    costlbl.setText(df.format(price));
    reservlbl.setText(df.format(reservefee));
}
    
 

public void date() {
    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();
    TimeVetoPolicy startTimePolicy;

    
    datechooser.setEnabled(false);
    enddate.setEnabled(false);
    timepicker1.setEnabled(false);
    timepicker2.setEnabled(false);


    
    TimeVetoPolicy officeHoursPolicy = time -> {
        java.time.LocalTime start = java.time.LocalTime.of(8, 0);
        java.time.LocalTime end = java.time.LocalTime.of(20, 0);
        return !time.isBefore(start) && !time.isAfter(end);
    };
    
    startTimePolicy = time -> {
        LocalTime officeStart = LocalTime.of(8, 0);
        LocalTime officeEnd = LocalTime.of(20, 0);

        // Must be within office hours
        boolean inOfficeHours = !time.isBefore(officeStart) && !time.isAfter(officeEnd);

        // If today, time cannot be before current time
        if (datechooser.getDate() != null && datechooser.getDate().isEqual(today)) {
            return inOfficeHours && !time.isBefore(now);
        } else {
           
            return inOfficeHours;
            
        }
    };
    
    timepicker1.getSettings().setVetoPolicy(startTimePolicy);
    timepicker2.getSettings().setVetoPolicy(officeHoursPolicy);

    DatePickerSettings settings = datechooser.getSettings();
    DatePickerSettings settings1 = enddate.getSettings();
    settings.setVetoPolicy(date -> !date.isBefore(java.time.LocalDate.now()));
    settings1.setVetoPolicy(date -> !date.isBefore(java.time.LocalDate.now()));


    if (carId != null && userId != -1) {
        datechooser.setEnabled(true);
        timepicker1.setEnabled(true);
    }

      ;
    
    
        datechooser.addDateChangeListener(e -> {
 
        LocalDate start = datechooser.getDate();
       if (start != null) {
        enddate.getSettings().setVetoPolicy(date -> {
            return !date.isBefore(start);
        });


        LocalDate end = enddate.getDate();
        if (end != null && start.isAfter(end)) {
            enddate.clear();
            timepicker2.clear();
        }
        }
        updateControls();   
        updateDurationLabel();
        });

        timepicker1.addTimeChangeListener(e -> {
            updateControls();
            updateDurationLabel();
        });
        
        enddate.addDateChangeListener(e -> {
            updateControls();
            updateDurationLabel();
            timepicker2.setEnabled(true);
        });
        
        timepicker2.addTimeChangeListener(e -> {
            updateDurationLabel();
        });
}

public void editdate() {
    // Disable start date/time pickers
    datechooser.setEnabled(false);
    timepicker1.setEnabled(false);

    // Enable end date/time pickers
    enddate.setEnabled(true);
    timepicker2.setEnabled(true);

    // Office hours veto for timepicker2
    TimeVetoPolicy officeHoursPolicy = time -> {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(20, 0);
        return !time.isBefore(start) && !time.isAfter(end);
    };
    timepicker2.getSettings().setVetoPolicy(officeHoursPolicy);

    // End date veto: cannot be before start date
    LocalDate existingEnd = enddate.getDate(); // save current end date
    enddate.getSettings().setVetoPolicy(date -> {
        LocalDate startdate = datechooser.getDate();
        return startdate == null || !date.isBefore(startdate);
    });
    enddate.setDate(existingEnd); // reapply existing value to avoid strikethrough

    // Listener for end date changes
    enddate.addDateChangeListener(e -> {
        updateControls();
        updateDurationLabel();

        LocalTime endtime = timepicker2.getTime();
        LocalTime starttime = timepicker1.getTime();

        if (starttime != null && endtime != null && endtime.equals(starttime)) {
            // Optional: handle same-time case here if needed
        }
    });

    // Listener for end time changes
    timepicker2.addTimeChangeListener(e -> updateDurationLabel());

    // Initial duration update
    updateDurationLabel();
}

private void updateControls() {
    LocalDate startDate = datechooser.getDate();
    LocalTime startTime = timepicker1.getTime();
    LocalDate endDate = enddate.getDate();

    // Only enable end fields if start exists
    boolean ready = (startDate != null && startTime != null);
    enddate.setEnabled(ready);
    timepicker2.setEnabled(ready);


    if (startDate != null && endDate != null && startTime != null) {
        if (startDate.equals(endDate)) {
            LocalTime minTime = startTime.plusHours(2);
            LocalTime maxTime = LocalTime.of(20, 0);
            LocalTime currentEndTime = timepicker2.getTime();
            
            timepicker2.getSettings().setVetoPolicy(time -> !time.isBefore(minTime) && !time.isAfter(maxTime));
            
            // Clear end time only if outside allowed range
            if (currentEndTime != null && (currentEndTime.isBefore(minTime) || currentEndTime.isAfter(maxTime))) {
                timepicker2.clear();
            }
        } else { 
            // Different day, just office hours
            timepicker2.getSettings().setVetoPolicy(time -> {
                LocalTime start = LocalTime.of(8, 0);
                LocalTime end = LocalTime.of(20, 0);
                return !time.isBefore(start) && !time.isAfter(end);
            });
        }
    }
}



    public void cardata(String carID) {
        
        String sql = "SELECT * FROM car_table WHERE vehicle_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, carID);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                rs.getString("vehicle_id");
                rs.getString("vehicle_brand");
                rs.getString("Plate_num");
                carlbl.setText(rs.getString("vehicle_model"));   
                carId = carID;
            }
        } catch(SQLException e) {
            e.printStackTrace(); 
        }
    
    
    }
    
    private void updateDurationLabel() {
    LocalDate startDate = datechooser.getDate();
    LocalTime startTime = timepicker1.getTime();
    LocalDate endDateValue = enddate.getDate();
    LocalTime endTime = timepicker2.getTime();

    String durationText = getDurationText(startDate, startTime, endDateValue, endTime);
    durationlbl.setText(durationText);
    }
    
    public void userdata(String userID) {
           String sql = "SELECT * " +
            "FROM user_table u" +
            " JOIN user_data d ON u.user_id = d.user_id " +
            "WHERE u.user_id = ?;";

        try (Connection conn = DatabaseConnection.connect()) {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, userID);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                
                Boolean verified = rs.getBoolean("verified");
                if(!verified) {
                    JOptionPane.showMessageDialog(null, "The Selected User must be verified or has a valid Drivers License.");
                   return;
                }
                userId = rs.getInt("user_id");
                userlbl.setText(rs.getString("full_name"));   
            }
        } catch(SQLException e) {
            e.printStackTrace(); 
        }
    }
    
   private void AdminID(int AdminId) {
   String sql = "SELECT role FROM user_table WHERE user_id = ?";
   try(Connection conn = DatabaseConnection.connect()) {
   PreparedStatement ps = conn.prepareStatement(sql);
   ps.setInt(1, AdminId);
   ResultSet rs = ps.executeQuery();
   if (rs.next()) {
        reservation(adminID);
        
    }} catch (SQLException e) { e.printStackTrace();}}
    
    
    private void reservation(int userid) {
            Object value = amountlbl.getValue();
            double amount = (value != null) ? ((Number) value).doubleValue() : 0.0;
            String ReservationSQL = "INSERT INTO reservations_table (user_id, car_id, start_date, end_date, duration_hrs ,status, made_by) VALUES (?, ?, ?, ?, ? , ?,?)";
            String PaymentSQL;
            
            String status;
            if(amount == 0) {
                status = "Pending";
            } else if (amount > totalprice) {
                JOptionPane.showMessageDialog(null, "Amount paid cannot exceed total price.");
                return;
            }
            else if (amount < totalprice) {
                status = "Partial";       
            }
              else {
                status = "Paid";
            }
            
            if (status.equals("Paid")) {
               
                PaymentSQL = "INSERT INTO payment_tbl (reservation_id, reservation_fee, deposit_amt, total_cost, amount_paid, status, fully_paid_date) " +
                             "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            } else {
               
                PaymentSQL = "INSERT INTO payment_tbl (reservation_id, reservation_fee, deposit_amt, total_cost, amount_paid, status) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
            }
            
            
                
            try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
                    
            PreparedStatement ps1 = conn.prepareStatement(ReservationSQL, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, userId);
            ps1.setString(2, carId);
            ps1.setTimestamp(3, Timestamp.valueOf(startDateTime));
            ps1.setTimestamp(4, Timestamp.valueOf(endDateTime));
            ps1.setDouble(5, durations);
            if("Paid".equalsIgnoreCase(status)) { 
            ps1.setString(6, "Confirmed");
            }
            else if("Partial".equalsIgnoreCase(status)) {
            ps1.setString(6, "Pending");
            }
            else {
            ps1.setString(6, "Pending");
            }
            ps1.setInt(7, userid);
            ps1.executeUpdate();
            
            
            ResultSet rs = ps1.getGeneratedKeys();
            int reservationId = 0;
            if (rs.next()) {
                reservationId = rs.getInt(1);
            }
            PreparedStatement ps2 = conn.prepareStatement(PaymentSQL);
            ps2.setInt(1, reservationId);
            ps2.setDouble(2, reservfee);
            ps2.setDouble(3, depositamt);
            ps2.setDouble(4, totalprice);
            if (amount != 0.00) {
                ps2.setDouble(5, amount);
            } else {
                ps2.setDouble(5, 0.00);
            }
            ps2.setString(6, status);
        
            ps2.executeUpdate();


            conn.commit();
            Success dialog = new Success(this,true, "Reservation has been Added!");
            dialog.setVisible(true);
            dispose();
            } 
            catch (SQLException e) {
            e.printStackTrace();
            }
    
    
    }
       
    private void paymentId() {
        String sql = "SELECT * FROM payment_tbl WHERE reservation_id = ?"; 
        try (Connection conn = DatabaseConnection.connect()) {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, reserveId);
        ResultSet rs = ps.executeQuery();
        
        if(rs.next()) {
            payId = rs.getInt("payment_id");
        }
  
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    private void editreserve() {
         Object value = amountlbl.getValue();
         double amount = (value != null) ? ((Number) value).doubleValue() : 0.0;
        String reservesql = "UPDATE reservations_table SET car_id = ?, end_date = ?, duration_hrs = ?, status = ? WHERE reservation_id = ? ";
        String paymentsql;
        
        String status;
            if(amount == 0) {
                status = "Pending";
            } else if (amount > totalprice) {
                JOptionPane.showMessageDialog(null, "Amount paid cannot exceed total price.");
                return;
            }
            else if (amount < totalprice) {
                status = "Partial";       
            }
              else {
                status = "Paid";
            }
            
            if (status.equals("Paid")) {
               
                paymentsql = "UPDATE payment_tbl SET reservation_fee = ?, "
                        + "total_cost = ?, amount_paid = ?, status = ?, fully_paid_date = NOW() "
                        + "WHERE payment_id = ?;";
            } else {
               
                paymentsql = "UPDATE payment_tbl SET reservation_fee = ?, "
                        + "total_cost = ?, amount_paid = ?, status = ? "
                        + "WHERE payment_id = ?;";
            }
        
        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(reservesql);
            
            ps.setString(1, carId);
            ps.setTimestamp(2, Timestamp.valueOf(endDateTime));
            ps.setDouble(3, durations);
            if("Paid".equalsIgnoreCase(status)) { 
            ps.setString(4, "Confirmed");
            }
            else if("Partial".equalsIgnoreCase(status)) {
            ps.setString(4, "Pending");
            }
            else {
            ps.setString(4, "Pending");
            }
            ps.setInt(5, reserveId);
            
            ps.executeUpdate();
            
            PreparedStatement ps1 = conn.prepareStatement(paymentsql);
            ps1.setDouble(1, reservfee);
            ps1.setDouble(2, totalprice);
            if (amount != 0.00) {
                ps1.setDouble(3, amount);
            } else {
                ps1.setDouble(3, 0.00);
            }
            ps1.setString(4, status);
            ps1.setInt(5, payId);
        
            ps1.executeUpdate();
            
            conn.commit();
            Success dialog = new Success(this,true, "Successfully Changed the Reservation.");
            dialog.setVisible(true);
            dispose();
        }
        
        catch (SQLException e) {
           e.printStackTrace();
        try (Connection conn = DatabaseConnection.connect()) {
            conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        }}
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        changebtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        userlbl = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        timepicker1 = new com.github.lgooddatepicker.components.TimePicker();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        durationlbl = new javax.swing.JLabel();
        timepicker2 = new com.github.lgooddatepicker.components.TimePicker();
        datechooser = new com.github.lgooddatepicker.components.DatePicker();
        enddate = new com.github.lgooddatepicker.components.DatePicker();
        jLabel20 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        carbtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        carlbl = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        totallbl = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        costlbl = new javax.swing.JLabel();
        rate12cost = new javax.swing.JLabel();
        rate24cost = new javax.swing.JLabel();
        excesslbl = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        reservlbl = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        depositfield = new javax.swing.JFormattedTextField();
        amountlbl = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        labelrsv = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBackground(new java.awt.Color(181, 245, 194));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel4.setBackground(new java.awt.Color(36, 150, 59));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        changebtn.setText("Change");
        changebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changebtnActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UserIcon.png"))); // NOI18N

        jLabel3.setText("Selected User");
        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N

        userlbl.setText("No user selected");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userlbl)
                            .addComponent(jLabel3))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(changebtn)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(userlbl))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2)))
                        .addGap(0, 19, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(changebtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(255, 221, 158));
        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel12.setBackground(new java.awt.Color(209, 136, 0));

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

        jLabel8.setText("Choose Rental Dates");
        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N

        timepicker1.setEnabled(false);

        jLabel7.setText("Start Date");
        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel10.setText("Return Date ");
        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel27.setText("Rental Duration :");
        jLabel27.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N

        durationlbl.setText("0");
        durationlbl.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N

        timepicker2.setEnabled(false);

        datechooser.setEnabled(false);

        enddate.setEnabled(false);

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download (7).png"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(datechooser, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(enddate, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(timepicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(timepicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(durationlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel20)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel8)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)))
                .addGap(10, 10, 10)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datechooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timepicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timepicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(durationlbl))
                .addGap(21, 21, 21))
        );

        jPanel2.setBackground(new java.awt.Color(161, 198, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel5.setBackground(new java.awt.Color(0, 71, 181));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        carbtn.setText("Change");
        carbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                carbtnActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Carimage.png"))); // NOI18N

        jLabel6.setText("Selected Vehicle");
        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N

        carlbl.setText("No vehicle Selected");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(carlbl)
                            .addComponent(jLabel6)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(carbtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(carlbl))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(carbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(247, 246, 237));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel7.setBackground(new java.awt.Color(204, 0, 51));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel7.setForeground(new java.awt.Color(204, 0, 51));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Total Amount");
        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel11.setText("₱ ");
        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 0, 24)); // NOI18N

        totallbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totallbl.setText("00.00");
        totallbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 24)); // NOI18N

        jLabel13.setText("Reservation Fee");
        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        jLabel14.setText("Deposit Amount");
        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        jLabel15.setText("Rental Cost");
        jLabel15.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        jLabel16.setText("12-hour Rate");
        jLabel16.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        jLabel17.setText("24-hour Rate");
        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        jLabel19.setText("Excess Rate");
        jLabel19.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        costlbl.setText("00.00");
        costlbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        rate12cost.setText("00.00");
        rate12cost.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        rate24cost.setText("00.00");
        rate24cost.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        excesslbl.setText("00.00");
        excesslbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel30.setText("Paid Amount");
        jLabel30.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        jLabel18.setText("(10% of the total rental cost)");
        jLabel18.setFont(new java.awt.Font("Segoe UI", 2, 10)); // NOI18N

        reservlbl.setText("00.00");
        reservlbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel21.setText("(Varies on Vehicle Type)");
        jLabel21.setFont(new java.awt.Font("Segoe UI", 2, 10)); // NOI18N

        jLabel22.setText("(Extra hours x 200)");
        jLabel22.setFont(new java.awt.Font("Segoe UI", 2, 10)); // NOI18N

        depositfield.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00#"))));
        depositfield.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        depositfield.setText("00.00");
        depositfield.setEnabled(false);
        depositfield.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        depositfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depositfieldActionPerformed(evt);
            }
        });

        amountlbl.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00#"))));
        amountlbl.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        amountlbl.setText("00.00");
        amountlbl.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        amountlbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountlblActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(reservlbl))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rate24cost))
                    .addComponent(jSeparator2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rate12cost))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(costlbl))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(excesslbl))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(depositfield, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel21)
                            .addComponent(jLabel18))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(amountlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(totallbl, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)))
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(totallbl))
                .addGap(20, 20, 20)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(reservlbl))
                .addGap(4, 4, 4)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(depositfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(rate12cost))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(rate24cost))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(excesslbl)))
                    .addComponent(costlbl))
                .addGap(5, 5, 5)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(amountlbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jButton1.setText("Cancel");
        jButton1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Save Reservation");
        jButton3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        labelrsv.setText("ADD RESERVATION");
        labelrsv.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(labelrsv)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelrsv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changebtnActionPerformed
        UserSelector dialog = new UserSelector(this, true);
        dialog.setVisible(true);

        String userID = dialog.getSelectedId();

        if (userID != null && !userID.isEmpty()) {
            userdata(userID);
        } else {
            JOptionPane.showMessageDialog(this, "No user was selected.");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_changebtnActionPerformed

    private void carbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_carbtnActionPerformed
        CarDialog dialog = new CarDialog(this, true);
        dialog.setVisible(true);

        String vehicleId = dialog.getSelectedID();

        if (vehicleId != null && !vehicleId.isEmpty()) {
            cardata(vehicleId);
            if (reserveId == null) {
            datechooser.setEnabled(true);
            timepicker1.setEnabled(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No vehicle was selected.");
        }  
        updateDurationLabel();// TODO add your handling code here:
    }//GEN-LAST:event_carbtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        LocalDate startdate = datechooser.getDate();
        LocalTime starttime = timepicker1.getTime();
        LocalDate endDate = enddate.getDate();
        LocalTime endtime = timepicker2.getTime();
        
        
        if (userId == 0) {
            Error dialog = new Error(this,true, "No user selected");
            dialog.setVisible(true);
        }
        else if (carId == null) {
            Error dialog = new Error(this,true, "No vehicle selected");
            dialog.setVisible(true);
        } 
        else if (startdate == null || starttime == null) {
            Error dialog = new Error(this,true, "No Dates Selected");
            dialog.setVisible(true);
        }
        else if (endDate == null || endtime == null) {
            Error dialog = new Error(this,true, "No Return Dates Selected");
            dialog.setVisible(true);
        }
        else {
        startDateTime = LocalDateTime.of(startdate,starttime);
        endDateTime = LocalDateTime.of(endDate,endtime);
        
        if (reserveId != null) {
        editreserve();
        }
        else {
        AdminID(adminID);
        }
        }
                    // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void depositfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depositfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_depositfieldActionPerformed

    private void amountlblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amountlblActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_amountlblActionPerformed

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
          java.awt.EventQueue.invokeLater(() -> {
            Reserve dialog = new Reserve(new java.awt.Frame(), true,null,0);
            dialog.setVisible(true);
       });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField amountlbl;
    private javax.swing.JButton carbtn;
    private javax.swing.JLabel carlbl;
    private javax.swing.JButton changebtn;
    private javax.swing.JLabel costlbl;
    private com.github.lgooddatepicker.components.DatePicker datechooser;
    private javax.swing.JFormattedTextField depositfield;
    private javax.swing.JLabel durationlbl;
    private com.github.lgooddatepicker.components.DatePicker enddate;
    private javax.swing.JLabel excesslbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel labelrsv;
    private javax.swing.JLabel rate12cost;
    private javax.swing.JLabel rate24cost;
    private javax.swing.JLabel reservlbl;
    private com.github.lgooddatepicker.components.TimePicker timepicker1;
    private com.github.lgooddatepicker.components.TimePicker timepicker2;
    private javax.swing.JLabel totallbl;
    private javax.swing.JLabel userlbl;
    // End of variables declaration//GEN-END:variables
}
