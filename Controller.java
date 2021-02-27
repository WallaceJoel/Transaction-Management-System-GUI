package proj3;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * This class is used to handle the GUI, adds functionality to the GUI.
 * 
 * @author Joel Wallace, Nicholas McConnell, jtw91 ncm78
 *
 */
public class Controller {
   private AccountDatabase accounts = new AccountDatabase();

   private String badInput = "Input data type mismatch.\n";
   private String invalidDate = " is not a valid date!\n";

   private String currencyFormat(Double d) {
      DecimalFormat format = new DecimalFormat("#,###,##0.00");

      return format.format(d);
   }

   ObservableList<String> accountList = FXCollections.observableArrayList();

   @FXML
   private TextArea textArea; // the place where output will be shown

   @FXML
   /**
    * Clears the text area
    * 
    * @param e action event
    */
   private void ClearButton(ActionEvent e) {
      textArea.clear();
   }

   @FXML
   /**
    * Used to print all accounts to textArea
    * 
    * @param e an action event
    */
   private void printAccounts(ActionEvent e) {
      if (accounts.isEmpty())
         textArea.appendText("Database is empty.\n");
      else
         accounts.printAccounts(textArea);
   }

   /*
    * Open account tab members
    * 
    * TextFields fName, lName, date, openBalance for
    * 
    * RadioButtons checking, Savings, Money market are apart of accountType
    * ToggleGroup
    * 
    */
   @FXML
   private TextField fName;
   @FXML
   private TextField lName;
   @FXML
   private DatePicker date;
   @FXML
   private TextField openBalance;
   @FXML
   private RadioButton checking;
   @FXML
   private RadioButton savings;
   @FXML
   private RadioButton moneyMarket;
   @FXML
   private ToggleGroup accountType;
   @FXML
   private CheckBox directDeposit;
   @FXML
   private CheckBox loyal;

   @FXML
   /**
    * Used to set certain controls active as soon as fxml is launched
    */
   private void initialize() {
      openBalance.setTextFormatter(new TextFormatter<>(x -> {
         if (x.getControlNewText().isEmpty()) {
            return x;
         }
         DecimalFormat format = new DecimalFormat("0.00###; 0.00###");
         ParsePosition parse = new ParsePosition(0);
         Object object = format.parse(x.getControlNewText(), parse);

         if (object == null || parse.getIndex() < x.getControlNewText().length()) {
            return null;
         } else {
            return x;
         }
      }));
      wdAmount.setTextFormatter(new TextFormatter<>(x -> {
         if (x.getControlNewText().isEmpty()) {
            return x;
         }
         DecimalFormat format = new DecimalFormat("0.00###; 0.00###");
         ParsePosition parse = new ParsePosition(0);
         Object object = format.parse(x.getControlNewText(), parse);

         if (object == null || parse.getIndex() < x.getControlNewText().length()) {
            return null;
         } else {
            return x;
         }
      }));
      accountsClose.setItems(accountList);
      accountsWD.setItems(accountList);

   }

   @FXML
   /**
    * This is used to disable the loyal checkbox on the event radio button
    * "checking" is clicked.
    * 
    * @param e an Action Event
    */

   private void disableLoyal(ActionEvent e) {
      loyal.setDisable(true);
      loyal.setSelected(false);
      directDeposit.setDisable(false);
   }

   @FXML
   /**
    * This is used to diasble the direct deposit check box on the event radio
    * button "savings" is clicked.
    * 
    * @param e an Action Event
    */
   private void disableDD(ActionEvent e) {
      loyal.setDisable(false);
      directDeposit.setSelected(false);
      directDeposit.setDisable(true);
   }

   @FXML
   /**
    * For when money Market is selected, used to disable both checkboxes
    * directDeposit and loyal
    * 
    * @param e an Action Event
    */
   private void disableBoth(ActionEvent e) {
      loyal.setDisable(true);
      directDeposit.setDisable(true);
      directDeposit.setSelected(false);
      loyal.setSelected(false);
   }

   /**
    * This is used to ensure the user has provided input for the categories for the
    * open tab
    * 
    * @return true if all the values are set, false if one or more is missing
    */
   private boolean valuesSet() {
      if (fName.getText().length() == 0 || lName.getText().length() == 0)
         return false;

      if (openBalance.getText().length() == 0)
         return false;

      if (date.getValue() == null)
         return false;

      return true;
   }

   @FXML
   /**
    * This is used when the Open Account Button is clicked
    * 
    * @param e an Action Event
    */
   private void OpenButton(ActionEvent e) {

      if (!valuesSet()) {
         textArea.appendText(badInput);
         return;
      }

      String first = fName.getText().replace(' ', '-');
      String last = lName.getText().replace(' ', '-');

      double balance = Double.parseDouble(openBalance.getText());

      Profile profile = new Profile(first, last);
      String stringDate = date.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
      String dateArray[] = stringDate.split("/");
      int mon, day, year;
      mon = Integer.parseInt(dateArray[0]);
      day = Integer.parseInt(dateArray[1]);
      year = date.getValue().getYear();
      Date accountDate = new Date(mon, day, year);
      Account account;
      if (checking == accountType.getSelectedToggle()) {
         if (directDeposit.isSelected()) {
            account = new Checking(profile, balance, accountDate, true);
         }
         account = new Checking(profile, balance, accountDate, false);
      } else if (savings == accountType.getSelectedToggle()) {
         if (loyal.isSelected()) {
            account = new Savings(profile, balance, accountDate, true);
         }
         account = new Savings(profile, balance, accountDate, false);
      } else if (moneyMarket == accountType.getSelectedToggle()) {
         account = new MoneyMarket(profile, balance, accountDate);
      } else {
         return;
      }
      if (accounts.add(account)) {
         textArea.appendText("Account opened and added to the database.\n");
         accountList.add("" + account.getBank() + " " + account.getProfile().toString());
      } else
         textArea.appendText("Account is already in the database.\n");

   }

   /*
    * This is for the Close Account list
    * 
    */
   @FXML
   private ChoiceBox<String> accountsClose;

   @FXML
   /**
    * handles what happens when the close button is clicked
    * 
    * @param e an ActionEvent e
    */
   private void closeAccount(ActionEvent e) {
      if (accountsClose.getValue() == null)
         return;
      String selected = accountsClose.getValue();
      String[] selectedAccount = selected.split(" ");

      if (selectedAccount[0].equals("Savings"))
         accounts.remove(new Savings(new Profile(selectedAccount[1], selectedAccount[2]), 0, null, false));
      else if (selectedAccount[0].equals("Checking"))
         accounts.remove(new Checking(new Profile(selectedAccount[1], selectedAccount[2]), 0, null, false));
      else
         accounts.remove(new MoneyMarket(new Profile(selectedAccount[2], selectedAccount[3]), 0, null));

      textArea.appendText("Account closed and removed from the database.\n");
      accountList.remove(selected);
   }

   /*
    * This is for the Withdraw/Deposit tab
    * 
    */
   @FXML
   private ChoiceBox<String> accountsWD;
   @FXML
   private TextField wdAmount; // amount to withdraw or deposit
   @FXML
   private RadioButton withdraw;
   @FXML
   private RadioButton deposit;
   @FXML
   private ToggleGroup withdrawDeposit;

   @FXML
   /**
    * Handles what happens when the transact button is clicked
    * 
    * @param e an action event
    */
   private void transact(ActionEvent e) {

      if (wdAmount.getText().isEmpty() || accountsWD.getValue() == null)
         return;
      if (withdraw == withdrawDeposit.getSelectedToggle())
         withdrawButton();
      else
         depositButton();
   }

   /**
    * handles what happens when the withdraw option is selected
    *
    */
   private void withdrawButton() {
      String selected = accountsWD.getValue();
      String[] selectedAccount = selected.split(" ");
      double amount = Double.parseDouble(wdAmount.getText());
      int result = -1;
      switch (selectedAccount[0]) {
      case "Money":
         result = accounts.withdrawal(new MoneyMarket(new Profile(selectedAccount[2], selectedAccount[3]), 0, null),
               amount);
         break;

      case "Checking":
         result = accounts.withdrawal(new Checking(new Profile(selectedAccount[1], selectedAccount[2]), 0, null, false),
               amount);
         break;

      case "Savings":
         result = accounts.withdrawal(new Savings(new Profile(selectedAccount[1], selectedAccount[2]), 0, null, false),
               amount);
         break;
      }

      if (result == 1)
         textArea.appendText("Insufficient funds.\n");
      else
         textArea.appendText("" + currencyFormat(amount) + " withdrawn from account\n");

   }

   /**
    * handles what happens when the deposit option is selected
    * 
    */
   private void depositButton() {
      String selected = accountsWD.getValue();
      String[] selectedAccount = selected.split(" ");
      double amount = Double.parseDouble(wdAmount.getText());

      if (selectedAccount[0].equals("Savings"))
         accounts.deposit(new Savings(new Profile(selectedAccount[1], selectedAccount[2]), 0, null, false), amount);
      else if (selectedAccount[0].equals("Checking"))
         accounts.deposit(new Checking(new Profile(selectedAccount[1], selectedAccount[2]), 0, null, false), amount);
      else
         accounts.deposit(new MoneyMarket(new Profile(selectedAccount[2], selectedAccount[3]), 0, null), amount);

      textArea.appendText("" + currencyFormat(amount) + " deposited to the account\n");

   }

   /*
    * This section is for the account management tab
    * 
    */

   @FXML
   private ToggleGroup sortGroup;

   @FXML
   private RadioButton lastName;
   @FXML
   private RadioButton dateOpen;

   @FXML
   /**
    * Used to get account Statements, occurs when the get statement button is
    * clicked
    * 
    * @param e an action event
    */
   private void getStatement(ActionEvent e) {
      if (lastName == sortGroup.getSelectedToggle())
         accounts.printByLastName(textArea);
      else
         accounts.printByDateOpen(textArea);
   }

   /*
    * Import a file tab
    */

   @FXML
   private TextField file;

   @FXML
   private void findFile(ActionEvent e) {
      try {
         FileChooser fileChooser = new FileChooser();
         fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));

         file.setText(fileChooser.showOpenDialog(null).getAbsolutePath());
      } catch (Exception exception) {
         return;
      }

   }

   private String handleContent(String input) {
      String[] content = input.split(",");
      int month, day, year;
      double balance;
      Date date;
      try {
         balance = Double.parseDouble(content[3]);
         String[] dateString = content[4].split("/");
         month = Integer.parseInt(dateString[0]);
         day = Integer.parseInt(dateString[1]);
         year = Integer.parseInt(dateString[2]);
      } catch (Exception e) {
         return badInput;
      }
      date = new Date(month, day, year);
      if (!date.isValid())
         return date.toString() + "" + invalidDate;
      Profile profile = new Profile(content[1], content[2]);
      boolean option = false;
      int withdrawals = -1;
      try {
         withdrawals = Integer.parseInt(content[5]);
      } catch (ArrayIndexOutOfBoundsException np) {
         return badInput;
      } catch (Exception e) {
         if (content[0].equals("M"))
            return badInput;
         if (content[5].toLowerCase().equals("true"))
            option = true;
         else if (content[5].toLowerCase().equals("false"))
            option = false;
         else
            return badInput;
      }
      boolean result = false;
      Account account;
      switch (content[0]) {
      case "S":
         account = new Savings(profile, balance, date, option);
         break;
      case "M":
         if (withdrawals < 0)
            withdrawals = 0;
         account = new MoneyMarket(profile, balance, date, withdrawals);
         break;
      case "C":
         account = new Checking(profile, balance, date, option);
         break;
      default:
         return badInput;
      }
      result = accounts.add(account);
      if (result == false)
         return "Account already exists.\n";
      accountList.add(account.getBank() + " " + account.getProfile().toString());
      return "Account added to the database.\n";
   }

   @FXML
   private void uploadFile(ActionEvent e) {
      if (file.getText().isEmpty()) {
         textArea.appendText("No file selected, or file doesn't exist.\n");
         return;
      }
      Scanner scanner;
      try {
         scanner = new Scanner(new File(file.getText()));
         while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            textArea.appendText(handleContent(input));
         }
      } catch (FileNotFoundException e1) {
         textArea.appendText("No file selected, or file doesn't exist.\n");
         e1.printStackTrace();
      }
   }

   /*
    * Export file tab
    */
   @FXML
   private TextField export;

   @FXML
   private void exportFile(ActionEvent e) {
      if (export.getText().isEmpty()) {
         textArea.appendText("No file name given.\n");
         return;
      }
      File exportingFile = new File(export.getText());
      if (accounts.exportFile(exportingFile))
         textArea.appendText("File successfully exported");
      else
         textArea.appendText("File export unsuccessful");

   }

   @FXML
   private void findExport(ActionEvent e) {

      try {
         FileChooser fileChooser = new FileChooser();
         fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
         export.setText(fileChooser.showSaveDialog(null).getAbsolutePath());
      } catch (Exception exception) {
         return;
      }
   }

}
