/**
* @file csci3060_team_surprised_pikachu\Version 1.0\Controller.java
* @author  Stephanie Phung
* @author  Abhiram Sinnarajah
* @author  Aaron Williams
* @date 9 Mar 2019
* @version 1.3
* @brief Runs the main program logic of the back end.
*/
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class Controller {
    private final String KEY_END = "END";

    // member variables
    private String userFile;
    private String ticketFile;
    private ArrayList<User> users;
    private ArrayList<Event> events;

    public Controller(String userFile, String ticketFile) {
      this.userFile = userFile;
      this.ticketFile = ticketFile;
      parseUsers();
      parseEvents();
    }

    /**
     * Parses the current user accounts file and returns an ArrayList
     * containing all the users.
     */
    private void parseUsers() {
        users = new ArrayList<>();

        // Try opening the file.
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            // While the file has a next line, create a new user.
            while (((line = br.readLine()) != null) && !line.trim().equals(KEY_END)) {
                User user = new User(line);
                // If the username is valid, then add it to the list
                users.add(user);
            }
            br.close();
        } catch (IOException e) { // Print error if exists.
            e.printStackTrace();
        }
    }

    /**
     * Parses the tickets file and returns an ArrayList
     * containing all the events.
     */
    private void parseEvents() {
        events = new ArrayList<>();

        // Try opening the file.
        try (BufferedReader br = new BufferedReader(new FileReader(ticketFile))) {
            String line;
            // While the file has a next line, create a new event.
            while (((line = br.readLine()) != null) && !line.trim().equals(KEY_END)) {
                Event event = new Event(line);
                // If the event name is valid, then add it to the list.
                events.add(event);
            }
            br.close();
        } catch (IOException e) { // Print error if exists.
            e.printStackTrace();
        }
    }

    /**
     * Creates a user and adds it to the ArrayList.
     *
     * @param trn transaction string containing the user data.
     * @param users the array list to add to.
     */
    public void createUser(String trn) {
        User user = new User(trn);
        // TODO: check that there are no duplicate users
        users.add(user);
    }

    /**
     * Deletes the object that matches the given transaction string.
     *
     * @param trn transaction string containing data to delete.
     * @param users search through this array list and delete the right object.
     */
    public void deleteUser(String trn) {
        // Go through the list.
        for (int i = 0; i < users.size(); i++) {
            // If the data matches, remove it.
            // TODO: maybe change the qualifier to identical username
            if (trn.equals(users.get(i).toTRN())) {
                users.remove(i);
            }
        }
    }

    /**
     * Creates an event and adds it to the ArrayList.
     *
     * @param trn transaction string containing the event data.
     */
    public void createEvent(String trn) {
        Event event = new Event(trn);
        events.add(event);
    }

    /**
     * Deletes the object that matches the given transaction string.
     *
     * @param trn transaction string containing data to delete.
     */
    public void deleteEvent(String trn) {
        // Go through the list.
        for (int i = 0; i < events.size(); i++) {
            // If the data matches, remove it.
            if (trn.equals(events.get(i).toTRN())) {
                  events.remove(i);
            }
        }
    }
    // TODO: fix above same as user

    // if true write to user file, if false, write to tickets file
    private void write(boolean isUserFile) {
      try {
        File file = new File(ticketFile);
        if (isUserFile) {
          file = new File(userFile);
        }
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        if (isUserFile) {
          for (User u : users) {
            bw.write(u.toTRN() + "\n");
          }
          bw.write("END                         ");
        } else {
          for (Event e : events) {
            bw.write(e.toTRN() + "\n");
          }
          bw.write("END                                                 ");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // save to both tickets and users file
    public void save() {
      write(true);
      write(false);
    }

    /**
    * @brief To update a current user's available credit based on the specified amount indicated by the
             transaction string of a current user.
    * @param [in] trn    A string representation of a transaction from the merged Daily transaction File (DTF)
    */
    public void addCredit(String trn) {
      // method to add credit to a user based on the information provided from the array list and transaction
      if (trn.length() != 31) {
        System.err.println("Transaction length is incorrect");
        System.exit(0);
      } else {
        String username = trn.substring(3, 18);
        String credit = trn.substring(22, 31);

        for (int i=0; i<users.size(); i++) {
          if (username.equals(users.get(i).paddUsername())) {
            double newCredit = users.get(i).getCredit() + Double.parseDouble(credit);
            // check for overflow issue
            if (newCredit > 999999.99) {
               System.out.println("Constraint Error: Overflow");
            } else {
               users.get(i).setCredit(newCredit);
            }
            return;
          }
        }
      }
    }

    //TODO: Fix comment for this method
    /**
    * @brief To update a buyer's available credit and seller's available credit based on the specified amount
             indiciated by the transaction string of a current user.
    * @param [in] trn    A string representation of a transaction from the merged Daily transaction File (DTF)
                  users  An ArrayList object that contains all the user's on the Current User Accounts File
    * @return void
    */
    public void refundCredit(String increase, String decrease, String credit) {
      // method to refund credit to a user based on the information provided from the array list and transaction
      // here increase represents the buyer () and decrease represents seller
      boolean checkIncrease = true;
      int increaseLoc = 0;
      boolean checkDecrease =  true;
      int decreaseLoc = 0;

      // find the matching user object from list
        for (int i=0; i<users.size(); i++) {
          if (checkIncrease) {
            if (increase.equals(users.get(i).paddUsername())) {
              increaseLoc = i;
              checkIncrease = false;
            }
          }
          if (checkDecrease) {
            if (decrease.equals(users.get(i).paddUsername())) {
              decreaseLoc = i;
              checkDecrease = false;
            }
          }
        }
        // check for overflow errors and then proceed
        if (((users.get(increaseLoc).getCredit() + Double.parseDouble(credit)) <= 999999.99) &&
          ((users.get(decreaseLoc).getCredit() - Double.parseDouble(credit)) >= 0.0)) {
          users.get(increaseLoc).setCredit(users.get(increaseLoc).getCredit() + Double.parseDouble(credit));
          users.get(decreaseLoc).setCredit(users.get(decreaseLoc).getCredit() - Double.parseDouble(credit));
        } else {
          System.out.println("Constraint Error: Overflow");
        }
      }

    // TODO: Fix comment for this method
    /**
    * @brief To update the buyer's available credit and seller's available credit based on the specified amount
             of tickets purchased and also decrement the number of available tickets for that event.
    * @param [in] trn    A string representation of a transaction from the merged Daily transaction File (DTF)
                  users  An ArrayList object that contains all the user's on the Current User Accounts File
                  events An ArrayList object that contains all the events on the available tickets File
    * @return void
    */
    public static String buyTrnString(String buyerString, String logoutString) {
      // assume only one transaction line for now
      //String originalTrn = buyerString.substring(3, 55);

      // create a loop to be able to handle more than one buy

      String buyer = logoutString.substring(3, 18);
      String buyer_avail_credit = logoutString.substring(22, 31);
      String seller = buyerString.substring(29, 44);
      String event = buyerString.substring(03, 28);
      String tickets = buyerString.substring(45, 48);
      String price = buyerString.substring(49, 55);

      int numTickets = Integer.parseInt(buyerString.substring(45, 48));
      double ticketPrice = Double.parseDouble(buyerString.substring(49, 55));
      double total = ticketPrice * numTickets;
      String amount = "";
      amount = String.format("%.2f", total);

      // return workable string
      return "04"+"\n"+buyer+"\n"+seller+"\n"+amount+"\n"+tickets+"\n"+event+"\n"
        + buyer_avail_credit+"\n"+price;
    }

    // TODO: Fix comment for this method
    /**
    * @brief Applies the changes based on the id from each transaction line. Invokes necessary methods for each matched
             possble actions (create, delete, buy, sell, addcredit and refund).
    * @param [in] id     Identifies each transaction line based on either types of transactions.
    * @return void
    */
    public void enforceRules(String trn) {
      // identify which action to take
      String id = trn.substring(0, 2);
      if (id != "00") {
        switch (id) {
          case "01":
            String cre_Harness_Trn = trn.substring(3, 31);
            System.out.println("Applying steps for create...");
            createUser(cre_Harness_Trn);
            System.out.println("Complete");
            break;

          case "02":
            String del_Harness_Trn = trn.substring(3, 31);
            System.out.println("Applying steps for delete!");
            deleteUser(del_Harness_Trn);
            System.out.println("Complete");
            break;

          case "03":
            String sel_Harness_Trn = trn.substring(3, 55);
            System.out.println("Applying steps for sell!");
            createEvent(sel_Harness_Trn);
            System.out.println("Complete");
            break;

          // special case ** for purchase transactions
          case "04":
            System.out.println("Applying steps for buy!");
            String[] lines = trn.split("\n");
            String b_Buyer = lines[1];
            String b_Seller = lines[2];
            String b_Total = lines[3];  // the total cost for all the tickets
            String b_NumTickets = lines[4];
            String b_Event = lines[5];
            String b_available = lines[6]; // buyer available credit
            String b_TickPrice = lines[7];
            boolean rejectTransaction = false;

            // update available tickets
            for (int i=0; i<events.size(); i++) {
              if (b_Event.equals(events.get(i).paddEventName()) && b_Seller.equals(events.get(i).paddEventSeller())) {
                double user_avail = Double.parseDouble(b_available);
                int buyable_Tickets = 0;
                double currentPrice = 0.0;
                int inventory = events.get(i).getTickets(); // inventory in tickets file for this event

                // if unable to purchase all check to purchase some tickets
                if (Double.parseDouble(b_available) < Double.parseDouble(b_Total)) {
                  for (int j=1; j<=(Integer.parseInt(b_NumTickets)); j++) {
                    currentPrice = j*(Double.parseDouble(b_TickPrice));
                    if (Double.parseDouble(b_available) < currentPrice) {
                      if (buyable_Tickets == 0) {
                        rejectTransaction = true;
                      }
                      break;
                    } else {
                      // check for inventory here
                      buyable_Tickets = j;
                      if (buyable_Tickets == inventory) {
                        break;
                      }
                    }
                  }
                  // update total price to new value
                  b_Total = String.format("%.2f", buyable_Tickets * (Double.parseDouble(b_TickPrice)));
                } else {
                  // implement check here for events neg available
                  buyable_Tickets = Integer.parseInt(b_NumTickets);
                  if (buyable_Tickets > inventory) {
                    // we prioritize inventory
                    buyable_Tickets = inventory;
                    // calculate new cost for the inventory based num of tickets
                    b_Total = String.format("%.2f", buyable_Tickets * (Double.parseDouble(b_TickPrice)));
                  }
                }

                // check here if buyer and seller will overflow or underflow
                // first find sellers availablet ticket
                double sellerAvailable = 0.0;
                for (int u=0; u<users.size(); u++) {
                  if (events.get(i).getSeller().equals(users.get(u).getName())) {
                      sellerAvailable = users.get(u).getCredit();
                  }
                }
                // test the values below **
                if ((Double.parseDouble(b_available) - Double.parseDouble(b_Total)) >= 0 && (sellerAvailable + Double.parseDouble(b_Total)) <= 999999.99) {
                  events.get(i).updateTickets(events.get(i).getTickets() - buyable_Tickets);      // were generating number of tickets for refund here and total price here
                  // remove available ticket if # of tickets == 0
                  if (events.get(i).getTickets() == 0) {
                    // passing in original transaction to remove the event from available ticket
                    System.out.println("Tickts for " + events.get(i).getName() + " are all sold out!!");
                    deleteEvent(b_Event + " " + b_Seller + " " + events.get(i).paddEventTicket() + " " + b_TickPrice);
                  }
                }
              }
            }
            if (rejectTransaction) {
              System.out.println("Constraint Error: Unable to purchase due to lack of available funds");
            } else {
                refundCredit(b_Seller, b_Buyer, b_Total); // update credit from buy transaction using refund method
            }
            System.out.println("Complete");
            break;

          case "05":
            System.out.println("Applying steps for refund!");
            String buyer = trn.substring(3, 18);
            String seller = trn.substring(19, 34);
            String credit = trn.substring(35, 44);
            refundCredit(buyer, seller, credit);
            System.out.println("Complete");
            break;

          case "06":
            System.out.println("Applying steps for addcredit!");
            addCredit(trn);
            System.out.println("Complete");
            break;
        }
      }
    }

      /**
    * @brief This is the main program logic. Also ensures standard saftey checks are met and also calls all necessary
             functions that result in modification of two files (user accounts & available tickets).
    * @param [in] args   Contains the supplied command-line-arguments, if any as an array of String objects.
    * @return void
    */
    public static void main(String[] args) {
      // TODO: read in filenames from command line
      // if (args.length < 2/4?) {
      //
      // } else {
      //  String blah = args[0];
      //     args[1];
      //     ..etc
      // }
      Controller c1 = new Controller("current_user_accounts.data", "available_tickets.data");

      //** step one: read in the mergedDTF fileName **
      try { File mergedDTF = new File("merged_DTF.data");
        FileReader dtf_fr = new FileReader(mergedDTF);
        BufferedReader dtf_br = new BufferedReader(dtf_fr);
        String trn_line;
        // three variables below used for buy transaction specifically
        boolean sameDTF = false;
        String buy_Trn = "";
        String logout_Trn = "";
        while ((trn_line = dtf_br.readLine()) != null) {
          //** step two: apply steps based on these per line instruction. **
          String id = trn_line.substring(0, 2);
          if (id.equals("04")) {
            buy_Trn += trn_line +"\n";
            sameDTF = true;
            // retreive all the necessary information first, then create a
            // new string that contains all three information. Pass this into the enforceRules.
          }
          if (sameDTF == true) {
            if (id.equals("00")) {
              // now check the length of this transaction to see if its logout trans. or end of file
              if (trn_line.length() == 31) {
                logout_Trn = trn_line;
                // At this point, I now have access to previous buyer information and logout info.
                // So for more than one buy transaction, simply parse it and call this method that many
                // times
                String[] allBuyerStrings = buy_Trn.split("\n");
                for (int i=0; i<allBuyerStrings.length; i++) {
                  c1.enforceRules(buyTrnString(allBuyerStrings[i], logout_Trn));
                }
                //c1.enforceRules(buyTrnString(buy_Trn, logout_Trn));
              }
              // this means "00" so the following line is not apart of the previous dtf
              else {
                sameDTF = false;
              }
            }
          }
          else {
            c1.enforceRules(trn_line);
          }
        }
        // add buyer and logout lines to test print of them below this..
        dtf_br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      //** step three: to apply the changes to onto both files **
      c1.save();
  }
}
