package strategy;

public interface FineStrategy extends java.io.Serializable {
    double calculateFine(double hoursOverstayed);
}