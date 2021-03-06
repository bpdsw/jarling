import org.jarling.Starling;
import org.jarling.StarlingBankEnvironment;
import org.jarling.exceptions.StarlingBankRequestException;
import org.jarling.models.accounts.Account;
import org.jarling.models.accounts.AccountBalance;
import org.jarling.models.cards.Card;
import org.jarling.models.cards.CardType;
import org.jarling.models.common.*;
import org.jarling.models.contacts.Contact;
import org.jarling.models.contacts.ContactAccount;
import org.jarling.models.contacts.ContactAccountType;
import org.jarling.models.customers.Customer;
import org.jarling.models.customers.WhoAmI;
import org.jarling.models.directDebits.DirectDebitMandate;
import org.jarling.models.directDebits.DirectDebitTransaction;
import org.jarling.models.merchants.Merchant;
import org.jarling.models.merchants.MerchantLocation;
import org.jarling.models.payments.Payment;
import org.jarling.models.transactions.FasterPaymentsInTransaction;
import org.jarling.models.transactions.FasterPaymentsOutTransaction;
import org.jarling.models.transactions.MasterCardTransaction;
import org.jarling.models.transactions.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 *
 * @author Nav Roudsari (nav@rzari.co.uk)
 *
 */
public class SimpleStarlingAPITests {

    private Starling starling = null;
    private static final DateFormat transactionDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //Regex for matching dynamic data
    final String regexUUID          = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    final String regexSortCode      = "[0-9]{6}";
    final String regexAccountNumber = "[0-9]{8}";
    final String regexBIC           = "([a-zA-Z]{4}[a-zA-Z]{2}[a-zA-Z0-9]{2}([a-zA-Z0-9]{3})?)";
    final String regexIBAN          = "[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}";
    final String regexPostCode      = "([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))\\s?[0-9][A-Za-z]{2})";
    final String regexEmail         = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";


    @Before
    public void setUp() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("./cfg/sandbox.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.starling = new Starling(StarlingBankEnvironment.SANDBOX, properties.getProperty("starling.access.token"));
    }

    @Test
    public void testGetAccount(){
        try {
            Account account = starling.getAccount();

            assertTrue(account.getName().matches(regexUUID + " GBP"));
            assertTrue(account.getSortCode().matches(regexSortCode));
            assertTrue(account.getNumber().matches(regexAccountNumber));
            assertEquals("GBP", account.getCurrency());
            assertTrue(account.getBic().matches(regexBIC));
            assertTrue(account.getIban().matches(regexIBAN));
            assertTrue(account.getId().matches(regexUUID));
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testGetAccountBalance(){
        try {
            AccountBalance accountBalance = starling.getAccountBalance();

            assertTrue(accountBalance.getAmount().compareTo(BigDecimal.ZERO) == 1);
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testGetAddresses() {
        try {
            Addresses addresses = starling.getAddresses();

            Address currentAddress = addresses.getCurrent();
            Address previousAddress = addresses.getPrevious();
            assertNotEquals("", currentAddress.getCity());
            assertNotEquals("", currentAddress.getCountry());
            assertTrue(currentAddress.getPostcode().matches(regexPostCode));
            assertNotEquals("", currentAddress.getStreetAddress());

            assertEquals(null, previousAddress.getCity());
            assertEquals(null, previousAddress.getCountry());
            assertEquals(null, previousAddress.getPostcode());
            assertEquals(null, previousAddress.getStreetAddress());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testGetContacts() {
        try {
            Contact contact = starling.listContacts().get(0);

            assertNotEquals("", contact.getName());
            assertTrue(contact.getId().matches(regexUUID));
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testWhoAmI() {
        try {
            WhoAmI whoAmI = starling.getWhoAmI();
            assertTrue(whoAmI.getCustomerUid().matches(regexUUID));
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testCardResource() {
        try {
            Card card = starling.getCard();

            assertNotNull(card.getActivated());
            assertNotNull(card.getActivationRequested());
            assertNotNull(card.getDispatchDate());
            assertTrue(card.getId().matches(regexUUID));
            assertNotEquals("", card.getNameOnCard());
            assertEquals(CardType.NONE.getValue(), card.getType());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testContactAccountResource() {
        try {
            Contact contact = starling.listContacts().get(0);
            ContactAccount contactAccount = starling.listContactAccounts(contact.getId()).get(0);

            assertTrue(contactAccount.getId().matches(regexUUID));
            assertNotNull(contactAccount.getType());
            assertNotEquals("", contactAccount.getName());
            assertTrue(contactAccount.getSortCode().matches(regexSortCode));
            assertTrue(contactAccount.getAccountNumber().matches(regexAccountNumber));
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testCustomerResource() {
        try {
            Customer customer = starling.getCustomer();

            assertTrue(customer.getCustomerUid().matches(regexUUID));
            assertNotNull(customer.getDateOfBirth());
            assertTrue(customer.getEmail().matches(regexEmail));
            assertNotEquals("", customer.getFirstName());
            assertNotEquals("", customer.getLastName());
            assertNotEquals("", customer.getPhone());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testDirectDebitMandateResource() {
        try {
            DirectDebitMandate directDebitMandate = starling.listDirectDebitMandates().get(0);

            assertNotEquals("", directDebitMandate.getOriginatorName());
            assertEquals(null, directDebitMandate.getCancelled());
            assertNotNull(directDebitMandate.getCreated());
            assertTrue(directDebitMandate.getOriginatorUid().matches(regexUUID));
            assertNotEquals("", directDebitMandate.getReference());
            assertTrue(directDebitMandate.getUid().matches(regexUUID));
            assertNotNull(directDebitMandate.getSource());
            assertNotNull(directDebitMandate.getStatus());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testDirectDebitTransactionResource() {
        try {
            DirectDebitTransaction directDebitTransaction = starling.listDirectDebitTransactions().get(0);

            assertNotNull(directDebitTransaction.getCreated());
            assertNotNull(directDebitTransaction.getSource());
            assertTrue(directDebitTransaction.getId().matches(regexUUID));
            assertNotEquals("", directDebitTransaction.getCurrency());
            assertNotNull(directDebitTransaction.getDirection());
            assertTrue(directDebitTransaction.getMandateId().matches(regexUUID));
            assertNotEquals("", directDebitTransaction.getNarrative());
            assertNotEquals("", directDebitTransaction.getTitle());
            assertTrue(directDebitTransaction.getAmount().compareTo(BigDecimal.ZERO) == -1);
            assertNull(directDebitTransaction.getBalance());
            assertNotNull(directDebitTransaction.getType());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testFasterPaymentsInTransactionResource() {

        try {
            FasterPaymentsInTransaction fasterPaymentsInTransaction = starling.listFasterPaymentsInTransactions().get(0);

            assertNotNull(fasterPaymentsInTransaction.getCreated().toString());
            assertNotNull(fasterPaymentsInTransaction.getSource());
            assertTrue(fasterPaymentsInTransaction.getId().matches(regexUUID));
            assertNotEquals("", fasterPaymentsInTransaction.getCurrency());
            assertNotNull(fasterPaymentsInTransaction.getDirection());
            assertNotEquals("", fasterPaymentsInTransaction.getNarrative());
            assertTrue(fasterPaymentsInTransaction.getAmount().compareTo(BigDecimal.ZERO) == 1);
            assertNull(fasterPaymentsInTransaction.getSendingContactAccountId());
            assertNull(fasterPaymentsInTransaction.getSendingContactId());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testFasterPaymentsOutTransactionResource() {

        try {
            FasterPaymentsOutTransaction fasterPaymentsOutTransaction = starling.listFasterPaymentsOutTransactions()
                    .stream()
                    .filter(payment -> payment.getId().equals("ab3b1fff-bd0e-4f85-8570-8a16f334c083"))
                    .findFirst()
                    .orElse(null);

            assertNotNull(fasterPaymentsOutTransaction.getCreated());
            assertNotNull(fasterPaymentsOutTransaction.getSource());
            assertTrue(fasterPaymentsOutTransaction.getId().matches(regexUUID));
            assertNotEquals("", fasterPaymentsOutTransaction.getCurrency());
            assertNotNull(fasterPaymentsOutTransaction.getDirection());
            assertNotEquals("", fasterPaymentsOutTransaction.getNarrative());
            assertTrue(fasterPaymentsOutTransaction.getAmount().compareTo(BigDecimal.ZERO) == -1);
            assertTrue(fasterPaymentsOutTransaction.getReceivingContactAccountId().matches(regexUUID));
            assertTrue(fasterPaymentsOutTransaction.getReceivingContactId().matches(regexUUID));
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testMasterCardTransactionResource() {

        try {
            MasterCardTransaction masterCardTransaction = starling.listMasterCardTransactions().get(0);

            assertNotNull(masterCardTransaction.getCreated());
            assertNotNull(masterCardTransaction.getSource());
            assertTrue(masterCardTransaction.getSourceAmount().compareTo(BigDecimal.ZERO) == 1);
            assertNotEquals("", masterCardTransaction.getSourceCurrency());
            assertTrue(masterCardTransaction.getId().matches(regexUUID));
            assertNotEquals("", masterCardTransaction.getCurrency());
            assertNotNull(masterCardTransaction.getDirection());
            assertNotEquals("", masterCardTransaction.getNarrative());
            assertTrue(masterCardTransaction.getAmount().compareTo(BigDecimal.ZERO) == -1);
            assertNotNull(masterCardTransaction.getMastercardTransactionMethod());
            assertTrue(masterCardTransaction.getMerchantId().matches(regexUUID));
            assertTrue(masterCardTransaction.getMerchantLocationId().matches(regexUUID));
            assertNotNull(masterCardTransaction.getStatus());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testMerchantLocationResource() {
        try {
            MerchantLocation merchantLocation = starling.getMerchantLocation("c052f76f-e919-427d-85fc-f46a75a3ff26", "b7b9f347-99ab-4a83-9317-ec9895f47755");

            assertEquals(null, merchantLocation.getAddress());
            assertEquals("ChIJkSyl5M_xd0gRvnIIm_kNiqg", merchantLocation.getGooglePlaceId());
            assertEquals("Mastercard Europe", merchantLocation.getLocationName());
            assertEquals("b7b9f347-99ab-4a83-9317-ec9895f47755", merchantLocation.getMerchantLocationUid());
            assertEquals("Mastercard", merchantLocation.getMerchantName());
            assertEquals("c052f76f-e919-427d-85fc-f46a75a3ff26", merchantLocation.getMerchantUid());
            assertEquals(null, merchantLocation.getPhoneNumber());
            assertEquals(new Integer(3431), merchantLocation.getMastercardMerchantCategoryCode());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testMerchantResource() {

        try {
            Merchant merchant = starling.getMerchant("c052f76f-e919-427d-85fc-f46a75a3ff26");

            assertEquals(null, merchant.getPhoneNumber());
            assertEquals("c052f76f-e919-427d-85fc-f46a75a3ff26", merchant.getMerchantUid());
            assertEquals("Mastercard", merchant.getName());
            assertEquals("@mastercard", merchant.getTwitterUsername());
            assertEquals("http://mastercard.co.uk", merchant.getWebsite());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testPaymentResource()  {

        try {
            Payment payment = starling.listPayments().get(0);
            assertNotNull(payment.getStartDate());
            assertNotNull(payment.getNextDate());
            assertNull(payment.getLastDate());
            assertNotNull(payment.getEndDate());
            assertNull(payment.getCancelledAt());
            assertNull(payment.getRecurrenceRule());
            assertTrue(payment.getPaymentOrderId().matches(regexUUID));
            assertTrue(payment.getReceivingContactAccountId().matches(regexUUID));
            assertNull(payment.getMandateId());
            assertNotNull(payment.getPaymentType());
            assertNotEquals("", payment.getRecipientName());
            assertNotEquals("", payment.getReference());
            assertTrue(payment.getAmount().compareTo(BigDecimal.ZERO) == 1);
            assertNotEquals("", payment.getCurrency());
            assertNotNull(payment.getImmediate());
        }catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testMakePayment(){
        try {
            Contact contact = starling.createContact("starlingMan", "608371", "84829206");
            ContactAccount contactAccount = starling.listContactAccounts(contact.getId())
                    .stream()
                    .filter(a -> a.getType().equals(ContactAccountType.UK_ACCOUNT_AND_SORT_CODE))
                    .findFirst()
                    .orElse(null);
            AccountBalance accountBalance           = starling.getAccountBalance();
            BigDecimal previousAccountBalanceAmount = accountBalance.getAmount();
            Payment payment = starling.makeLocalPayment(contactAccount.getId(), "TEST_PAYMENT_1", new BigDecimal(1.00));
            BigDecimal newAccountBalanceAmount = starling.getAccountBalance().getAmount();
            assertEquals(previousAccountBalanceAmount.subtract(new BigDecimal(1.0)), newAccountBalanceAmount);
            assertEquals("TEST_PAYMENT_1", payment.getReference());
        } catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }

    @Test
    public void testMakeScheduledPayment()  {
        try {
            Contact contact = starling.createContact("starlingMan", "608371", "84829206");
            ContactAccount contactAccount = starling.listContactAccounts(contact.getId())
                    .stream()
                    .filter(a -> a.getType().equals(ContactAccountType.UK_ACCOUNT_AND_SORT_CODE))
                    .findFirst()
                    .orElse(null);
            RecurrenceRule recurrenceRule = new RecurrenceRule(
                    transactionDateFormat.parse("2018-04-02"),
                    Frequency.MONTHLY,
                    1,
                    1,
                    transactionDateFormat.parse("2020-02-02"),
                    DayOfWeek.MONDAY,
                    null,
                    3,
                    1);
            recurrenceRule.add(DayOfWeek.WEDNESDAY);

            Payment payment = starling.makeScheduledPayment(contactAccount.getId(), "TEST_RECURRING_PAYMENT", new BigDecimal(1.00), recurrenceRule);
            assertEquals(new BigDecimal(1.00).setScale(2, BigDecimal.ROUND_HALF_EVEN), payment.getAmount());
            assertEquals("TEST_RECURRING_PAYMENT", payment.getReference());
        }catch(StarlingBankRequestException se){
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }catch(ParseException pe){
            fail("Could not parse date");
        }
    }

    @Test
    public void testTransactionResource() {

        try {
            Transaction transaction = starling.listTransactions()
                    .stream()
                    .filter(t -> t.getNarrative().equals("TEST_PAYMENT"))
                    .findFirst()
                    .orElse(null);

            assertNotEquals("", transaction.getNarrative());
            assertNotEquals("", transaction.getCurrency());
            assertTrue(transaction.getAmount().compareTo(BigDecimal.ZERO) == -1);
            assertTrue(transaction.getBalance().compareTo(BigDecimal.ZERO) == 1);
            assertNotNull(transaction.getCreated());
            assertNotNull(transaction.getDirection());
            assertTrue(transaction.getId().matches(regexUUID));
            assertNotNull(transaction.getSource());

            for (Transaction t : starling.listTransactions(transactionDateFormat.parse("2017-09-01"), transactionDateFormat.parse("2017-09-01"))){
                assertTrue(t.getAmount().compareTo(BigDecimal.ZERO) == 1);
                assertNotNull(t.getCreated());
            }
        }catch(StarlingBankRequestException se){
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }catch(ParseException pe){
            fail("Could not parse date");
        }


    }

    @Test
    public void testCreateAndDeleteContact() {

        try {
            Contact newContact = starling.createContact("starlingMan", "608371", "84829206");
            assertEquals("starlingMan", newContact.getName());

            //Delete contact
            starling.deleteContact(newContact.getId());

            //Check contact no longer exists
            Contact deletedContact = starling.listContacts().stream().filter(contact -> contact.getId().equals("starlingMan")).findFirst().orElse(null);
            assertNull(deletedContact);
        } catch (StarlingBankRequestException se) {
            fail("StarlingBankRequestException: " + se.getReason() + ":" + se.getErrorDescription());
        }
    }
}
