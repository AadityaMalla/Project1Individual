import java.util.List;

/*
 * Individual integration-test driver for the classes I own:
 *     WaitlistItem, Catalog, Warehouse
 * Collaborators (Client, ClientList, Product, WishlistItem, Transaction) are
 * included because Warehouse wires them together internally. Style follows the
 * BookTest SimpleTester example: build objects, call methods, compare to expected.
 */
public class DriverAM
{
    static int pass = 0, fail = 0;
    static void check(String label, boolean cond)
    {
        System.out.println((cond ? "  [PASS] " : "  [FAIL] ") + label);
        if (cond) pass++; else fail++;
    }

    public static void main(String[] args)
    {
        System.out.println("========== 1. WaitlistItem (unit) ==========");
        WaitlistItem wi = new WaitlistItem("C1", 5);
        check("getClientID() is C1",        wi.getClientID().equals("C1"));
        check("getQuantity() is 5",         wi.getQuantity() == 5);
        check("getDate() not null",         wi.getDate() != null);
        wi.setQuantity(8);
        check("setQuantity(8) -> 8",        wi.getQuantity() == 8);
        System.out.println("  toString(): " + wi);

        System.out.println("\n========== 2. Catalog (with Product) ==========");
        Catalog cat = new Catalog();
        Product a = new Product("Apple", 1.00, 10);
        Product b = new Product("Banana", 2.00, 20);
        check("addProduct(a) is true",                  cat.addProduct(a));
        check("addProduct(b) is true",                  cat.addProduct(b));
        check("addProduct(a) again is false (unique)",  !cat.addProduct(a));
        check("searchForProduct(a.id) returns a",       cat.searchForProduct(a.getID()) == a);
        check("searchForProduct(\"P9999\") is null",    cat.searchForProduct("P9999") == null);
        List<Product> copy = cat.getAllProducts();
        check("getAllProducts().size() is 2",           copy.size() == 2);
        copy.clear();
        check("getAllProducts() is a defensive copy",   cat.getAllProducts().size() == 2);

        System.out.println("\n========== 3. Warehouse (integration) ==========");
        Warehouse w = new Warehouse();
        String c1 = w.addClient("Ada Lovelace", "1 Analytical Way");
        String p1 = w.addProduct("Widget", 9.99, 3);
        System.out.println("  addClient  -> " + c1);
        System.out.println("  addProduct -> " + p1);
        check("client id starts with C",                c1.startsWith("C"));
        check("product id starts with P",               p1.startsWith("P"));
        check("returned product id is really in catalog", w.getProductWaitlist(p1) != null);

        check("addToWishlist(c1, p1, 5) is true",       w.addToWishlist(c1, p1, 5));
        check("addToWishlist(bad client) is false",     !w.addToWishlist("C0", p1));
        check("addToWishlist(bad product) is false",    !w.addToWishlist(c1, "P0"));

        System.out.println("  -- printAllClients (should show balance) --");
        w.printAllClients();
        System.out.println("  -- printAllProducts --");
        w.printAllProducts();
        System.out.println("  -- printClientWishlist(" + c1 + ") --");
        w.printClientWishlist(c1);

        System.out.println("\n  -- Waitlist path (ties in WaitlistItem) --");
        check("addToWaitlist(p1, c1, 5) is true",       w.addToWaitlist(p1, c1, 5));
        check("addToWaitlist(p1, c1, 2) bumps is true", w.addToWaitlist(p1, c1, 2));
        check("addToWaitlist(bad product) is false",    !w.addToWaitlist("P0", c1, 1));
        System.out.println("  printProductWaitlist(" + p1 + "):");
        w.printProductWaitlist(p1);

        System.out.println("\n  -- Transaction path --");
        check("recordTransaction(c1,...) is true",      w.recordTransaction(c1, "PAYMENT", "Initial deposit", 50.0));
        check("recordTransaction(bad client) is false", !w.recordTransaction("C0", "PAYMENT", "x", 1.0));
        System.out.println("  printClientTransactions(" + c1 + "):");
        w.printClientTransactions(c1);

        System.out.println("\n========== RESULT: " + pass + " passed, " + fail + " failed ==========");
    }
}