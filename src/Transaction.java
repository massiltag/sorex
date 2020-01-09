
public class Transaction {
    private Client from;

    private Client to;

    private float value;

    public String toString(){
        return "From " + from.getName() + " (id: " + from.getId() + "), to " + to.getName() + " (id: " + to.getId() + "), value: " + value + " SorEx";
    }

    public Client getFrom() {
        return from;
    }

    public void setFrom(Client from) {
        this.from = from;
    }

    public Client getTo() {
        return to;
    }

    public void setTo(Client to) {
        this.to = to;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
