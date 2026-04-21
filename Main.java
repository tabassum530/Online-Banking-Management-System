import java.util.*;

// ======================= ACCOUNT =======================
abstract class Account {   //  ABSTRACTION: abstract class

    private int accNo;     //  ENCAPSULATION: data hiding (private)
    private String name;
    private String email;
    private String pin;

    protected double balance;
    protected ArrayList<String> history;

    public Account(int accNo, String name, String email, String pin, double balance) {
        this.accNo = accNo;
        this.name = name;
        this.email = email;
        this.pin = pin;
        this.balance = balance;
        this.history = new ArrayList<>();
    }

    //  ENCAPSULATION: controlled access via getters
    public int getAccNo() { return accNo; }
    public String getName() { return name; }
    public double getBalance() { return balance; }

    public boolean checkPin(String inputPin) {  //  ENCAPSULATION: secure access
        return this.pin.equals(inputPin);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }
        balance += amount;
        history.add("Deposited: " + amount + " | " + new Date());
        System.out.println("Deposit successful");
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            System.out.println("Invalid or insufficient balance");
            return false;
        }
        balance -= amount;
        history.add("Withdraw: " + amount + " | " + new Date());
        System.out.println("Withdraw successful");
        return true;
    }

    //  POLYMORPHISM SUPPORT (used differently in transfer)
    public boolean withdrawSilent(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        history.add("Transferred: " + amount + " | " + new Date());
        return true;
    }

    public void depositSilent(double amount) {
        balance += amount;
        history.add("Received: " + amount + " | " + new Date());
    }

    public void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No transactions yet");
            return;
        }
        for (String h : history) {
            System.out.println(h);
        }
    }

    public abstract void accountType();  //  ABSTRACTION: abstract method
}

// ======================= SAVINGS =======================
class SavingsAccount extends Account {   //  INHERITANCE

    public SavingsAccount(int accNo, String name, String email, String pin, double balance) {
        super(accNo, name, email, pin, balance);
    }

    public void accountType() {   //  POLYMORPHISM (method overriding)
        System.out.println("Savings Account");
    }
}

// ======================= CURRENT =======================
class CurrentAccount extends Account {   //  INHERITANCE

    public CurrentAccount(int accNo, String name, String email, String pin, double balance) {
        super(accNo, name, email, pin, balance);
    }

    public void accountType() {   // POLYMORPHISM
        System.out.println("Current Account");
    }
}

// ======================= FDR =======================
class FDRAccount extends Account {   // INHERITANCE

    private double interestRate = 0.08;

    public FDRAccount(int accNo, String name, String email, String pin, double balance) {
        super(accNo, name, email, pin, balance);
    }

    public void accountType() {   // 🔴 POLYMORPHISM
        System.out.println("FDR Account");
    }

    public void calculateInterest(int years) {
        double finalAmount = balance * Math.pow(1 + interestRate, years);
        double interest = finalAmount - balance;
        System.out.println("Interest: " + interest);
    }
}

// ======================= BANK =======================
class Bank {
    private ArrayList<Account> accounts = new ArrayList<>();

    public void create(Account acc) {
        if (find(acc.getAccNo()) != null) {
            System.out.println("Account already exists");
            return;
        }
        accounts.add(acc);
        System.out.println("Account created successfully");
    }

    public Account login(int accNo, String pin) {
        for (Account a : accounts) {
            if (a.getAccNo() == accNo && a.checkPin(pin)) {
                return a;
            }
        }
        return null;
    }

    public Account find(int accNo) {
        for (Account a : accounts) {
            if (a.getAccNo() == accNo) return a;
        }
        return null;
    }

    public void transfer(Account sender, int receiverAcc, double amount) {
        Account receiver = find(receiverAcc);

        if (receiver == null) {
            System.out.println("Receiver not found");
            return;
        }

        //  POLYMORPHISM: Account reference, different object behavior at runtime
        if (sender.withdrawSilent(amount)) {
            receiver.depositSilent(amount);
            System.out.println("Transfer successful");
        } else {
            System.out.println("Transfer failed");
        }
    }

    public void showAll() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts available");
            return;
        }

        for (Account a : accounts) {
            a.accountType();   // POLYMORPHISM (runtime decision)
            System.out.println("Acc No: " + a.getAccNo());
            System.out.println("Name: " + a.getName());
            System.out.println("Balance: " + a.getBalance());
            System.out.println("----------------------");
        }
    }
}

// ======================= MAIN =======================
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Bank bank = new Bank();

        while (true) {
            try {
                System.out.println("\n====== BANK MENU ======");
                System.out.println("1. Create Account");
                System.out.println("2. Login");
                System.out.println("3. Show All Accounts");
                System.out.println("4. Exit");

                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        System.out.print("Account No: ");
                        int accNo = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        System.out.print("PIN: ");
                        String pin = sc.next();

                        System.out.print("Initial Balance: ");
                        double bal = sc.nextDouble();

                        System.out.println("1. Savings 2. Current 3. FDR");
                        int type = sc.nextInt();

                        if (type == 1)
                            bank.create(new SavingsAccount(accNo, name, email, pin, bal));
                        else if (type == 2)
                            bank.create(new CurrentAccount(accNo, name, email, pin, bal));
                        else if (type == 3)
                            bank.create(new FDRAccount(accNo, name, email, pin, bal));
                        else
                            System.out.println("Invalid type");

                        break;

                    case 2:
                        int attempts = 3;
                        Account user = null;

                        while (attempts > 0) {
                            System.out.print("Account No: ");
                            int lacc = sc.nextInt();

                            System.out.print("PIN: ");
                            String lpin = sc.next();

                            user = bank.login(lacc, lpin);

                            if (user != null) break;

                            attempts--;
                            System.out.println("Wrong credentials! Attempts left: " + attempts);
                        }

                        if (user == null) {
                            System.out.println("Login failed");
                            break;
                        }

                        System.out.println("Login successful");

                        while (true) {
                            System.out.println("\n1. Deposit");
                            System.out.println("2. Withdraw");
                            System.out.println("3. Transfer");
                            System.out.println("4. Balance");
                            System.out.println("5. History");
                            System.out.println("6. Interest (FDR)");
                            System.out.println("7. Logout");

                            int op = sc.nextInt();

                            if (op == 1) {
                                System.out.print("Amount: ");
                                user.deposit(sc.nextDouble());
                            } else if (op == 2) {
                                System.out.print("Amount: ");
                                user.withdraw(sc.nextDouble());
                            } else if (op == 3) {
                                System.out.print("Receiver Account: ");
                                int r = sc.nextInt();
                                System.out.print("Amount: ");
                                double amt = sc.nextDouble();
                                bank.transfer(user, r, amt);
                            } else if (op == 4) {
                                System.out.println("Balance: " + user.getBalance());
                            } else if (op == 5) {
                                user.showHistory();
                            } else if (op == 6) {
                                if (user instanceof FDRAccount) {  //  POLYMORPHISM check
                                    System.out.print("Years: ");
                                    int y = sc.nextInt();
                                    ((FDRAccount) user).calculateInterest(y);
                                } else {
                                    System.out.println("Not FDR account");
                                }
                            } else if (op == 7) {
                                break;
                            }
                        }
                        break;

                    case 3:
                        bank.showAll();
                        break;

                    case 4:
                        System.out.println("Thank you!");
                        return;

                    default:
                        System.out.println("Invalid choice");
                }

            } catch (Exception e) {
                System.out.println("Invalid input! Try again.");
                sc.nextLine();
            }
        }
    }
}
