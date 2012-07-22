/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package census;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Danylo Vashchilenko
 */
public class DataBaseConverter {

    public static void main(String[] args) {
        try {
            Logger.getLogger(DataBaseConverter.class.getName()).info("Connecting...");
            getSourceConnection();
            getTargetConnection();

            processItems();
            processTimeRanges();
            processSubscriptions();

            processAdSources();
            processClients();

            processKeys();
            processAttendances();
            processPayments();

        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        closeConnection();

        System.exit(0);
    }

    private static void processClients() throws SQLException {

        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying clients...");

        ids = executeQuery("SELECT DISTINCT `id` FROM `clients`");

        // skips the first client - it's a dummy record
        ids.first();

        while (ids.next()) {
            Short id = ids.getShort("id");

            result = executeQuery("SELECT * FROM `clients` WHERE `id` = " + id);
            result.first();

            String fullName = result.getString("full_name");
            Long card = result.getLong("card");

            if (card == 0) {
                card = null;
            }

            Date registrationDate = result.getDate("registration_date");
            Date expirationDate = result.getDate("expiration_date");
            Short attendancesBalance = result.getShort("attendances_balance");
            String note = result.getString("note");

            executeUpdate("INSERT INTO  `client_cln` (`id_cln` ,`card` ,`full_name` ,`registration_date` ,`money_balance` ,`attendances_balance` ,`expiration_date` ,`note`)"
                    + " VALUES ('" + id + "'," + card + ",'" + fullName + "','" + registrationDate + "','" + "0.00" + "','" + attendancesBalance + "','" + expirationDate + "','" + note + "')");

            Short sex = result.getShort("sex");
            Date bithday = result.getDate("birthday");
            String address = result.getString("address");
            String telephone = result.getString("telephone");
            String goal = result.getString("goal");
            String possibleAttendanceRate = result.getString("possible_attendances_rate");
            String healthRestrictions = result.getString("health_restrictions");
            String favouriteSport = result.getString("favourite_sport");
            Short fitnessExperience = result.getShort("fitness_experience");

            // the 'no' and 'unknown' values are swaped
            if (fitnessExperience == 0) {
                fitnessExperience = 2;
            } else if (fitnessExperience == 2) {
                fitnessExperience = 0;
            }

            String specialWishes = result.getString("special_wishes");
            Short height = result.getShort("height");
            Short weight = result.getShort("weight");
            Short adSourceId = result.getShort("ad_source_id");
            adSourceId++; // adjust for the 'unknown' ad source at the beginning of the ad sources table

            executeUpdate("INSERT INTO  `client_profile_cpf` (`idcln_cpf` ,`sex` ,`birthday` ,`address` ,`telephone` ,`goal` ,`possible_attendance_rate` ,`health_restrictions` ,`favourite_sport` ,`fitness_experience` ,`special_wishes` ,`height` ,`weight` ,`idads_cpf`)"
                    + "VALUES ('" + id + "',  '" + sex + "', '" + new SimpleDateFormat("yyyy-MM-dd").format(bithday) + "',  '" + address + "',  '" + telephone + "',  '" + goal + "',  '" + possibleAttendanceRate + "',  '" + healthRestrictions + "',  '" + favouriteSport + "',  '" + fitnessExperience + "',  '" + specialWishes + "',  '" + height + "',  '" + weight + "',  '" + adSourceId + "');");

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " clients!");
    }

    private static void processAdSources() throws SQLException {
        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying ad sources...");

        // the ad sources with the id = 1 will be used by processClients for unknown ad sources
        executeUpdate("INSERT INTO `ad_source_ads` (`id_ads`, `title`) VALUES(1, 'Неизвестный')");

        ids = executeQuery("SELECT DISTINCT `id` FROM `ad_sources`");

        while (ids.next()) {
            Short id = ids.getShort("id");

            result = executeQuery("SELECT * FROM `ad_sources` WHERE `id` = " + id);
            result.first();

            String title = result.getString("title");
            id++; // adjust for the 'unknown' record at the beginning

            executeUpdate("INSERT INTO `ad_source_ads` (`id_ads` ,`title`)"
                    + " VALUES ('" + id + "','" + title + "')");

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " ad sources!");
    }

    private static void processKeys() throws SQLException {
        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying keys...");

        ids = executeQuery("SELECT DISTINCT `id` FROM `keys`");

        while (ids.next()) {
            Short id = ids.getShort("id");

            result = executeQuery("SELECT * FROM `keys` WHERE `id` = " + id);
            result.first();

            String title = result.getString("title");

            executeUpdate("INSERT INTO `key_key` (`id_key` ,`title`)"
                    + " VALUES ('" + id + "','" + title + "')");

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " keys!");
    }

    private static void processAttendances() throws SQLException {
        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying attendances...");

        ids = executeQuery("SELECT DISTINCT `id` FROM `attendances`");

        while (ids.next()) {
            Short id = ids.getShort("id");

            result = executeQuery("SELECT * FROM `attendances` WHERE `id` = " + id);
            result.first();

            Short clientId = result.getShort("client_id");

            // dummy records
            if (clientId == 1) {
                continue;
            }

            if (clientId == 0) {
                clientId = null;
            }

            Timestamp begin = result.getTimestamp("datetime_begin");
            Timestamp end = result.getTimestamp("datetime_end");
            Short keyId = result.getShort("key_id");

            if (keyId > 30) {
                keyId = 1;
            }

            executeUpdate("INSERT INTO `attendance_atd` (`id_atd` ,`datetime_begin`, `datetime_end`, `idkey_atd`, `idcln_atd`)"
                    + " VALUES ('" + id + "','" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(begin) + "','" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end) + "','" + keyId + "'," + clientId + ")");

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " attendances!");
    }

    private static void processItems() throws SQLException {
        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying items...");

        ids = executeQuery("SELECT DISTINCT `id` FROM `items`");

        while (ids.next()) {
            Short id = ids.getShort("id");

            result = executeQuery("SELECT * FROM `items` WHERE `id` = " + id);
            result.first();

            String title = result.getString("title");
            Long barcode = result.getLong("barcode");

            if (barcode == 0) {
                barcode = null;
            }

            Short quantity = result.getShort("quantity");

            if (quantity == 255) {
                quantity = null;
            }

            BigDecimal price = result.getBigDecimal("price");


            executeUpdate("INSERT INTO `item_itm` (`id_itm` ,`title`, `barcode`, `quantity`, `price`)"
                    + " VALUES ('" + id + "','" + title + "'," + barcode + "," + quantity + ",'" + price.toPlainString() + "')");

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " items!");
    }

    private static void processTimeRanges() throws SQLException {
        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying time ranges...");

        ids = executeQuery("SELECT DISTINCT `id` FROM `time_ranges`");

        while (ids.next()) {
            Short id = ids.getShort("id");

            result = executeQuery("SELECT * FROM `time_ranges` WHERE `id` = " + id);
            result.first();

            Time begin = result.getTime("time_begin");
            Time end = result.getTime("time_end");

            executeUpdate("INSERT INTO `time_range_tmr` (`id_tmr` ,`time_begin`, `time_end`)"
                    + " VALUES ('" + id + "','" + new SimpleDateFormat("HH:mm:ss").format(begin) + "','" + new SimpleDateFormat("HH:mm:ss").format(end) + "')");

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " time ranges!");
    }

    private static void processSubscriptions() throws SQLException {
        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying subscriptions...");

        ids = executeQuery("SELECT DISTINCT `item_id` FROM `item_subscription`");

        while (ids.next()) {
            Short id = ids.getShort("item_id");

            result = executeQuery("SELECT * FROM `item_subscription` WHERE `item_id` = " + id);
            result.first();

            Short units = result.getShort("units");
            Short termDays = result.getShort("term_days");
            Short termMonths = result.getShort("term_monthes");
            Short termYears = result.getShort("term_years");
            Short timeRangeId = result.getShort("time_range_id");

            executeUpdate("INSERT INTO `item_subscription_its` (`iditm_its` ,`units`, `term_days`, `term_months`, `term_years`, `idtmr_its`)"
                    + " VALUES ('" + id + "','" + units + "','" + termDays + "','" + termMonths + "','" + termYears + "','" + timeRangeId + "')");

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " subscriptions!");
    }

    private static void processPayments() throws SQLException {

        ResultSet ids;
        ResultSet result;
        int counter = 0;

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copying payments...");

        ids = executeQuery("SELECT DISTINCT `id` FROM `payments`");

        while (ids.next()) {
            Short id = ids.getShort("id");

            result = executeQuery("SELECT * FROM `payments` WHERE `id` = " + id);
            result.first();

            Short clientId = result.getShort("client_id");

            // dummy records
            if (clientId == 1) {
                continue;
            }

            if (clientId == 0) {
                clientId = null;
            }

            Date date = result.getTimestamp("date");
            BigDecimal amount = new BigDecimal(result.getInt("amount"));
            
            if(amount.compareTo(new BigDecimal(1000)) >= 0) {
                amount = new BigDecimal(200);
            } else if(amount.compareTo(new BigDecimal(-1000)) <= 0) {
                amount = new BigDecimal(-200);
            }
            
            Short attendanceId = null;

            if (clientId == null) {
                ResultSet subresult = executeQuery("SELECT * FROM `payment_anonymous_attendance` WHERE `payment_id` = '" + id + "'");

                if(subresult.first()) {
                    attendanceId = subresult.getShort("attendance_id");

                    if(attendanceId == 0) {
                        attendanceId = null;
                    }
                }
            }

            executeUpdate("INSERT INTO `financial_activity_fna` (`id_fna` ,`date_recorded`, `idcln_fna`, `idatd_fna`, `payment`)"
                    + " VALUES ('" + id + "','" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "'," + clientId + "," + attendanceId + ",'" + amount.toPlainString() + "')");

            ResultSet subresult = executeQuery("SELECT * FROM `payment_item` WHERE `payment_id` = " + id);

            while (subresult.next()) {
                Short itemId = subresult.getShort("item_id");

                executeUpdate("INSERT INTO `financial_activity_purchase_fnp` (`idfna_fnp`, `iditm_fnp`) VALUES('" + id + "','" + itemId + "')");
            }

            counter++;
        }

        Logger.getLogger(DataBaseConverter.class.getName()).info("Copied " + counter + " payments!");
    }

    public static Connection getSourceConnection() throws SQLException {
        if (sourceConnection != null) {
            return sourceConnection;
        }

        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "test");
        connectionProps.put("characterEncoding", "UTF-8");

        sourceConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/census_old", connectionProps);
        return sourceConnection;
    }

    public static Connection getTargetConnection() throws SQLException {
        if (targetConnection != null) {
            return targetConnection;
        }

        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "test");
        connectionProps.put("characterEncoding", "UTF-8");

        targetConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/census", connectionProps);
        return targetConnection;
    }

    public static ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = getSourceConnection().createStatement();
        try {
            return stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConverter.class.getName()).info(query);
            throw ex;
        }
    }

    public static int executeUpdate(String query) throws SQLException {
        try {
            Statement statement = null; // statement
            statement = getTargetConnection().createStatement();
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConverter.class.getName()).info(query);
            throw ex;
        }
        return 0;
    }

    public static boolean closeConnection() {
        try {
            targetConnection.close();
            sourceConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    private static Connection sourceConnection;
    private static Connection targetConnection; // hash
}
