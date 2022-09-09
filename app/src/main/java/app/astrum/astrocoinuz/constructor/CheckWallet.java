package app.astrum.astrocoinuz.constructor;

public class CheckWallet {
    String address;

    public CheckWallet(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
