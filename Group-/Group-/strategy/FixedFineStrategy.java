package strategy;

public class FixedFineStrategy implements FineStrategy {
    @Override
    public double calculateFine(double hoursOverstayed) {
        if (hoursOverstayed > 0) {
            return 50.0;
        }
        return 0.0;
    }
}