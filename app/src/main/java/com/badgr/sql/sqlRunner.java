package com.badgr.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


import com.badgr.scoutClasses.*;

import Fragments.ScoutFrags.MyListDrivers.MyListExpandListAdapter;
import Fragments.ScoutFrags.SearchFragmentDrivers.SearchExpandListAdapter;

public class sqlRunner {
    //divider strings are easier to call than type out
    private final static String strDivider = "', '";
    private final static String intDivider = ", ";

    //sql connection strings
    private final static String url = "jdbc:mysql://192.168.1.25:3306/users?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false&allowMultiQueries=true&connectRetryInterval=1";
    private final static String username = "AppRunner";
    private final static String password = "AppRunner1";

    //error variables
    private static boolean authSuccess = false, registerSuccess = false, userInDB = false;



    public static void addUser(scoutPerson p) {

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            Statement stmt = c.createStatement();
            String addStmt = "INSERT INTO userpass VALUES (userPassID, '" + p.getPass() + "'); " +
                    "INSERT INTO users VALUES (userID, '" + p.getFName() + strDivider + p.getLName() + strDivider + p.getUser() + "', last_insert_id(), " +
                    p.getAge() + intDivider + p.isSM() + intDivider + p.getTroop() + "); " +
                    "INSERT INTO troop VALUES (" + p.getTroop() + ", last_insert_id(), " + p.isSM() + ");";
            stmt.executeUpdate(addStmt);
            registerSuccess = true;
        } catch (SQLException e) {
            e.printStackTrace();
            registerSuccess = false;
        }

    }

    public static ArrayList<String> getUserInfo(String givenU) {
        ArrayList<String> retList = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT * FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            rs.first();

            int id = rs.getInt("userID");
            retList.add(String.valueOf(id));
            retList.add(rs.getString("firstName"));
            retList.add(rs.getString("lastName"));
            retList.add(rs.getString("username"));
            int uPID = rs.getInt("userPassID");
            retList.add(String.valueOf(uPID));
            int age = rs.getInt("age");
            retList.add(String.valueOf(age));
            int isSM = rs.getInt("isScoutmaster");
            retList.add(String.valueOf(isSM));
            int troop = rs.getInt("troop");
            retList.add(String.valueOf(troop));


        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static void authUser(String givenU, String givenP) {
        authSuccess = false;

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT userPassID FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);


            boolean isFirst;
            int uPID;

            //sets cursor at beginning of resultSet
            isFirst = rs.first();
            if (isFirst) {
                //if cursor is at beginning, get the user's userPassID
                uPID = rs.getInt(1);
            }
            else {
                //if not (or there's no data), success set to false
                //kills method
                return;
            }


            //just in case userPassID didn't update, kills method
            if (uPID < 0) {
                return;
            }


            String pass;

            //selects password from userpass database with the userPassID found from the username
            rs = stmt.executeQuery("SELECT pass FROM userpass WHERE userPassID = " + uPID + ";");
            //sets cursor at beginning, if error then success = false and kill method
            if (!rs.first()) {
                return;
            }
            else {
                //sets the pass string to the database password
                pass = rs.getString(1);
            }

            //sets the successful login if the username in database = username given, and same for password
            authSuccess = pass.equals(givenP); //TODO and username stuff here


            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static boolean isUserInDatabase(String givenU) {
        userInDB = false;
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds userPassID with the given username
            String ex = "SELECT userPassID FROM users WHERE username = '" + givenU + "'";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE);

            //pauses just a second to let database do its thing
            TimeUnit.SECONDS.sleep(1);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            //moves the cursor to the first person in the database and sets global variable userInDB true if username exists
            //if the user does not exists, then rs.first() is false so userInDB will be set to false
            userInDB = rs.first();

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            userInDB = false;
        }
        return userInDB;
    }

    public static ArrayList<meritBadge> getListOfBadges(String bName) {
        ArrayList<meritBadge> retList = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM badgetable where badgeName LIKE '%" + bName + "%';";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            while (rs.next())
            {
                meritBadge mb = new meritBadge();
                mb.setName(rs.getString("badgeName"));
                mb.setEagle(rs.getInt("isEagleReq") == 1);
                mb.setNumReqs(rs.getInt("numReqs"));
                mb.setId(rs.getInt("BadgeTableID"));
                retList.add(mb);
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static meritBadge getBadge(int badgeID) {
        meritBadge mb = new meritBadge();
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM badgetable where BadgeTableID = " + badgeID + ";";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);

            while (rs.next())
            {
                mb.setName(rs.getString("badgeName"));
                mb.setEagle(rs.getInt("isEagleReq") == 1);
                mb.setNumReqs(rs.getInt("numReqs"));
                mb.setId(rs.getInt("BadgeTableID"));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

        return mb;
    }

    public static ArrayList<meritBadge> getListOfBadges(scoutPerson p) {
        ArrayList<meritBadge> retList = new ArrayList<>();
        ArrayList<Integer> addedBadges = addedBadges(p);
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            for (int i : addedBadges) {
                String ex = "SELECT * FROM badgetable where BadgeTableID = " + i + ";";
                Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


                //gets results from database
                ResultSet rs = stmt.executeQuery(ex);

                while (rs.next()) {
                    meritBadge mb = new meritBadge();
                    mb.setName(rs.getString("badgeName"));
                    mb.setEagle(rs.getInt("isEagleReq") == 1);
                    mb.setNumReqs(rs.getInt("numReqs"));
                    mb.setId(rs.getInt("BadgeTableID"));
                    retList.add(mb);
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

        return retList;
    }

    public static void toggleAddToList(scoutPerson p, int badgeID, boolean add) {
        ArrayList<Integer> addedBadges = new ArrayList<>();
        meritBadge badge = getBadge(badgeID);
        try (Connection c = DriverManager.getConnection(url, username, password)) {
            String addOrRemoveBadge;
            Statement stmtAddOrRemove = c.createStatement();

            //Finds badge names with the given ID
            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next())
            {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                addedBadges.add(badgeIDAdded);
            }


            //if we should add merit badge...
            if (add)
            {
                //and it doesn't exist
                if (!addedBadges.contains(badgeID)) {
                    //add to table
                    addOrRemoveBadge = "INSERT INTO userbadges VALUES (" + badgeID + ", " + p.getUserID() + ", 0);";
                    stmtAddOrRemove.executeUpdate(addOrRemoveBadge);

                    //add requirements to list
                    for (int i = 1; i <= Objects.requireNonNull(badge).getNumReqs(); i++)
                    {
                        String addReq = "INSERT INTO userreq VALUES (" + p.getUserID() + ", " + badgeID + ", " + i + ", 0);";
                        stmtAddOrRemove.executeUpdate(addReq);
                    }
                }

            }
            //if we shouldn't add to table
            else
            {
                //and it's in the table
                if (addedBadges.contains(badgeID))
                {
                    //remove the merit badge
                    addOrRemoveBadge = "DELETE FROM userbadges WHERE BadgeTableID = " + badgeID + ";";
                    stmtAddOrRemove.executeUpdate(addOrRemoveBadge);

                    String addReq = "DELETE FROM userreq WHERE BadgeTableID = " + badgeID + " AND userID = " + p.getUserID() + ";";
                    stmtAddOrRemove.executeUpdate(addReq);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //update checked boxes for when the children of the expandable list are created
        SearchExpandListAdapter.pullAddedBadges(p);
    }

    public static void toggleAddToReqList(scoutPerson p, HashMap<Integer, ArrayList<Integer>> addReqsMap, HashMap<Integer, ArrayList<Integer>> delReqsMap) {
        ArrayList<Integer> addedReqList = new ArrayList<>();
        ArrayList<Integer> delReqList = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : addReqsMap.entrySet()) {
                //Finds badge names with the given ID

                int badgeID = entry.getKey();
                ArrayList<Integer> requirements = entry.getValue();
                for (int reqNum : requirements) {
                    String updateReq;
                    Statement stmtAddOrRemove = c.createStatement();
                    String ex = "SELECT * FROM userreq WHERE BadgeTableID = " + badgeID + ";";
                    Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

                    //gets results from database
                    ResultSet rs = stmt.executeQuery(ex);
                    while (rs.next()) {
                        int badgeReqNum = rs.getInt("reqNum");
                        int isCompleted = rs.getInt("isCompleted");
                        if (isCompleted == 1) addedReqList.add(badgeReqNum);
                    }


                    //if the requirement we are adding isn't already in the database
                    if (!addedReqList.contains(reqNum)) {
                        //add to table
                        updateReq = "UPDATE userreq SET isCompleted = 1 WHERE userID = " + p.getUserID() + " AND BadgeTableID = " + badgeID + " AND reqNum = " + reqNum + ";";
                        stmtAddOrRemove.executeUpdate(updateReq);
                    }
                    addedReqList.clear();
                }
            }

            for (Map.Entry<Integer, ArrayList<Integer>> entryDel : delReqsMap.entrySet()) {
                //Finds badge names with the given ID

                int badgeIDDel = entryDel.getKey();
                ArrayList<Integer> requirementsDel = entryDel.getValue();
                for (int reqNum : requirementsDel) {
                    String updateReq;
                    Statement stmtAddOrRemove = c.createStatement();
                    String ex = "SELECT * FROM userreq WHERE BadgeTableID = " + badgeIDDel + ";";
                    Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

                    //gets results from database
                    ResultSet rs = stmt.executeQuery(ex);
                    while (rs.next()) {
                        int badgeReqNum = rs.getInt("reqNum");
                        int isCompleted = rs.getInt("isCompleted");
                        if (isCompleted == 1) delReqList.add(badgeReqNum);
                    }


                    if (delReqList.contains(reqNum)) {
                        //remove the merit badge
                        updateReq = "UPDATE userreq SET isCompleted = 0 WHERE userID = " + p.getUserID() + " AND BadgeTableID = " + badgeIDDel + " AND reqNum = " + reqNum + ";";
                        stmtAddOrRemove.executeUpdate(updateReq);
                    }
                }

                delReqList.clear();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Integer> addedBadges(scoutPerson p) {
        ArrayList<Integer> added = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next())
            {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                added.add(badgeIDAdded);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return added;
    }

    public static ArrayList<Integer> finishedBadges(scoutPerson p) {
        ArrayList<Integer> completed = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT * FROM userbadges WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next())
            {
                int badgeIDAdded = rs.getInt("BadgeTableID");
                completed.add(badgeIDAdded);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completed;
    }

    public static HashMap<Integer, HashMap<Integer, String>> getReqs()
    {
        HashMap<Integer, HashMap<Integer, String>> retList = new HashMap<>();


        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name

            String ex = "SELECT * FROM badgereqs;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);


            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next())
            {
                int badgeNum = rs.getInt("BadgeTableID");
                int reqNum = rs.getInt("reqNum");
                String requirement = rs.getString("reqDesc");
                if (retList.get(badgeNum) == null)
                    retList.put(badgeNum, new HashMap<Integer, String>(){{put(reqNum, requirement);}});
                else
                {
                    HashMap<Integer, String> badgeMap = retList.get(badgeNum);
                    if (badgeMap == null) retList.put(badgeNum, new HashMap<Integer, String>(){{put(reqNum, requirement);}});
                    else badgeMap.put(reqNum, requirement);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retList;
    }

    public static HashMap<Integer, ArrayList<Integer>> finishedReqs(scoutPerson p)
    {
        HashMap<Integer, ArrayList<Integer>> compReqs = new HashMap<>();
        ArrayList<Integer> reqsPerBadge = new ArrayList<>();
        int badgeID = 0;
        int reqNum;
        int lastReqNum = 0;
        int lastBadgeID = -1;
        int iteration = 0;


        try (Connection c = DriverManager.getConnection(url, username, password)) {
            //Finds badge names with the given name and completed

            String ex = "SELECT * FROM userReq WHERE userID = " + p.getUserID() + " AND isCompleted = TRUE ORDER BY BadgeTableID;";
            Statement stmt = c.createStatement(ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);

            //gets results from database
            ResultSet rs = stmt.executeQuery(ex);
            while (rs.next())
            {
                badgeID = rs.getInt("BadgeTableID");
                reqNum = rs.getInt("reqNum");

                if (badgeID != lastBadgeID && iteration != 0)
                {

                    compReqs.put(lastBadgeID, new ArrayList<>(reqsPerBadge));
                    reqsPerBadge.clear();

                    lastBadgeID = badgeID;
                    lastReqNum = reqNum;
                }
                else
                {
                    reqsPerBadge.add(reqNum);
                    lastBadgeID = badgeID;
                    iteration++;
                }
            }

            if (badgeID != 0)
            {
                reqsPerBadge.add(lastReqNum);
                compReqs.put(badgeID, new ArrayList<>(reqsPerBadge));
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }


        MyListExpandListAdapter.pullFinishedReqs(p);
        return compReqs;
    }


    public static boolean getAuthSuccess() {return authSuccess;}

    public static boolean getRegisterSuccess() { return registerSuccess; }

    public static boolean getUserInDB() { return userInDB; }
}
