package bd.com.evaly.evalyshop.util;

public class MyVoucherInfo {

    String invoiceNumber,customerNumber,startDate,endDate,applicableDate,applyStatus,paymentStatus,date,voucherName,voucherDescription,voucherImage,voucherSlug,status;
    int number,claimAmount,totalPrice,totalPaid,value,amount,quantity;
    boolean applicable;

    public MyVoucherInfo() {
    }

    public MyVoucherInfo(String invoiceNumber, String customerNumber, String startDate, String endDate, String applicableDate, String applyStatus, String paymentStatus, String date, String voucherName, String voucherDescription, String voucherImage, String voucherSlug, String status, int number, int claimAmount, int totalPrice, int totalPaid, int value, int amount, int quantity, boolean applicable) {
        this.invoiceNumber = invoiceNumber;
        this.customerNumber = customerNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.applicableDate = applicableDate;
        this.applyStatus = applyStatus;
        this.paymentStatus = paymentStatus;
        this.date = date;
        this.voucherName = voucherName;
        this.voucherDescription = voucherDescription;
        this.voucherImage = voucherImage;
        this.voucherSlug = voucherSlug;
        this.status = status;
        this.number = number;
        this.claimAmount = claimAmount;
        this.totalPrice = totalPrice;
        this.totalPaid = totalPaid;
        this.value = value;
        this.amount = amount;
        this.quantity = quantity;
        this.applicable = applicable;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getApplicableDate() {
        return applicableDate;
    }

    public void setApplicableDate(String applicableDate) {
        this.applicableDate = applicableDate;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public String getVoucherDescription() {
        return voucherDescription;
    }

    public void setVoucherDescription(String voucherDescription) {
        this.voucherDescription = voucherDescription;
    }

    public String getVoucherImage() {
        return voucherImage;
    }

    public void setVoucherImage(String voucherImage) {
        this.voucherImage = voucherImage;
    }

    public String getVoucherSlug() {
        return voucherSlug;
    }

    public void setVoucherSlug(String voucherSlug) {
        this.voucherSlug = voucherSlug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(int claimAmount) {
        this.claimAmount = claimAmount;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(int totalPaid) {
        this.totalPaid = totalPaid;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isApplicable() {
        return applicable;
    }

    public void setApplicable(boolean applicable) {
        this.applicable = applicable;
    }
}
